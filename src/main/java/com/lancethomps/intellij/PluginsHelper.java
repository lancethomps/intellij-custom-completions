package com.lancethomps.intellij;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageUtil;

public class PluginsHelper {

  public static String USER_HOME = System.getProperty("user.home");
  public static YAMLMapper YAML_MAPPER = new YAMLMapper();

  public static <T> T fromYaml(String yaml, Class<T> type) {
    try {
      return YAML_MAPPER.readValue(yaml, type);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static Language getLanguage(String id) {
    return LanguageUtil.getFileLanguages().stream().filter(lang -> id.equals(lang.getID())).findFirst().orElse(null);
  }

}
