package com.jaredsburrows.spoon

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

final class NewSpoonPluginSpec extends Specification {
  @Rule public TemporaryFolder testProjectDir = new TemporaryFolder()
  private List<File> pluginClasspath
  private String classpathString
  private File buildFile

  def 'setup'() {
    def pluginClasspathResource = getClass().classLoader.getResource('plugin-classpath.txt')
    if (pluginClasspathResource == null) {
      throw new IllegalStateException(
        'Did not find plugin classpath resource, run `testClasses` build task.')
    }

    pluginClasspath = pluginClasspathResource.readLines().collect { new File(it) }
    classpathString = pluginClasspath
      .collect { it.absolutePath.replace('\\', '\\\\') } // escape backslashes in Windows paths
      .collect { "'$it'" }
      .join(", ")
    buildFile = testProjectDir.newFile('build.gradle')
  }

  @Unroll
  def 'spoonDebugAndroidTest with gradle #gradleVersion and android gradle plugin #agpVersion'() {
    given:
    buildFile <<
      """
      buildscript {
        repositories {
          jcenter()
          google()
        }
        dependencies {
          classpath "com.android.tools.build:gradle:${agpVersion}"
          classpath files($classpathString)
        }
      }
      apply plugin: 'com.android.application'
      apply plugin: 'com.jaredsburrows.spoon'
      android {
        compileSdkVersion 28
        defaultConfig {
          applicationId 'com.example'
        }
      }
      """

    when:
    def result = GradleRunner.create()
      .withGradleVersion(gradleVersion)
      .withProjectDir(testProjectDir.root)
      .withArguments('spoonDebugAndroidTest', '-s')
      .build()

    then:
    result.task(':spoonDebugAndroidTest').outcome == SUCCESS

    where:
    [gradleVersion, agpVersion] << [
      [
        '5.6.4',
        '6.1.1'
      ],
      [
        '3.5.0',
        '3.6.0',
        '4.0.0'
      ]
    ].combinations()
  }
}
