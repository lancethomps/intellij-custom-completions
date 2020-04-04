package com.lancethomps.intellij;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableSet;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;

public class ShCustomCompletionContributor extends CompletionContributor {

  public static final String LANGUAGE = "Shell Script";
  public static final Set<File> PATH_DIRS = ImmutableSet.of(
      new File(System.getenv("HOME") + "/bin"),
      new File(System.getenv("HOME") + "/.dotfiles"),
      new File("/usr/local/bin")
  );

  public ShCustomCompletionContributor() {
    extend(
        CompletionType.BASIC,
        psiElement().withLanguage(PluginsHelper.getLanguage(LANGUAGE)),
        new CompletionProvider<CompletionParameters>() {

          @Override
          public void addCompletions(
              @NotNull CompletionParameters parameters,
              @NotNull ProcessingContext context,
              @NotNull CompletionResultSet result
          ) {
            List<LookupElementBuilder> elements = PluginsHelper.findFilesRecursive(PATH_DIRS, file -> file.canExecute()).stream()
                .map(file -> LookupElementBuilder.create(file.getName()))
                .collect(toList());
            result.addAllElements(elements);
          }
        }
    );
  }

}