// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.IntakeCommands;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.IntakeConstants;
import frc.robot.subsystems.IntakeSubsystems.IntakeDeploySubsystemV2;

public class IntakeDeployCommandV2 extends Command {
  /** Creates a new IntakeDeployCommandV2. */
  private IntakeDeploySubsystemV2 m_subsystem;
  private BooleanSupplier xPressed, yPressed;

  public IntakeDeployCommandV2(IntakeDeploySubsystemV2 subsystem, BooleanSupplier xPressed, BooleanSupplier yPressed) {
    this.m_subsystem = subsystem;
    this.xPressed = xPressed;
    this.yPressed = yPressed;

    addRequirements(subsystem);
  }

  public IntakeDeployCommandV2() {}


  @Override
  public void execute() {
    if (xPressed.getAsBoolean()) {
      m_subsystem.intakePosition(IntakeConstants.DEPLOYED);
    } else if (yPressed.getAsBoolean()) {
      m_subsystem.intakePosition(IntakeConstants.RETRACTED);
    }
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
