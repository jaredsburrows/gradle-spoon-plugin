package com.jaredsburrows.spoon

import com.android.ddmlib.testrunner.IRemoteAndroidTestRunner.TestSize
import com.squareup.spoon.SpoonRunner
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.time.Duration

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
open class SpoonTask : DefaultTask() {
    companion object {
        private const val ANDROID_EXTENSION_NAME = "android"
        private const val SDK_DIRECTORY_METHOD = "getSdkDirectory"
    }

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
            .setTitle(extension.title)
            .setTestApk(instrumentationApk)
            .addOtherApk(applicationApk)
            .setOutputDirectory(File(extension.output))
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

        // File and add the SDK
        val android = project.extensions.findByName(ANDROID_EXTENSION_NAME)
        val sdkDirectory = android?.javaClass?.getMethod(SDK_DIRECTORY_METHOD)?.invoke(android) as File?
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

        // Only apply test size if given, no default
        if (extension.testSize.isNotEmpty()) {
            builder.setTestSize(TestSize.valueOf(extension.testSize))
        }

        // Add all skipped devices
        extension.skipDevices.forEach {
            builder.skipDevice(it)
        }

        // Add all devices
        extension.devices.forEach {
            builder.addDevice(it)
        }

        val success = if (testing) testValue else builder.build().run()
        if (!success && !extension.ignoreFailures) {
            throw GradleException("Tests failed! See ${extension.output}/index.html")
        }
    }
}
