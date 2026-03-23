// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.utility;

import java.util.Optional;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.Constants;
import frc.robot.Constants.TurretConstants;
import edu.wpi.first.math.util.Units;

/** Add your docs here. */
public class pose2Dutility {

    private Pose2d currentPose2d;
    private Pose2d turretPose2d;

    private Optional<Alliance> alliance;
    private Alliance color;

    private boolean isTurretBusy = false;
    private boolean areTagsDetected = false;

    // Constructors 
    public pose2Dutility() {
        this.alliance = DriverStation.getAlliance();
        this.currentPose2d = new Pose2d();

        
    }

    public pose2Dutility(Optional<Alliance> alliance) {
        this.alliance = alliance;
        setAllianceColor();
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
    public void updatePose2D(Pose2d visionPose2d) {
        currentPose2d = visionPose2d;
        double theta = currentPose2d.getRotation().getRadians();
        turretPose2d = new Pose2d(currentPose2d.getX() + Constants.TurretConstants.turretOffsets[0] * Math.cos(theta) - Constants.TurretConstants.turretOffsets[2] * Math.sin(theta),
         currentPose2d.getY() + Constants.TurretConstants.turretOffsets[0] * Math.sin(theta) + Constants.TurretConstants.turretOffsets[2] * Math.cos(theta),
          currentPose2d.getRotation());
    }

    public void updateAlliance(Optional<Alliance> alliance) {
        this.alliance = alliance;
        setAllianceColor();
    }

    public double getMagnitudeToPositionTurret(Double[] position) {
        double magnitude = -1;
        //double[] Pose2dPosition = {currentPose2d.getX(), currentPose2d.getY()};
        double[] hubOffsetRelativeToRobot = {turretPose2d.getX() - position[0], turretPose2d.getY() - position[1]};
        
        magnitude = Math.sqrt(Math.pow(position[0] - hubOffsetRelativeToRobot[0], 2) + Math.pow(position[1] - hubOffsetRelativeToRobot[1], 2));

        return magnitude;
    }

    public double getAngleToPositionTurret(double[] position) {
        double angle = -1;

        double robotAngle = turretPose2d.getRotation().getDegrees();
        double[] hubOffsetRelativeToRobot = {turretPose2d.getX() - position[0], turretPose2d.getY() - position[1]};

        double atanAngle = Math.atan2(hubOffsetRelativeToRobot[0], hubOffsetRelativeToRobot[1]);
        
        angle = Math.toDegrees(atanAngle) - robotAngle;

        return angle;
    }

    public Double getBestFieldGoalAngle() {
        //double[] coordinates = new double[2];
        Double angle = null;

        if (color != Alliance.Red) { // Our Alliance is Blue
            if (turretPose2d.getX() > Constants.FieldQuadrants.BlueAllyHub[0]) { // Past the hub, intent: shoot into alliance corners
                if (turretPose2d.getY() > Constants.FieldQuadrants.BlueAllyHub[1]) { // Left of hub, in the middle of the field
                    angle = getAngleToPositionTurret(Constants.FieldQuadrants.BlueAllyCornerLeft);
                } else { // Right of hub, in the middle of the field
                    angle = getAngleToPositionTurret(Constants.FieldQuadrants.BlueAllyCornerRight);
                }
            } else if (turretPose2d.getX() < Constants.FieldQuadrants.BlueAllyHub[0]) { // On Alliance's side, intent: aim at hub
                angle = getAngleToPositionTurret(Constants.FieldQuadrants.BlueAllyHub);
            }
        } else { // Our Alliance is Red
            if (turretPose2d.getX() < dimension.offsetLocationFromAlliance(color, Constants.FieldQuadrants.RedAllyHub)[0]) { // Past the hub, intent: shoot into alliance corners
                if (turretPose2d.getY() < dimension.offsetLocationFromAlliance(color, Constants.FieldQuadrants.RedAllyHub)[1]) { // Left of the hub, in the middle of the field (relative to driver)
                    angle = getAngleToPositionTurret(Constants.FieldQuadrants.RedAllyCornerLeft);
                } else { // Right of the hub, in the middle of the field (relative to driver)
                    angle = getAngleToPositionTurret(Constants.FieldQuadrants.RedAllyCornerRight);
                }
            } else { // On Alliance's side, intent: aim at hub
                angle = getAngleToPositionTurret(dimension.offsetLocationFromAlliance(color, Constants.FieldQuadrants.RedAllyHub));
            }
        }

        return angle;
        //return coordinates;
    }

    public Double[] getBestFieldGoalPosition() {
        Double[] position = {null, null, null};

        if (color == Alliance.Blue) { // Our Alliance is Blue
            if (turretPose2d.getX() > Constants.FieldQuadrants.BlueAllyHub[0]) { // Past the hub, intent: shoot into alliance corners
                if (turretPose2d.getY() > Constants.FieldQuadrants.BlueAllyHub[1]) { // Left of hub, in the middle of the field
                    position[0] = Constants.FieldQuadrants.BlueAllyCornerLeft[0];
                    position[1] = Constants.FieldQuadrants.BlueAllyCornerLeft[1];
                    position[2] = 0.0;
                } else { // Right of hub, in the middle of the field
                    position[0] = Constants.FieldQuadrants.BlueAllyCornerRight[0];
                    position[1] = Constants.FieldQuadrants.BlueAllyCornerRight[1];
                    position[2] = 0.0;
                }
            } else if (turretPose2d.getX() < Constants.FieldQuadrants.BlueAllyHub[0]) { // On Alliance's side, intent: aim at hub
                //angle = getAngleToPositionTurret(Constants.FieldQuadrants.BlueAllyHub);
                
                position[0] = Constants.FieldQuadrants.BlueAllyHub[0];
                position[1] = Constants.FieldQuadrants.BlueAllyHub[1];
                position[2] = 1.8288;
            }
        } else { // Our Alliance is Red
            if (turretPose2d.getX() < dimension.offsetLocationFromAlliance(color, Constants.FieldQuadrants.RedAllyHub)[0]) { // Past the hub, intent: shoot into alliance corners
                if (turretPose2d.getY() < dimension.offsetLocationFromAlliance(color, Constants.FieldQuadrants.RedAllyHub)[1]) { // Left of the hub, in the middle of the field (relative to driver)
                    double[] offsetLocation = dimension.offsetLocationFromAlliance(color, Constants.FieldQuadrants.RedAllyCornerLeft);

                    position[0] = offsetLocation[0];
                    position[1] = offsetLocation[1];
                    position[2] = 0.0;
                } else { // Right of the hub, in the middle of the field (relative to driver)
                    double[] offsetLocation = dimension.offsetLocationFromAlliance(color, Constants.FieldQuadrants.RedAllyCornerRight);

                    position[0] = offsetLocation[0];
                    position[1] = offsetLocation[1];
                    position[2] = 0.0;
                }
            } else { // On Alliance's side, intent: aim at hub
                //angle = getAngleToPositionTurret(dimension.offsetLocationFromAlliance(color, Constants.FieldQuadrants.RedAllyHub));

                double[] offsetLocation = dimension.offsetLocationFromAlliance(color, Constants.FieldQuadrants.RedAllyHub);

                position[0] = offsetLocation[0];
                position[1] = offsetLocation[1];
                position[2] = 1.8288;
            }
        }

        return position;
    }

    private Double getShooterAngleFromHoodAngle(double HoodAngle) {
        Double shooterAngle = null;

        shooterAngle = 90 - HoodAngle;

        return shooterAngle;
    }

    private Double getHoodAngleFromShooterAngle(double ShooterAngle) {
        Double hoodAngle = null;

        hoodAngle = 90 - ShooterAngle;

        return hoodAngle;
    }

    private Double getShooterAngleToPositionTurret(int arc, double distance, double velocity, double heightDisplacement) { // Returns degrees
        Double shooterAngle = null;
        double gravity = 9.81;

        if (arc == 0) { // Low arc
            shooterAngle = Math.atan(
                (velocity*velocity) + Math.sqrt(Math.pow(velocity, 4.0) - gravity * (gravity * (distance * distance) + 2 * heightDisplacement * (velocity * velocity)))
                / (gravity * distance)
            );
        } else { // High arc
            shooterAngle = Math.atan(
                (velocity*velocity) - Math.sqrt(Math.pow(velocity, 4.0) - gravity * (gravity * (distance * distance) + 2 * heightDisplacement * (velocity * velocity)))
                / (gravity * distance)
            );
        }

        shooterAngle = Units.radiansToDegrees(shooterAngle);

        return shooterAngle;
    }

    public Double getHoodAngleToPositionTurret(Double[] position) {
        Double hoodAngle = null;

        double distance = getMagnitudeToPositionTurret(position);
        Double shooterAngle = getShooterAngleToPositionTurret(1, distance, 60, position[2] - TurretConstants.turretOffsets[1]);

        hoodAngle = getHoodAngleFromShooterAngle(shooterAngle);

        return hoodAngle;

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
