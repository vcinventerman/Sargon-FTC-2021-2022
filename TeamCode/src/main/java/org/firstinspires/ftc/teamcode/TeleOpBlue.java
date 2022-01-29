/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.TeleOpBase;

import java.util.Timer;
import java.util.TimerTask;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

    // Drive left stick
    // Toggle intake with left for speed, right for direction bumpers
    // Carousel triggers
    // Dumper d-pad y
    // Lifter right stick y

    // Gamepad 1 (Primary)
    // left stick: drive
    // right stick y: capping servo (UNUSED)
    // shoulders: rotate
    // d-pad: arm position:
        // Up: high level
        // Down: ready for intake
        // Left: eject block with servo
        // Right: low level
    // a: release block
    // b: reverse drive controls
    // x: spin carousel
    // y: spin carousel reverse
    // left bumper: activate intake
    // right bumper: eject stuck block from intake

    // Gamepad 2 (Emergency)
    // right stick y: arm target
    // a: Reset arm zero
    // d-pad: Set servo position
    // left stick y: Move servo


    /* Configuration

    Control Hub
        0: driveBackLeft
        1: driveFrontLeft
        2: carouselLeft
        3: intake

    Expansion Hub
        0: driveBackRight
        1: driveFrontRight
        2: carouselRight
        3: arm

    I2C
        Control Hub
            0: imu

    Servo
        Control Hub
            0: dumper
    */

    // DriveSimple2
@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="TeleOpBlue", group="Linear OpMode")
@com.acmerobotics.dashboard.config.Config
public class TeleOpBlue extends TeleOpBase {


    @Override
    public void runOpMode()
    {
        alliance = Constants.Alliance.Blue;
        targetDuckHub = new Pose2d((-4.95 * 12), (4.9 * 12), Math.toRadians(90));

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        initMotors();

        p1 = new GamepadEx(gamepad1);
        p2 = new GamepadEx(gamepad2);

        //gamepad1.setJoystickDeadzone(0.1);


        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive() && !isStopRequested())
        {
            p1.readButtons();
            p2.readButtons();

            runArm();

            runDumper();

            runDriveSmart();

            runCarousel();

            runIntake();

            dumpStats(true);

        }
    }
}