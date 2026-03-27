// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.TurretSubsystems;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants.MotorConstants;
import frc.robot.Constants.ShooterConstants;
import frc.robot.Constants.TurretConstants;

import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;



public class TurretShooterSubsystem extends SubsystemBase {
    
  TalonFX shooterMotor = new TalonFX(MotorConstants.TURRET_SHOOTER_MOTOR_ID);
  MotorOutputConfigs coastMode = new MotorOutputConfigs().withNeutralMode(NeutralModeValue.Coast);

  /** Creates a new ExampleSubsystem. */
  public TurretShooterSubsystem() {
    Slot0Configs slot0Configs = new Slot0Configs();
    TalonFXConfiguration talonFXConfigs = new TalonFXConfiguration();

    slot0Configs.kG = ShooterConstants.kG;
    slot0Configs.kS = ShooterConstants.kS;
    slot0Configs.kV = ShooterConstants.kV;
    slot0Configs.kA = ShooterConstants.kA;
    slot0Configs.kP = ShooterConstants.kP;
    slot0Configs.kI = ShooterConstants.kI;
    slot0Configs.kD = ShooterConstants.kD;

    // Set Motion Magic settings
    var motionMagicConfigs = talonFXConfigs.MotionMagic;
    motionMagicConfigs.MotionMagicCruiseVelocity = ShooterConstants.CRUISEVELOCITY;
    motionMagicConfigs.MotionMagicAcceleration = ShooterConstants.ACCELERATION;

    shooterMotor.getConfigurator().apply(talonFXConfigs);
    shooterMotor.getConfigurator().apply(motionMagicConfigs);
    shooterMotor.getConfigurator().apply(slot0Configs);

    shooterMotor.getConfigurator().apply(coastMode);
  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber("turretSpeed", shooterMotor.getVelocity().getValueAsDouble());
  }

  public void setShooterSetpoint(double setpoint) {
    //double EncoderVelocity = shooterMotor.getVelocity().getValueAsDouble() > 1 ? shooterMotor.getVelocity().getValueAsDouble() : 0;
    SmartDashboard.putNumber("Setpoint", setpoint);

    setpoint = 70;

    MotionMagicVelocityVoltage m_request = new MotionMagicVelocityVoltage(setpoint);

    shooterMotor.setControl(m_request);

    //shooterMotor.set(0.8);
  }

  public void setShooterSpeed(double speed) {
    shooterMotor.set(speed);
  }
  

}
