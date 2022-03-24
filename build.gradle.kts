plugins {
  java
  id("com.github.ben-manes.versions") version "0.42.0"
  id("org.jetbrains.intellij") version "1.4.0"
}

java { sourceCompatibility = JavaVersion.VERSION_11 }

repositories {
  mavenLocal()
  mavenCentral()
  google()
  gradlePluginPortal()
}

dependencies {
  implementation("com.lancethomps:lava:1.13.2")
  testImplementation("junit:junit:4.13")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
// See https://github.com/JetBrains/gradle-intellij-plugin/blob/master/FAQ.md
intellij {
  version.set("2021.3")

  pluginName.set("intellij-custom-completions")
  downloadSources.set(true)
  sameSinceUntilBuild.set(false)
  updateSinceUntilBuild.set(false)

  plugins.set(
    listOf(
      "java",
      "pro.bashsupport:2.1.5.213",
    )
  )
}

tasks {
  buildSearchableOptions { enabled = false }
  patchPluginXml { changeNotes.set("""CHANGE NOTES""") }
  wrapper { distributionType = Wrapper.DistributionType.ALL }
}
