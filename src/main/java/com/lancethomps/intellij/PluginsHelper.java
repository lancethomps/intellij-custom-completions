package com.lancethomps.intellij;

import com.intellij.lang.Language;
import com.intellij.lang.LanguageUtil;

public class PluginsHelper {

  public static Language getLanguage(String id) {
    return LanguageUtil.getFileLanguages().stream().filter(lang -> id.equals(lang.getID())).findFirst().orElse(null);
  }

}
