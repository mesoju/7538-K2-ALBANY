// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.IntakeSubsystems;

import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.EncoderConstants;
import frc.robot.Constants.MotorConstants;
import frc.robot.Constants.IntakeConstants;;

public class IntakeDeploySubsystemV2 extends SubsystemBase {
  /** Creates a new IntakeDeploySubsystemV2. */
TalonFX intakeDeployLeftMotor = new TalonFX(MotorConstants.INTAKE_DEPLOY_LEFT_MOTOR_ID);
TalonFX intakeDeployRightMotor = new TalonFX(MotorConstants.INTAKE_DEPLOY_RIGHT_MOTOR_ID);
CANcoder intakeDeployCANCoder = new CANcoder(EncoderConstants.INTAKE_DEPLOY_CANCODER_ID);

MotorOutputConfigs brakeMode = new MotorOutputConfigs().withNeutralMode(NeutralModeValue.Brake);
  Double kG, kS, kV, kA, kP, kI, kD; 

  public IntakeDeploySubsystemV2() {
    var slot0Configs = new Slot0Configs();
    var talonFXConfigs = new TalonFXConfiguration();

    talonFXConfigs.Feedback.FeedbackRemoteSensorID = intakeDeployCANCoder.getDeviceID();
    talonFXConfigs.Feedback.FeedbackSensorSource = FeedbackSensorSourceValue.FusedCANcoder;
    talonFXConfigs.Feedback.SensorToMechanismRatio = IntakeConstants.SensorToMechanismRatio;
    talonFXConfigs.Feedback.RotorToSensorRatio = IntakeConstants.RotorToSensorRatio;

    slot0Configs.kG = IntakeConstants.kG;
    slot0Configs.kS = IntakeConstants.kS;
    slot0Configs.kV = IntakeConstants.kV;
    slot0Configs.kA = IntakeConstants.kA;
    slot0Configs.kP = IntakeConstants.kP;
    slot0Configs.kI = IntakeConstants.kI;
    slot0Configs.kD = IntakeConstants.kD;

    var motionMagicConfigs = talonFXConfigs.MotionMagic;
    motionMagicConfigs.MotionMagicCruiseVelocity = IntakeConstants.CRUISEVELOCITY;
    motionMagicConfigs.MotionMagicAcceleration = IntakeConstants.ACCELERATION;
    motionMagicConfigs.MotionMagicJerk = IntakeConstants.JERK;

    intakeDeployLeftMotor.getConfigurator().apply(talonFXConfigs);
    intakeDeployRightMotor.getConfigurator().apply(talonFXConfigs);
    intakeDeployLeftMotor.getConfigurator().apply(motionMagicConfigs);
    intakeDeployRightMotor.getConfigurator().apply(motionMagicConfigs);
    intakeDeployLeftMotor.getConfigurator().apply(slot0Configs);
    intakeDeployLeftMotor.getConfigurator().apply(brakeMode);
    intakeDeployRightMotor.getConfigurator().apply(slot0Configs);
    intakeDeployRightMotor.getConfigurator().apply(brakeMode);
  }

  public void intakePosition(double position) {
    final MotionMagicVoltage m_request = new MotionMagicVoltage(0);
    //double CANCoderPosition = intakeDeployCANCoder.getPosition().getValueAsDouble();

    intakeDeployLeftMotor.setControl(m_request.withPosition(position));
    intakeDeployRightMotor.setControl(new Follower(intakeDeployLeftMotor.getDeviceID(), MotorAlignmentValue.Opposed));
  }

  
}
