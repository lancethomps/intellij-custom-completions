package com.lancethomps.intellij;

import java.io.File;
import java.util.Set;
import java.util.regex.Pattern;

public class ShCustomCompletionConfig {

  private Set<String> addCompletions;
  private Set<String> addEnvVars;
  private Set<String> addFunctions;
  private Set<File> bashFunctionsFiles;
  private Long cacheExpireAfterSeconds;
  private Set<String> fileNameBlackList;
  private Set<Pattern> fileNameBlackListRegex;
  private Set<String> fileNameWhiteList;
  private Set<Pattern> fileNameWhiteListRegex;
  private String logLevel;
  private Set<File> pathDirs;
  private Double priority;

  public Set<String> getAddCompletions() {
    return addCompletions;
  }

  public void setAddCompletions(Set<String> addCompletions) {
    this.addCompletions = addCompletions;
  }

  public Set<String> getAddEnvVars() {
    return addEnvVars;
  }

  public void setAddEnvVars(Set<String> addEnvVars) {
    this.addEnvVars = addEnvVars;
  }

  public Set<String> getAddFunctions() {
    return addFunctions;
  }

  public void setAddFunctions(Set<String> addFunctions) {
    this.addFunctions = addFunctions;
  }

  public Set<File> getBashFunctionsFiles() {
    return bashFunctionsFiles;
  }

  public void setBashFunctionsFiles(Set<File> bashFunctionsFiles) {
    this.bashFunctionsFiles = bashFunctionsFiles;
  }

  public Long getCacheExpireAfterSeconds() {
    return cacheExpireAfterSeconds;
  }

  public void setCacheExpireAfterSeconds(Long cacheExpireAfterSeconds) {
    this.cacheExpireAfterSeconds = cacheExpireAfterSeconds;
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

  public String getLogLevel() {
    return logLevel;
  }

  public void setLogLevel(String logLevel) {
    this.logLevel = logLevel;
  }

  public Set<File> getPathDirs() {
    return pathDirs;
  }

  public void setPathDirs(Set<File> pathDirs) {
    this.pathDirs = pathDirs;
  }

  public Double getPriority() {
    return priority;
  }

  public void setPriority(Double priority) {
    this.priority = priority;
  }

}
