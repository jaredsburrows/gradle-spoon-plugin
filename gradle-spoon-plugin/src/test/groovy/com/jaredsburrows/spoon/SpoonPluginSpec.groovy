package com.jaredsburrows.spoon

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

@Ignore // integration in test-app
final class SpoonPluginSpec extends Specification {
  @Rule TemporaryFolder testProjectDir = new TemporaryFolder()
  private def MANIFEST_FILE_PATH = 'src/main/AndroidManifest.xml'
  private def MANIFEST = "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\" package=\"com.example\"/>"
  private  def APP_APK = 'project-debug.apk'
  private def TEST_APK = 'project-debug-androidTest.apk'
  private Project project
  private File appApk
  private File testApk

  def 'setup'() {
    // Setup project
    project = ProjectBuilder.builder()
      .withProjectDir(testProjectDir.root)
      .withName('project')
      .build()

    // Make sure Android projects have a manifest
    testProjectDir.newFolder('src', 'main')
    testProjectDir.newFile(MANIFEST_FILE_PATH) << MANIFEST
    testProjectDir.newFolder('build', 'outputs', 'apk', 'debug')
    appApk = testProjectDir.newFile('build/outputs/apk/debug/' + APP_APK)
    testApk = testProjectDir.newFile('build/outputs/apk/debug/' + TEST_APK)
  }

  @Unroll "android project running #taskName with full spoon extension and buildTypes"() {
    given:
    project.apply plugin: "com.android.application"
    new SpoonPlugin().apply(project)
    project.android {
      compileSdkVersion 28

      defaultConfig {
        applicationId 'com.example'
      }

      buildTypes {
        debug {}
        release {}
      }
    }
    project.spoon {
      // Supported directly by Spoon's SpoonRunner
      title = "My tests"
      baseOutputDir = "spoonTests"
      debug = true
      noAnimations = true
      adbTimeout = 5
      devices = ["emulator-5554", "emulator-5556"]
      skipDevices = ["emulator-5555"]
      instrumentationArgs = ["listener:com.foo.Listener,com.foo.Listener2",
                             "classLoader:com.foo.CustomClassLoader"]
      className = "com.android.foo.FooClassName"
      testSize = "large"
      allowNoDevices = true
      sequential = true
      grantAll = true
      methodName = "testMethodName"
      codeCoverage = true
      shard = true
      singleInstrumentationCall = true

      // Other
      ignoreFailures = true

      // Passed in via -e, extra arguments
      numShards = 10
      shardIndex = 2
    }

    when:
    project.evaluate()

    SpoonTask task = project.tasks.getByName(taskName) as SpoonTask
    task.applicationApk = appApk
    task.instrumentationApk = testApk

    then:
    // Supported directly by Spoon's SpoonRunner
    task.extension.title == "My tests"
    task.extension.baseOutputDir == "spoonTests"
    task.extension.debug
    task.extension.noAnimations
    task.extension.adbTimeout == 5000
    task.extension.devices as List<String> == ["emulator-5554", "emulator-5556"] as List<String>
    task.extension.skipDevices as List<String> == ["emulator-5555"] as List<String>
    task.extension.instrumentationArgs as List<String> == ["listener:com.foo.Listener,com.foo.Listener2",
                                                           "classLoader:com.foo.CustomClassLoader"] as List<String>
    task.extension.className == "com.android.foo.FooClassName"
    task.extension.testSize == "large"
    task.extension.allowNoDevices
    task.extension.sequential
    task.extension.grantAll
    task.extension.methodName == "testMethodName"
    task.extension.codeCoverage
    task.extension.shard
    task.extension.singleInstrumentationCall

    // Other
    task.extension.ignoreFailures

    // Passed in via -e, extra arguments
    task.extension.numShards == 10
    task.extension.shardIndex == 2

    where:
    taskName << ["spoonDebugAndroidTest"]
  }

  @Unroll "android project running #taskName with full spoon extension and productFlavors"() {
    given:
    project.apply plugin: "com.android.application"
    new SpoonPlugin().apply(project)
    project.android {
      compileSdkVersion 28

      defaultConfig {
        applicationId 'com.example'
      }

      buildTypes {
        debug {}
        release {}
      }

      flavorDimensions "a", "b"

      productFlavors {
        flavor1 { dimension "a" }
        flavor2 { dimension "a" }
        flavor3 { dimension "b" }
        flavor4 { dimension "b" }
      }
    }
    project.spoon {
      // Supported directly by Spoon's SpoonRunner
      title = "My tests"
      baseOutputDir = "spoonTests"
      debug = true
      noAnimations = true
      adbTimeout = 5
      devices = ["emulator-5554", "emulator-5556"]
      skipDevices = ["emulator-5555"]
      instrumentationArgs = ["listener:com.foo.Listener,com.foo.Listener2",
                             "classLoader:com.foo.CustomClassLoader"]
      className = "com.android.foo.FooClassName"
      testSize = "large"
      allowNoDevices = true
      sequential = true
      grantAll = true
      methodName = "testMethodName"
      codeCoverage = true
      shard = true
      singleInstrumentationCall = true

      // Other
      ignoreFailures = true

      // Passed in via -e, extra arguments
      numShards = 10
      shardIndex = 2
    }

    when:
    project.evaluate()

    SpoonTask task = project.tasks.getByName(taskName) as SpoonTask
    task.applicationApk = appApk
    task.instrumentationApk = testApk

    then:
    // Supported directly by Spoon's SpoonRunner
    task.extension.title == "My tests"
    task.extension.baseOutputDir == "spoonTests"
    task.extension.debug
    task.extension.noAnimations
    task.extension.adbTimeout == 5000
    task.extension.devices as List<String> == ["emulator-5554", "emulator-5556"] as List<String>
    task.extension.skipDevices as List<String> == ["emulator-5555"] as List<String>
    task.extension.instrumentationArgs as List<String> == ["listener:com.foo.Listener,com.foo.Listener2",
                                                           "classLoader:com.foo.CustomClassLoader"] as List<String>
    task.extension.className == "com.android.foo.FooClassName"
    task.extension.testSize == "large"
    task.extension.allowNoDevices
    task.extension.sequential
    task.extension.grantAll
    task.extension.methodName == "testMethodName"
    task.extension.codeCoverage
    task.extension.shard
    task.extension.singleInstrumentationCall

    // Other
    task.extension.ignoreFailures

    // Passed in via -e, extra arguments
    task.extension.numShards == 10
    task.extension.shardIndex == 2

    where:
    taskName << ["spoonFlavor1Flavor3DebugAndroidTest", "spoonFlavor2Flavor4DebugAndroidTest"]
  }
}
