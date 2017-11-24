package com.jaredsburrows.spoon

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.ApkVariantOutput
import com.android.build.gradle.api.TestVariant
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
class SpoonPlugin : Plugin<Project> {
    companion object {
        private const val EXTENSION_NAME = "spoon"
        private const val APPLICATION_PLUGIN = "com.android.application"
        private const val LIBRARY_PLUGIN = "com.android.library"
    }

    override fun apply(project: Project) {
        configureAndroidProject(project)
    }

    /**
     * Configure project and all variants for Android.
     */
    private fun configureAndroidProject(project: Project) {
        // Create "spoon" extension
        val extension = project.extensions.create(EXTENSION_NAME, SpoonExtension::class.java)

        // Use "variants" from "buildTypes" to get all types for "testVariants"
        // Configure tasks for all variants
        val variants = getTestVariants(project)

        variants?.all { variant ->
            variant.outputs.all {
                // Create tasks based on variant
                val task = project.tasks.create("spoon${variant.name.capitalize()}", SpoonTask::class.java)
                task.description = "Run instrumentation tests for '${variant.name}' variant."
                task.group = "Verification"
                task.outputs.upToDateWhen { false }
                task.dependsOn(variant.testedVariant.assemble, variant.assemble)
                task.instrumentationApk = variant.outputs.first().outputFile
                task.doFirst {
                    val testedOutput = variant.testedVariant.outputs.first()
                    // This is a hack for library projects.
                    // We supply the same apk as an application and instrumentation to the soon runner.
                    task.applicationApk = if (testedOutput is ApkVariantOutput) testedOutput.outputFile else task.instrumentationApk

                    // Do this only before the Task runs to make sure that finalOutput points to the current variant.
                    if (SpoonExtension.DEFAULT_OUTPUT_DIRECTORY == extension.output) {
                        extension.finalOutput = File("${project.buildDir}/${extension.output}", variant.testedVariant.name).path
                    } else {
                        extension.finalOutput = File(extension.output, variant.testedVariant.name).path
                    }
                }
                task.extension = extension
            }
        }
    }

    private fun getTestVariants(project: Project): DomainObjectSet<TestVariant>? {
        return when {
            project.plugins.hasPlugin(APPLICATION_PLUGIN) -> project.extensions.findByType(AppExtension::class.java)?.testVariants
            project.plugins.hasPlugin(LIBRARY_PLUGIN) -> project.extensions.findByType(LibraryExtension::class.java)?.testVariants
            else -> throw IllegalStateException("Spoon plugin can only be applied to android application or library projects.")
        }
    }
}
