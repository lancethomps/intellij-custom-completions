package com.lancethomps.intellij;

import static com.intellij.patterns.PlatformPatterns.psiElement;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableSet;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PatternCondition;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;

public class ShCustomCompletionContributor extends CompletionContributor {

  public static final String LANGUAGE_SHELL = "Shell Script";
  public static final String LANGUAGE_BASH_SUPPORT = "BashPro Shell Script";
  public static final Set<String> LANGUAGES = ImmutableSet.of(LANGUAGE_SHELL, LANGUAGE_BASH_SUPPORT);

  public ShCustomCompletionContributor() {
    extend(
        CompletionType.BASIC,
        psiElement().with(new AllShellLanguagesPattern<>()),
        new ShCustomCompletionProvider()
    );
  }

  public static class AllShellLanguagesPattern<T extends PsiElement> extends PatternCondition<T> {

    public AllShellLanguagesPattern() {
      super("withAllShellLanguages");
    }

    @Override
    public boolean accepts(@NotNull T t, ProcessingContext context) {
      return LANGUAGES.contains(t.getLanguage().getID());
    }

  }

}