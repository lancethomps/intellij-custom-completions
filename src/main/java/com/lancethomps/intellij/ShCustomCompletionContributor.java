package com.lancethomps.intellij;

import static com.intellij.patterns.PlatformPatterns.psiElement;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;

public class ShCustomCompletionContributor extends CompletionContributor {

  public static final String LANGUAGE = "Shell Script";

  public ShCustomCompletionContributor() {
    extend(
        CompletionType.BASIC,
        psiElement().withLanguage(PluginsHelper.getLanguage(LANGUAGE)),
        new ShCustomCompletionProvider()
    );
  }

}