package com.lancethomps.intellij;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

import com.intellij.lang.Language;
import com.intellij.lang.LanguageUtil;

public class PluginsHelper {

  public static Language getLanguage(String id) {
    return LanguageUtil.getFileLanguages().stream().filter(lang -> id.equals(lang.getID())).findFirst().orElse(null);
  }

  public static Set<File> findFilesRecursive(Collection<File> dirs, Predicate<File> include) {
    Set<File> result = new LinkedHashSet<>();
    IOFileFilter fileFilter = new AbstractFileFilter() {
      @Override
      public boolean accept(File file) {
        return include.test(file);
      }
    };
    for (File dir : dirs) {
      if (!dir.isDirectory()) {
        continue;
      }
      result.addAll(FileUtils.listFiles(dir, fileFilter, FalseFileFilter.INSTANCE));
    }
    return result;
  }

}
