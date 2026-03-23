// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants.MotorConstants;

import com.ctre.phoenix6.hardware.TalonFX;



public class FeederSubsystem extends SubsystemBase {
  TalonFX feederMotor = new TalonFX(MotorConstants.FEEDER_MOTOR_ID);
  /** Creates a new ExampleSubsystem. */public FeederSubsystem() {}



  public void feedSpeed(double speed){
    feederMotor.set(speed);
  }

}
