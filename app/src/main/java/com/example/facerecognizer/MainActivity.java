package com.example.facerecognizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView txt;
    Button btn;
    ImageView img;
    //ResultDialog rd;
    private final static int REQUEST_IMAGE_CAPTURE = 124;
    InputImage firebaseVision;
    FaceDetector visionFaceDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt = findViewById(R.id.text1);
        btn = findViewById(R.id.camera_btn);
        img = findViewById(R.id.imageview);
        FirebaseApp.initializeApp(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFile();
            }
        });
        Toast.makeText(this, "App is Started", Toast.LENGTH_SHORT).show();

    }


    private void OpenFile() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (i.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bundle bundle = data.getExtras();
        Bitmap bitmap = (Bitmap) bundle.get("data");

        FaceDetectionProcess(bitmap);
        Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
    }

    private void FaceDetectionProcess(Bitmap bitmap) {

        txt.setText("Processing the image...");
        final StringBuilder builder = new StringBuilder();

        BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();

        InputImage image = InputImage.fromBitmap(bitmap, 0);
        FaceDetectorOptions faceDetectorOptions = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .enableTracking().build();
        FaceDetector faceDetector = FaceDetection.getClient(faceDetectorOptions);

        Task<List<Face>> result = faceDetector.process(image);

        result.addOnSuccessListener(new OnSuccessListener<List<Face>>() {
            @Override
            public void onSuccess(List<Face> faces) {
                if (faces.size() != 0) {
                    if (faces.size() == 1) {
                        builder.append(faces.size() + "Face Detected \n\n");
                    } else if (faces.size() > 1) {
                        builder.append(faces.size() + "Face Detected\n\n");
                    }
                }
                for (Face face : faces) {

                    // Tilting and Rotation
                    int id = face.getTrackingId();
                    float rotY = face.getHeadEulerAngleY();
                    float rotZ = face.getHeadEulerAngleZ();

                    builder.append("1. FACE TRACKING ID [" + id + "] ");
                    builder.append("2. Head Rotation to right [" + String.format("%.2f", rotY) + "deg. ]\n");
                    builder.append("3. Head Tilted  [" + String.format("%.2f", rotZ) + "deg. ]\n");
                    // smiling
                    if (face.getSmilingProbability() > 0) {
                        float SmilingProbability = face.getSmilingProbability();
                        builder.append("4. Smiling Probability [" + String.format("%.3f", SmilingProbability) + "]\n");
                    }

                    // left eye open
                    if (face.getLeftEyeOpenProbability() > 0) {
                        float leftEyeOpenProbability = face.getLeftEyeOpenProbability();
                        builder.append("5. Left Eye Open Probability [" + String.format("%.3f", leftEyeOpenProbability) + "]\n");
                    }

                    // right eye open
                    if (face.getRightEyeOpenProbability() > 0) {
                        float rightEyeOpenProbability = face.getRightEyeOpenProbability();
                        builder.append("6. Right Eye Open Probability [" + String.format("%.3f", rightEyeOpenProbability) + "]\n");
                    }
                    builder.append("\n");

                }
                showDetection("Face Detection", builder, true);
            }
        });
        result.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                StringBuilder builder1 = new StringBuilder();
                builder1.append("Sorry There is an Error!");
                showDetection("Face Detection", builder, false);
            }
        });
    }

    private void showDetection(final String face_detection, final StringBuilder builder, boolean b) {

        if (b == true) {
            txt.setText(null);
            txt.setMovementMethod(new ScrollingMovementMethod());
            if (builder.length() != 0) {
                txt.append(builder);
                if (face_detection.substring(0, face_detection.indexOf(' ')).equalsIgnoreCase("OCR")) {
                    txt.append("\n(hold the text to copy it");
                } else {
                    txt.append("(hold the text to copy it");
                }

                txt.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText(face_detection, builder);
                        clipboardManager.setPrimaryClip(clipData);
                        return true;
                    }
                });
            } else {
                txt.append(face_detection.substring(0, face_detection.indexOf(' ')) + "Failed !");
            }
        } else if (b == false) {
            txt.setText(null);
            txt.setMovementMethod(new ScrollingMovementMethod());
            txt.append(builder);

        }
    }
}