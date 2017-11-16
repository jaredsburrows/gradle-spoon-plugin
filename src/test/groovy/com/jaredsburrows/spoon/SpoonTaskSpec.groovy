package com.jaredsburrows.spoon

import org.gradle.api.GradleException
import spock.lang.Unroll

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
final class SpoonTaskSpec extends BaseSpec {
  @Unroll "android - #taskName - no spoon extension - run task"() {
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

    when:
    project.evaluate()

    SpoonTask task = project.tasks.getByName(taskName)
    task.testing = true
    task.applicationApk = appApk
    task.instrumentationApk = testApk
    task.execute()

    then:
    // Supported directly by Spoon's SpoonRunner
    task.extension.title == "Spoon Execution"
    task.extension.output == "spoon-output/debug"
    !task.extension.debug
    !task.extension.noAnimations
    task.extension.adbTimeout == 600000
    task.extension.devices.empty
    task.extension.skipDevices.empty
    task.extension.instrumentationArgs.empty
    task.extension.className.empty
    !task.extension.sequential
    !task.extension.grantAll
    task.extension.methodName.empty
    !task.extension.codeCoverage
    !task.extension.failIfNoDeviceConnected
    !task.extension.ignoreFailures
    !task.extension.singleInstrumentationCall

    // Passed in via -e, extra arguments
    !task.extension.shard
    task.extension.numShards == 0
    task.extension.shardIndex == 0

    where:
    taskName << ["spoonDebugAndroidTest"]
  }

  @Unroll "android - #taskName - full spoon extension - run task"() {
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
      singleInstrumentationCall = true

      // Passed in via -e, extra arguments
      shard = true
      numShards = 1
      shardIndex = 1
      ignoreFailures = true
    }

    when:
    project.evaluate()

    SpoonTask task = project.tasks.getByName(taskName)
    task.testing = true
    task.applicationApk = appApk
    task.instrumentationApk = testApk
    task.execute()

    then:
    // Supported directly by Spoon's SpoonRunner
    task.extension.title == "Spoon Execution"
    task.extension.output == "spoonTests/debug"
    task.extension.debug
    task.extension.noAnimations
    task.extension.adbTimeout == 5000
    task.extension.devices as List<String> == ["emulator-5554", "emulator-5556"] as List<String>
    task.extension.skipDevices as List<String> == ["emulator-5555"] as List<String>
    task.extension.instrumentationArgs as List<String> == ["listener com.foo.Listener,com.foo.Listener2", "classLoader com.foo.CustomClassLoader", "numShards=1", "shardIndex=1"] as List<String>
    task.extension.className == "com.android.foo.FooClassName"
    task.extension.sequential
    task.extension.grantAll
    task.extension.methodName == "testMethodName"
    task.extension.codeCoverage
    task.extension.failIfNoDeviceConnected
    task.extension.ignoreFailures
    task.extension.singleInstrumentationCall

    // Passed in via -e, extra arguments
    task.extension.shard
    task.extension.numShards == 1
    task.extension.shardIndex == 1

    where:
    taskName << ["spoonDebugAndroidTest"]
  }

  @Unroll "android - #taskName - methodname with not classname"() {
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
      methodName = "testMethodName"
    }

    when:
    project.evaluate()

    SpoonTask task = project.tasks.getByName(taskName)
    task.applicationApk = appApk
    task.instrumentationApk = testApk
    task.execute()

    then:
    def e = thrown(GradleException)
    e.cause instanceof IllegalStateException
    e.cause.message == "'testMethodName' must have a fully qualified class name."

    where:
    taskName << ["spoonDebugAndroidTest"]
  }

  @Unroll "android - #taskName - exception if test failure"() {
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
      ignoreFailures = false
    }

    when:
    project.evaluate()

    SpoonTask task = project.tasks.getByName(taskName)
    task.testing = true
    task.testValue = false
    task.applicationApk = appApk
    task.instrumentationApk = testApk
    task.execute()

    then:
    def e = thrown(GradleException)
    e.cause instanceof GradleException
    e.cause.message.contains("Tests failed! See")
    e.cause.message.contains("${project.spoon.output}/index.html")

    where:
    taskName << ["spoonDebugAndroidTest"]
  }
}
