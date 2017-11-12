package com.jaredsburrows.spoon

import com.android.build.gradle.api.ApkVariantOutput
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
final class SpoonPlugin implements Plugin<Project> {
  final static ANDROID_PLUGINS = ["com.android.application", "com.android.library"]

  @Override void apply(Project project) {
    if (isAndroidProject(project)) {
      configureAndroidProject(project)
    } else {
      throw new IllegalStateException(
        "Spoon plugin can only be applied to android application or library projects.")
    }
  }

  /**
   * Configure project and all variants for Android.
   */
  static configureAndroidProject(project) {
    // Create "spoon" extension
    final SpoonExtension extension = project.extensions.create("spoon", SpoonExtension)

    // Use "variants" from "buildTypes" to get all types for "testVariants"
    // Configure tasks for all variants
    project.android.testVariants.all { variant -> //    project.android.testVariants.all { variant ->
      variant.outputs.all { ->
        final variantName = variant.name.capitalize()
        final taskName = "spoon${variantName}"
        final instrumentationPackage = variant.outputs[0].outputFile

        // Create tasks based on variant
        final SpoonTask task = project.tasks.create("$taskName", SpoonTask)
        // task properties
        task.description = "Run instrumentation tests for '${variant.name}' variant."
        task.group = "Verification"
        task.outputs.upToDateWhen { false }
        task.dependsOn variant.testedVariant.assemble, variant.assemble

        // extra task properties
        task.title = "$project.name $variant.name"

        // extension properties developers can modify
        final File outputBase = new File(project.buildDir, extension.output)
        extension.output = new File(outputBase, variant.testedVariant.name).path
        task.extension = extension

        task.instrumentationApk = instrumentationPackage
        task.doFirst {
          final testedOutput = variant.testedVariant.outputs[0]

          if (testedOutput instanceof ApkVariantOutput) {
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

  /**
   * Check if the project has Android plugins.
   */
  static isAndroidProject(project) {
    ANDROID_PLUGINS.find { plugin -> project.plugins.hasPlugin(plugin) }
  }
}
