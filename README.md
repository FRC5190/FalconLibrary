# FalconLibrary
[![Build Status](https://dev.azure.com/frc5190/FRC%202018%20Power%20Up/_apis/build/status/Falcon%20Library)](https://dev.azure.com/frc5190/FRC%202018%20Power%20Up/_build/latest?definitionId=2)

## Overview

Feature-rich Kotlin JVM based robotics library, primarily for use in the FIRST Robotics Competition. FalconLibrary is the backend for all Team 5190 robots.

### `core`: Platform-agnostic mathematics and units code
* Two-dimensional parametric and functional splines.
* Arc length of parametric quintic hermite splines evaluated using recursive arc subdivision (from Team 254).
* Trajectory generation that respects constraints (i.e. centripetal acceleration, motor voltage).
* Custom trajectory followers
    * Ramsete
    * Adaptive Pure Pursuit
    * Feedforward
* Typesafe units of measure
    * Quick and easy conversions between all length, velocity, acceleration, electrical units.
    * Support for Talon SRX native unit length and rotation models.

### `wpi`: Code specific to the RoboRIO and FIRST Robotics Competition
* Wrapped WPILib Commands and Subsystems with Kotlin Coroutines asynchronous optimization.
* Built-in drive subsystem abstraction with support for arbitrary localization.
* Custom robot base built on coroutines.

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
compile 'org.ghrobotics:FalconLibrary:core:2019.5.11'
compile 'org.ghrobotics:FalconLibrary:wpi:2019.5.11'
compile 'org.ghrobotics:FalconLibrary:vendorCTRE:2019.5.11'
compile 'org.ghrobotics:FalconLibrary:vendorNAVX:2019.5.11'
compile 'org.ghrobotics:FalconLibrary:vendorREV:2019.5.11'
```

Alternatively, you can include all submodules at once:
```groovy
compile 'org.ghrobotics:FalconLibrary:2019.5.11'
```

## Contributing
You are always welcome to submit a PR if you think that you can contribute something to this library. Remember that this is a FRC-game-agnostic library, so please don't ask for season-specific code to be merged.




