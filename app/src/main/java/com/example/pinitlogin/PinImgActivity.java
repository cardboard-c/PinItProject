package com.example.pinitlogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
//REMEMBER TO ALLOW PERMISSIONS IN PHONE BEFORE USE(LOCATION AND CAMERA)

public class PinImgActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private ImageView img;
    private TextView txt;
    private TextView distance;
    private Button back;
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_img);

        img = findViewById(R.id.imgPin2);
        txt = findViewById(R.id.textView2);
        back = findViewById(R.id.btnBackToMap);
        distance = findViewById(R.id.distcance);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Intent i = getIntent();
        String ID = i.getStringExtra("ID");
        ID = ID + ".jpg";
        StorageReference storageRef = storage.getReference().child("pictures").child(ID);
        txt.setText(storageRef.toString());
        GlideApp.with(PinImgActivity.this)
                .load(storageRef)
                .into(img);


        double lat = i.getDoubleExtra("Lat", 0);
        double lon = i.getDoubleExtra("Lon", 0);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
               Location location2 = new Location("");
               location2.setLatitude(lat);
               location2.setLongitude(lon);

               double distanceInMeters = location.distanceTo(location2);
               distanceInMeters = distanceInMeters/1609.34;
               distance.setText(String.valueOf(distanceInMeters) + " miles from the point");
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PinImgActivity.this, MapsActivity.class);
                String extras = getIntent().getStringExtra("Tag");
                i.putExtra("Tag", extras);
                startActivity(i);
            }
        });
    }
}