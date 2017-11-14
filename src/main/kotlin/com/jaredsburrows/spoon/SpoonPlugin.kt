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
    override fun apply(project: Project) {
        configureAndroidProject(project)
    }

    /**
     * Configure project and all variants for Android.
     */
    private fun configureAndroidProject(project: Project) {
        // Create "spoon" extension
        val extension = project.extensions.create("spoon", SpoonExtension::class.java)

        // Use "variants" from "buildTypes" to get all types for "testVariants"
        // Configure tasks for all variants
        val variants: DomainObjectSet<TestVariant>? = when {
            project.plugins.hasPlugin("com.android.application") -> project.extensions.findByType(AppExtension::class.java)?.testVariants
            project.plugins.hasPlugin("com.android.library") -> project.extensions.findByType(LibraryExtension::class.java)?.testVariants
            else -> throw IllegalStateException("Spoon plugin can only be applied to android application or library projects.")
        }

        variants?.all { variant ->
            variant.outputs.all {
                val variantName = variant.name.capitalize()
                val taskName = "spoon$variantName"
                val instrumentationPackage = variant.outputs.first().outputFile

                // Create tasks based on variant
                val task = project.tasks.create(taskName, SpoonTask::class.java)
                // task properties
                task.description = "Run instrumentation tests for '${variant.name}' variant."
                task.group = "Verification"
                task.outputs.upToDateWhen { false }
                task.dependsOn(variant.testedVariant.assemble, variant.assemble)

                // extra task properties
                task.title = "$project.name $variant.name"

                // extension properties developers can modify
                val outputBase = File(project.buildDir, extension.output)
                extension.output = File(outputBase, variant.testedVariant.name).path
                task.extension = extension

                task.instrumentationApk = instrumentationPackage
                task.doFirst {
                    val testedOutput = variant.testedVariant.outputs.first()

                    if (testedOutput is ApkVariantOutput) {
                        task.applicationApk = testedOutput.outputFile
                    } else {
                        // This is a hack for library projects.
                        // We supply the same apk as an application and instrumentation to the soon runner.
                        task.applicationApk = task.instrumentationApk
                    }
                }
            }
        }
    }
}
