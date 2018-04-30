package com.jaredsburrows.spoon

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class BaseSpec extends Specification {
  @Rule TemporaryFolder testProjectDir = new TemporaryFolder()
  def COMPILE_SDK_VERSION = 27
  def BUILD_TOOLS_VERSION = "27.0.3"
  def APPLICATION_ID = "com.example"
  def MANIFEST_FILE_PATH = "src/main/AndroidManifest.xml"
  def MANIFEST = "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\" package=\"$APPLICATION_ID\"/>"
  def APP_APK = "project-debug.apk"
  def TEST_APK = "project-debug-androidTest.apk"

  // Project
  Project project
  File appApk
  File testApk

  def "setup"() {
    // Setup project
    project = ProjectBuilder.builder()
      .withProjectDir(testProjectDir.root)
      .withName("project")
      .build()

    // Make sure Android projects have a manifest
    testProjectDir.newFolder("src", "main")
    testProjectDir.newFile(MANIFEST_FILE_PATH) << MANIFEST
    testProjectDir.newFolder("build", "outputs", "apk", "debug")
    appApk = testProjectDir.newFile("build/outputs/apk/debug/" + APP_APK)
    testApk = testProjectDir.newFile("build/outputs/apk/debug/" + TEST_APK)
  }
}
