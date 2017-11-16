package com.jaredsburrows.spoon

import com.squareup.spoon.SpoonRunner
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.time.Duration

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
open class SpoonTask : DefaultTask() {
    /** Use our Spoon extension. */
    lateinit var extension: SpoonExtension

    /** Application APK (eg. app-debug.apk). */
    lateinit var applicationApk: File

    /** Instrumentation APK (eg. app-debug-androidTest.apk). */
    lateinit var instrumentationApk: File

    /** TESTING ONLY */
    var testing: Boolean = false
    var testValue: Boolean = true

    @Suppress("unused")
    @TaskAction
    fun spoonTask() {
        if (extension.className.isEmpty() && extension.methodName.isNotEmpty()) {
            throw IllegalStateException("'${extension.methodName}' must have a fully qualified class name.")
        }

        val builder = SpoonRunner.Builder()
            // Not in extension
            .setTerminateAdb(false) // default is true
            .addOtherApk(applicationApk)
            .setTestApk(instrumentationApk)
            // In SpoonExtension
            .setTitle(extension.title)
            .setOutputDirectory(File(extension.output))
            .setDebug(extension.debug)
            .setNoAnimations(extension.noAnimations)
            .setAdbTimeout(Duration.ofSeconds(extension.adbTimeout.toLong()))
            .setClassName(extension.className)
            .setSequential(extension.sequential)
            .setGrantAll(extension.grantAll)
            .setMethodName(extension.methodName)
            .setCodeCoverage(extension.codeCoverage)
            .setAllowNoDevices(!extension.failIfNoDeviceConnected)
            .setShard(extension.shard)
            .setSingleInstrumentationCall(extension.singleInstrumentationCall)

        // File and add the SDK
        val android = project.extensions.findByName("android")
        val sdkDirectory = android?.javaClass?.getMethod("getSdkDirectory")?.invoke(android) as File?
        if (sdkDirectory != null) {
            builder.setAndroidSdk(sdkDirectory)
        }

        // Add shard information to instrumentation args if there are any
        if (extension.numShards > 0) {
            extension.instrumentationArgs.add("numShards=${extension.numShards}")
            extension.instrumentationArgs.add("shardIndex=${extension.shardIndex}")
        }

        // If we have args apply them else let them be null
        if (extension.instrumentationArgs.isNotEmpty()) {
            builder.setInstrumentationArgs(extension.instrumentationArgs)
        }

        // Add all skipped devices
        extension.skipDevices.forEach {
            builder.skipDevice(it)
        }

        // Add all devices
        extension.devices.forEach {
            builder.addDevice(it)
        }

        logger.log(LogLevel.INFO, "Run instrumentation tests $instrumentationApk for app $applicationApk")
        logger.log(LogLevel.INFO, "Output: $extension.output")
        logger.log(LogLevel.INFO, "Ignore failures: $extension.ignoreFailures")
        logger.log(LogLevel.INFO, "Fail if no device connected: $extension.failIfNoDeviceConnected")
        logger.log(LogLevel.INFO, "Debug mode: $extension.debug")
        if (extension.className.isNotEmpty()) {
            logger.log(LogLevel.INFO, "Class name: $extension.className")
            if (extension.methodName.isNotEmpty()) {
                logger.log(LogLevel.INFO, "Method name: $extension.methodName")
            }
        }
        logger.log(LogLevel.INFO, "No animations: $extension.noAnimations")
        logger.log(LogLevel.INFO, "numShards: $extension.numShards")
        logger.log(LogLevel.INFO, "shardIndex: $extension.shardIndex")

        val runner = builder.build()
        val success = if (testing) testValue else runner.run()

        if (!success && !extension.ignoreFailures) {
            throw GradleException("Tests failed! See ${extension.output}/index.html")
        }
    }
}
