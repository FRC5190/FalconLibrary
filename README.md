# FalconLibrary
[![Build Status](https://dev.azure.com/frc5190/FRC%202018%20Power%20Up/_apis/build/status/Falcon%20Library)](https://dev.azure.com/frc5190/FRC%202018%20Power%20Up/_build/latest?definitionId=2)

## Overview

Falcon Library is the backend library that is used on all FRC Team 5190 robots. This library was written in the Kotlin JVM Language. Some features of this library include:

 * Wrapped WPILib Commands and Subsystems with Kotlin Coroutines asynchronous optimzation.

 * High level mathematics for path generation, tracking, custom typesafe units of measure, etc.
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

 * AHRS sensor wrapper for Pigeon IMU and NavX.

 * Tank Drive Subsystem abstraction with built-in odometry and command to follow trajectories.

 * Talon SRX wrapper that utilizes Kotlin properties to set configurations.

 * Custom robot base with fully implemented state machine and coroutine support.

 * Other WPILib wrappers for NetworkTables, etc.

## Contributing

This library is open source and we would love to have you contribute code to this repository. Please make sure that before submitting a pull request, your code is formatted according to `ktlint` (already in the project). The Gradle build will fail if all code is not formatted correctly.

To format code automatically, run `./gradlew spotlessApply`. Please build the project locally using `./gradlew build` to make sure everything works before submitting a pull request. 

When adding new features, it is encouraged that these features be game-agnostic. This library is intended to be used for robots that play any game. Also make sure to include unit-tests for any new features.



