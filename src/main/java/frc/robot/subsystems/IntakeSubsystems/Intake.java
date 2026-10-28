// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.IntakeSubsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.EncoderConstants;
import frc.robot.Constants.MotorConstants;

public class Intake extends SubsystemBase {
  TalonFX intakeDeploy = new TalonFX(MotorConstants.INTAKE_DEPLOY_ID);
  TalonFX intakeIndexer = new TalonFX(MotorConstants.INTAKE_INDEXER_ID);
  CANcoder intakeDeployCANCoder = new CANcoder(EncoderConstants.INTAKE_DEPLOY_ENCODER_ID);

  TalonFXConfiguration brakeConfig = new TalonFXConfiguration();
  TalonFXConfiguration freeConfig = new TalonFXConfiguration();

  /** Creates a new Intake. */
  public Intake() {
    brakeConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;
    freeConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;
  }

  public void setDeployBrakes(boolean toggle) {
    if (toggle) {
      intakeDeploy.getConfigurator().apply(brakeConfig);
    } else {
      intakeDeploy.getConfigurator().apply(freeConfig);
    }
  };

  public void setDeploy(double speed) {
    intakeDeploy.set(speed);
  };

  public void setIndexer(double speed) {
    intakeIndexer.set(speed);
  };

  public double getEncoder() {
    double encoderPosition = intakeDeployCANCoder.getAbsolutePosition().getValueAsDouble();
    return encoderPosition;
  }

  @Override
  public void periodic() {
    double encoderPosition = intakeDeployCANCoder.getAbsolutePosition().getValueAsDouble();
    SmartDashboard.putNumber("intakeDeployCANCoder", encoderPosition);
  }
}
