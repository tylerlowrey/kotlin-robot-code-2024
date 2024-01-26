package frc.robot.commands

import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.ShooterPrototypeSubsystem

class SetFlywheelSpeedCommand(
    private val shooterSubsystem: ShooterPrototypeSubsystem,
    private val flywheelSpeed: Double
) : Command() {
    override fun initialize() {
        shooterSubsystem.setLeftFlywheelSpeed(flywheelSpeed)
        shooterSubsystem.setRightFlywheelSpeed(flywheelSpeed)
    }

    override fun isFinished(): Boolean {
        return true
    }
}