package com.jaredsburrows.spoon

import com.android.ddmlib.testrunner.IRemoteAndroidTestRunner.TestSize
import com.squareup.spoon.SpoonRunner
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.time.Duration

/** A [Task] that creates and runs the Spoon test runner. */
open class SpoonTask : DefaultTask() { // tasks can't be final

  /** Results baseOutputDir. */
  @get:OutputDirectory lateinit var outputDir: File

  @Internal lateinit var buildDir: File

  /** For testing only. */
  @get:Internal internal var testing: Boolean = false

  /** Spoon Extension. */
  @get:Internal internal lateinit var spoonExtension: SpoonExtension

  /** Android SDK directory */
  @get:Internal internal lateinit var sdkDirectory: File

  /** Instrumentation APK (eg. app-debug-androidTest.apk). */
  @get:Internal internal lateinit var instrumentationApk: File

  /** Application APK (eg. app-debug.apk). */
  @get:Internal internal lateinit var applicationApk: File

  /** The variant ran by Spoon. */
  @get:Internal internal lateinit var variantName: String

  init {
    description = "Run instrumentation tests for '$name' variant."
    group = "Verification"
  }

  @TaskAction
  fun spoonTask() {
    if (spoonExtension.className.isEmpty() && spoonExtension.methodName.isNotEmpty()) {
      throw IllegalStateException(
        "'${spoonExtension.methodName}' must have a fully qualified class name.",
      )
    }

    var outputBase = spoonExtension.baseOutputDir
    if (SpoonExtension.DEFAULT_OUTPUT_DIRECTORY == outputBase) {
      outputBase = File(buildDir, SpoonExtension.DEFAULT_OUTPUT_DIRECTORY).path
    }
    outputDir = File(outputBase, variantName)

    val builder = SpoonRunner.Builder()
      .setTitle(spoonExtension.title)
      .setOutputDirectory(outputDir)
      .setDebug(spoonExtension.debug)
      .setNoAnimations(spoonExtension.noAnimations)
      .setAdbTimeout(Duration.ofSeconds(spoonExtension.adbTimeout.toLong()))
      .setClassName(spoonExtension.className)
      .setAllowNoDevices(spoonExtension.allowNoDevices)
      .setSequential(spoonExtension.sequential)
      .setGrantAll(spoonExtension.grantAll)
      .setMethodName(spoonExtension.methodName)
      .setCodeCoverage(spoonExtension.codeCoverage)
      .setShard(spoonExtension.shard)
      .setTerminateAdb(false)
      .setSingleInstrumentationCall(spoonExtension.singleInstrumentationCall)
      .setClearAppDataBeforeEachTest(spoonExtension.clearAppDataBeforeEachTest)

    // APKs
    if (testing) {
      builder.setTestApk(instrumentationApk)
      builder.addOtherApk(applicationApk)
    }

    // File and add the SDK
    builder.setAndroidSdk(sdkDirectory)

    // Add shard information to instrumentation args if there are any
    if (spoonExtension.numShards > 0) {
      if (spoonExtension.shardIndex >= spoonExtension.numShards) {
        throw UnsupportedOperationException("'shardIndex' needs to be less than 'numShards'.")
      }

      spoonExtension.instrumentationArgs.add("numShards:${spoonExtension.numShards}")
      spoonExtension.instrumentationArgs.add("shardIndex:${spoonExtension.shardIndex}")
    }

    // If we have args apply them else let them be null
    if (spoonExtension.instrumentationArgs.isNotEmpty()) {
      val instrumentationArgs = hashMapOf<String, String>()
      spoonExtension.instrumentationArgs.forEach { instrumentation ->
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
    if (spoonExtension.testSize.isNotEmpty()) {
      builder.setTestSize(TestSize.getTestSize(spoonExtension.testSize))
    }

    // Add all skipped devices
    spoonExtension.skipDevices.forEach {
      builder.skipDevice(it)
    }

    // Add all devices
    spoonExtension.devices.forEach {
      builder.addDevice(it)
    }

    val success = if (testing) builder.build().run() else true
    if (!success && !spoonExtension.ignoreFailures) {
      throw GradleException(
        "Tests failed! See ${ConsoleRenderer.asClickableFileUrl(File(outputDir, "index.html"))}",
      )
    }
  }
}
