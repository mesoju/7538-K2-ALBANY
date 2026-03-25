// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.TurretSubsystems;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.networktables.DoubleEntry;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
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

  public static final NetworkTableInstance networkInstance = NetworkTableInstance.getDefault();
  private double targetVelocity;

  
  SimpleMotorFeedforward m_Feedforward = 
    new SimpleMotorFeedforward(
      ShooterConstants.kFlywheelKs, 
      ShooterConstants.kFlywheelKv, 
      ShooterConstants.kFlywheelKa
    );

    public static class ShooterTable {
    private static final NetworkTable shooterTable = networkInstance.getTable("shooterTable");

    public static final DoublePublisher velocity = shooterTable.getDoubleTopic("Shooter Velocity").publish();
    public static final DoublePublisher velocityGoal = shooterTable.getDoubleTopic("Shooter Velocity Goal").publish();
    public static final DoubleEntry kS = shooterTable.getDoubleTopic("S (Shooter)").getEntry(TurretConstants.kS);; // Add 0.25 V output to overcome static friction
    public static final DoubleEntry kV = shooterTable.getDoubleTopic("V (Shooter)").getEntry(TurretConstants.kV);; // A velocity target of 1 rps results in 0.12 V output
    public static final DoubleEntry kA = shooterTable.getDoubleTopic("A (Shooter)").getEntry(TurretConstants.kA);; // An acceleration of 1 rps/s requires 0.01 V output
    public static final DoubleEntry kP = shooterTable.getDoubleTopic("P (Shooter)").getEntry(TurretConstants.kP);
    public static final DoubleEntry kI = shooterTable.getDoubleTopic("I (Shooter)").getEntry(TurretConstants.kI);
    public static final DoubleEntry kD = shooterTable.getDoubleTopic("D (Shooter)").getEntry(TurretConstants.kD);

    // set Motion Magic settings
    public static final DoubleEntry kMotionMagicCruiseVelocity = shooterTable.getDoubleTopic("MM Velocity (Shooter)").getEntry(TurretConstants.CRUISEVELOCITY);; // Target cruise velocity of 80 rps
    public static final DoubleEntry kMotionMagicAcceleration = shooterTable.getDoubleTopic("MM Acceleration (Shooter)").getEntry(TurretConstants.ACCELERATION);; // Target acceleration of 160 rps/s (0.5 seconds)
    public static final DoubleEntry kMotionMagicJerk = shooterTable.getDoubleTopic("MM Jerk (Shooter)").getEntry(TurretConstants.JERK);; // Target jerk of 1600 rps/s/s (0.1 seconds)

    public static final void init() {
        kS.set(kS.get());
        kV.set(kV.get());
        kA.set(kA.get());
        kP.set(kP.get());
        kI.set(kI.get());
        kD.set(kD.get());

        kMotionMagicCruiseVelocity.set(kMotionMagicCruiseVelocity.get());
        kMotionMagicAcceleration.set(kMotionMagicAcceleration.get());
        kMotionMagicJerk.set(kMotionMagicJerk.get());
    }
  }

  /** Creates a new ExampleSubsystem. */
  public TurretShooterSubsystem() {
    ShooterTable.init();
    configureMotors();
  }

  public void configureMotors() {
    Slot0Configs slot0Configs = new Slot0Configs();
    TalonFXConfiguration talonFXConfigs = new TalonFXConfiguration();

    // slot0Configs.kG = TurretConstants.kG;
    // slot0Configs.kS = TurretConstants.kS;
    // slot0Configs.kV = TurretConstants.kV;
    // slot0Configs.kA = TurretConstants.kA;
    // slot0Configs.kP = TurretConstants.kP;
    // slot0Configs.kI = TurretConstants.kI;
    // slot0Configs.kD = TurretConstants.kD;

    // // Set Motion Magic settings
    // var motionMagicConfigs = talonFXConfigs.MotionMagic;
    // motionMagicConfigs.MotionMagicCruiseVelocity = TurretConstants.CRUISEVELOCITY;
    // motionMagicConfigs.MotionMagicAcceleration = TurretConstants.ACCELERATION;

    slot0Configs.kS = ShooterTable.kS.get(); // Add 0.25 V output to overcome static friction
    slot0Configs.kV = ShooterTable.kV.get(); // A velocity target of 1 rps results in 0.12 V output
    slot0Configs.kA = ShooterTable.kA.get(); // An acceleration of 1 rps/s requires 0.01 V output
    slot0Configs.kP = ShooterTable.kP.get(); // An error of 1 rps results in 0.11 V output
    slot0Configs.kI = ShooterTable.kI.get(); // no output for integrated error
    slot0Configs.kD = ShooterTable.kD.get(); // no output for error derivative

    // set Motion Magic settings
    var motionMagicConfigs = talonFXConfigs.MotionMagic;
    motionMagicConfigs.MotionMagicAcceleration = ShooterTable.kMotionMagicAcceleration.get(); // Target acceleration of 400 rps/s (0.25 seconds to max)
    motionMagicConfigs.MotionMagicJerk = ShooterTable.kMotionMagicJerk.get(); // Target jerk of 4000 rps/s/s (0.1 seconds)

    shooterMotor.getConfigurator().apply(talonFXConfigs);
    shooterMotor.getConfigurator().apply(motionMagicConfigs);
    shooterMotor.getConfigurator().apply(slot0Configs);

    shooterMotor.getConfigurator().apply(coastMode);
  }

  @Override
  public void periodic() {
    ShooterTable.velocity.set(shooterMotor.getVelocity().getValueAsDouble());
    ShooterTable.velocityGoal.set(targetVelocity);
  }

  public void setShooterSetpoint(double setpoint) {
    //double EncoderVelocity = shooterMotor.getVelocity().getValueAsDouble() > 1 ? shooterMotor.getVelocity().getValueAsDouble() : 0;
    targetVelocity = setpoint;
    MotionMagicVelocityVoltage m_request = new MotionMagicVelocityVoltage(setpoint);

    shooterMotor.setControl(m_request);
  }
  

}
