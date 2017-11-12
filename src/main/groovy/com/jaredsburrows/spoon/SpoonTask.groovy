package com.jaredsburrows.spoon

import com.squareup.spoon.SpoonRunner
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.TaskAction

import java.time.Duration

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
class SpoonTask extends DefaultTask {
  /** Title of the generated HTML website. */
  String title

  /** Use our Spoon extension. */
  SpoonExtension extension

  /** Application APK (eg. app-debug.apk). */
  File applicationApk

  /** Instrumentation APK (eg. app-debug-androidTest.apk). */
  File instrumentationApk

  /** TESTING ONLY */
  boolean testing

  @SuppressWarnings("GroovyUnusedDeclaration") @TaskAction spoonTask() {
    if (!extension.className && extension.methodName) {
      throw new IllegalStateException("$extension.methodName must have a fully qualified class name.")
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

    // Add shard information to instrumentation args if there are any
    if (extension.numShards > 0) {
      extension.instrumentationArgs.add("numShards=${extension.numShards}".toString())
      extension.instrumentationArgs.add("shardIndex=${extension.shardIndex}".toString())
    }

    // If we have args apply them else let them be null
    if (!extension.instrumentationArgs.empty) {
      builder.setInstrumentationArgs(extension.instrumentationArgs)
    }

    // Add all skipped devices
    extension.skipDevices.each {
      builder.skipDevice(it)
    }

    // Add all devices
    extension.devices.each {
      builder.addDevice(it)
    }

    logger.log(LogLevel.INFO,"Run instrumentation tests $instrumentationApk for app $applicationApk")
    logger.log(LogLevel.INFO, "Output: $extension.output")
    logger.log(LogLevel.INFO, "Ignore failures: $extension.ignoreFailures")
    logger.log(LogLevel.INFO, "Fail if no device connected: $extension.failIfNoDeviceConnected")
    logger.log(LogLevel.INFO, "Debug mode: $extension.debug")
    if (extension.className) {
      logger.log(LogLevel.INFO, "Class name: $extension.className")
      if (extension.methodName) {
        logger.log(LogLevel.INFO, "Method name: $extension.methodName")
      }
    }
    logger.log(LogLevel.INFO, "No animations: $extension.noAnimations")
    logger.log(LogLevel.INFO, "numShards: $extension.numShards")
    logger.log(LogLevel.INFO, "shardIndex: $extension.shardIndex")

    final SpoonRunner runner = builder.build()
    final boolean success = testing ? true : runner.run()

    if (!success) {
      throw new GradleException("Tests failed! See ${extension.output}/index.html")
    }
  }
}
