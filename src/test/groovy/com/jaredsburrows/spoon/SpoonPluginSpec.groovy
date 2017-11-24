package com.jaredsburrows.spoon

import spock.lang.Unroll

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
final class SpoonPluginSpec extends BaseSpec {
  def "unsupported project project"() {
    when:
    new SpoonPlugin().apply(project) // project.apply plugin: "com.jaredsburrows.spoon"

    then:
    def e = thrown(IllegalStateException)
    e.message == "Spoon plugin can only be applied to android application or library projects."
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
    projectPlugin << ["com.android.application", "com.android.library"]
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
      title = "My tests"
      output = "spoonTests"
      debug = true
      noAnimations = true
      adbTimeout = 5
      devices = ["emulator-5554", "emulator-5556"]
      skipDevices = ["emulator-5555"]
      instrumentationArgs = ["listener com.foo.Listener,com.foo.Listener2", "classLoader com.foo.CustomClassLoader"]
      className = "com.android.foo.FooClassName"
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
      numShards = 1
      shardIndex = 1
    }

    when:
    project.evaluate()

    def task = project.tasks.getByName(taskName) as SpoonTask
    task.applicationApk = appApk
    task.instrumentationApk = testApk

    then:
    // Supported directly by Spoon's SpoonRunner
    task.extension.title == "My tests"
    task.extension.output == "spoonTests"
    task.extension.debug
    task.extension.noAnimations
    task.extension.adbTimeout == 5000
    task.extension.devices as List<String> == ["emulator-5554", "emulator-5556"] as List<String>
    task.extension.skipDevices as List<String> == ["emulator-5555"] as List<String>
    task.extension.instrumentationArgs as List<String> == ["listener com.foo.Listener,com.foo.Listener2", "classLoader com.foo.CustomClassLoader"] as List<String>
    task.extension.className == "com.android.foo.FooClassName"
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
    task.extension.numShards == 1
    task.extension.shardIndex == 1

    where:
    taskName << ["spoonDebugAndroidTest"]
  }
}
