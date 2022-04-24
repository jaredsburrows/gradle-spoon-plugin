package com.jaredsburrows.spoon

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.TestVariant
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import java.util.Locale

/** Returns true if Android Gradle project */
internal fun Project.isAndroidProject(): Boolean {
  return hasPlugin(
    listOf(
      // AppPlugin
      "android",
      "com.android.application",
      // LibraryPlugin
      "android-library",
      "com.android.library",
    )
  )
}

/**
 * Configure for Android projects.
 *
 * AppPlugin - "android", "com.android.application"
 * LibraryPlugin - "android-library", "com.android.library"
 */
internal fun Project.configureAndroidProject() {
  project.plugins.all {
    when (it) {
      is AppPlugin -> {
        project.extensions.getByType(AppExtension::class.java).run {
          configureVariant(testVariants)
        }
      }
      is LibraryPlugin -> {
        project.extensions.getByType(LibraryExtension::class.java).run {
          configureVariant(testVariants)
        }
      }
    }
  }
}

private fun Project.configureVariant(variants: DomainObjectSet<TestVariant>? = null) {
  // Configure tasks for all variants
  variants?.all { variant ->
    val name = variant.name.replaceFirstChar {
      if (it.isLowerCase()) {
        it.titlecase(Locale.getDefault())
      } else {
        it.toString()
      }
    }

    // Create tasks based on variant
    tasks.register("spoon$name", SpoonTask::class.java) {
      if (project.isNotTest()) {
        it.dependsOn(variant.testedVariant.assembleProvider, variant.assembleProvider)
      }
      it.variant = variant
    }
  }
}
