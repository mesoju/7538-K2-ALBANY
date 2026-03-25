// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.TurretCommands;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ShooterConstants;
import frc.robot.subsystems.TurretSubsystems.TurretShooterSubsystem;

/** An example command that uses an example subsystem. */
public class TurretShooterCommands extends Command {
  @SuppressWarnings("PMD.UnusedPrivateField")

  private final TurretShooterSubsystem m_subsystem;
  
  private DoubleSupplier leftTrigger;
  private DoubleSupplier rightTrigger;

  /**
   * Creates a new TurretShooterCommands.
   *
   * @param subsystem The subsystem used by this command.
   */
  public TurretShooterCommands(TurretShooterSubsystem subsystem, DoubleSupplier leftTrigger, DoubleSupplier rightTrigger) {
    this.leftTrigger = leftTrigger;
    this.rightTrigger = rightTrigger;


    m_subsystem = subsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }
  
  @Override
  public void execute(){
    if (rightTrigger.getAsDouble() >= 0.1) {
      double setpoint = Math.max
        (0, 
        (rightTrigger.getAsDouble() - leftTrigger.getAsDouble()) 
            * Units.rotationsPerMinuteToRadiansPerSecond(ShooterConstants.kMaxSetpointValue));
      m_subsystem.shooterspeed(setpoint);
    } else {
      m_subsystem.shooterspeed(0);
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
