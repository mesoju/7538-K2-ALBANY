// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/*         Team 7538 Driver Controls
 * 
 *           Driver Controller
 * 
 *      A Button        - Zero Hood Angle
 *      B Button        - Undo Hood Zero
 *      X Button        - Nothing
 *      Y Button        - Nothing
 * 
 *    Left Bumper       - Intake Spin
 *    Left Trigger      - Reverse Turret Shoot, Reverse Feeder, Reverse Spindexer
 *    Right Bumper      - Nothing
 *    Right Trigger     - Turret Shoot, Feeder Spin, Spindexer Spin
 * 
 *    Left DriverController     - Swerve Rotate
 *    Right DriverController    - Swerve Drive
 * 
 *    Dpad Left & Right - Turret Rotation
 *    Dpad Up & Down    - Turret Hood Angle
 */   


import static edu.wpi.first.units.Units.*;

import java.util.Optional;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

// Subsystems
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.SpindexerSubsystem;
import frc.robot.subsystems.IntakeSubsystems.IntakeDeploySubsystem;
import frc.robot.subsystems.IntakeSubsystems.IntakeSubsystem;
import frc.robot.subsystems.TurretSubsystems.TurretRotationSubsystem;
import frc.robot.subsystems.TurretSubsystems.TurretShooterSubsystem;
import frc.robot.subsystems.CommandSwerveDrivetrain;
// import frc.robot.subsystems.Vision;
import frc.robot.subsystems.Vision.Vision;
import frc.robot.utility.pose2Dutility;
//Commands
import frc.robot.commands.FeederCommands;
import frc.robot.commands.SpindexerCommands;
import frc.robot.commands.IntakeCommands.IntakeDeployCommand;
import frc.robot.commands.IntakeCommands.IntakeCommand;
import frc.robot.commands.TurretCommands.TurretRotationCommands;
import frc.robot.commands.TurretCommands.TurretShooterCommands;

public class RobotContainer {
private double MaxSpeed = 1.0 * TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
    private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity

    /* Setting up bindings for necessary control of the swerve drive platform */
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.1).withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    private final Telemetry logger = new Telemetry(MaxSpeed);

    // Controllers
    private final XboxController driverController = new XboxController(0);
    private final CommandXboxController DriverController = new CommandXboxController(0);

    // Subsystems
    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

    private final IntakeDeploySubsystem intakeDeploySubsystem = new IntakeDeploySubsystem();
    private final IntakeSubsystem intakeSubsystem = new IntakeSubsystem();

    private final SpindexerSubsystem spindexerSubsystem = new SpindexerSubsystem();

    private final FeederSubsystem feederSubsystem = new FeederSubsystem();

    private final TurretShooterSubsystem turretShooterSubsystem = new TurretShooterSubsystem();
    private final TurretRotationSubsystem turretRotationSubsystem = new TurretRotationSubsystem();

    // Field Data

    private Optional<Alliance> alliance = null;
    private Alliance color = null;

    // public final Vision visionSubsystem = new Vision();

    private final pose2Dutility poseUtility = new pose2Dutility(drivetrain);
    private final Vision vision = new Vision(drivetrain, poseUtility);

    public RobotContainer() {
          
        feederSubsystem.setDefaultCommand(new FeederCommands(
            feederSubsystem, 
            driverController::getLeftTriggerAxis, 
            driverController::getRightTriggerAxis
        ));

        intakeDeploySubsystem.setDefaultCommand(new IntakeDeployCommand(
            intakeDeploySubsystem,
            driverController::getLeftBumperButton, 
            driverController::getRightBumperButton,
            driverController::getXButton
        ));

        intakeSubsystem.setDefaultCommand(new IntakeCommand(
            intakeSubsystem, 
            driverController::getYButton
        )); // CHANGE THIS TO SOMETHING ELSE

        spindexerSubsystem.setDefaultCommand(new SpindexerCommands(
            spindexerSubsystem, 
            driverController::getLeftTriggerAxis, 
            driverController::getRightTriggerAxis
        ));

        turretShooterSubsystem.setDefaultCommand(new TurretShooterCommands(turretShooterSubsystem,
            poseUtility,
            driverController::getLeftTriggerAxis,
            driverController::getRightTriggerAxis
        ));

        turretRotationSubsystem.setDefaultCommand(new TurretRotationCommands(
            turretRotationSubsystem, 
            poseUtility
        ));

        configureBindings();
    }

   private void configureBindings() {
        // Note that X is defined as forward according to WPILib convention,
        // and Y is defined as to the left according to WPILib convention.
        drivetrain.setDefaultCommand(
            // Drivetrain will execute this command periodically
            drivetrain.applyRequest(() ->
                drive.withVelocityX(-DriverController.getLeftY() * MaxSpeed) // Drive forward with negative Y (forward)
                    .withVelocityY(-DriverController.getLeftX() * MaxSpeed) // Drive left with negative X (left)
                    .withRotationalRate(-DriverController.getRightX() * MaxAngularRate) // Drive counterclockwise with negative X (left)
            )
        );

        // Idle while the robot is disabled. This ensures the configured
        // neutral mode is applied to the drive motors while disabled.
        final var idle = new SwerveRequest.Idle();
        RobotModeTriggers.disabled().whileTrue(
            drivetrain.applyRequest(() -> idle).ignoringDisable(true)
        );

        DriverController.a().whileTrue(drivetrain.applyRequest(() -> brake));
        DriverController.b().whileTrue(drivetrain.applyRequest(() ->
            point.withModuleDirection(new Rotation2d(-DriverController.getLeftY(), -DriverController.getLeftX()))
        ));

        // Run SysId routines when holding back/start and X/Y.
        // Note that each routine should be run exactly once in a single log.
        DriverController.back().and(DriverController.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
        DriverController.back().and(DriverController.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
        DriverController.start().and(DriverController.y()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
        DriverController.start().and(DriverController.x()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

        // Reset the field-centric heading on left bumper press.
        DriverController.leftBumper().onTrue(drivetrain.runOnce(drivetrain::seedFieldCentric));

        drivetrain.registerTelemetry(logger::telemeterize);
    }

    public Command getAutonomousCommand() {
        // Simple drive forward auton
        final var idle = new SwerveRequest.Idle();
        return Commands.sequence(
            // Reset our field centric heading to match the robot
            // facing away from our alliance station wall (0 deg).
            drivetrain.runOnce(() -> drivetrain.seedFieldCentric(Rotation2d.kZero)),
            // Then slowly drive forward (away from us) for 5 seconds.
            drivetrain.applyRequest(() ->
                drive.withVelocityX(0.5)
                    .withVelocityY(0)
                    .withRotationalRate(0)
            )
            .withTimeout(5.0),
            // Finally idle for the rest of auton
            drivetrain.applyRequest(() -> idle)
        );
    }
}
