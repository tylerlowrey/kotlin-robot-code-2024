// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot

import edu.wpi.first.wpilibj.PS5Controller
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.Commands
import edu.wpi.first.wpilibj2.command.button.JoystickButton
import frc.robot.subsystems.DriveSubsystem
import frc.robot.subsystems.ShooterPrototypeSubsystem

class RobotContainer(
  shuffleboardTab: ShuffleboardTab
) {
  private val driveSubsystem: DriveSubsystem = DriveSubsystem()
  private val shooterPrototypeSubsystem: ShooterPrototypeSubsystem = ShooterPrototypeSubsystem(shuffleboardTab)
  private val ps5Controller = PS5Controller(Inputs.OPERATOR_CONTROLLER_PORT)

  init {
    configureBindings()
  }

  private fun configureBindings() {
    JoystickButton(ps5Controller, Inputs.START_FLYWHEEL_BUTTON)
      .onTrue(shooterPrototypeSubsystem.createSetFlywheelSpeedCommand(0.5))
  }

  fun getAutonomousCommand(): Command {
    return Commands.print("No autonomous command configured");
  }
}
