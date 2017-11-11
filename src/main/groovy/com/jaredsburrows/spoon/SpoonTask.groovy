package com.jaredsburrows.spoon

import com.android.build.gradle.api.TestVariant
import com.squareup.spoon.SpoonRunner
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import java.time.Duration

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
class SpoonTask extends DefaultTask {
  /** Title of the generated HTML website. */
  @Input String title

  /** Variant of test being run. */
  @Input TestVariant testVariant

  /** Use our Spoon extension. */
  @Input SpoonExtension extension

  /** Application APK (eg. app-debug.apk). */
  @Input File applicationApk

  /** Instrumentation APK (eg. app-debug-androidTest.apk). */
  @Input File instrumentationApk

  /** TEST ONLY */
  @Internal boolean isTesting
  @Internal SpoonRunner spoonRunner

  @SuppressWarnings("GroovyUnusedDeclaration") @TaskAction spoonTask() {
    if (!extension.className && extension.methodName) {
      throw new IllegalStateException("$extension.methodName must have a fully qualified class name.")
    }

    if (extension.numShards > 0) {
      extension.instrumentationArgs.add("numShards=${extension.numShards}".toString())
      extension.instrumentationArgs.add("shardIndex=${extension.shardIndex}".toString())
    }

    final SpoonRunner.Builder builder = new SpoonRunner.Builder()
      // Not in extension
      .setTerminateAdb(false) // default is true
      .setTitle(title)
      .addOtherApk(applicationApk)
      .setTestApk(instrumentationApk)
      .setAndroidSdk(project.android.sdkDirectory)
      // In SpoonExtension
      .setOutputDirectory(new File(extension.output))
      .setDebug(extension.debug)
      .setNoAnimations(extension.noAnimations)
      .setAdbTimeout(Duration.ofSeconds(extension.adbTimeout))
      .setClassName(extension.className)
      .setSequential(extension.sequential)
      .setGrantAll(extension.grantAll)
      .setMethodName(extension.methodName)
      .setCodeCoverage(extension.codeCoverage)
      .setAllowNoDevices(!extension.failIfNoDeviceConnected)
      .setShard(extension.shard)

    if (!extension.instrumentationArgs.empty) {
      builder.setInstrumentationArgs(extension.instrumentationArgs)
    }

    extension.skipDevices.each {
      builder.skipDevice(it)
    }

    if (!extension.devices.empty) {
      extension.devices.each {
        builder.addDevice(it)
      }
    }

    logger.log(LogLevel.INFO,"Run instrumentation tests $instrumentationApk for app $applicationApk")
//    logger.log(LogLevel.INFO, "Title: $extension.title")
    logger.log(LogLevel.INFO, "Output: $extension.output")
//    logger.log(LogLevel.INFO, "Ignore failures: $extension.ignoreFailures")
    logger.log(LogLevel.INFO, "Fail if no device connected: $extension.failIfNoDeviceConnected")
    logger.log(LogLevel.INFO, "Debug mode: $extension.debug")
    if (extension.className) {
      logger.log(LogLevel.INFO, "Class name: $extension.className")
      if (extension.methodName) {
        logger.log(LogLevel.INFO, "Method name: $extension.methodName")
      }
    }
    logger.log(LogLevel.INFO, "No animations: $extension.noAnimations")
//    logger.log(LogLevel.INFO, "Test size: $extension.testSize")
    logger.log(LogLevel.INFO, "numShards: $extension.numShards")
    logger.log(LogLevel.INFO, "shardIndex: $extension.shardIndex")


    spoonRunner = builder.build()
    boolean success = isTesting ? true : spoonRunner.run()

    if (!success) {
      throw new GradleException("Tests failed! See ${extension.output}/index.html")
    }
  }
}
