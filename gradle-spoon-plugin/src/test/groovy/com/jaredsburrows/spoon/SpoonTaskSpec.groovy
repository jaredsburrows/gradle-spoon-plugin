package com.jaredsburrows.spoon

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static test.TestUtils.gradleWithCommand

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

final class SpoonTaskSpec extends Specification {
  @Rule public final TemporaryFolder testProjectDir = new TemporaryFolder()
  private int compileSdkVersion = 32
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

  @Unroll "running #taskName with no spoon extension"() {
    given:
    buildFile <<
      """
      buildscript {
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
    def result = gradleWithCommand(testProjectDir.root, "${taskName}", '-s', '-Ptesting')

    then:
    result.task(":${taskName}").outcome == SUCCESS

    where:
    taskName << ['spoonDebugAndroidTest']
  }

  @Unroll "running #taskName with #testBuildType and with no spoon extension"() {
    given:
    buildFile <<
      """
      buildscript {
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

        testBuildType = "$testBuildType"

        buildTypes {
          debug {}
          release {}
        }
      }
      """

    when:
    def result = gradleWithCommand(testProjectDir.root, "${taskName}", '-s', '-Ptesting')

    then:
    result.task(":${taskName}").outcome == SUCCESS

    where:
    taskName << ['spoonDebugAndroidTest', 'spoonReleaseAndroidTest']
    testBuildType << ['debug', 'release']
  }

  @Unroll "running #taskName with #testBuildType and productFlavors and with no spoon extension"() {
    given:
    buildFile <<
      """
      buildscript {
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

        testBuildType = "$testBuildType"

        buildTypes {
          debug {}
          release {}
        }

        flavorDimensions 'a', 'b'

        productFlavors {
          flavor1 { dimension 'a' }
          flavor2 { dimension 'a' }
          flavor3 { dimension 'b' }
          flavor4 { dimension 'b' }
        }
      }
      """

    when:
    def result = gradleWithCommand(testProjectDir.root, "${taskName}", '-s', '-Ptesting')

    then:
    result.task(":${taskName}").outcome == SUCCESS

    where:
    testBuildType | taskName
    'debug'       | 'spoonFlavor1Flavor3DebugAndroidTest'
    'debug'       | 'spoonFlavor1Flavor4DebugAndroidTest'
    'debug'       | 'spoonFlavor2Flavor3DebugAndroidTest'
    'debug'       | 'spoonFlavor2Flavor4DebugAndroidTest'
    'release'     | 'spoonFlavor1Flavor3ReleaseAndroidTest'
    'release'     | 'spoonFlavor1Flavor4ReleaseAndroidTest'
    'release'     | 'spoonFlavor2Flavor3ReleaseAndroidTest'
    'release'     | 'spoonFlavor2Flavor4ReleaseAndroidTest'
  }

  @Unroll "running #taskName with basic spoon extension"() {
    given:
    buildFile <<
      """
      buildscript {
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

      spoon {
        debug = true

        baseOutputDir = file("custom-report-dir")

        if (project.hasProperty('spoonClassName')) {
          className = project.spoonClassName

          if (project.hasProperty('spoonMethodName')) {
            methodName = project.spoonMethodName
          }
        }

        instrumentationArgs = ['disableAnalytics:true']

        adbTimeout = 30

        codeCoverage = true

        grantAllPermissions = true
      }

      import com.jaredsburrows.spoon.SpoonExtension
      def extension = project.extensions.getByType(SpoonExtension.class)
      println extension.properties.entrySet()*.toString().sort().toString().replaceAll(", ","\\n")
      """

    when:
    def result = gradleWithCommand(testProjectDir.root, "${taskName}", '-s', '-Ptesting')

    then:
    result.task(":${taskName}").outcome == SUCCESS
    result.output.contains("title=Spoon Execution")
    result.output.find("baseOutputDir.*custom-report-dir")
    result.output.contains("debug=true")
    result.output.contains("noAnimations=false")
    result.output.contains("adbTimeout=30000")
    result.output.contains("devices=[]")
    result.output.contains("skipDevices=[]")
    result.output.contains("instrumentationArgs=[disableAnalytics:true]")
    result.output.contains("className=")
    result.output.contains("testSize=")
    result.output.contains("allowNoDevices=false")
    result.output.contains("sequential=false")
    result.output.contains("grantAll=true")
    result.output.contains("methodName=")
    result.output.contains("codeCoverage=true")
    result.output.contains("shard=false")
    result.output.contains("singleInstrumentationCall=false")
    result.output.contains("clearAppDataBeforeEachTest=false")
    result.output.contains("numShards=0")
    result.output.contains("shardIndex=0")
    result.output.contains("ignoreFailures=false")

    where:
    taskName << ['spoonDebugAndroidTest']
  }
}
