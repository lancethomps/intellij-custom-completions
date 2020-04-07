package com.lancethomps.intellij;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.psi.PsiElement;

public class CompletionsHelper {

  @Nullable
  public static String getCurrentText(@NotNull CompletionParameters parameters) {
    PsiElement original = parameters.getOriginalPosition();
    if (original == null) {
      return null;
    }

    int textLength = parameters.getOffset() - parameters.getPosition().getTextRange().getStartOffset();
    return original.getText().substring(0, textLength);
  }

}
