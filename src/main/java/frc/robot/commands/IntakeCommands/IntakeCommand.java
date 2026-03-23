// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.IntakeCommands;

import frc.robot.subsystems.IntakeSubsystems.IntakeSubsystem;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class IntakeCommand extends Command {
  @SuppressWarnings("PMD.UnusedPrivateField")

  private final IntakeSubsystem m_subsystem;

  private BooleanSupplier leftBumper;
  private BooleanSupplier rightBumper;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public IntakeCommand(IntakeSubsystem subsystem, BooleanSupplier leftBumper) {
    this.leftBumper = leftBumper;
    // this.rightBumper = rightBumper;

    m_subsystem = subsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }
  


  @Override
  public void execute(){

    if(leftBumper.getAsBoolean()) {

      m_subsystem.intakeSpeed(-0.667);

    } else {

      m_subsystem.intakeSpeed(0);
    
    }
  }

}
