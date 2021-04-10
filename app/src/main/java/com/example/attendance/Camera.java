package com.example.attendance;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

public class Camera extends AppCompatActivity {
    StorageReference storageReference;
    ImageView imagegview;
    Button bt;
    String currentPhotoPath;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    private final  String ssid = "Mayank";
    private final String bssid="10:62:d0:c0:0a:8e";
    WifiManager wifiManager;
    WifiInfo wifiInfo;
    ProgressBar pg;
    private Uri imageuri;
    private User unifo;
    private CalendarUtil calendarUtil;
    private  String punch_state;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        storageReference = FirebaseStorage.getInstance().getReference();
        imagegview=findViewById(R.id.camimageView);
        bt=findViewById(R.id.cambutton);
        pg=findViewById(R.id.camprogressBar);
        imageuri=null;
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pg.setVisibility(View.VISIBLE);
                 punch_state = getIntent().getStringExtra("punchstate");
                calendarUtil=CalendarUtil.getcalendar();
                getcurruser();
            }
        });

    }
    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            dispatchTakePictureIntent();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                imagegview.setImageURI(Uri.fromFile(f));
                Log.d("URILOCAL", "ABsolute Url of Image is " + Uri.fromFile(f));
                imageuri=Uri.fromFile(f);
                uploadimagetoFirebase(f);
            }

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + calendarUtil.time + "_";
 File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    public void uploadimagetoFirebase(File f){
        String path="AttendanceUser"+"/"+calendarUtil.cYear+"/"+unifo.scholarno+"/"+calendarUtil.month+
                "/"+calendarUtil.cDay+"/"+f.getName();
        StorageReference sr=storageReference.child(path);
        sr.putFile(Uri.fromFile(f)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                f.delete();
                sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("URIFIREBASE",uri.toString());
                        if(punch_state.equals("punch_in")) {
                            punchinutil(uri);
                        }
                        else if(punch_state.equals("punch_out")){
                            calendarUtil=CalendarUtil.getcalendar();
                            punchoututil(uri);
                        }
                        // Toast.makeText(Camera.this,"Image uploaded successfully",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }



    public void getcurruser(){
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                unifo=snapshot.getValue(User.class);
                if(punch_state.equals("punch_in")) {
                    Log.d("p3",punch_state);
                    punchinattendance();
                }
                else if(punch_state.equals("punch_out")){
                    calendarUtil=CalendarUtil.getcalendar();
                    punchoutattendance();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void punchinattendance(){
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        if(wifiInfo.getSSID().equals("\"Mayank\"") && bssid.equals(wifiInfo.getBSSID()))
        {

                    String time= calendarUtil.cHour + ":" + calendarUtil.cMinute + ":" + calendarUtil.cSecond + calendarUtil.ampm;
                    String path= calendarUtil.cYear + "/" + unifo.branch + "/" + calendarUtil.month + "/" + calendarUtil.cDay +"/"+unifo.scholarno;
                    final String temp=path;
                    FirebaseDatabase.getInstance().getReference("Attendance").child(path).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getValue()==null)
                            {
                                askCameraPermissions();
                            }
                            else
                            {
                                Toast.makeText(Camera.this,"Already Punched In Attendance for today",Toast.LENGTH_LONG).show();
                                Intent i=new Intent(Camera.this,Home.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();
                            }
                        }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        else
        {
            Toast.makeText(Camera.this,"Please connect to campus WIFI",Toast.LENGTH_SHORT).show();
        }
    }
    public void punchinutil(Uri uri){
        Attendance at=new Attendance();
        at.setName(unifo.name);
        at.setPunchintime(calendarUtil.time);
        at.setPunchoutime("NIL");
        at.setPunchinimage(uri.toString());
        at.setPunchoutimage("NIl");
        String path= calendarUtil.cYear + "/" + unifo.branch + "/" + calendarUtil.month + "/" + calendarUtil.cDay +"/"+unifo.scholarno;
        FirebaseDatabase.getInstance().getReference("Attendance").child(path).setValue(at).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pg.setVisibility(View.GONE);
                Toast.makeText(Camera.this,"ATTENDANCE PUNCHED IN AT"+" "+at.punchintime,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Camera.this,Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
    public void punchoutattendance(){
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        if(wifiInfo.getSSID().equals("\"Mayank\"") && bssid.equals(wifiInfo.getBSSID()))
        {
                    String time=calendarUtil.time;
                    String path= calendarUtil.cYear + "/" + unifo.branch + "/" + calendarUtil.month + "/" + calendarUtil.cDay+"/"+unifo.scholarno;;
                    final String temp=path;
                    FirebaseDatabase.getInstance().getReference("Attendance").child(path).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getValue()==null)
                            {
                                Toast.makeText(Camera.this,"ATTENDANCE NOT PUNCHED IN TODAY",Toast.LENGTH_LONG).show();
                                Intent i=new Intent(Camera.this,Home.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                            }
                            else
                            {
                                Attendance at=snapshot.getValue(Attendance.class);
                                if(at.getPunchoutime().equals("NIL"))
                                {
                                    askCameraPermissions();
                                }
                                else
                                {
                                    Toast.makeText(Camera.this,"ALREADY PUNCHED OUT ATTENDANCE",Toast.LENGTH_SHORT).show();
                                    Intent i=new Intent(Camera.this,Home.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                    finish();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
        }
        else
        {
            Toast.makeText(Camera.this,"Please connect to campus WIFI",Toast.LENGTH_SHORT).show();
        }
    }
 public void punchoututil(Uri uri){
     String time=calendarUtil.time;
     String path= calendarUtil.cYear + "/" + unifo.branch + "/" + calendarUtil.month + "/" + calendarUtil.cDay+"/"+unifo.scholarno;;
     FirebaseDatabase.getInstance().getReference("Attendance").child(path).addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
             Attendance at=snapshot.getValue(Attendance.class);
             at.setPunchoutime(calendarUtil.time);
             at.setPunchoutimage(uri.toString());
             FirebaseDatabase.getInstance().getReference("Attendance").child(path).setValue(at).addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                    markattendanceuser(at);

                 }
             });
         }
         @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }
     });
 }
    public void markattendanceuser(Attendance at){

        String path= calendarUtil.cYear+ "/" +unifo.branch+ "/" +unifo.scholarno + "/" +calendarUtil.month+ "/" +calendarUtil.cDay;
        FirebaseDatabase.getInstance().getReference("AttendanceUser").child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()==null)
                {
                    AttendanceUser au=new AttendanceUser();
                    au.setDate(calendarUtil.cDay+" "+calendarUtil.month+"-"+calendarUtil.cYear);
                    au.setPunchintime(at.punchintime);
                    au.setPunchoutime(at.punchoutime);
                    au.setPunchinimage(at.punchinimage);
                    au.setPunchoutimage(at.punchoutimage);
                    FirebaseDatabase.getInstance().getReference("AttendanceUser").child(path).setValue(au).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Camera.this,"ATTENDANCE PUNCHED OUT AT"+" "+at.punchintime,Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Camera.this,Home.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
//    public void uploadImage(View v)
//    {
//        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent,101);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode== Activity.RESULT_OK){
//            if(requestCode==101){
//             //   onCaptureImageresult(data);
//                Uri uri=data.getData();
//                StorageReference sr=storageReference.child("PunchinImages").child(uri.getLastPathSegment());
//              sr.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                  @Override
//                  public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                  }
//              }).addOnFailureListener(new OnFailureListener() {
//                  @Override
//                  public void onFailure(@NonNull Exception e) {
//
//                  }
//              });
//            }
//        }
//    }
//    private void onCaptureImageresult(Intent data){
//        Bitmap image=(Bitmap)data.getExtras().get("data");
//
//        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
//        imagegview.setImageBitmap(image);
//       image.compress(Bitmap.CompressFormat.PNG,100,bytes);
//        byte bb[]=bytes.toByteArray();
//        imagegview.setImageBitmap(image);
//        uploadToFirebase(bb);
//    }
//    private void uploadToFirebase(byte [] bb){
//        StorageReference sr=storageReference.child("PunchinImages/a.png");
//        sr.putBytes(bb).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//
//                        Log.d("URISS",uri.toString());
//                    }
//                });
//                Toast.makeText(Camera.this,"Successfully uploaded",Toast.LENGTH_SHORT).show();
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(Camera.this,"Failed to upload",Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
