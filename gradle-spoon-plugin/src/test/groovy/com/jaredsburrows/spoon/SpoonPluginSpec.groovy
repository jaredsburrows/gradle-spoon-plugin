package com.jaredsburrows.spoon

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static test.TestUtils.gradleWithCommand
import static test.TestUtils.gradleWithCommandWithFail

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

final class SpoonPluginSpec extends Specification {
  @Rule public final TemporaryFolder testProjectDir = new TemporaryFolder()
  private int compileSdkVersion = 28
  private String agpVersion = "3.6.4"
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

  def 'apply with buildscript'() {
    given:
    buildFile <<
      """
      buildscript {
        repositories {
          mavenCentral()
          google()
        }
        dependencies {
          classpath files($classpathString)
        }
      }

      apply plugin: 'com.android.application'
      apply plugin: 'com.jaredsburrows.spoon'

      android {
        compileSdkVersion $compileSdkVersion

        defaultConfig {
          applicationId 'com.example'
        }
      }
      """

    when:
    def result = gradleWithCommand(testProjectDir.root, 'spoonDebugAndroidTest', '-s', '-Ptesting')

    then:
    result.task(':spoonDebugAndroidTest').outcome == SUCCESS
  }

  def 'apply with plugins'() {
    given:
    buildFile <<
      """
      plugins {
        id 'com.android.application'
        id 'com.jaredsburrows.spoon'
      }

      android {
        compileSdkVersion $compileSdkVersion

        defaultConfig {
          applicationId 'com.example'
        }
      }
      """

    when:
    def result = gradleWithCommand(testProjectDir.root, 'spoonDebugAndroidTest', '-s', '-Ptesting')

    then:
    result.task(':spoonDebugAndroidTest').outcome == SUCCESS
  }

  def 'apply with no plugins'() {
    given:
    buildFile <<
      """
      plugins {
        id 'com.jaredsburrows.spoon'
      }
      """

    when:
    def result = gradleWithCommandWithFail(testProjectDir.root, 'spoonDebugAndroidTest', '-s', '-Ptesting')

    then:
    result.output.contains("'com.jaredsburrows.spoon' requires the Android Gradle Plugins.")
  }

  @Unroll def 'apply with non agp plugins: #plugin'() {
    given:
    buildFile <<
      """
      plugins {
        id '${plugin}'
        id 'com.jaredsburrows.spoon'
      }
      """

    when:
    def result = gradleWithCommandWithFail(testProjectDir.root, 'spoonDebugAndroidTest', '-s', '-Ptesting')

    then:
    result.output.contains("'com.jaredsburrows.spoon' requires the Android Gradle Plugins.")

    where:
    // https://github.com/gradle/gradle/find/master, search for "gradle-plugins"
    plugin << [
      // Java
      'antlr', // AntlrPlugin, applies JavaLibraryPlugin, JavaPlugin
      'application', // JavaApplicationPlugin, applies JavaPlugin
      'groovy', // GroovyPlugin, applies JavaPlugin
      'java', // JavaPlugin, applies JavaBasePlugin
      'java-gradle-plugin', // JavaGradlePluginPlugin, applies JavaLibraryPlugin, JavaPlugin
      'java-library', // JavaLibraryPlugin, applies JavaPlugin
      'java-library-distribution', // JavaLibraryDistributionPlugin, applies JavaPlugin
      'scala', // ScalaPlugin, applies JavaPlugin
      'war', // WarPlugin, applies JavaPlugin
      // Native
      'assembler', // AssemblerPlugin
      'assembler-lang', // AssemblerLangPlugin
      'c', // CPlugin
      'c-lang', // CLangPlugin
      'cpp', // CppPlugin
      'cpp-application', // CppApplicationPlugin
      'cpp-lang', // CppLangPlugin
      'cpp-library', // CppLibraryPlugin
      'objective-c', // ObjectiveCPlugin
      'objective-c-lang', // ObjectiveCLangPlugin
      'objective-cpp', // ObjectiveCppPlugin
      'objective-cpp-lang', // ObjectiveCppLangPlugin
      'swift-application', // SwiftApplicationPlugin
      'swift-library', // SwiftLibraryPlugin
    ]
  }

  @Unroll def 'apply with allowed android plugins: #androidPlugin'() {
    given:
    testProjectDir.newFile('settings.gradle') <<
      """
      include 'subproject'
      """

    testProjectDir.newFolder('subproject')

    testProjectDir.newFile('subproject/build.gradle') <<
      """
     buildscript {
        repositories {
          mavenCentral()
          google()
        }
        dependencies {
          classpath "com.android.tools.build:gradle:$agpVersion"
          classpath files($classpathString)
        }
      }

      apply plugin: 'com.android.application'
      apply plugin: 'com.jaredsburrows.spoon'

      android {
        compileSdkVersion $compileSdkVersion

        defaultConfig {
          if (project.plugins.hasPlugin("com.android.application")) applicationId 'com.example'
          if (project.plugins.hasPlugin("com.android.test")) targetProjectPath ':subproject'
        }
      }
      """

    buildFile <<
      """
      buildscript {
        repositories {
          mavenCentral()
          google()
        }
        dependencies {
          classpath "com.android.tools.build:gradle:$agpVersion"
          classpath files($classpathString)
        }
      }

      apply plugin: '${androidPlugin}'
      apply plugin: 'com.jaredsburrows.spoon'

      android {
        compileSdkVersion $compileSdkVersion

        defaultConfig {
          if (project.plugins.hasPlugin("com.android.application")) applicationId 'com.example'
          if (project.plugins.hasPlugin("com.android.test")) targetProjectPath ':subproject'
        }
      }
      """

    when:
    def result = gradleWithCommand(testProjectDir.root, 'spoonDebugAndroidTest', '-s', '-Ptesting')

    then:
    result.task(':spoonDebugAndroidTest').outcome == SUCCESS

    where:
    androidPlugin << [
      // AppPlugin
      'android',
      'com.android.application',
      // LibraryPlugin
      'android-library',
      'com.android.library',
    ]
  }
}
