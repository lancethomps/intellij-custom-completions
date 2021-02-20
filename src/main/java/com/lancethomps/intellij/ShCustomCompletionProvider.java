package com.lancethomps.intellij;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.replaceOnce;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
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
  private static final Logger LOG = Logger.getInstance(ShCustomCompletionContributor.class);

  public static ShCustomCompletionConfig getConfig() {
    if (CONFIG_FILE.exists() && CONFIG_FILE.isFile()) {
      String yaml = PropertyParser.parseAndReplaceWithProps(FileUtil.readFile(CONFIG_FILE));
      return PluginsHelper.fromYaml(yaml, ShCustomCompletionConfig.class);
    }
    return new ShCustomCompletionConfig();
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

  @Override
  public void addCompletions(
      @NotNull CompletionParameters parameters,
      @NotNull ProcessingContext context,
      @NotNull CompletionResultSet result
  ) {
    String text = CompletionsHelper.getCurrentText(parameters);
    LOG.trace("sh completion text: " + text);
    if (StringUtils.isNumeric(text)) {
      return;
    }
    ShCustomCompletionConfig config = getConfig();
    List<LookupElement> elements = new ArrayList<>();

    elements.addAll(getBashFunctionsCompletions(parameters, context, result, config));
    elements.addAll(getManualCompletions(parameters, context, result, config));
    elements.addAll(getPathExecutablesCompletions(parameters, context, result, config));

    if (config.getPriority() != null) {
      elements = elements.stream().map(elem -> PrioritizedLookupElement.withPriority(elem, config.getPriority())).collect(toList());
    }

    result.addAllElements(elements);
  }

  private List<LookupElement> getBashFunctionsCompletions(
      @NotNull CompletionParameters parameters,
      @NotNull ProcessingContext context,
      @NotNull CompletionResultSet result,
      @NotNull ShCustomCompletionConfig config
  ) {
    if (Checks.isEmpty(config.getBashFunctionsFiles())) {
      return Collections.emptyList();
    }
    return config.getBashFunctionsFiles().stream()
        .filter(File::isFile)
        .flatMap(file -> Patterns.findMatchesAndExtract(BASH_FUNCTIONS_EXTRACTOR, FileUtil.readFile(file), 1).stream())
        .map(LookupElementBuilder::create)
        .map(elem -> elem.withIcon(PlatformIcons.FUNCTION_ICON).withTypeText("function", true))
        .collect(toList());
  }

  private List<LookupElement> getManualCompletions(
      @NotNull CompletionParameters parameters,
      @NotNull ProcessingContext context,
      @NotNull CompletionResultSet result,
      @NotNull ShCustomCompletionConfig config
  ) {
    if (Checks.isEmpty(config.getAddCompletions())) {
      return Collections.emptyList();
    }
    return config.getAddCompletions().stream()
        .map(LookupElementBuilder::create)
        .collect(toList());
  }

  private List<LookupElement> getPathExecutablesCompletions(
      @NotNull CompletionParameters parameters,
      @NotNull ProcessingContext context,
      @NotNull CompletionResultSet result,
      @NotNull ShCustomCompletionConfig config
  ) {
    if (Checks.isEmpty(config.getPathDirs())) {
      return Collections.emptyList();
    }
    return FileUtil.findFiles(file -> includePathFile(config, file), false, config.getPathDirs()).stream()
        .map(File::getName)
        .flatMap(name -> name.startsWith("git-") ? Stream.of(replaceOnce(name, "git-", "git ")) : Stream.of(name))
        .map(LookupElementBuilder::create)
        .map(elem -> elem.withIcon(AllIcons.Nodes.Console).withTypeText("command", true))
        .collect(toList());
  }

}
