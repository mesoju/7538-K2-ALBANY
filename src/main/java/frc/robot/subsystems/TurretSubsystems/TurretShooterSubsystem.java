// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.TurretSubsystems;

import edu.wpi.first.math.controller.BangBangController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants.MotorConstants;
import frc.robot.Constants.ShooterConstants;

import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;



public class TurretShooterSubsystem extends SubsystemBase {
    
  TalonFX shooterMotor = new TalonFX(MotorConstants.TURRET_SHOOTER_MOTOR_ID);
  MotorOutputConfigs coastMode = new MotorOutputConfigs().withNeutralMode(NeutralModeValue.Coast);
  BangBangController Kaden = new BangBangController();
  double m_encoder = shooterMotor.getPosition().getValueAsDouble();
  

  SimpleMotorFeedforward m_Feedforward = 
    new SimpleMotorFeedforward(
      ShooterConstants.kFlywheelKs, 
      ShooterConstants.kFlywheelKv, 
      ShooterConstants.kFlywheelKa
    );

  /** Creates a new ExampleSubsystem. */
  public TurretShooterSubsystem() {
    shooterMotor.getConfigurator().apply(coastMode);
    
  }

  @Override
  public void periodic() {
    m_encoder = shooterMotor.getPosition().getValueAsDouble();
    SmartDashboard.putData(Kaden);
    
  }


  public void shooterspeed(double setpoint) {
    double EncoderVelocity = shooterMotor.getVelocity().getValueAsDouble() > 1 ? shooterMotor.getVelocity().getValueAsDouble() : 0;
    SmartDashboard.putNumber("EncoderVelocity", shooterMotor.getVelocity().getValueAsDouble());
    double bangOutput = Kaden.calculate(EncoderVelocity, setpoint) * 12;
    shooterMotor.setVoltage(bangOutput + 0.9 * m_Feedforward.calculate(setpoint));
  }
  

}
