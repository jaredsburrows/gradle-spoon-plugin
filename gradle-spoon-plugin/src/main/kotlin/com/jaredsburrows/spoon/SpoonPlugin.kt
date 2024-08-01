package com.jaredsburrows.spoon

import org.gradle.api.Plugin
import org.gradle.api.Project

/** A [Plugin] which wraps the Spoon test runner. */
class SpoonPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.extensions.create("spoon", SpoonExtension::class.java)

    project.afterEvaluate {
      when {
        project.isAndroidProject() -> project.configureAndroidProject()
        else -> throw UnsupportedOperationException("'com.jaredsburrows.spoon' requires the Android Gradle Plugins.")
      }
    }
  }
}
