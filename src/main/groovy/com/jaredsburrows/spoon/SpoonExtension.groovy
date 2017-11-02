package com.jaredsburrows.spoon

/**
 * Variables based on the following documentation:
 * - https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner.html
 * - https://developer.android.com/training/testing/espresso/setup.html
 * - https://github.com/square/spoon/blob/master/spoon-runner/src/main/java/com/squareup/spoon/SpoonRunner.java
 * - https://github.com/square/spoon#execution
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
class SpoonExtension {
  private static final String DEFAULT_OUTPUT_DIRECTORY = "spoon-output"
  private static final int DEFAULT_ADB_TIMEOUT_SEC = 10 * 60 // 10 minutes

  ////////////////////////////////////////////////////
  // Supported directly by Spoon's SpoonRunner
  ///////////////////////////////////////////////////

  /** Path to output directory. */
  String output = DEFAULT_OUTPUT_DIRECTORY

  void setOutput(String path) {
    if (path != null) output = path
  }

  /** Whether or not debug logging is enabled. */
  boolean debug

  /** Whether or not animations are enabled. Disable animated gif generation. */
  boolean noAnimations

  /** Set ADB timeout. (minutes) */
  int adbTimeout = DEFAULT_ADB_TIMEOUT_SEC * 1000

  void setAdbTimeout(int time) {
    if (time > 0) adbTimeout = time * 1000
  }

  /** Add device serials for test execution. */
  Set<String> devices = []

  /** Add device serials for skipping test execution. */
  Set<String> skipDevices = []

  /** Extra arguments to pass to instrumentation. */
  List<String> instrumentationArgs = []

  /** Test class name to run (fully-qualified). */
  String className = ""

  // TODO size

  /** Execute the tests device by device. */
  boolean sequential

  /** Grant all runtime permissions during installation on Marshmallow and above devices. */
  boolean grantAll

  /** Test method name to run (must also use className) */
  String methodName = ""

  /** Code coverage flag. For Spoon to calculate coverage file your app must have the `WRITE_EXTERNAL_STORAGE` permission.
   (This option pulls the coverage file from all devices and merge them into a single file `merged-coverage.ec`.) */
  boolean codeCoverage

  /** Fail if no device is connected. */
  boolean failIfNoDeviceConnected

  ////////////////////////////////////////////////////
  // Passed in via -e, extra arguments
  ///////////////////////////////////////////////////

  /** Toggle sharding. */
  boolean shard

  /** The number of separate shards to create. */
  int numShards

  void setNumShards(int shards) {
    if (shards > 0) numShards = shards
  }

  /** The shardIndex option to specify which shard to run. */
  int shardIndex

  void setShardIndex(int index) {
    if (index > 0) shardIndex = index
  }

  ////////////////////////////////////////////////////
  // Deprecated/Renamed
  ///////////////////////////////////////////////////

  @Deprecated File baseOutputDir

  void setBaseOutputDir(File directory) {
    if (directory != null) {
      output = directory.absolutePath
      baseOutputDir = directory
    }
  }

  @Deprecated boolean grantAllPermissions

  void setGrantAll(boolean grant) {
    grantAll = grant
    grantAllPermissions = grant
  }

  ////////////////////////////////////////////////////
  // Do not want to support?
  ///////////////////////////////////////////////////

  @Deprecated boolean ignoreFailures
}
