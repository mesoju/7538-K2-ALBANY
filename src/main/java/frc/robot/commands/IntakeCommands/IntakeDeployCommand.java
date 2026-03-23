// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.IntakeCommands;

import frc.robot.subsystems.IntakeSubsystems.IntakeDeploySubsystem;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class IntakeDeployCommand extends Command {
  @SuppressWarnings("PMD.UnusedPrivateField")

  private final IntakeDeploySubsystem m_subsystem;

  private BooleanSupplier leftBumper;
  private BooleanSupplier rightBumper;
  private BooleanSupplier XButton;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public IntakeDeployCommand(IntakeDeploySubsystem subsystem, BooleanSupplier leftBumper, BooleanSupplier rightBumper, BooleanSupplier XButton) {
    this.leftBumper = leftBumper;
    this.rightBumper = rightBumper;
    this.XButton = XButton;

    m_subsystem = subsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }
  


  @Override
  public void execute(){

    // If left bumper == true then deploy, else if right bumper == true then undeploy

    if(leftBumper.getAsBoolean()){
      m_subsystem.deploySpeed(0.1, XButton.getAsBoolean());
    } else if (rightBumper.getAsBoolean()){
      m_subsystem.deploySpeed(-0.1, XButton.getAsBoolean());
    } else {
      m_subsystem.deploySpeed(0, XButton.getAsBoolean());
    }


  }
  
}