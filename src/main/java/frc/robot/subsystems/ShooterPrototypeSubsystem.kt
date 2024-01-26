package frc.robot.subsystems

import com.revrobotics.CANSparkLowLevel.MotorType
import com.revrobotics.CANSparkMax
import edu.wpi.first.networktables.GenericEntry
import edu.wpi.first.wpilibj.motorcontrol.MotorController
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab
import edu.wpi.first.wpilibj2.command.SubsystemBase
import frc.robot.ShooterSubsystemConstants.LEFT_FLYWHEEL_SPARK_MAX_CAN_ID
import frc.robot.ShooterSubsystemConstants.MAXIMUM_MOTOR_SPEED
import frc.robot.ShooterSubsystemConstants.MINIMUM_MOTOR_SPEED
import frc.robot.ShooterSubsystemConstants.RIGHT_FLYWHEEL_SPARK_MAX_CAN_ID
import frc.robot.commands.SetFlywheelSpeedCommand
import frc.robot.util.printlnErr
import kotlin.math.max
import kotlin.math.min

class ShooterPrototypeSubsystem(
    private val shuffleboardTab: ShuffleboardTab,
) : SubsystemBase() {
    private val leftFlywheelMotorController: MotorController = CANSparkMax(LEFT_FLYWHEEL_SPARK_MAX_CAN_ID, MotorType.kBrushless)
    private val rightFlywheelMotorController: MotorController = CANSparkMax(RIGHT_FLYWHEEL_SPARK_MAX_CAN_ID, MotorType.kBrushless)
    private val leftFlywheelSpeedDashboardEntry: GenericEntry
    private val rightFlywheelSpeedDashboardEntry: GenericEntry

    init {
        shuffleboardTab.add(this)

        leftFlywheelSpeedDashboardEntry = shuffleboardTab.add("Left Flywheel Speed", 0)
            .withWidget(BuiltInWidgets.kNumberSlider)
            .withProperties(mapOf(Pair("min", MINIMUM_MOTOR_SPEED), Pair("max", MAXIMUM_MOTOR_SPEED)))
            .entry

        rightFlywheelSpeedDashboardEntry = shuffleboardTab.add("Right Flywheel Speed", 0)
            .withWidget(BuiltInWidgets.kNumberSlider)
            .withProperties(mapOf(Pair("min", MINIMUM_MOTOR_SPEED), Pair("max", MAXIMUM_MOTOR_SPEED)))
            .entry
    }

    override fun periodic() {
        val leftFlywheelSpeed = leftFlywheelSpeedDashboardEntry.getDouble(0.0)
        if (leftFlywheelMotorController.get() != leftFlywheelSpeed) {
            setLeftFlywheelSpeed(leftFlywheelSpeed)
        }

        val rightFlywheelSpeed = rightFlywheelSpeedDashboardEntry.getDouble(0.0)
        if (rightFlywheelMotorController.get() != rightFlywheelSpeed) {
            setRightFlywheelSpeed(rightFlywheelSpeed)
        }
    }

    fun createSetFlywheelSpeedCommand(flywheelSpeed: Double): SetFlywheelSpeedCommand {
        return SetFlywheelSpeedCommand(this, flywheelSpeed)
    }

    fun setLeftFlywheelSpeed(motorSpeed: Double) {
        val clampedSpeed = setClampedFlywheelSpeed(leftFlywheelMotorController, motorSpeed)

        leftFlywheelSpeedDashboardEntry.setDouble(clampedSpeed)
    }

    fun setRightFlywheelSpeed(motorSpeed: Double) {
        val clampedSpeed = setClampedFlywheelSpeed(rightFlywheelMotorController, motorSpeed)

        rightFlywheelSpeedDashboardEntry.setDouble(clampedSpeed)
    }

    private fun setClampedFlywheelSpeed(motorController: MotorController, motorSpeed: Double) : ClampedSpeed {
        val clampedMotorSpeed = clampMotorSpeed(motorSpeed)

        motorController.set(clampedMotorSpeed)

        return clampedMotorSpeed
    }

    private fun clampMotorSpeed(motorSpeed: Double): ClampedSpeed {
        var clampedMotorSpeed = motorSpeed
        if (motorSpeed < MINIMUM_MOTOR_SPEED || motorSpeed > MAXIMUM_MOTOR_SPEED) {
            printlnErr("provided motor speed was outside acceptable range, received value: $motorSpeed")
            clampedMotorSpeed = max(motorSpeed, MINIMUM_MOTOR_SPEED)
            clampedMotorSpeed = min(clampedMotorSpeed, MAXIMUM_MOTOR_SPEED )
        }

        return clampedMotorSpeed
    }
}

private typealias ClampedSpeed = Double
