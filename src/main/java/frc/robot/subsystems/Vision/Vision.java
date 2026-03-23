// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.Vision;

import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.utility.pose2Dutility;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.LimelightHelpers;
import frc.robot.TunerConstants;
import frc.robot.Constants.CameraConfigs;

import java.util.Optional;

import frc.robot.Constants;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;


public class Vision extends SubsystemBase {

  private final CommandSwerveDrivetrain drivetrain;
  private final pose2Dutility poseUtility;

  private final String leftCamName = "limelight-left";
  private final String RightCamName = "limelight-right";

  private Optional<Alliance> alliance;
  private Alliance color;
  
  private boolean enabled = true;

  /** Creates a new Vision. */
  public Vision(CommandSwerveDrivetrain drivetrain, pose2Dutility poseUtility) {
    this.drivetrain = drivetrain;
    this.poseUtility = poseUtility;

    LimelightHelpers.setCameraPose_RobotSpace(RightCamName,
      CameraConfigs.LL_Forward,
      CameraConfigs.LL_Right,
      CameraConfigs.LL_Up,
      0,
      CameraConfigs.LL_Pitch,
      -90.0);

    LimelightHelpers.setCameraPose_RobotSpace(leftCamName,
      CameraConfigs.LL_Forward,
      -CameraConfigs.LL_Right,
      CameraConfigs.LL_Up,
      0,
      CameraConfigs.LL_Pitch,
      90.0);
  }

  @Override
  public void periodic() {
    if (this.enabled) {
      // This method will be called once per scheduler run
      double heading = drivetrain.getState().Pose.getRotation().getDegrees();
      //double heading = 0;

      LimelightHelpers.SetIMUMode(leftCamName, 2);
      LimelightHelpers.SetIMUMode(RightCamName, 2);
    
      LimelightHelpers.PoseEstimate leftEstimate =
        LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2("limelight-left");

      LimelightHelpers.PoseEstimate rightEstimate =
        LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2("limelight-right");

      boolean testTurretFromLocalization = false;
      boolean useLeft = false;
      
      if (leftEstimate.tagCount > 0) {
        // System.out.println("Left Limelight pose: " + leftEstimate.pose);
        // System.out.println("Left " + leftEstimate);
        LimelightHelpers.SetRobotOrientation(leftCamName, heading, 0, 0, 0, 0, 0);
        LimelightHelpers.setCameraPose_RobotSpace(leftCamName, heading, 0, 0, 0, 0, 0);
          

        drivetrain.setVisionMeasurementStdDevs(VecBuilder.fill(0.7, 0.7, 99999));
        drivetrain.addVisionMeasurement(
          leftEstimate.pose,
          leftEstimate.timestampSeconds
        );

        //useLeft = true;
        //testTurretFromLocalization = true;
      } else if (rightEstimate.tagCount > 0) {
        // System.out.println("Right Limelight pose: " + rightEstimate.pose);
        // System.out.println("Right " + rightEstimate);
        LimelightHelpers.SetRobotOrientation(RightCamName, heading, 0, 0, 0, 0, 0);
        LimelightHelpers.setCameraPose_RobotSpace(RightCamName, heading, 0, 0, 0, 0, 0);


        drivetrain.setVisionMeasurementStdDevs(VecBuilder.fill(0.7, 0.7, 99999));
        drivetrain.addVisionMeasurement(
          rightEstimate.pose,
          rightEstimate.timestampSeconds
        );

        //testTurretFromLocalization = true;
      }

      //if (testTurretFromLocalization) {
        //this.poseUtility.updatePose2D(useLeft ? leftEstimate.pose : rightEstimate.pose);

        this.poseUtility.updatePose2D(drivetrain.getState().Pose);

        double angle = this.poseUtility.getAngleToPositionTurret(this.color == Alliance.Blue ? Constants.FieldQuadrants.BlueAllyHub : Constants.FieldQuadrants.RedAllyHub);
        System.out.println("Turret Angle needed: "+angle);
      //}
    }
  }

  public void toggleLimelight(boolean enable) {
    this.enabled = enable;
  }

  public void setAlliance(Optional<Alliance> alliance) {
    this.alliance = alliance;

    if (this.alliance.isPresent()) {
        this.color = this.alliance.get();
    } else {
        this.color = Alliance.Blue;
    }
  }
}
