/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.sensors

import edu.wpi.first.wpilibj.SPI
import edu.wpi.first.wpilibj.geometry.Rotation2d
import edu.wpi.first.wpilibj.geometry.Translation2d
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.units.foot
import org.ghrobotics.lib.utils.Source
import kotlin.coroutines.CoroutineContext

/**
 *  Implementation of the ADNS3080 Optical Flow Sensor connected over SPI
 *
 *  @param port the spi port
 *  @param ticksPerFoot amount of ticks per foot
 *  @param rotation2d the rotation offset (sometimes we dont mount things perfectly)
 */
class ADNS3080FlowSensor(
    private val port: SPI.Port,
    private val ticksPerFoot: Double,
    private val rotation2d: Rotation2d,
    parent: CoroutineContext
) : Source<Translation2d> {

    private val job = Job(parent[Job])
    private val scope = CoroutineScope(parent + job)

    private val dataSent = ByteArray(1) { 0.toByte() }
    private val dataReceived = ByteArray(1) { 0.toByte() }

    var surfaceQuality = 0
        private set
    private var accumX = 0.0
    private var accumY = 0.0

    init {
        scope.launch {
            val spi = SPI(port)
            spi.setChipSelectActiveLow()
            spi.setClockActiveHigh()
            spi.setClockRate(500000)

            while (isActive) {
                var rawDx = 0.0
                var rawDy = 0.0
                val motionRegister = spi.readRegister(2.toByte()).toInt()
                if (motionRegister and 0x80 != 0) {
                    rawDy = spi.readRegister(3.toByte()).toDouble()
                    rawDx = spi.readRegister(4.toByte()).toDouble()
                }
                surfaceQuality = spi.readRegister(5.toByte()).toInt()
                accumX += rawDx
                accumY += rawDy
            }
        }
    }

    private fun SPI.readRegister(register: Byte): Byte {
        dataSent[0] = register
        write(dataSent, 1)
        read(true, dataReceived, 1)
        read(false, dataReceived, 1)
        return dataReceived[0]
    }

    override fun invoke() = Translation2d(accumX.foot, accumY.foot).rotateBy(rotation2d)

    fun free() {
        job.cancel()
    }
}