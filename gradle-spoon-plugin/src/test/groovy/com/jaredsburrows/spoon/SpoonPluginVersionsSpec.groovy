package com.jaredsburrows.spoon

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

final class SpoonPluginVersionsSpec extends Specification {
  @Rule public final TemporaryFolder testProjectDir = new TemporaryFolder()
  private int compileSdkVersion = 28
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

  @Unroll def 'AGP version 3.6+, gradle: #gradleVersion and AGP: #agpVersion'() {
    given:
    buildFile <<
      """
      buildscript {
        repositories {
          mavenCentral()
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
        compileSdkVersion $compileSdkVersion

        defaultConfig {
          applicationId 'com.example'
        }
      }
      """

    when:
    def result = GradleRunner.create()
      .withGradleVersion(gradleVersion)
      .withProjectDir(testProjectDir.root)
      .withArguments('spoonDebugAndroidTest', '-s', '-Ptesting')
      .build()

    then:
    result.task(':spoonDebugAndroidTest').outcome == SUCCESS

    where:
    // https://docs.gradle.org/current/userguide/compatibility.html
    // https://developer.android.com/studio/releases/gradle-plugin
    // 5.6.4+, 3.6.0-3.6.4
    [gradleVersion, agpVersion] << [
      [
        '7.0.2',
        '7.1.1',
        '7.2',
        '7.3.3',
        '7.4.2',
      ],
      [
        '3.6.4',
      ]
    ].combinations()
  }

  @Unroll def 'AGP version 4.0+, gradle: #gradleVersion and AGP: #agpVersion'() {
    given:
    buildFile <<
      """
      buildscript {
        repositories {
          mavenCentral()
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
        compileSdkVersion $compileSdkVersion

        defaultConfig {
          applicationId 'com.example'
        }
      }
      """

    when:
    def result = GradleRunner.create()
      .withGradleVersion(gradleVersion)
      .withProjectDir(testProjectDir.root)
      .withArguments('spoonDebugAndroidTest', '-s', '-Ptesting')
      .build()

    then:
    result.task(':spoonDebugAndroidTest').outcome == SUCCESS

    where:
    // https://docs.gradle.org/current/userguide/compatibility.html
    // https://developer.android.com/studio/releases/gradle-plugin
    // 6.1.1+, 4.0.0+
    [gradleVersion, agpVersion] << [
      [
        '7.0.2',
        '7.1.1',
        '7.2',
        '7.3.3',
        '7.4.2',
      ],
      [
        '4.0.2',
      ]
    ].combinations()
  }

  @Unroll def 'agp version 4.1+, gradle: #gradleVersion and agp: #agpVersion'() {
    given:
    buildFile <<
      """
      buildscript {
        repositories {
          mavenCentral()
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
        compileSdkVersion $compileSdkVersion

        defaultConfig {
          applicationId 'com.example'
        }
      }
      """

    when:
    def result = GradleRunner.create()
      .withGradleVersion(gradleVersion)
      .withProjectDir(testProjectDir.root)
      .withArguments('spoonDebugAndroidTest', '-s', '-Ptesting')
      .build()

    then:
    result.task(':spoonDebugAndroidTest').outcome == SUCCESS

    where:
    // https://docs.gradle.org/current/userguide/compatibility.html
    // https://developer.android.com/studio/releases/gradle-plugin
    // 6.5+, 4.1.0+
    [gradleVersion, agpVersion] << [
      [
        '7.0.2',
        '7.1.1',
        '7.2',
        '7.3.3',
        '7.4.2',
      ],
      [
        '4.1.3',
      ]
    ].combinations()
  }

  @Unroll def 'agp version 4.2+, gradle: #gradleVersion and agp: #agpVersion'() {
    given:
    buildFile <<
      """
      buildscript {
        repositories {
          mavenCentral()
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
        compileSdkVersion $compileSdkVersion

        defaultConfig {
          applicationId 'com.example'
        }
      }
      """

    when:
    def result = GradleRunner.create()
      .withGradleVersion(gradleVersion)
      .withProjectDir(testProjectDir.root)
      .withArguments('spoonDebugAndroidTest', '-s', '-Ptesting')
      .build()

    then:
    result.task(':spoonDebugAndroidTest').outcome == SUCCESS

    where:
    // https://docs.gradle.org/current/userguide/compatibility.html
    // https://developer.android.com/studio/releases/gradle-plugin
    // 6.7.1+, 4.2.0+
    [gradleVersion, agpVersion] << [
      [
        '7.0.2',
        '7.1.1',
        '7.2',
        '7.3.3',
        '7.4.2',
      ],
      [
        '4.2.2',
      ]
    ].combinations()
  }

  @Unroll def 'agp version 7.0+, gradle: #gradleVersion and agp: #agpVersion'() {
    given:
    buildFile <<
      """
      buildscript {
        repositories {
          mavenCentral()
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
        compileSdkVersion $compileSdkVersion

        defaultConfig {
          applicationId 'com.example'
        }
      }
      """

    when:
    def result = GradleRunner.create()
      .withGradleVersion(gradleVersion)
      .withProjectDir(testProjectDir.root)
      .withArguments('spoonDebugAndroidTest', '-s', '-Ptesting')
      .build()

    then:
    result.task(':spoonDebugAndroidTest').outcome == SUCCESS

    where:
    // https://docs.gradle.org/current/userguide/compatibility.html
    // https://developer.android.com/studio/releases/gradle-plugin
    // 7.0+, 7.0
    [gradleVersion, agpVersion] << [
      [
        '7.0.2',
        '7.1.1',
        '7.2',
        '7.3.3',
        '7.4.2',
      ],
      [
        '7.0.4',
      ]
    ].combinations()
  }

  @Unroll def 'agp version 7.1+, gradle: #gradleVersion and agp: #agpVersion'() {
    given:
    buildFile <<
      """
      buildscript {
        repositories {
          mavenCentral()
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
        compileSdkVersion $compileSdkVersion

        defaultConfig {
          applicationId 'com.example'
        }
      }
      """

    when:
    def result = GradleRunner.create()
      .withGradleVersion(gradleVersion)
      .withProjectDir(testProjectDir.root)
      .withArguments('spoonDebugAndroidTest', '-s', '-Ptesting')
      .build()

    then:
    result.task(':spoonDebugAndroidTest').outcome == SUCCESS

    where:
    // https://docs.gradle.org/current/userguide/compatibility.html
    // https://developer.android.com/studio/releases/gradle-plugin
    // 7.2+, 7.1
    [gradleVersion, agpVersion] << [
      [
        '7.2',
        '7.3.3',
        '7.4.2',
      ],
      [
        '7.1.3',
      ]
    ].combinations()
  }

  @Unroll def 'agp version 7.2+, gradle: #gradleVersion and agp: #agpVersion'() {
    given:
    buildFile <<
      """
      buildscript {
        repositories {
          mavenCentral()
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
        compileSdkVersion $compileSdkVersion

        defaultConfig {
          applicationId 'com.example'
        }
      }
      """

    when:
    def result = GradleRunner.create()
      .withGradleVersion(gradleVersion)
      .withProjectDir(testProjectDir.root)
      .withArguments('spoonDebugAndroidTest', '-s', '-Ptesting')
      .build()

    then:
    result.task(':spoonDebugAndroidTest').outcome == SUCCESS

    where:
    // https://docs.gradle.org/current/userguide/compatibility.html
    // https://developer.android.com/studio/releases/gradle-plugin
    // 7.3+, 7.2
    [gradleVersion, agpVersion] << [
      [
        '7.3.3',
        '7.4.2',
      ],
      [
        '7.2.0-rc01',
      ]
    ].combinations()
  }
}
