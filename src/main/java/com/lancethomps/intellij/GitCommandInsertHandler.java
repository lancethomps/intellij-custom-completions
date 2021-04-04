package com.lancethomps.intellij;

import org.jetbrains.annotations.NotNull;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;

public class GitCommandInsertHandler implements InsertHandler<LookupElement> {

  private static final Logger LOG = Logger.getInstance(GitCommandInsertHandler.class);

  @Override
  public void handleInsert(
    @NotNull InsertionContext context, @NotNull LookupElement item
  ) {
    PsiFile file = context.getFile();
    int startOffset = context.getStartOffset();
    PsiElement insertedElement = file.findElementAt(startOffset);

    logElement(insertedElement, "startOffset");
    boolean shouldDelete = false;

    if (startOffset > 1) {
      PsiElement probWhiteSpace = file.findElementAt(startOffset - 1);
      logElement(probWhiteSpace, "startOffset - 1");
      PsiElement probPrevWord = file.findElementAt(startOffset - 2);
      logElement(probPrevWord, "startOffset - 2");
      if (probWhiteSpace instanceof PsiWhiteSpace && probPrevWord != null && "git".equals(probPrevWord.getText())) {
        shouldDelete = true;
      }
    }

    if (shouldDelete) {
      LOG.debug("Deleting inserted git command prefix of git-");
      context.getEditor().getDocument().deleteString(startOffset, startOffset + 4);
    }
  }

  private void logElement(PsiElement element, String elemId) {
    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format(
        "git command PsiElement [%s]: %s type=%s text=%s",
        elemId,
        element,
        element == null ? null : element.getClass(),
        element == null ? null : element.getText()
      ));
    }
  }

}
