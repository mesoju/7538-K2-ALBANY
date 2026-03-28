// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.security.PublicKey;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static class OperatorConstants {
    public static final double Sonic_Hedgehog_Protein = 1.4; // Phillip's Constant 
    public static final double Oiler = 2.71; // Mr Oiled Up
  }

  public final class MotorConstants {

    public static final int SPINDEXER_MOTOR_ID = 18;

    public static final int FEEDER_MOTOR_ID = 19;
    
    public static final int TURRET_SHOOTER_MOTOR_ID = 20;
    public static final int TURRET_ROTATION_MOTOR_ID = 21;
    public static final int TURRET_HOOD_MOTOR_ID = 22; // REV SparkMax Motor
    public static final int INTAKE_DEPLOY_ID = 14;
    public static final int INTAKE_INDEXER_ID = 15;

    public static final double DEPLOY_SPEED = .22;

    public static final double INDEXER_SPEED = 0.57;

    }



  public final class EncoderConstants {
    public static final int INTAKE_DEPLOY_CANCODER_ID = 17;

    public static final int ROTATION_ENCODER_ID = 23;
    public static final int HOOD_ENCODER_ID = 24;
    
    public static final double ENCODER_ROTATIONS_TO_ONE_TURRET_ROTATION = 4.3333;
    public static final double ONE_ENCODER_ROTATION_TO_TURRET_DEGREES = 83.0769;
    //40 : 195
    // 20 : 1

    public static final int INTAKE_DEPLOY_ENCODER_ID = 17;

    public static final double DEPLOY_POS = 0.304932;

    public static final double INTAKE_TOLERANCE = .01;

    public static final double RETRACT_POS = 0;

    }

  public final class IntakeConstants {
    public static final double kG = 0.1; // Output needed to overcome gravity
    public static final double kS = 0.25; // Add 0.25 V output to overcome static friction
    public static final double kV = 0.12; // A velocity target of 1 rps results in 0.12 V output
    public static final double kA = 0.01; // An acceleration of 1 rps/s require 0.01 v output
    public static final double kP = 2.4; // An error of 1 rotation results in 2.4 V output
    public static final double kI = 0.01; // no output for integrated error
    public static final double kD = 0.1; // A velocity of 1 rps results in 0.1 V output

    public static final double CRUISEVELOCITY = 20;
    public static final double ACCELERATION = 40;
    public static final double JERK = 0;

    public static final double DEPLOYED = 4;
    public static final double RETRACTED = 0;

    public static final double SensorToMechanismRatio = 1;
    public static final double RotorToSensorRatio = 45.5;
  }
  
  public final class ShooterConstants {
    public static final double kG = 0.0; // Output needed to overcome gravity
    public static final double kS = 0.0; // Add 0.25 V output to overcome static friction
    public static final double kV = 0.13; // A velocity target of 1 rps results in 0.12 V output
    public static final double kA = 0.02; // An acceleration of 1 rps/s require 0.01 v output
    public static final double kP = 4.8; // An error of 1 rotation results in 2.4 V output
    public static final double kI = 0.0; // no output for integrated error
    public static final double kD = 0.15; // A velocity of 1 rps results in 0.1 V output

    public static final double CRUISEVELOCITY = 80;
    public static final double ACCELERATION = 40;
    public static final double JERK = 0;
  }

  public final class TurretConstants {
    public static final double kG = 0.0; // Output needed to overcome gravity
    public static final double kS = 0.4; // Add 0.25 V output to overcome static friction
    public static final double kV = 4; // A velocity target of 1 rps results in 0.12 V output
    public static final double kA = 0.2; // An acceleration of 1 rps/s require 0.01 v output
    public static final double kP = 120.0; // An error of 1 rotation results in 2.4 V output
    public static final double kI = 0.0; // no output for integrated error
    public static final double kD = 1.0; // A velocity of 1 rps results in 0.1 V output

    public static final double CRUISEVELOCITY = 8;
    public static final double ACCELERATION = 5;
    public static final double JERK = 0;

    public static final double SensorToMechanismRatio = 4.875;
    public static final double RotorToSensorRatio = 45.5;

    public static final double[] turretOffsets = {-0.12065, 24, -0.14605}; // {-4.75, 24, -5.25};
  }

  public final class HoodConstants {
    public static final double hoodMax = -0.2;
    public static final double hoodMin = -1.2;

    public static final double hoodMaxDeg = 58;
    public static final double hoodMinDeg = 32;
    
    public static final double kP = 4;
    public static final double kI = 0.05;
    public static final double kD = 0.00;
    public static final double[] voltageClamp = {-0.3, 0.3};

    public static final double[] shooterAngleBounds = {32, 58};
  }

  public final class CameraConfigs {
    public static final double LL_Right = 0.338537;
    public static final double LL_Forward = -0.07695;
    public static final double LL_Up = 0.225425;
    public static final double LL_Pitch = 10;
  }

  public final class FieldQuadrants {
    // in meters
    public static final double[] BlueAllyHub = {4.6228, 4.0259}; // X represents longest side! Y is the width relative to the driver.
    public static final double[] RedAllyHub = {4.6228, 4.0259};

    public static final double[] BlueAllyCornerLeft = {15.5354, 1};
    public static final double[] BlueAllyCornerRight = {1, 1};

    public static final double[] RedAllyCornerLeft = {15.5354, 1};
    public static final double[] RedAllyCornerRight = {1, 1};
  }

  public final class FieldDimensions {
    public static final double FieldX = 16.5354;
    public static final double FieldY = 8.0518;

  
  }

}
