# FalconLibrary
[![Build Status](https://dev.azure.com/frc5190/Robot%20Code/_apis/build/status/FalconLibrary?branchName=master)](https://dev.azure.com/frc5190/Robot%20Code/_build/latest?definitionId=11&branchName=master)

## Overview

Feature-rich Kotlin JVM based robotics library, primarily for use in the FIRST Robotics Competition. FalconLibrary is the backend for all Team 5190 robots.

Note: Due to the usage of inline classes, the units code does NOT work in Java.

### `core`: Platform-agnostic mathematics and units code
* Typesafe units of measure
    * Quick and easy conversions between all length, velocity, acceleration, electrical units.
    * Support for Talon SRX native unit length and rotation models.
* Shape-safe matrix operations
    * Detect matrix shape mismatches at runtime by attempting to mimic C++'s templates using Generics
* State-space modern control

### `wpi`: Code specific to the RoboRIO and FIRST Robotics Competition
* Built-in drive subsystem abstraction with support for arbitrary localization.
* Kotlin wrappers around the command based framework

### `vendorXXX`: RoboRIO vendor extensions
* Talon SRX and Spark MAX wrappers that utilize Kotlin properties to set configurations.
* Custom gyro `Rotation2d` sources.

## Using FalconLibrary in your project

Make sure you can retrieve dependencies from JitPack. Add this to your `build.gradle`:
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

Under the `dependencies` section of your `build.gradle`, add the specific submodules that you want in your project. All of the submodules are presented below.

```groovy
compile 'org.ghrobotics.FalconLibrary:core:2020.1.2'
compile 'org.ghrobotics.FalconLibrary:wpi:2020.1.2'
compile 'org.ghrobotics.FalconLibrary:vendorCTRE:2020.1.2'
compile 'org.ghrobotics.FalconLibrary:vendorNAVX:2020.1.2'
compile 'org.ghrobotics.FalconLibrary:vendorREV:2020.1.2'
```

Alternatively, you can include all submodules at once:
```groovy
compile 'org.ghrobotics:FalconLibrary:2020.1.2'
```

Note that you must include the `vendordeps` JSON file in your own robot project to correctly use the `vendorXXX` modules.

## Contributing
You are always welcome to submit a PR if you think that you can contribute something to this library. Remember that this is a FRC-game-agnostic library, so please don't ask for season-specific code to be merged.




