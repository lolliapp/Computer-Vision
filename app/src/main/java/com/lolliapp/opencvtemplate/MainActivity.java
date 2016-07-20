package com.lolliapp.opencvtemplate;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The original version of this application can be downloaded from Google Play at
 * https://play.google.com/store/apps/details?id=com.lolliapp.opencvtemplate
 *
 * A template/skeleton Android Studio application that includes the OpenCV library as a module
 * OpenCV Version: OpenCVLoader.OPENCV_VERSION_3_1_0
 *
 * To do your own image processing
 * 1. Add a ui component to activity_main.xml to set app to your custom mode
 * Search for R.id.mode_container in activity_main.xml
 * 2. Edit the portions of this java file marked with TODO
 *    - in the updateUi() method, add logic for what parts of the ui should be visible
 */

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    // Arbitrarily selected permission request code to be able to use the device's camera (for > Android Marshmallow)
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1111;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    public enum ImgProcMode {
        EDGE_DETECTION, FACE_DETECTION, CUSTOM_MODE_1
    }

    // About app (license)
    @BindView(R.id.about_container)
    ScrollView aboutContainer;
    @BindView(R.id.license)
    TextView licenseTextView;
    // OpenCV view to represent camera
    @BindView(R.id.ocv_camera_view)
    JavaCameraView mOpenCvCameraView;
    // UI controls
    @BindView(R.id.controls_container)
    ScrollView controlsContainer;
    @BindView(R.id.edge_detector_mode)
    TextView edgeDetectorMode;
    @BindView(R.id.face_detector_mode)
    TextView faceDetectorMode;
    @BindView(R.id.rotation_degrees)
    TextView rotationDegrees;
    @BindView(R.id.canny_value_container)
    LinearLayout cannyValueContainer;
    @BindView(R.id.canny_threshold)
    TextView cannyThreshold;

    private ImgProcMode applicationMode = ImgProcMode.EDGE_DETECTION;

    // Camera selection (regular or selfie, 0 is usually regular, 1 is usually selfie)
    private int cameraIdx = 0;

    // Image rotation offset (allows user to view image rightside up by adjusting using ui)
    private int rotationDegreesVal = 90;

    // Member variables for edge detection
    private int cannyThresholdVal = 20;
    private int blurKernelWidthVal = 3;
    private int blurKernelHeightVal = 3;

    // Member variables for face detection
    private CascadeClassifier cascadeClassifier;
    private Mat grayscaleImg;
    private int faceDetectionEdgeLength;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    loadFrontalFaceDetectionCascadeClassifier();
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // A third party library that allows annotations like @BindView(resId) and @OnClick(resId)
        // For easier/cleaner access of views defined in /layout/{layout_file_name.xml}
        ButterKnife.bind(MainActivity.this);

        // Request camera permission (for > Android Marshmallow)
        if (!isCameraPermissionGranted()) {
            requestCameraPermission();
        }

        // Load licenseTextView with String that represents the distribution license in html format
        licenseTextView.setText(Html.fromHtml(Constants.LICENSE_HTML));

        // Initialize OpenCV Camera View, the view that will display the camera feed
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        // Loads values into textviews for Canny threshold value, rotation degrees, and updates visibility of ui
        updateUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCameraPermissionGranted()) {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    public void onBackPressed() {
        if (aboutContainer.getVisibility() == View.VISIBLE) {
            aboutContainer.setVisibility(View.GONE);
            controlsContainer.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }


    private void updateUi() {
        rotationDegrees.setText("Rotation CCW: " + rotationDegreesVal + "\u00B0");

        switch (applicationMode) {
            case EDGE_DETECTION:
                cannyThreshold.setText("Canny Threshold: " + cannyThresholdVal);
                edgeDetectorMode.setTextColor(getResources().getColor(R.color.colorAccent));
                faceDetectorMode.setTextColor(Color.WHITE);
                cannyValueContainer.setVisibility(View.VISIBLE);
                break;
            case FACE_DETECTION:
                faceDetectorMode.setTextColor(getResources().getColor(R.color.colorAccent));
                edgeDetectorMode.setTextColor(Color.WHITE);
                cannyValueContainer.setVisibility(View.GONE);
                break;
            case CUSTOM_MODE_1:
                // TODO: update UI state to be the custom mode
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    //
    // OnClickListeners, code that gets triggered when user clicks on a UI component
    //
    ////////////////////////////////////////////////////////////////////////////////
    @OnClick(R.id.edge_detector_mode)
    public void onEdgeDetectorModeClick() {
        applicationMode = ImgProcMode.EDGE_DETECTION;
        updateUi();
    }

    @OnClick(R.id.face_detector_mode)
    public void onFaceDetectorModeCick() {
        applicationMode = ImgProcMode.FACE_DETECTION;
        updateUi();
    }

    // TODO: UI selected for CUSTOM_MODE_1, write code to handle click
    //@OnClick(the id in xml of the button that selects this mode)
    //public void onCustomMode1Click() {
    //    applicationMode = ImgProcMode.CUSTOM_MODE_1
    //    updateUi();
    // }

    @OnClick(R.id.switch_camera_btn)
    public void onSwitchCameraBtnClick() {
        // toggle between regular and selfie cams every time btn is clicked
        mOpenCvCameraView.disableView();
        cameraIdx = cameraIdx == 0 ? 1 : 0;
        mOpenCvCameraView.setCameraIndex(cameraIdx);
        mOpenCvCameraView.enableView();
    }

    @OnClick(R.id.dec_canny_threshold)
    public void onDecreaseCannyThresholdClick() {
        cannyThresholdVal -= 10;
        updateUi();
    }

    @OnClick(R.id.inc_canny_threshold)
    public void onIncreaseCannyThresholdClick() {
        cannyThresholdVal += 10;
        updateUi();
    }

    @OnClick(R.id.rotate_ccw)
    public void onRotateCCWClck() {
        rotationDegreesVal += 90;
        rotationDegreesVal = rotationDegreesVal % 360;
        updateUi();
    }

    @OnClick(R.id.rotate_cw)
    public void onRotateCWClick() {
        rotationDegreesVal -= 90;
        if (rotationDegreesVal < 0) {
            rotationDegreesVal = rotationDegreesVal + 360;
        }
        updateUi();
    }

    @OnClick(R.id.about_container)
    public void onAboutContainerClick() {
        // noop to prevent click bleeding through
    }

    @OnClick(R.id.about_btn)
    public void onAboutBtnClick() {
        if (aboutContainer.getVisibility() == View.GONE) {
            aboutContainer.setVisibility(View.VISIBLE);
            controlsContainer.setVisibility(View.GONE);
        } else {
            aboutContainer.setVisibility(View.GONE);
            controlsContainer.setVisibility(View.VISIBLE);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    //
    // Method callbacks from OpenCV library for the view with the video feed
    //
    ////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCameraViewStarted(int width, int height) {
        // initialize member variable values for face detection
        grayscaleImg = new Mat(height, width, CvType.CV_8UC4);
        faceDetectionEdgeLength = (int) (height * 0.2);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        final int degrees = rotationDegreesVal;
        Mat mat = inputFrame.rgba();
        Mat rotatedMat = rotate(mat, degrees);

        switch (applicationMode) {
            case EDGE_DETECTION:
                return runCanny(rotatedMat, cannyThresholdVal, blurKernelWidthVal, blurKernelHeightVal);
            case FACE_DETECTION:
                return runFacialRecognition(rotatedMat);
            case CUSTOM_MODE_1:
                return runCustomModeCodeHere(rotatedMat);
        }

        return rotatedMat; // the applicationMode did not match any in the switch statement
    }

    private Mat rotate(Mat mat, int degCCW) {
        Point center = new Point(mat.width() / 2, mat.height() / 2);
        double scale = 1.0;
        Mat mapMatrix = Imgproc.getRotationMatrix2D(center, degCCW, scale);
        Imgproc.warpAffine(mat, mat, mapMatrix, new Size(mat.width(), mat.height()), Imgproc.INTER_LINEAR);
        return mat;
    }

    private Mat runCanny(Mat mat, final int threshold, final int blurKernelWidth, final int blurKernelHeight) {
        Mat gray = new Mat();
        Mat detectedEdges = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(gray, detectedEdges, new Size(blurKernelWidth, blurKernelHeight));
        Imgproc.Canny(detectedEdges, detectedEdges, threshold, threshold * blurKernelWidth, blurKernelHeight, false);
        return detectedEdges;
    }

    private Mat runFacialRecognition(Mat mat) {
        Mat inputFrame = mat;
        Imgproc.cvtColor(inputFrame, grayscaleImg, Imgproc.COLOR_RGBA2RGB);

        MatOfRect faces = new MatOfRect();

        if (cascadeClassifier != null) {
            cascadeClassifier.detectMultiScale(grayscaleImg, faces, 1.1, 2, 2,
                    new Size(faceDetectionEdgeLength, faceDetectionEdgeLength), new Size());
        }

        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(inputFrame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);

        return mat;
    }

    private Mat runCustomModeCodeHere(Mat mat) {
        // TODO: process mat here for CUSTOM_MODE_1
        return mat;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //
    // Initialization related (load cascade classifier for face detection from res/raw folder
    //
    ////////////////////////////////////////////////////////////////////////////////
    private void loadFrontalFaceDetectionCascadeClassifier() {
        try {
            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            is.close();
            os.close();

            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        } catch (Exception e) {

        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    //
    // Code related to obtaining Camera permission
    //
    ////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // continue with app
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Please grant camera access", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void requestCameraPermission() {
        if (!isCameraPermissionGranted()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
                showMessageOKCancel("You need to allow access to Camera",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                            }
                        });
                return;
            }

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean isCameraPermissionGranted() {
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

}
