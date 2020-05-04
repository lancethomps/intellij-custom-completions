package com.lancethomps.intellij;

import java.io.File;
import java.util.Set;
import java.util.regex.Pattern;

public class ShCustomCompletionConfig {

  private Set<String> addCompletions;
  private Set<File> bashFunctionsFiles;
  private Set<String> fileNameBlackList;
  private Set<Pattern> fileNameBlackListRegex;
  private Set<String> fileNameWhiteList;
  private Set<Pattern> fileNameWhiteListRegex;
  private Set<File> pathDirs;

  public Set<String> getFileNameWhiteList() {
    return fileNameWhiteList;
  }

  public void setFileNameWhiteList(Set<String> fileNameWhiteList) {
    this.fileNameWhiteList = fileNameWhiteList;
  }

  public Set<Pattern> getFileNameWhiteListRegex() {
    return fileNameWhiteListRegex;
  }

  public void setFileNameWhiteListRegex(Set<Pattern> fileNameWhiteListRegex) {
    this.fileNameWhiteListRegex = fileNameWhiteListRegex;
  }

  public Set<String> getAddCompletions() {
    return addCompletions;
  }

  public void setAddCompletions(Set<String> addCompletions) {
    this.addCompletions = addCompletions;
  }

  public Set<File> getBashFunctionsFiles() {
    return bashFunctionsFiles;
  }

  public void setBashFunctionsFiles(Set<File> bashFunctionsFiles) {
    this.bashFunctionsFiles = bashFunctionsFiles;
  }

  public Set<String> getFileNameBlackList() {
    return fileNameBlackList;
  }

  public void setFileNameBlackList(Set<String> fileNameBlackList) {
    this.fileNameBlackList = fileNameBlackList;
  }

  public Set<Pattern> getFileNameBlackListRegex() {
    return fileNameBlackListRegex;
  }

  public void setFileNameBlackListRegex(Set<Pattern> fileNameBlackListRegex) {
    this.fileNameBlackListRegex = fileNameBlackListRegex;
  }

  public Set<File> getPathDirs() {
    return pathDirs;
  }

  public void setPathDirs(Set<File> pathDirs) {
    this.pathDirs = pathDirs;
  }

}
