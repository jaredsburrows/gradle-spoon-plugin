package com.jaredsburrows.spoon

import org.gradle.api.Project

/** Returns true if plugin exists in project */
internal fun Project.hasPlugin(list: List<String>): Boolean {
  return list.find { project.plugins.hasPlugin(it) } != null
}

/** Help with testing and debugging. */
internal fun Project.isNotTest(): Boolean = findProperty("testing") == null
