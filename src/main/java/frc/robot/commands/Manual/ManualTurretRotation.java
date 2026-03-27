// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.Manual;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.TurretSubsystems.TurretRotationSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class ManualTurretRotation extends Command {

  TurretRotationSubsystem m_subsystem;
  DoubleSupplier leftJoystickX;
  double angle;

  /** Creates a new ManualTurretRotation. */
  public ManualTurretRotation(TurretRotationSubsystem m_subsystem, DoubleSupplier leftJoystickX) {
    // Use addRequirements() here to declare subsystem dependencies.

    this.m_subsystem = m_subsystem;
    this.leftJoystickX = leftJoystickX;
    this.angle = 0;

    addRequirements(m_subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (Math.abs(leftJoystickX.getAsDouble()) >= 0.1) {
      this.angle = MathUtil.clamp(angle + leftJoystickX.getAsDouble(), -180, 180);
      SmartDashboard.putNumber("Manual Request Angle", angle);
      m_subsystem.setTurretAngle(angle);
    }
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
