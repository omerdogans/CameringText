package com.appvdev.cameringtext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    ImageView ivPhoto;
    Button btCamera,btConvert;
    TextView tvReadText;
    Bitmap imageBitmap;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();
        onClick();
        dispatchTakePictureIntent();

    }
    public void findView(){
        ivPhoto = findViewById(R.id.iv_photo);
        btCamera = findViewById(R.id.camera);
        btConvert = findViewById(R.id.bt_convert);
        tvReadText = findViewById(R.id.tv_read_text);

    }
    public void onClick(){
        btConvert.setOnClickListener(v -> {
            scanningPhotoForText();
           // Toast.makeText(getApplicationContext(),"Dönüştür",Toast.LENGTH_LONG).show();
        });

        btCamera.setOnClickListener(v -> {
            dispatchTakePictureIntent();
            // Toast.makeText(getApplicationContext(),"Kamera",Toast.LENGTH_LONG).show();

        });
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(),"Hata",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageBitmap = (Bitmap) data.getExtras().get("data");
            ivPhoto.setImageBitmap(imageBitmap);

        }
    }
    private void  scanningPhotoForText(){
        //Convert text in photo to text
        InputImage ivPhotoImage = InputImage.fromBitmap(imageBitmap,0);
        TextRecognizer textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> tkText = textRecognizer.process(ivPhotoImage).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                StringBuilder sbText = new StringBuilder();
                for (Text.TextBlock txBlockTx: text.getTextBlocks()){
                    String txBlock = txBlockTx.getText();
                    Point[] bkPoint = txBlockTx.getCornerPoints();
                    Rect rtFrame = txBlockTx.getBoundingBox();
                    for (Text.Line txLine : txBlockTx.getLines()){
                        String lnText = txLine.getText();
                        Point[] lnTextPoint = txLine.getCornerPoints();
                        Rect rtLine = txLine.getBoundingBox();

                        for (Text.Element elText : txLine.getElements()){
                            String stText = elText.getText();
                            sbText.append(stText);
                        }

                        tvReadText.setText(txBlock);

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Bu cümle hatalıdır", Toast.LENGTH_LONG).show();
            }
        });

    }


}