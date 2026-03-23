// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.TurretSubsystems;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants.MotorConstants;
import frc.robot.Constants.TurretConstants;
import frc.robot.Constants.EncoderConstants;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.SensorDirectionValue;


public class TurretRotationSubsystem extends SubsystemBase {

  TalonFX turretRotationMotor = new TalonFX(MotorConstants.TURRET_ROTATION_MOTOR_ID, CANBus.roboRIO());
  CANcoder turretRotationCANCoder = new CANcoder(EncoderConstants.ROTATION_ENCODER_ID, CANBus.roboRIO());
  DigitalInput ls_0 = new DigitalInput(0);
  DigitalInput ls_1 = new DigitalInput(1);

  private Double turretRotationLowerBound = null;
  private Double turretRotationUpperBound = null;

  private boolean isTurretBusy = true;

  /** Creates a new TurretRotationSubsystem. */
  public TurretRotationSubsystem() {
    Slot0Configs slot0Configs = new Slot0Configs();
    TalonFXConfiguration talonFXConfigs = new TalonFXConfiguration();
    CANcoderConfiguration cancoderConfigs = new CANcoderConfiguration();

    talonFXConfigs.Feedback.FeedbackRemoteSensorID = turretRotationCANCoder.getDeviceID();
    talonFXConfigs.Feedback.FeedbackSensorSource = FeedbackSensorSourceValue.RemoteCANcoder;
    talonFXConfigs.Feedback.SensorToMechanismRatio = TurretConstants.SensorToMechanismRatio;
    talonFXConfigs.Feedback.RotorToSensorRatio = TurretConstants.RotorToSensorRatio;

    talonFXConfigs.MotorOutput.Inverted = InvertedValue.Clockwise_Positive; // Since the gears are inverted, we should assume CW+.

    slot0Configs.kG = TurretConstants.kG;
    slot0Configs.kS = TurretConstants.kS;
    slot0Configs.kV = TurretConstants.kV;
    slot0Configs.kA = TurretConstants.kA;
    slot0Configs.kP = TurretConstants.kP;
    slot0Configs.kI = TurretConstants.kI;
    slot0Configs.kD = TurretConstants.kD;

    cancoderConfigs.MagnetSensor.SensorDirection = SensorDirectionValue.Clockwise_Positive;

    // Set Motion Magic settings
    var motionMagicConfigs = talonFXConfigs.MotionMagic;
    motionMagicConfigs.MotionMagicCruiseVelocity = TurretConstants.CRUISEVELOCITY;
    motionMagicConfigs.MotionMagicAcceleration = TurretConstants.ACCELERATION;
    //motionMagicConfigs.MotionMagicJerk = TurretConstants.JERK;

    turretRotationMotor.getConfigurator().apply(talonFXConfigs);
    turretRotationMotor.getConfigurator().apply(motionMagicConfigs);
    turretRotationMotor.getConfigurator().apply(slot0Configs);
    turretRotationCANCoder.getConfigurator().apply(cancoderConfigs);
  }

  public void forceRotationMotorSpeed(double speed) {
    turretRotationMotor.set(speed);
    
    //System.out.println(turretRotationCANCoder.getPosition());
  }

  public void setRotationMotorSpeed(double speed) {
    if (!isTurretBusy) {
      if (turretRotationLowerBound != null && turretRotationUpperBound != null) {
          if ((turretRotationCANCoder.getPosition().getValueAsDouble() < turretRotationUpperBound && (turretRotationCANCoder.getPosition().getValueAsDouble() > turretRotationLowerBound))) {
            turretRotationMotor.set(speed);
          } else {
            turretRotationMotor.set(0);
          }
        } else {
          turretRotationMotor.set(speed);
        }
    }
  }

  public void setTurretAngle(double degrees) {
    degrees = -degrees;
    if (!isTurretBusy) {
      final MotionMagicVoltage m_request = new MotionMagicVoltage(0);
      double rotation = Units.degreesToRotations(degrees);
      // set target position to # rotations
      turretRotationMotor.setControl(m_request.withPosition(rotation));
    }
  }

  public void zeroEncoderFromInit() {
    isTurretBusy = true;

    // Case 1: Both limit switches are hit at the same times -> Successful homing
    // Case 2: Limit switch 0 is detected -> Rehome in other direction until both limit switches are hit simultaneously
    // Case 3: Limit switch 1 is detected -> Rehome in other direction until both limit switches are hit simultaneously

    forceRotationMotorSpeed(0.5); // Initially go CCW until a limit switch is hit.
    while (isTurretBusy) {
      
      if (!ls_0.get() && !ls_1.get()) { // Case 1
        forceRotationMotorSpeed(0);
        isTurretBusy = false;
      } else if (!ls_0.get() || !ls_1.get()) { // Case 2 & 3
        forceRotationMotorSpeed(-0.15);
      }

    }

    // while (isTurretBusy) {
    //   if (!ls_0.get()) {
    //     isTurretBusy = false;
    //     forceRotationMotorSpeed(0);
    //   }
    // }

    new Thread(() -> {
    try {

      Thread.sleep(200);
      turretRotationCANCoder.setPosition(0);

    } catch (InterruptedException e) {
        turretRotationCANCoder.setPosition(0);
        e.printStackTrace();
        
    }
    }).start();
  }
  
  @Override
  public void periodic() {
    
    SmartDashboard.putBoolean("TurretLimitSwitch0", ls_0.get());
    SmartDashboard.putBoolean("TurretLimitSwitch1", ls_1.get());
    SmartDashboard.putNumber("EncoderValue", Units.rotationsToDegrees(turretRotationMotor.getPosition().getValueAsDouble()));

  }
}
