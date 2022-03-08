package com.example.pinitlogin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.firestore.GeoPoint;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraActivity extends AppCompatActivity {

    private Button takePicture;
    private Button uploadPicture;
    private ImageView picture;
    private EditText tags;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;
    private String filename;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        takePicture = findViewById(R.id.btnTakePicture);
        uploadPicture = findViewById(R.id.btnUploadPicture);
        picture = findViewById(R.id.imgPicture);
        tags = findViewById(R.id.etUserTags);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        takePicture.setOnClickListener(new View.OnClickListener() { //takes picture
            @Override
            public void onClick(View v) {
                filename = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                //String filename = "photo";
                File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                try {

                    File imageFile = File.createTempFile(filename, ".jpg", storageDirectory);

                    currentPhotoPath = imageFile.getAbsolutePath();

                    Uri imageUri = FileProvider.getUriForFile(CameraActivity.this, "com.example.pinitlogin.fileprovider", imageFile);

                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(i, 1);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        uploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //NEED TO CHECK FOR NULL DATA

                if (ActivityCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        GeoPoint loc = new GeoPoint(location.getLatitude(), location.getLongitude());
                        List<String> tagList = getTags();
                        Map<String, Object> data = new HashMap<>();
                        data.put("coords", loc);
                        data.put("tags", tagList);
                        db.collection("pictures").document(filename).set(data);
                        uploadPicture();
                    }
                });

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if(resultCode == 1 && resultCode == RESULT_OK){
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        picture.setImageBitmap(bitmap);
        // }
    }


    protected void uploadPicture() {
        Uri file = Uri.fromFile(new File(currentPhotoPath));
        StorageReference storageReference = storage.getReference().child("pictures").child(filename+".jpg");
        UploadTask uploadTask = storageReference.putFile(file);

    }

    protected List<String> getTags() {
        String tagString = tags.getText().toString();
        List<String> convertedTags = Arrays.asList(tagString.split(",", -1));
        return convertedTags;
    }


}