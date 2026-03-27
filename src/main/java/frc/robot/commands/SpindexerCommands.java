// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.SpindexerSubsystem;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class SpindexerCommands extends Command {
  @SuppressWarnings("PMD.UnusedPrivateField")

  private final SpindexerSubsystem m_subsystem;

  private DoubleSupplier leftTrigger;
  private DoubleSupplier rightTrigger;

  private long timeout;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public SpindexerCommands(SpindexerSubsystem subsystem, DoubleSupplier leftTrigger, DoubleSupplier rightTrigger) {
    this.leftTrigger = leftTrigger;
    this.rightTrigger = rightTrigger;
    this.timeout = 0;

    m_subsystem = subsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }
  


  @Override
  public void execute(){
    if(Math.abs(rightTrigger.getAsDouble() - leftTrigger.getAsDouble()) >= 0.1 && timeout == 0) {
      timeout = System.currentTimeMillis() + 2000;
    } else if(Math.abs(rightTrigger.getAsDouble() - leftTrigger.getAsDouble()) >= 0.1 && System.currentTimeMillis() >= timeout && timeout != 0) {

      m_subsystem.feedSpeed((rightTrigger.getAsDouble() - leftTrigger.getAsDouble()) * 0.8);

    } else if(Math.abs(rightTrigger.getAsDouble() - leftTrigger.getAsDouble()) < 0.1) {
      timeout = 0;
      m_subsystem.feedSpeed(0);

    }

  }
  
}
