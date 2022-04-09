package com.jaredsburrows.spoon

import com.android.ddmlib.testrunner.IRemoteAndroidTestRunner.TestSize
import com.squareup.spoon.SpoonRunner
import java.io.File
import java.time.Duration
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/** A [Task] that creates and runs the Spoon test runner. */
open class SpoonTask : DefaultTask() { // tasks can't be final

  /** Use our Spoon extension. */
  @Internal lateinit var extension: SpoonExtension

  /** Application APK (eg. app-debug.apk). */
  @Internal lateinit var applicationApk: File

  /** Instrumentation APK (eg. app-debug-androidTest.apk). */
  @Internal lateinit var instrumentationApk: File

  /** Results baseOutputDir. */
  @Internal lateinit var outputDir: File

  /** TESTING ONLY */
  @Internal var testing: Boolean = false
  @Internal var testValue: Boolean = true
  @Internal var spoonRenderer: SpoonRunner.Builder? = null

  @Suppress("unused")
  @TaskAction
  fun spoonTask() {
    if (extension.className.isEmpty() && extension.methodName.isNotEmpty()) {
      throw IllegalStateException("'${extension.methodName}' must have a fully qualified class " +
        "name.")
    }

    val builder = SpoonRunner.Builder()
      .setTitle(extension.title)
      .setOutputDirectory(outputDir)
      .setDebug(extension.debug)
      .setNoAnimations(extension.noAnimations)
      .setAdbTimeout(Duration.ofSeconds(extension.adbTimeout.toLong()))
      .setClassName(extension.className)
      .setAllowNoDevices(extension.allowNoDevices)
      .setSequential(extension.sequential)
      .setGrantAll(extension.grantAll)
      .setMethodName(extension.methodName)
      .setCodeCoverage(extension.codeCoverage)
      .setShard(extension.shard)
      .setTerminateAdb(false)
      .setSingleInstrumentationCall(extension.singleInstrumentationCall)
      .setClearAppDataBeforeEachTest(extension.clearAppDataBeforeEachTest)

    // APKs
    if (!testing) {
      builder.setTestApk(instrumentationApk)
      builder.addOtherApk(applicationApk)
    }

    // File and add the SDK
    val android = project.extensions.findByName(ANDROID_EXTENSION_NAME)
    val sdkFolder = android?.javaClass?.getMethod(SDK_DIRECTORY_METHOD)?.invoke(android) as File?
    sdkFolder?.let {
      builder.setAndroidSdk(sdkFolder)
    }

    // Add shard information to instrumentation args if there are any
    if (extension.numShards > 0) {
      if (extension.shardIndex >= extension.numShards) {
        throw UnsupportedOperationException("'shardIndex' needs to be less than 'numShards'.")
      }

      extension.instrumentationArgs.add("numShards:${extension.numShards}")
      extension.instrumentationArgs.add("shardIndex:${extension.shardIndex}")
    }

    // If we have args apply them else let them be null
    if (extension.instrumentationArgs.isNotEmpty()) {
      val instrumentationArgs = hashMapOf<String, String>()
      extension.instrumentationArgs.forEach { instrumentation ->
        if (!(instrumentation.contains(':') or instrumentation.contains('='))) {
          throw UnsupportedOperationException("Please use '=' or ':' to separate arguments.")
        }

        val keyVal = if (instrumentation.contains(':')) {
          instrumentation.split(':')
        } else {
          instrumentation.split('=')
        }
        instrumentationArgs[keyVal[0]] = keyVal[1]
      }
      builder.setInstrumentationArgs(instrumentationArgs)
    }

    // Only apply test size if given, no default
    if (extension.testSize.isNotEmpty()) {
      builder.setTestSize(TestSize.getTestSize(extension.testSize))
    }

    // Add all skipped devices
    extension.skipDevices.forEach {
      builder.skipDevice(it)
    }

    // Add all devices
    extension.devices.forEach {
      builder.addDevice(it)
    }

    spoonRenderer = builder

    val success = if (testing) testValue else builder.build().run()
    if (!success && !extension.ignoreFailures) {
      throw GradleException("Tests failed! " +
        "See ${ConsoleRenderer.asClickableFileUrl(File(outputDir, "index.html"))}")
    }
  }

  companion object {
    private const val ANDROID_EXTENSION_NAME = "android"
    private const val SDK_DIRECTORY_METHOD = "getSdkDirectory"
  }
}
