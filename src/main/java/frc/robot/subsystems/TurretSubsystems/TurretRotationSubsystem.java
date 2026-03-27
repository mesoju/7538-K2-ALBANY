// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.TurretSubsystems;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DigitalInput;
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
  private boolean isTurretHoming = true;
  private Double homeDeadband = null;

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

    turretRotationMotor.getConfigurator().apply(talonFXConfigs);
    turretRotationMotor.getConfigurator().apply(motionMagicConfigs);
    turretRotationMotor.getConfigurator().apply(slot0Configs);
    turretRotationCANCoder.getConfigurator().apply(cancoderConfigs);
  }

  public void forceRotationMotorSpeed(double speed) {
    turretRotationMotor.set(speed);
  }

  public void setRotationMotorSpeed(double speed) {
    if (!isTurretHoming) {
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
    // // Let's not kill our robot through strangulation :P
    // degrees = MathUtil.clamp(degrees, -180, 180);
    
    // if (!isTurretHoming) { // Make sure turret is not currently in homing process
    //   final MotionMagicVoltage m_request = new MotionMagicVoltage(0); // Create a new voltage request
    //   double rotation = Units.degreesToRotations(degrees); // Voltage request based on position from rotations. Encoder reads 1:1 with mechanism.
    //   // set target position to # rotations
    //   turretRotationMotor.setControl(m_request.withPosition(rotation));
    // }
  }

  public void zeroEncoderFromInit() {
    isTurretBusy = true;
    isTurretHoming = true;
    homeDeadband = null;
    boolean[] limitSwitches = {true, true};

    // Case 1: Both limit switches are hit at the same times -> Successful homing
    // Case 2: Limit switch 0 is detected -> Rehome in other direction until both limit switches are hit simultaneously
    // Case 3: Limit switch 1 is detected -> Rehome in other direction until both limit switches are hit simultaneously
    // * Add an encoder deadband of approximately 5 deg

    forceRotationMotorSpeed(0.5); // Initially go CCW until a limit switch is hit.
    while (isTurretBusy) {
      
        if (!ls_0.get() && !ls_1.get()) { // Case 1 (simultaneously pressed with no deadband)
          isTurretBusy = false;
          forceRotationMotorSpeed(0);
          homeDeadband = null;
        } else if ((!ls_0.get() || !ls_1.get()) && homeDeadband == null) { // Case 2 & 3, then apply deadband
          homeDeadband = turretRotationCANCoder.getPosition().getValueAsDouble();
          limitSwitches[0] = ls_0.get();
          limitSwitches[1] = ls_1.get();
        } else if (homeDeadband != null && (Units.rotationsToDegrees( Math.abs(turretRotationCANCoder.getPosition().getValueAsDouble() - homeDeadband) ) > 5.0 ) ) {
          limitSwitches[0] = true;
          limitSwitches[1] = true;
          homeDeadband = null;
          forceRotationMotorSpeed(-0.2);
        } else if (homeDeadband != null && (Units.rotationsToDegrees( Math.abs(turretRotationCANCoder.getPosition().getValueAsDouble() - homeDeadband)) <= 5.0 ) && (!ls_0.get() || !ls_0.get())) {
          limitSwitches[0] = ls_0.get();
          limitSwitches[1] = ls_1.get();

          if (!limitSwitches[0] && !limitSwitches[1]) { // Both limit switches are hit within the allocated deadband
            isTurretBusy = false;
            forceRotationMotorSpeed(0);
            homeDeadband = null;
          }
        }

    }

    new Thread(() -> {
    try {
      // We should allow the thread to sleep for a second before applying changes to account for overshoot and not apply encoder values from muddy data.
      Thread.sleep(500);
      turretRotationCANCoder.setPosition(0);
      isTurretHoming = false;

    } catch (InterruptedException e) {
        turretRotationCANCoder.setPosition(0);
        e.printStackTrace();
        isTurretHoming = false;
        
    }
    }).start();
  }
  
  @Override
  public void periodic() {
    // I love elastic.
    SmartDashboard.putBoolean("TurretLimitSwitch0", ls_0.get());
    SmartDashboard.putBoolean("TurretLimitSwitch1", ls_1.get());
    SmartDashboard.putNumber("EncoderValue", Units.rotationsToDegrees(turretRotationMotor.getPosition().getValueAsDouble()));

  }
}
