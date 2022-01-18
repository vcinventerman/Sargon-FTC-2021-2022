package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

import java.util.List;

@Autonomous
public class AutoRedDuck extends LinearOpMode
{
    enum BarcodePosition
    {
        Unknown,
        Left,
        Middle,
        Right
    }



    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
    private static final String[] LABELS = {
            "Ball",
            "Cube",
            "Duck",
            "Marker",
            "TeamFabric"
    };

    private static final String VUFORIA_KEY = "AQfwG73/////AAABmaET3hUmm0WIjCN9wIx3AKA6l22iwwwVNCUbgJkn4v5KLzvswWwlRaShGcgpS2jgvjX+aBry9XKAoM0JeE1yFK1hpyDD3+mR68nn4uT/NoAKQvTDPC2a6+3rN91dN5qyCwg0UWv3oslFUIjQIX9HZBuRjVdHYfS1LU/Ea93hQ0wxulW3Hij8gdqRstJSYTi9u+IiGyYzv560wYoH5wZP2rJxbB3Av/E6O1C08lYAjKgRPMqsl27Wy1CA+lKzJ0pVYjRA3Z4+9AaQFFzFPjTKHPxXG75lzYXj0eB/aA8K91fokCK16SJp5xNJqoccpgO1t3IO7B1CVonzAEz9juq+WBsGPRffzMAxanmczBJjgh7Y";
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    private DcMotorEx carouselLeft = null;
    private DcMotorEx carouselRight = null;
    private DcMotorEx intake = null;
    private Servo dumper = null;
    private DcMotorEx arm = null;


    private void placeHeldObject()
    {
        //arm.setTargetPosition(-740);

    }

    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        carouselLeft = (DcMotorEx)hardwareMap.get(DcMotor.class, "carouselLeft");
        carouselLeft.setDirection(DcMotor.Direction.FORWARD);
        carouselRight = (DcMotorEx)hardwareMap.get(DcMotor.class, "carouselRight");
        carouselRight.setDirection(DcMotor.Direction.REVERSE);

        intake = (DcMotorEx)hardwareMap.get(DcMotor.class, "intake");
        intake.setDirection(DcMotor.Direction.REVERSE);

        //arm = (DcMotorEx)hardwareMap.get(DcMotor.class, "arm");
        //arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);



        Pose2d startPose = new Pose2d(-3 * 12, (6 * 12) - 8.375, 1 * Math.PI);

        drive.setPoseEstimate(startPose);

        TrajectorySequence trajSeq = drive.trajectorySequenceBuilder(startPose)
                //.splineTo(new Vector2d(8.5 * 12, 3.75 * 12), 1.5 * Math.PI)
                .forward(5)
                .splineTo(new Vector2d(-5 * 12, 3 * 12), 1 * Math.PI)
                //.addSpatialMarker(new Vector2d(8.5 * 12, 3.75 * 12), placeHeldObject)
                //.addDisplacementMarker(() -> {})
                .waitSeconds(2)
                .build();

        /*TrajectorySequence trajSeq = drive.trajectorySequenceBuilder(startPose)
                .splineTo(new Vector2d(10, 10), 0)
                .turn(Math.toRadians(90))
                .splineTo(new Vector2d(25, -15), 0)
                .waitSeconds(3)
                .turn(Math.toRadians(45))
                .forward(10)
                .strafeRight(5)
                .turn(Math.toRadians(90))
                .strafeLeft(5)
                .waitSeconds(1)
                .splineToLinearHeading(new Pose2d(-10, -10, Math.toRadians(45)), 0)
                .build();*/



        telemetry.addData("%", "Sargon Robotics");
        telemetry.update();

        waitForStart();

        carouselLeft.setPower(0.5);
        carouselRight.setPower(0.5);

        //arm.setTargetPosition(-740);
        //arm.setDirection(DcMotorSimple.Direction.REVERSE);
        ///arm.setPower(-0.5);

        telemetry.addData("%", "Barcode Position: " + findObject().toString());
        telemetry.update();

        //drive.followTrajectorySequenceAsync(trajSeq);
        drive.followTrajectorySequence(trajSeq);

        while (!isStopRequested())
        {
            //if (arm.getCurrentPosition() <= -730 && arm.getCurrentPosition() >= -750)
              //  arm.setTargetPosition(-740);
        }


    }

    private BarcodePosition findObject()
    {
        if (tfod != null) {
            // getUpdatedRecognitions() will return null if no new information is available since
            // the last time that call was made.
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null) {
                telemetry.addData("# Object Detected", updatedRecognitions.size());
                // step through the list of recognitions and display boundary info.
                int i = 0;
                for (Recognition recognition : updatedRecognitions) {
                    telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                    telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                            recognition.getLeft(), recognition.getTop());
                    telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                            recognition.getRight(), recognition.getBottom());
                    i++;
                }
                telemetry.update();
            }
        }

        return BarcodePosition.Unknown;
    }

    private void initObjectRecognition()
    {
        initVuforia();
        initTfod();

        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            tfod.setZoom(2.5, 16.0/9.0);
        }
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.8f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 320;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }
}
