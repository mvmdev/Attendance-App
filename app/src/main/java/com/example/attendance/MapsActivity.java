package com.example.attendance;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener
{
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
 public PendingIntent pendingIntent;
    private float GEOFENCE_RADIUS = 200;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";
  private static Button mapcontinue;
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;

   private final  String ssid = "Mayank";
    private final String bssid="10:62:d0:c0:0a:8e";
    WifiManager wifiManager;
    WifiInfo wifiInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
         pendingIntent = geofenceHelper.getPendingIntent();
         mapcontinue =findViewById(R.id.mapcontinue);
         mapcontinue.setVisibility(View.GONE);
         mapcontinue.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
               checkWIFIandpunchstate();
             }
         });

    }

    void checkWIFIandpunchstate(){
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();

        if(wifiInfo.getSSID().equals("\"Mayank\"") && bssid.equals(wifiInfo.getBSSID())) {
            String punch_state = getIntent().getStringExtra("punchstate");
            Log.d("punch",String.valueOf(punch_state));
            if(punch_state.equals("punch_in")){

                Intent i=new Intent(MapsActivity.this,Camera.class);
                i.putExtra("punchstate", punch_state);
                startActivity(i);
            }
            else if(punch_state.equals("punch_out"))
            {
                Intent i=new Intent(MapsActivity.this,Camera.class);
                i.putExtra("punchstate", punch_state);
                startActivity(i);
            }
        }

        else {
            Toast.makeText(MapsActivity.this,"Please connect to campus WIFI",Toast.LENGTH_SHORT).show();
        }

    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng eiffel = new LatLng(48.8589, 2.29365);
        LatLng eiffel = new LatLng(22.73246 , 75.88465);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eiffel, 16));

        enableUserLocation();
        Toast.makeText(MapsActivity.this, "Hi", Toast.LENGTH_SHORT);
      mMap.setOnMapLongClickListener(this);
        if (Build.VERSION.SDK_INT >= 29) {
            //We need background permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                handleMapLongClick(eiffel);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }

        } else {
            handleMapLongClick(eiffel);
        }

    }

     void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                //We do not have the permission..

            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(this, "Geofencing is ON...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                Toast.makeText(this, "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (Build.VERSION.SDK_INT >= 29) {
            //We need background permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                handleMapLongClick(latLng);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }

        } else {
            handleMapLongClick(latLng);
        }

    }

    private void handleMapLongClick(LatLng latLng) {
        mMap.clear();
        addMarker(latLng);
        addCircle(latLng, GEOFENCE_RADIUS);
        addGeofence(latLng, GEOFENCE_RADIUS);
    }

    private void addGeofence(LatLng latLng, float radius) {

        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });

    }

    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }

    private void addCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0,0));
        circleOptions.fillColor(Color.argb(64, 255, 0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }
    public static  void enteract(){
        mapcontinue.setVisibility(View.VISIBLE);
    }
    public static void exitact(){
        mapcontinue.setVisibility(View.GONE);

    }
    public void punchinattendance(){
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();

        Attendance at=new Attendance();
     if(wifiInfo.getSSID().equals("\"Mayank\"") && bssid.equals(wifiInfo.getBSSID()))
     {
         FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 User unifo=snapshot.getValue(User.class);
                 CalendarUtil calendarUtil=CalendarUtil.getcalendar();
                 String time= calendarUtil.cHour + ":" + calendarUtil.cMinute + ":" + calendarUtil.cSecond + calendarUtil.ampm;
                 String path= calendarUtil.cYear + "/" + unifo.branch + "/" + calendarUtil.month + "/" + calendarUtil.cDay +"/"+unifo.scholarno;
                 at.setName(unifo.getName());
                 at.setPunchintime(time);
                 at.setPunchoutime("NIL");
                 final String temp=path;
                 FirebaseDatabase.getInstance().getReference("Attendance").child(path).addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                         if(snapshot.getValue()==null)
                         {
                             FirebaseDatabase.getInstance().getReference("Attendance").child(temp).setValue(at).addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     Toast.makeText(MapsActivity.this,"ATTENDANCE PUNCHED IN AT"+" "+at.punchintime,Toast.LENGTH_LONG).show();
                                 }
                             });
                         }
                         else
                         {
                             Toast.makeText(MapsActivity.this,"Already Punched In Attendance for today",Toast.LENGTH_LONG).show();

                         }
                         Intent i=new Intent(MapsActivity.this,Home.class);
                         i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                         startActivity(i);
                         finish();
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError error) {

                     }
                 });




             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });

                        }
                else
                {
                    Toast.makeText(MapsActivity.this,"Please connect to campus WIFI",Toast.LENGTH_SHORT).show();
                }

    }
    public void punchoutattendance(){
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        if(wifiInfo.getSSID().equals("\"Mayank\"") && bssid.equals(wifiInfo.getBSSID()))
        {
            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User unifo=snapshot.getValue(User.class);
                    Calendar calander = Calendar.getInstance();
                    int    cDay    = calander.get(Calendar.DAY_OF_MONTH);
                    int    cMonth  = calander.get(Calendar.MONTH);
                    int    cYear   = calander.get(Calendar.YEAR);
                    int    cHour   = calander.get(Calendar.HOUR);
                    int    cMinute = calander.get(Calendar.MINUTE);
                    if(cHour==0)
                    {
                        cHour=12;
                    }
                    int    cSecond = calander.get(Calendar.SECOND);
                    int am_pm=calander.get(Calendar.AM_PM);
                    String ampm=(am_pm==0)?"AM":"PM";
                    String month[]={"January","February","March","April","May","June","July","August","September","October","November","December"};
                    String time=String.valueOf(cHour+":"+cMinute+":"+cSecond+ampm);
                    String path=String.valueOf(cYear+"/"+unifo.branch+"/"+month[cMonth]+"/"+cDay);
                    path=path+"/"+unifo.scholarno;
                    final String temp=path;
                    FirebaseDatabase.getInstance().getReference("Attendance").child(path).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getValue()==null)
                            {
                                Toast.makeText(MapsActivity.this,"ATTENDANCE NOT PUNCHED IN TODAY",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Attendance at=snapshot.getValue(Attendance.class);
                                if(at.getPunchoutime().equals("NIL"))
                                {
                                    at.setPunchoutime(time);
                                    FirebaseDatabase.getInstance().getReference("Attendance").child(temp).setValue(at).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            markattendanceuser(cYear, unifo.branch, unifo.scholarno, month[cMonth], cMonth + 1, cDay, at.punchintime, at.punchoutime);
                                            Toast.makeText(MapsActivity.this, "ATTENDANCE PUNCHED OUT AT" + " " + at.punchintime, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                else
                                {
                                    Toast.makeText(MapsActivity.this,"ALREADY PUNCHED OUT ATTENDANCE",Toast.LENGTH_SHORT).show();
                                }
                            }
                            Intent i=new Intent(MapsActivity.this,Home.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });




                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else
        {
            Toast.makeText(MapsActivity.this,"Please connect to campus WIFI",Toast.LENGTH_SHORT).show();
        }

    }
    public void markattendanceuser(int year,String branch,String scholarno,String smonth,int month,int day,String pintime,String pouttime){

        String path=String.valueOf(year+"/"+branch+"/"+scholarno+"/"+smonth+"/"+day);
        FirebaseDatabase.getInstance().getReference("AttendanceUser").child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()==null)
                {
                    Toast.makeText(MapsActivity.this,"MEEEEEEEE",Toast.LENGTH_LONG).show();
                    AttendanceUser au=new AttendanceUser();
                    au.setDate(String.valueOf(day+"/"+month+"/"+year));
                    au.setPunchintime(pintime);
                    au.setPunchoutime(pouttime);
                    FirebaseDatabase.getInstance().getReference("AttendanceUser").child(path).setValue(au).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }
                else
                {
//                    Toast.makeText(MapsActivity.this,"MEEEEEEEE",Toast.LENGTH_LONG).show();
//                    AttendanceUser au=snapshot.getValue(AttendanceUser.class);
//                    au.setPunchoutime("OUT");
//                    FirebaseDatabase.getInstance().getReference("AttendanceUser").child(path).setValue(au).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//
//                        }
//                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
