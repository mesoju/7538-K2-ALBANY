// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.TurretCommands;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ShooterConstants;
import frc.robot.subsystems.TurretSubsystems.TurretShooterSubsystem;
import frc.robot.utility.pose2Dutility;

/** An example command that uses an example subsystem. */
public class TurretShooterCommands extends Command {
  @SuppressWarnings("PMD.UnusedPrivateField")

  private final TurretShooterSubsystem m_subsystem;
  private final pose2Dutility m_poseUtility;
  
  private DoubleSupplier leftTrigger;
  private DoubleSupplier rightTrigger;

  /**
   * Creates a new TurretShooterCommands.
   *
   * @param subsystem The subsystem used by this command.
   */
  public TurretShooterCommands(TurretShooterSubsystem subsystem, pose2Dutility m_poseUtility, DoubleSupplier leftTrigger, DoubleSupplier rightTrigger) {
    this.m_poseUtility = m_poseUtility;
    this.leftTrigger = leftTrigger;
    this.rightTrigger = rightTrigger;

    m_subsystem = subsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }
  
  @Override
  public void execute(){
    if (rightTrigger.getAsDouble() >= 0.1 && leftTrigger.getAsDouble() <= 0.1) {
      double setpoint = m_poseUtility.getBestVelocitySetpoint();
      m_subsystem.setShooterSetpoint(setpoint);
    } else if (leftTrigger.getAsDouble() >= 0.1) {
      m_subsystem.setShooterSetpoint(-60);
    } else {
      m_subsystem.setShooterSpeed(0);
    }
    //m_subsystem.setShooterSetpoint(0);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
