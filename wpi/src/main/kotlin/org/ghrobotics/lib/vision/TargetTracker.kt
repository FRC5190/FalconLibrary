/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.vision

import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Rotation2d
import edu.wpi.first.wpilibj.geometry.Translation2d
import org.ghrobotics.lib.debug.FalconDashboard
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.seconds

/**
 * Class used to track the locations of various vision targets throughout
 * the field.
 *
 * This class should be overriden to add year-specific funtionality along
 * with a method to obtain the "best target". A "best target" method is provided
 * by default; however, this uses absolute pose and not the robot pose. The best
 * target based on the robot pose may vary due to robot geometry (passthrough, turret),
 * etc.
 */
open class TargetTracker(private val constants: TargetTrackerConstants) {

    // Stores all tracked targets.
    protected val targets = mutableSetOf<TrackedTarget>()

    /**
     * Updates all targets that are stored in the TargetTracker.
     */
    fun update() {
        // Get the current time.
        val time = Timer.getFPGATimestamp().seconds

        // Update and remove old targets.
        targets.removeIf { it.update(time); !it.isAlive }

        // Publish targets to Falcon Dashboard.
        FalconDashboard.visionTargets = targets.asSequence()
            .filter { it.isReal }
            .map { it.averagePose }
            .toList()
    }

    /**
     * Adds (a) new sample(s) to the Target Tracker.
     *
     * @param captureTime The time of capture.
     * @param poses The field-relative poses of the targets.
     */
    fun addSamples(captureTime: SIUnit<Second>, poses: Iterable<Pose2d>) {
        // Remove samples from the "future".
        if (captureTime > Timer.getFPGATimestamp().seconds) {
            return
        }

        // Find the target to add this sample to.
        for (pose in poses) {
            // Get the closest target to this pose.
            val closestTarget = targets.minBy { it.averagePose.translation.getDistance(pose.translation) }

            // Create the sample.
            val sample = TrackedTargetSample(captureTime, pose)

            // Decide on whether to add to the closest target or add to a new target.
            if (closestTarget == null || closestTarget.averagePose.translation.getDistance(pose.translation) >
                constants.kTargetTrackingDistanceErrorTolerance.value
            ) {
                // Create a new target because nothing is near the tolerance.
                targets += TrackedTarget(sample, constants)
            } else {
                // We are within tolerance of the closest target. We can add this sample
                // to that target.
                closestTarget.addSample(sample)
            }
        }
    }

    /**
     * Returns the closest target to the absolute translation on the field.
     *
     * @param translation The absolute translation of the pose on the field.
     * @return The tracked target or null if there is no nearby best target.
     */
    open fun getAbsoluteTarget(translation: Translation2d): TrackedTarget? {
        return targets.asSequence()
            .filter {
                it.isReal && translation.getDistance(it.averagePose.translation) <
                    constants.kTargetTrackingDistanceErrorTolerance.value
            }.minBy { it.averagePose.translation.getDistance(translation) }
    }

    /**
     * Represents one target on the field.
     *
     * @param initialSample The initial target sample for this target.
     */
    class TrackedTarget(
        private val initialSample: TrackedTargetSample,
        private val constants: TargetTrackerConstants
    ) {
        // The samples that make up this target.
        private val samples = mutableSetOf<TrackedTargetSample>()

        // The average pose of this target.
        var averagePose = initialSample.pose
            private set

        // The target is alive when it has at least one data point for a
        // certain amount of time.
        var isAlive = true
            private set

        // The target is real when it has received a certain number of
        // data points for a certain period of time.
        var isReal = true
            private set

        /**
         * Adds a sample to the tracked target.
         */
        fun addSample(sample: TrackedTargetSample) {
            samples.add(sample)
        }

        /**
         * Updates the target state.
         *
         * @param time The current time.
         */
        fun update(time: SIUnit<Second>) {
            // Remove expired samples.
            samples.removeIf { time - it.captureTime >= constants.kMaxTargetTrackingLifetime }

            // Update state.
            isAlive = samples.isNotEmpty()
            isReal = samples.size > 2

            // Update average pose.
            var accumulatedX = 0.0
            var accumulatedY = 0.0
            var accumulatedAngle = 0.0
            for (sample in samples) {
                accumulatedX += sample.pose.translation.x
                accumulatedY += sample.pose.translation.y
                accumulatedAngle += sample.pose.rotation.radians
            }
            val size = samples.size
            averagePose = Pose2d(accumulatedX / size, accumulatedY / size, Rotation2d(accumulatedAngle / size))
        }
    }

    /**
     * Represents one sample of a target on the field.
     *
     * @param captureTime The time at which the frame containing the target was captured.
     * @param pose The field-relative pose of the target.
     */
    data class TrackedTargetSample(val captureTime: SIUnit<Second>, val pose: Pose2d)

    /**
     * Represents constants for the target tracker.
     *
     * @param kMaxTargetTrackingLifetime The maximum amount of time that a target
     * should be tracked after a specific data point has been received.
     * @param kTargetTrackingDistanceErrorTolerance The distance tolerance to
     * create a new target given a sample.
     */
    data class TargetTrackerConstants(
        val kMaxTargetTrackingLifetime: SIUnit<Second>,
        val kTargetTrackingDistanceErrorTolerance: SIUnit<Meter>
    )
}
