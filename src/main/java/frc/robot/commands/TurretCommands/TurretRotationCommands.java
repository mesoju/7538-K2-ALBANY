// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.TurretCommands;

import java.util.function.DoubleSupplier;
import java.util.function.BooleanSupplier;

// import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.TurretSubsystems.TurretRotationSubsystem;
import frc.robot.utility.pose2Dutility;

/** An example command that uses an example subsystem. */
public class TurretRotationCommands extends Command {
  @SuppressWarnings("PMD.UnusedPrivateField")

  private final TurretRotationSubsystem m_subsystem;
  private boolean intervene; 
  private pose2Dutility poseUtility;

  private long lastUpdate = 0;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public TurretRotationCommands(TurretRotationSubsystem subsystem, pose2Dutility poseUtility) {
    this.poseUtility = poseUtility;

    m_subsystem = subsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
    intervene = false;
  }


  @Override
  public void execute(){

    if (!intervene && System.currentTimeMillis() >= lastUpdate) {
      lastUpdate = System.currentTimeMillis() + 250;
      Double angle = poseUtility.getBestFieldGoalAngle();

      m_subsystem.setTurretAngle(angle);
    }

  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_subsystem.zeroEncoderFromInit();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
