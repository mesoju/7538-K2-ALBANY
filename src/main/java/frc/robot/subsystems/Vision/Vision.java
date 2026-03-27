// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.Vision;

import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.utility.pose2Dutility;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.LimelightHelpers;
import frc.robot.Constants.CameraConfigs;

import java.util.Optional;

import com.ctre.phoenix6.Utils;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


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

      LimelightHelpers.SetIMUMode(leftCamName, 1);
      LimelightHelpers.SetIMUMode(RightCamName, 1);

      LimelightHelpers.SetRobotOrientation(leftCamName, heading, 0, 0, 0, 0, 0);          
      LimelightHelpers.SetRobotOrientation(RightCamName, heading, 0, 0, 0, 0, 0);
    
      LimelightHelpers.PoseEstimate leftEstimate =
        LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2("limelight-left");

      LimelightHelpers.PoseEstimate rightEstimate =
        LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2("limelight-right");
      
      if (leftEstimate.tagCount > 0) {
        drivetrain.setVisionMeasurementStdDevs(VecBuilder.fill(0.7, 0.7, 99999));
        drivetrain.addVisionMeasurement(
          leftEstimate.pose, 
          Utils.fpgaToCurrentTime( leftEstimate.timestampSeconds )
        );

        SmartDashboard.putString("leftCamPose", "X: "+leftEstimate.pose.getY()+" | Y: "+leftEstimate.pose.getX());
      } else if (rightEstimate.tagCount > 0) {
        drivetrain.setVisionMeasurementStdDevs(VecBuilder.fill(0.7, 0.7, 99999));
        drivetrain.addVisionMeasurement(
          rightEstimate.pose,
          Utils.fpgaToCurrentTime( rightEstimate.timestampSeconds )
        );

        SmartDashboard.putString("righCamPose", "X: "+rightEstimate.pose.getY()+" | Y: "+rightEstimate.pose.getX());

      }
        this.poseUtility.updatePose2D(drivetrain.getState().Pose);

        //double angle = this.poseUtility.getAngleToPositionTurret(this.color == Alliance.Blue ? Constants.FieldQuadrants.BlueAllyHub : Constants.FieldQuadrants.RedAllyHub);
        double angle = this.poseUtility.getBestFieldGoalAngle();
        SmartDashboard.putNumber("DesiredTurretAngle", -angle);
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
