package com.lancethomps.intellij;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NotNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ProcessingContext;
import com.lancethomps.lava.common.Checks;
import com.lancethomps.lava.common.Patterns;
import com.lancethomps.lava.common.file.FileUtil;
import com.lancethomps.lava.common.properties.PropertyParser;

public class ShCustomCompletionProvider extends CompletionProvider<CompletionParameters> {

  public static final File CONFIG_FILE = new File(PluginsHelper.USER_HOME, ".config/intellij-custom-completions/sh-completions.yaml");
  private static final Pattern BASH_FUNCTIONS_EXTRACTOR = Pattern.compile("function (.*?)\\(\\)");
  private static final CompletionCacheLoader CACHE_LOADER = new CompletionCacheLoader();
  private static final long DEFAULT_CACHE_EXPIRE_AFTER_SECONDS = 60;
  private static final GitCommandInsertHandler GIT_COMMAND_INSERT_HANDLER = new GitCommandInsertHandler();
  private static final Logger LOG = Logger.getInstance(ShCustomCompletionContributor.class);
  private static LoadingCache<ShCustomCompletionType, List<LookupElement>> cache = createCache(DEFAULT_CACHE_EXPIRE_AFTER_SECONDS);
  private static ShCustomCompletionConfig loadedConfig = new ShCustomCompletionConfig();
  private static long loadedConfigLastModified;

  public static ShCustomCompletionConfig getConfig() {
    if (CONFIG_FILE.exists() && CONFIG_FILE.isFile() && CONFIG_FILE.lastModified() > loadedConfigLastModified) {
      loadedConfigLastModified = CONFIG_FILE.lastModified();
      Long currentCacheExpireAfterSeconds = loadedConfig.getCacheExpireAfterSeconds();
      LOG.info("Loading new ShCustomCompletionConfig file");
      String yaml = PropertyParser.parseAndReplaceWithProps(FileUtil.readFile(CONFIG_FILE));
      loadedConfig = PluginsHelper.fromYaml(yaml, ShCustomCompletionConfig.class);
      if (Objects.equals(loadedConfig.getCacheExpireAfterSeconds(), currentCacheExpireAfterSeconds)) {
        cache.invalidateAll();
      } else {
        cache = createCache(Optional.ofNullable(loadedConfig.getCacheExpireAfterSeconds()).orElse(DEFAULT_CACHE_EXPIRE_AFTER_SECONDS));
      }
      LOG.setLevel(Level.toLevel(loadedConfig.getLogLevel(), Level.DEBUG));
    }
    return loadedConfig;
  }

  public static boolean includePathFile(ShCustomCompletionConfig config, File file) {
    if (!file.canExecute()) {
      return false;
    }
    return Checks.passesWhiteAndBlackListCheck(
      file.getName(),
      config.getFileNameWhiteList(),
      config.getFileNameBlackList(),
      config.getFileNameWhiteListRegex(),
      config.getFileNameBlackListRegex(),
      true
    ).getLeft();
  }

  private static LoadingCache<ShCustomCompletionType, List<LookupElement>> createCache(long expireAfterSeconds) {
    LOG.info(String.format("Creating cache with expireAfterWrite of %s seconds", expireAfterSeconds));
    return CacheBuilder.newBuilder()
      .expireAfterWrite(expireAfterSeconds, TimeUnit.SECONDS)
      .build(CACHE_LOADER);
  }

  private static List<LookupElement> createElements(
    @NotNull ShCustomCompletionConfig config
  ) {
    List<LookupElement> elements = new ArrayList<>();
    elements.addAll(getBashFunctionsCompletions(config));
    elements.addAll(getEnvVarsCompletions(config));
    elements.addAll(getManualCompletions(config));
    elements.addAll(getPathExecutablesCompletions(config));
    return elements;
  }

  private static List<LookupElement> getBashFunctionsCompletions(
    @NotNull ShCustomCompletionConfig config
  ) {
    return Stream.concat(getBashFunctionsFilesCompletions(config), getBashFunctionsManualCompletions(config))
      .collect(toList());
  }

  private static Stream<LookupElementBuilder> getBashFunctionsFilesCompletions(
    @NotNull ShCustomCompletionConfig config
  ) {
    if (Checks.isEmpty(config.getBashFunctionsFiles())) {
      return Stream.empty();
    }
    return config.getBashFunctionsFiles().stream()
      .filter(File::isFile)
      .flatMap(file -> Patterns.findMatchesAndExtract(BASH_FUNCTIONS_EXTRACTOR, FileUtil.readFile(file), 1).stream()
        .map(LookupElementBuilder::create)
        .map(elem -> elem.withIcon(PlatformIcons.FUNCTION_ICON).withTypeText(String.format("function (%s)", file.getName()), true)));
  }

  private static Stream<LookupElementBuilder> getBashFunctionsManualCompletions(
    @NotNull ShCustomCompletionConfig config
  ) {
    if (Checks.isEmpty(config.getAddFunctions())) {
      return Stream.empty();
    }
    return config.getAddFunctions().stream()
      .map(LookupElementBuilder::create)
      .map(elem -> elem.withIcon(PlatformIcons.FUNCTION_ICON).withTypeText("function (manual)", true));
  }

  private static List<LookupElement> getEnvVarsCompletions(
    @NotNull ShCustomCompletionConfig config
  ) {
    if (Checks.isEmpty(config.getAddEnvVars())) {
      return Collections.emptyList();
    }
    return config.getAddEnvVars().stream()
      .map(LookupElementBuilder::create)
      .map(elem -> elem
        .withIcon(PlatformIcons.VARIABLE_ICON)
        .withTypeText("Env Var (manual)", true)
        .withPresentableText(String.format("$%s", elem.getLookupString())))
      .collect(toList());
  }

  private static List<LookupElement> getManualCompletions(
    @NotNull ShCustomCompletionConfig config
  ) {
    if (Checks.isEmpty(config.getAddCompletions())) {
      return Collections.emptyList();
    }
    return config.getAddCompletions().stream()
      .map(LookupElementBuilder::create)
      .collect(toList());
  }

  private static List<LookupElement> getPathExecutablesCompletions(
    @NotNull ShCustomCompletionConfig config
  ) {
    if (Checks.isEmpty(config.getPathDirs())) {
      return Collections.emptyList();
    }

    List<String> gitNames = new ArrayList<>();
    List<String> names = FileUtil.findFiles(file -> includePathFile(config, file), false, config.getPathDirs()).stream()
      .map(File::getName)
      .peek(name -> {
        if (name.startsWith("git-")) {
          gitNames.add(name);
        }
      }).filter(name -> !name.startsWith("git-"))
      .collect(toList());

    Stream<LookupElementBuilder> gitNamesStream = gitNames.stream()
      .map(name -> {
        String multiWordName = StringUtils.replaceOnce(name, "git-", "git ");
        return LookupElementBuilder.create(name)
          .withLookupStrings(ImmutableSet.of(multiWordName, name))
          .withPresentableText(multiWordName);
      })
      .map(builder -> builder.withInsertHandler(GIT_COMMAND_INSERT_HANDLER));

    return Stream.concat(names.stream().map(LookupElementBuilder::create), gitNamesStream)
      .map(elem -> elem.withIcon(Nodes.Console).withTypeText("command", true))
      .collect(toList());
  }

  @Override
  public void addCompletions(
    @NotNull CompletionParameters parameters,
    @NotNull ProcessingContext context,
    @NotNull CompletionResultSet result
  ) {
    String text = CompletionsHelper.getCurrentText(parameters);
    LOG.debug("sh completion text: " + text);
    if (StringUtils.isNumeric(text)) {
      return;
    }

    ShCustomCompletionConfig config = getConfig();
    List<LookupElement> elements = cache.getUnchecked(ShCustomCompletionType.ALL);

    if (config.getPriority() != null) {
      elements = elements.stream().map(elem -> PrioritizedLookupElement.withPriority(elem, config.getPriority())).collect(toList());
    }

    result.addAllElements(elements);
  }

  static class CompletionCacheLoader extends CacheLoader<ShCustomCompletionType, List<LookupElement>> {

    @Override
    public List<LookupElement> load(ShCustomCompletionType key) throws Exception {
      LOG.info(String.format("Getting new List<LookupElement> for: %s", key));
      ShCustomCompletionConfig config = getConfig();
      switch (key) {
        case ALL:
          return createElements(config);
        case ENV_VARS:
          return getEnvVarsCompletions(config);
        case BASH_FUNCTIONS:
          return getBashFunctionsCompletions(config);
        case MANUAL:
          return getManualCompletions(config);
        case PATH_EXECUTABLES:
          return getPathExecutablesCompletions(config);
        default:
          throw new IllegalArgumentException(String.format("ShCustomCompletionType not recognized: %s", key));
      }
    }

  }

}
