// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.utility;

import java.util.Optional;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.subsystems.CommandSwerveDrivetrain;

/** Add your docs here. */
public class pose2Dutility {

    private final CommandSwerveDrivetrain drivetrain;

    private Pose2d currentPose2d;
    private Pose2d turretPose2d;

    private Optional<Alliance> alliance;
    private Alliance color;

    private boolean isTurretBusy = false;
    private boolean areTagsDetected = false;

    // Constructors 
    public pose2Dutility(CommandSwerveDrivetrain drivetrain) {
        this.drivetrain = drivetrain;
        this.alliance = DriverStation.getAlliance();
        this.currentPose2d = new Pose2d();
    }

    // Private Methods
    private void setAllianceColor() {
        if (this.alliance.isPresent()) {
            this.color = this.alliance.get();
        } else {
            this.color = Alliance.Blue;
        }
    }

    // Public Methods
    // Set Instance Variables
    public void updatePose2D(Pose2d visionPose2d) {
        currentPose2d = visionPose2d;
        double theta = currentPose2d.getRotation().getRadians();
        // #2
        // turretPose2d = new Pose2d(
        //     currentPose2d.getX() + Constants.TurretConstants.turretOffsets[0] * Math.cos(theta) - Constants.TurretConstants.turretOffsets[2] * Math.cos(theta),
        //     currentPose2d.getY() + Constants.TurretConstants.turretOffsets[0] * Math.sin(theta) + Constants.TurretConstants.turretOffsets[2] * Math.sin(theta),
        //     currentPose2d.getRotation()
        // );

        turretPose2d = currentPose2d;


        SmartDashboard.putString("TurretPose", "X: "+turretPose2d.getX()+" | Y: "+turretPose2d.getY()+" | Yaw: "+turretPose2d.getRotation().getDegrees());
    }

    public void updateAlliance(Optional<Alliance> alliance) {
        this.alliance = alliance;
        setAllianceColor();
    }

    // Calculations
    public double getMagnitudeToPositionTurret(Double[] position) {
        double magnitude = -1;
        //double[] Pose2dPosition = {currentPose2d.getX(), currentPose2d.getY()};
        double[] hubOffsetRelativeToRobot = {turretPose2d.getY() - position[0], turretPose2d.getX() - position[1]};
        
        magnitude = Math.sqrt(Math.pow(position[0] - hubOffsetRelativeToRobot[0], 2) + Math.pow(position[1] - hubOffsetRelativeToRobot[1], 2));

        return magnitude;
    }

    public double normalizeAngle(double angle) {
        angle = angle % 360;

        if (angle > 180) {
            angle -= 360;
        } else if (angle < -180) {
            angle += 360;
        }

        return angle;
    }

    public Double getAngleToPositionTurret(Double[] position) {

        if (position[0] != null && position[1] != null) {

            double dx = position[0] - turretPose2d.getY();
            double dy = position[1] - turretPose2d.getX();

            Rotation2d angle = Rotation2d.fromRadians(Math.atan2(dx, dy) - Math.toRadians(-75)); // #1
            Rotation2d relative = angle.minus(turretPose2d.getRotation());

            return relative.getDegrees();
        }

        return 0.0;
    }

    // Returns a double: encoder_velocity, where encoder_velocity is the best setpoint motor velocity given distance.
    public double getBestVelocitySetpoint() {
        double setpoint = 0.0;

        Double[] bestPosition = getBestFieldGoalPosition(); // Our target where we should aim at.

        if (bestPosition[3] != null) { // We are given an automatically generated setpoint and we shouldn't use a variabled setpoint.
            setpoint = bestPosition[3];
        } else { // Use dynamic variable setpoint. 
            double magnitude = getMagnitudeToPositionTurret(bestPosition);
            if (magnitude != -1) {
                setpoint = 0.249804*Math.pow(magnitude,3)-0.425772*Math.pow(magnitude,2)-0.423057*magnitude+46.98942; // THANK YOU MR. MYERS!! goated physics teacher
            }
        }

        return setpoint;
    }

    // Returns a Double: angle, where angle is in degrees and represents best possible angle given robot pose.
    public Double getBestFieldGoalAngle() {
        Double[] position = getBestFieldGoalPosition();
        return getAngleToPositionTurret(position);
    }

    // Returns a Double[]: position, where {x, y, h, encoder_velocity} and position represents best possible target given robot pose.
    public Double[] getBestFieldGoalPosition() {
        Double[] position = {null, null, null, null};

        if (color == Alliance.Blue) { // Our Alliance is Blue
            // if (turretPose2d.getX() > Constants.FieldQuadrants.BlueAllyHub[0]) { // Past the hub, intent: shoot into alliance corners
            //     if (turretPose2d.getY() > Constants.FieldQuadrants.BlueAllyHub[1]) { // Left of hub, in the middle of the field
            //         position[0] = Constants.FieldQuadrants.BlueAllyCornerLeft[0];
            //         position[1] = Constants.FieldQuadrants.BlueAllyCornerLeft[1];
            //         position[2] = 0.0;
            //         position[3] = 60.0; // Use encoder velocity setpoint
            //     } else { // Right of hub, in the middle of the field
            //         position[0] = Constants.FieldQuadrants.BlueAllyCornerRight[0];
            //         position[1] = Constants.FieldQuadrants.BlueAllyCornerRight[1];
            //         position[2] = 0.0;
            //         position[3] = 60.0; // Use encoder velocity setpoint
            //     }
            // } else if (turretPose2d.getX() < Constants.FieldQuadrants.BlueAllyHub[0]) { // On Alliance's side, intent: aim at hub
            //     //angle = getAngleToPositionTurret(Constants.FieldQuadrants.BlueAllyHub);
                
            //     position[0] = Constants.FieldQuadrants.BlueAllyHub[0];
            //     position[1] = Constants.FieldQuadrants.BlueAllyHub[1];
            //     position[2] = 1.8288;
            //     position[3] = null; // Attempt to use velocity formula calibrated for hub.
            // }

                position[0] = Constants.FieldQuadrants.BlueAllyHub[0];
                position[1] = Constants.FieldQuadrants.BlueAllyHub[1];
                position[2] = 1.8288;
                position[3] = null; // Attempt to use velocity formula calibrated for hub.
        } else { // Our Alliance is Red
            double[] RedAllyHubCached = dimension.offsetLocationFromAlliance(color, Constants.FieldQuadrants.RedAllyHub);
            // if (turretPose2d.getX() < RedAllyHubCached[0]) { // Past the hub, intent: shoot into alliance corners
            //     if (turretPose2d.getY() < RedAllyHubCached[1]) { // Left of the hub, in the middle of the field (relative to driver)
            //         double[] offsetLocation = dimension.offsetLocationFromAlliance(color, Constants.FieldQuadrants.RedAllyCornerLeft);

            //         position[0] = offsetLocation[0];
            //         position[1] = offsetLocation[1];
            //         position[2] = 0.0;
            //         position[3] = 60.0; // Use encoder velocity setpoint
            //     } else { // Right of the hub, in the middle of the field (relative to driver)
            //         double[] offsetLocation = dimension.offsetLocationFromAlliance(color, Constants.FieldQuadrants.RedAllyCornerRight);

            //         position[0] = offsetLocation[0];
            //         position[1] = offsetLocation[1];
            //         position[2] = 0.0;
            //         position[3] = 60.0; // Use encoder velocity setpoint
            //     }
            // } else { // On Alliance's side, intent: aim at hub
            //     //angle = getAngleToPositionTurret(dimension.offsetLocationFromAlliance(color, Constants.FieldQuadrants.RedAllyHub));

            //     double[] offsetLocation = RedAllyHubCached;

            //     position[0] = offsetLocation[0];
            //     position[1] = offsetLocation[1];
            //     position[2] = 1.8288;
            //     position[3] = null; // Attempt to  use velocity formula calibrated for hub. 
            // }

                double[] offsetLocation = RedAllyHubCached;

                position[0] = offsetLocation[0];
                position[1] = offsetLocation[1];
                position[2] = 1.8288;
                position[3] = null; // Attempt to  use velocity formula calibrated for hub. 
        }

        return position;
    }

    public void setTagDetected(boolean areTagsDetected) {
        this.areTagsDetected = areTagsDetected;
    }

    public boolean getTagsDetected() {
        return areTagsDetected;
    }

    public void resetPose2d() {
        this.currentPose2d = null;
        this.turretPose2d = null;
    }
}
