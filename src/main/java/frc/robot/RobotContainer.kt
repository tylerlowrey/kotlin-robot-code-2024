// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot

import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.Commands
import frc.robot.subsystems.DriveSubsystem

class RobotContainer {
  private val driveSubsystem = DriveSubsystem()

  init {
    configureBindings()
  }

  private fun configureBindings() {}

  fun getAutonomousCommand(): Command {
    return Commands.print("No autonomous command configured");
  }
}
