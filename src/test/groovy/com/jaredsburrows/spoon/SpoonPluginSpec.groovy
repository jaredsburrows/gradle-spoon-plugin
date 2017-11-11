package com.jaredsburrows.spoon

import com.android.build.gradle.internal.SdkHandler
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
final class SpoonPluginSpec extends Specification {
  @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
  static def COMPILE_SDK_VERSION = 26
  static def BUILD_TOOLS_VERSION = "26.0.2"
  static def APPLICATION_ID = "com.example"
  static def TEST_ANDROID_SDK = this.getResource("/android-sdk").toURI() // Test fixture that emulates a local android sdk
  static def MANIFEST_FILE_PATH = "src/main/AndroidManifest.xml"
  static def MANIFEST = "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\" package=\"$APPLICATION_ID\"/>"
  static def APP_APK = "project-debug.apk"
  static def TEST_APK = "project-debug-androidTest.apk"

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

    // Set mock test sdk, we only need to test the plugins tasks
    SdkHandler.sTestSdkFolder = project.file(TEST_ANDROID_SDK)
  }

  def "unsupported project project"() {
    when:
    new SpoonPlugin().apply(project) // project.apply plugin: "com.jaredsburrows.spoon"

    then:
    def e = thrown(IllegalStateException)
    e.message == "Spoon report plugin can only be applied to android application or library projects."
  }

  @Unroll "android - #projectPlugin project"() {
    given:
    project.apply plugin: projectPlugin

    when:
    new SpoonPlugin().apply(project) // project.apply plugin: "com.jaredsburrows.spoon"
    project.spoon {
    }

    then:
    noExceptionThrown()

    where:
    projectPlugin << SpoonPlugin.ANDROID_PLUGINS
  }

  @Unroll "android - #taskName - spoon extension"() {
    given:
    project.apply plugin: "com.android.application"
    new SpoonPlugin().apply(project) // project.apply plugin: "com.jaredsburrows.spoon"
    project.android {
      compileSdkVersion COMPILE_SDK_VERSION
      buildToolsVersion BUILD_TOOLS_VERSION

      defaultConfig {
        applicationId APPLICATION_ID
      }

      buildTypes {
        debug {}
        release {}
      }
    }
    project.spoon {
      // Supported directly by Spoon's SpoonRunner
      output = "spoonTests"
      debug = true
      noAnimations = true
      adbTimeout = 5
      devices = ["emulator-5554", "emulator-5556"]
      skipDevices = ["emulator-5555"]
      instrumentationArgs = ["listener com.foo.Listener,com.foo.Listener2", "classLoader com.foo.CustomClassLoader"]
      className = "com.android.foo.FooClassName"
      sequential = true
      grantAll = true
      methodName = "testMethodName"
      codeCoverage = true
      failIfNoDeviceConnected = true

      // Passed in via -e, extra arguments
      shard = true
      numShards = 1
      shardIndex = 1
    }

    when:
    project.evaluate()

    SpoonTask task = project.tasks.getByName(taskName)
    task.applicationApk = appApk
    task.instrumentationApk = testApk

    then:
    // Supported directly by Spoon's SpoonRunner
    task.extension.output.contains("spoonTests/debug")
    task.extension.debug
    task.extension.noAnimations
    task.extension.adbTimeout == 5000
    task.extension.devices as List<String> == ["emulator-5554", "emulator-5556"] as List<String>
    task.extension.skipDevices as List<String> == ["emulator-5555"] as List<String>
    task.extension.instrumentationArgs as List<String> == ["listener com.foo.Listener,com.foo.Listener2", "classLoader com.foo.CustomClassLoader"] as List<String>
    task.extension.className == "com.android.foo.FooClassName"
    task.extension.sequential
    task.extension.grantAll
    task.extension.methodName == "testMethodName"
    task.extension.codeCoverage
    task.extension.failIfNoDeviceConnected

    // Passed in via -e, extra arguments
    task.extension.shard
    task.extension.numShards == 1
    task.extension.shardIndex == 1

    where:
    taskName << ["spoonDebugAndroidTest"]
  }
}
