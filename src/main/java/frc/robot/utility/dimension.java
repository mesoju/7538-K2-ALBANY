// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.utility;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.Constants;

/** Add your docs here. */
public class dimension {
    public static double[] offsetLocationFromAlliance(Alliance alliance, double[] vector2Location) {
        if (alliance == Alliance.Red) {
            double[] offsetLocation = {-1, -1};

            offsetLocation[0] = Constants.FieldDimensions.FieldX - vector2Location[0];
            offsetLocation[1] = Constants.FieldDimensions.FieldY - vector2Location[1];

            return offsetLocation;
        } else {
            return vector2Location;
        }
    }
}
