// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.TurretCommands;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.MotorConstants;
import frc.robot.Constants.ShooterConstants;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.SpindexerSubsystem;
import frc.robot.subsystems.TurretSubsystems.TurretShooterSubsystem;
import frc.robot.utility.pose2Dutility;

/** An example command that uses an example subsystem. */
public class TurretShooterCommands extends Command {
  @SuppressWarnings("PMD.UnusedPrivateField")

  private final TurretShooterSubsystem m_subsystem;
  private final FeederSubsystem m_feeder_subsystem;
  private final SpindexerSubsystem m_spindexer_subsystem;
  private final pose2Dutility m_poseUtility;

  private DoubleSupplier direction;
  private long timeout;

  /**
   * Creates a new TurretShooterCommands.
   *
   * @param subsystem The subsystem used by this command.
   */
  public TurretShooterCommands(TurretShooterSubsystem subsystem, FeederSubsystem feeder_subsystem, SpindexerSubsystem spindexer_subsystem, pose2Dutility m_poseUtility, DoubleSupplier direction) {
    this.m_poseUtility = m_poseUtility;
    this.timeout = 0;

    m_subsystem = subsystem;
    m_feeder_subsystem = feeder_subsystem;
    m_spindexer_subsystem = spindexer_subsystem;

    this.direction = direction;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }
  
  @Override
  public void execute(){
    if (direction.getAsDouble() >= 0.1) {
      // double setpoint = m_poseUtility.getBestVelocitySetpoint();
      // m_subsystem.setShooterSetpoint(setpoint);
      if (timeout == 0) {
        timeout = System.currentTimeMillis() + MotorConstants.INDEXER_DELAY;
      } else if (System.currentTimeMillis() >= timeout) {
        m_feeder_subsystem.feedSpeed(0.6);
        m_spindexer_subsystem.feedSpeed(0.4);
      }
      m_subsystem.setShooterSetpoint(65);
    } else if (direction.getAsDouble() <= -0.1) {
      timeout = 0;
      m_subsystem.setShooterSetpoint(-60);
      m_feeder_subsystem.feedSpeed(-0.4);
      m_spindexer_subsystem.feedSpeed(-0.4);
    } else {
      timeout = 0;
      m_subsystem.setShooterSpeed(0);
      m_feeder_subsystem.feedSpeed(0);
      m_spindexer_subsystem.feedSpeed(0);
    }
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
