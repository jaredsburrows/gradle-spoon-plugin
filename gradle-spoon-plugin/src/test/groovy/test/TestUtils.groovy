package test

import org.gradle.testkit.runner.GradleRunner

final class TestUtils {
  private TestUtils() {
    throw new AssertionError('No instances')
  }

  static def gradleWithCommand(def file, String... commands) {
    return GradleRunner.create()
      .withArguments(commands)
      .withDebug(true)
      .withProjectDir(file)
      .withPluginClasspath()
      .forwardOutput()
      .build()
  }

  static def gradleWithCommandWithFail(def file, String... commands) {
    return GradleRunner.create()
      .withArguments(commands)
      .withDebug(true)
      .withProjectDir(file)
      .withPluginClasspath()
      .forwardOutput()
      .buildAndFail()
  }
}
