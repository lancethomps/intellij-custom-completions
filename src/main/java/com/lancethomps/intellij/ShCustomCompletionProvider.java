package com.lancethomps.intellij;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.replaceOnce;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableSet;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.ProcessingContext;
import com.lancethomps.lava.common.Checks;
import com.lancethomps.lava.common.file.FileUtil;

public class ShCustomCompletionProvider extends CompletionProvider<CompletionParameters> {

  public static final Set<String> FILE_NAME_BLACK_LIST = ImmutableSet.of(
      "kitchen"
  );
  public static final Set<Pattern> FILE_NAME_BLACK_LIST_REGEX = ImmutableSet.of(
      Pattern.compile("^x86_.*"),
      Pattern.compile(".*-[0-9.]+$")
  );
  public static final Set<File> PATH_DIRS = ImmutableSet.of(
      new File(System.getenv("HOME") + "/bin"),
      new File(System.getenv("HOME") + "/.dotfiles"),
      new File("/usr/local/bin")
  );
  private static final Logger LOG = Logger.getInstance(ShCustomCompletionContributor.class);

  public static boolean includePathFile(File file) {
    if (!file.canExecute()) {
      return false;
    }
    return Checks.passesWhiteAndBlackListCheck(
        file.getName(),
        null,
        FILE_NAME_BLACK_LIST,
        null,
        FILE_NAME_BLACK_LIST_REGEX,
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
    List<LookupElementBuilder> elements = FileUtil.findFiles(ShCustomCompletionProvider::includePathFile, false, PATH_DIRS).stream()
        .map(File::getName)
        .flatMap(name -> name.startsWith("git-") ? Stream.of(replaceOnce(name, "git-", "git ")) : Stream.of(name))
        .map(LookupElementBuilder::create)
        .collect(toList());
    result.addAllElements(elements);
  }

}
