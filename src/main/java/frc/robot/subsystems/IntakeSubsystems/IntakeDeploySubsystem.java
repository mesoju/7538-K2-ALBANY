// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.IntakeSubsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants.MotorConstants;
import frc.robot.Constants.EncoderConstants;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.hardware.CANcoder;



public class IntakeDeploySubsystem extends SubsystemBase {

  TalonFX intakeDeployLeftMotor = new TalonFX(MotorConstants.INTAKE_DEPLOY_LEFT_MOTOR_ID);
  TalonFX intakeDeployRightMotor = new TalonFX(MotorConstants.INTAKE_DEPLOY_RIGHT_MOTOR_ID);
  CANcoder intakeDeployCANCoder = new CANcoder(EncoderConstants.INTAKE_DEPLOY_CANCODER_ID);

  // CHANGE THESE VALUES
  double intakeHighestRange = 1;
  double intakeLowestRange = -1;
  
  /** Creates a new ExampleSubsystem. */
  public IntakeDeploySubsystem() {}



  public void deploySpeed(double speed, boolean XButton) {

    // if (XButton) {
    //   intakeDeployCANCoder.setPosition(0);
    // }

    // move only if encoder is between the high and low ranges
    if (intakeDeployCANCoder.getPosition().getValueAsDouble() > intakeLowestRange && intakeDeployCANCoder.getPosition().getValueAsDouble() < intakeHighestRange) {

        intakeDeployLeftMotor.set(-speed);
        intakeDeployRightMotor.set(speed);

        // move only if encoder is at lowest and speed is positive, or if encoder is at highest and speed is negative
    } else if ((intakeDeployCANCoder.getPosition().getValueAsDouble() < intakeLowestRange && speed > 0) || (intakeDeployCANCoder.getPosition().getValueAsDouble() > intakeHighestRange && speed < 0)) {

        intakeDeployLeftMotor.set(-speed);
        intakeDeployRightMotor.set(speed);

    } else {
        intakeDeployLeftMotor.set(0);
        intakeDeployRightMotor.set(0);
    }

    
  }
 
}
