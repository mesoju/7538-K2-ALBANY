// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.TurretSubsystems;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants.EncoderConstants;
import frc.robot.Constants.HoodConstants;
import frc.robot.Constants.MotorConstants;

import com.ctre.phoenix6.hardware.CANcoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;



public class TurretHoodSubsystem extends SubsystemBase {

  SparkMax turretHoodMotor = new SparkMax(MotorConstants.TURRET_HOOD_MOTOR_ID, MotorType.kBrushless);
  CANcoder turretHoodCANCoder = new CANcoder(EncoderConstants.HOOD_ENCODER_ID); 
  PIDController hoodPID = new PIDController(HoodConstants.kP, HoodConstants.kI, HoodConstants.kD);

  double encoderHighestRange = 3;
  double encoderLowestRange = HoodConstants.hoodMin;

  /** Creates a new TurretHoodSubsystem. */
  public TurretHoodSubsystem() {}

   private double encoderToDegrees() {
    double encoderPosition = turretHoodCANCoder.getPosition().getValueAsDouble();
    double percent = Math.abs( (encoderPosition - encoderHighestRange) / (encoderHighestRange - encoderLowestRange) );

    return HoodConstants.hoodMaxDeg - (HoodConstants.hoodMaxDeg - HoodConstants.hoodMinDeg) * percent;
  }

  public void setHoodAngle(double degrees) {
    degrees = MathUtil.clamp(degrees, HoodConstants.hoodMinDeg, HoodConstants.hoodMaxDeg);
    //if (degrees < HoodConstants.hoodMaxDeg && degrees > HoodConstants.hoodMinDeg) {
      hoodPID.calculate(encoderToDegrees(), degrees);
    //}
  }

  public void turretHoodAngle(double speed, boolean AButton, boolean BButton) {
    // System.out.println("Hood Encoder " + turretHoodCANCoder.getPosition());
    // System.out.println("Max " + encoderHighestRange);

    // move only if ender is between the high and low ranges
    if (turretHoodCANCoder.getPosition().getValueAsDouble() > encoderLowestRange && turretHoodCANCoder.getPosition().getValueAsDouble() < encoderHighestRange) {
        turretHoodMotor.set(speed);

        // move only if encoder is at lowest and speed is positive, or if encoder is at highest and speed is negative
    } else if ((turretHoodCANCoder.getPosition().getValueAsDouble() <= encoderLowestRange && speed > 0) || (turretHoodCANCoder.getPosition().getValueAsDouble() > encoderHighestRange && speed < 0)){
        turretHoodMotor.set(speed);
    } else {
        turretHoodMotor.set(0);
    }

    // Press to zero the hood (zero it by bringing it up to the top)
    if(AButton) {
      turretHoodCANCoder.setPosition(0);
      encoderHighestRange = HoodConstants.hoodMax;
    }
    
    // If you messed up on zeroing the hood
    if(BButton) {
        turretHoodCANCoder.setPosition(1);
        encoderHighestRange = 3;
    }
  }

  public void setHoodSpeed(double speed) {

  }

  // 32 - 58 HOOD ANGLES
  // 
  public void setHoodAngle() {

  }

  public void zeroHoodAngle() {

  }

  public void flattenHoodAngle() {

  }

  // /**
  //  * Example command factory method.
  //  *
  //  * @return a command
  //  */
  // public Command exampleMethodCommand() {
  //   // Inline construction of command goes here.
  //   // Subsystem::RunOnce implicitly requires `this` subsystem.
  //   return runOnce(
  //       () -> {
  //         /* one-time action goes here */
  //       });
  // }

  // /**
  //  * An example method querying a boolean state of the subsystem (for example, a digital sensor).
  //  *
  //  * @return value of some boolean subsystem state, such as a digital sensor.
  //  */
  // public boolean exampleCondition() {
  //   // Query some boolean state, such as a digital sensor.
  //   return false;
  // }

  // @Override
  // public void periodic() {
  //   // This method will be called once per scheduler run
  // }

  // @Override
  // public void simulationPeriodic() {
  //   // This method will be called once per scheduler run during simulation
  // }
}
