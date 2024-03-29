// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot

import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandScheduler

class Robot: TimedRobot() {
  private lateinit var robotContainer: RobotContainer
  private lateinit var autonomousCommand: Command
  private val testingTab: ShuffleboardTab = Shuffleboard.getTab(Dashboard.TESTING_TAB_NAME)

  override fun robotInit() {
    robotContainer = RobotContainer(testingTab)
    autonomousCommand = robotContainer.getAutonomousCommand()
  }

  override fun robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  override fun autonomousInit() {
    autonomousCommand = robotContainer.getAutonomousCommand()

    autonomousCommand.schedule()
  }

  override fun autonomousPeriodic() {}

  override fun teleopInit() {
    autonomousCommand.cancel()
  }

  override fun teleopPeriodic() {}
}
