package com.example.calculadora;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionCloudDocumentRecognizerOptions;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class FirebaseLabelTU extends AppCompatActivity {


    private final int MY_PERMISSIONS_REQUEST_USE_CAMARA_AND_EXTERNAL_STORAGE = 100;
    CameraView cameraView;
    Button btnDetector;
    AlertDialog WaitingDialog;
    FirebaseVisionImage firebaseVisionImage;
    LottieAnimationView animacion1;

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_label_tu);

        animacion1=findViewById(R.id.animacion1);
        this.cameraView=findViewById(R.id.camera_view);
        this.btnDetector =findViewById(R.id.btnDetectar);
        this.cameraView=findViewById(R.id.camera_view);
        this.btnDetector =findViewById(R.id.btnDetectar);

        if(Validar_DeboPedirPermisos_SegunAPI()){
            WaitingDialog=new SpotsDialog.Builder().setContext(this).setMessage("Por favor espere....").setCancelable(false).build();
            cameraView.addCameraKitListener(new CameraKitEventListener() {
                @Override
                public void onEvent(CameraKitEvent cameraKitEvent) {

                }

                @Override
                public void onError(CameraKitError cameraKitError) {

                }

                @Override
                public void onImage(CameraKitImage cameraKitImage) {
                    WaitingDialog.show();
                    Bitmap bitmap=cameraKitImage.getBitmap();
                    bitmap=Bitmap.createScaledBitmap(bitmap,cameraView.getWidth(),cameraView.getHeight(),false);
                    cameraView.stop();
                    Procesar(bitmap);
                }

                @Override
                public void onVideo(CameraKitVideo cameraKitVideo) {

                }
            });

            btnDetector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    cameraView.start();
                    cameraView.captureImage();
                }
            });
        }else{
            Toast.makeText(this, "DEBO DESACTIVAR ALGO", Toast.LENGTH_LONG).show();
        }
    }


    public boolean Validar_DeboPedirPermisos_SegunAPI(){

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if ((ContextCompat.checkSelfPermission(FirebaseLabelTU.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)){
            return true;
        }else {
            ActivityCompat.requestPermissions(FirebaseLabelTU.this,
                    new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_USE_CAMARA_AND_EXTERNAL_STORAGE);
        }
/*        if((shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) || (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))){

            CargarDialogoPermisos();

        }else{*/


//        }
        return false;
    }

    private void CargarDialogoPermisos() {
        androidx.appcompat.app.AlertDialog.Builder Abuilder= new androidx.appcompat.app.AlertDialog.Builder(FirebaseLabelTU.this);
        Abuilder.setMessage("Es necesario proporcionar permisos para acceder a BeSmartCam ");
        Abuilder.setTitle("Permisos desactivados");
        Abuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(FirebaseLabelTU.this,
                        new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_USE_CAMARA_AND_EXTERNAL_STORAGE);
            }
        });
        Abuilder.show();
    }

    public void Procesar(Bitmap bitmap){
        final String[] resultado = {""};
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionCloudDocumentRecognizerOptions options =
                new FirebaseVisionCloudDocumentRecognizerOptions.Builder()
                        .setLanguageHints(Arrays.asList("en"))
                        .build();
        FirebaseVisionDocumentTextRecognizer detector = FirebaseVision.getInstance()
                .getCloudDocumentTextRecognizer(options);

        detector.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionDocumentText>() {
                    @Override
                    public void onSuccess(FirebaseVisionDocumentText result) {
                        // Task completed successfully
                        // ...
                        String resultText = result.getText();
                        for (FirebaseVisionDocumentText.Block block: result.getBlocks()) {
                            String blockText = block.getText();
                            Float blockConfidence = block.getConfidence();
                            List<RecognizedLanguage> blockRecognizedLanguages = block.getRecognizedLanguages();
                            Rect blockFrame = block.getBoundingBox();
                            for (FirebaseVisionDocumentText.Paragraph paragraph: block.getParagraphs()) {
                                String paragraphText = paragraph.getText();
                                Float paragraphConfidence = paragraph.getConfidence();
                                List<RecognizedLanguage> paragraphRecognizedLanguages = paragraph.getRecognizedLanguages();
                                Rect paragraphFrame = paragraph.getBoundingBox();
                                for (FirebaseVisionDocumentText.Word word: paragraph.getWords()) {
                                    String wordText = word.getText();
                                    Float wordConfidence = word.getConfidence();
                                    List<RecognizedLanguage> wordRecognizedLanguages = word.getRecognizedLanguages();
                                    Rect wordFrame = word.getBoundingBox();
                                    resultado[0] +=(wordText);
/*                                    for (FirebaseVisionDocumentText.Symbol symbol: word.getSymbols()) {
                                        String symbolText = symbol.getText();
                                        Float symbolConfidence = symbol.getConfidence();
                                        List<RecognizedLanguage> symbolRecognizedLanguages = symbol.getRecognizedLanguages();
                                        Rect symbolFrame = symbol.getBoundingBox();
                                    }*/
                                }
                            }
                        }
                        WaitingDialog.dismiss();
                        Calculadora.resultadoScan=resultado[0];
/*                        Intent in = new Intent();
                        in.putExtra("num", resultado[0]);
                        setResult(RESULT_OK, in);

                        finish();*/
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                        Toast.makeText(FirebaseLabelTU.this, "Ha ocurrido un error en la identificación de caracateres", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}
