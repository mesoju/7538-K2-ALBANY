// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.TurretCommands;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.TurretSubsystems.TurretHoodSubsystem;

/** An example command that uses an example subsystem. */
public class TurretHoodCommands extends Command {
  @SuppressWarnings("PMD.UnusedPrivateField")

  private final TurretHoodSubsystem m_subsystem;
  private DoubleSupplier dPad;
  private BooleanSupplier AButton;
  private BooleanSupplier BButton;

  /**
   * Creates a new TurretHoodCommands.
   *
   * @param subsystem The subsystem used by this command.
   */
  public TurretHoodCommands(TurretHoodSubsystem subsystem, DoubleSupplier dPad, BooleanSupplier AButton, BooleanSupplier BButton) {
    this.dPad = dPad;
    this.AButton = AButton;
    this.BButton = BButton;

    m_subsystem = subsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }
  


  @Override
  public void execute(){
    //System.out.println("dPad " + dPad.getAsDouble());
    // System.out.println("A" + AButton.getAsBoolean());


    if (dPad.getAsDouble() == 0) {
        m_subsystem.turretHoodAngle(0.2, AButton.getAsBoolean(), BButton.getAsBoolean());
    } else if (dPad.getAsDouble() == 180) {
      m_subsystem.turretHoodAngle(-0.2, AButton.getAsBoolean(), BButton.getAsBoolean());
    } else {
      m_subsystem.turretHoodAngle(0, AButton.getAsBoolean(), BButton.getAsBoolean());
    }
  }





  // // Called when the command is initially scheduled.
  // @Override
  // public void initialize() {}

  // // Called every time the scheduler runs while the command is scheduled.
  // @Override
  // public void execute() {}

  // // Called once the command ends or is interrupted.
  // @Override
  // public void end(boolean interrupted) {}

  // // Returns true when the command should end.
  // @Override
  // public boolean isFinished() {
  //   return false;
  // }
}
