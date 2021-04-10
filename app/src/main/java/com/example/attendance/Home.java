package com.example.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class Home extends AppCompatActivity implements Serializable {
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private androidx.biometric.BiometricPrompt.PromptInfo promptInfo;
    public String state;
    ProgressBar hmprogressBar;
CardView hmlogout, hmpunchin, hmdetails, hmpunchout,hmviewattendance,hmviewmessages;
  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
         hmprogressBar=findViewById(R.id.hm2progress);
         hmprogressBar.setVisibility(View.GONE);
        executor= ContextCompat.getMainExecutor(Home.this);
        biometricPrompt=new BiometricPrompt(Home.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(Home.this,"Authentication error"+errString,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
             //   Toast.makeText(Home.this,"Authentication succeed",Toast.LENGTH_SHORT).show();
                Intent i=new Intent(Home.this,MapsActivity.class);
                Log.d("p2",String.valueOf(state));
                i.putExtra("punchstate", String.valueOf(state));
                startActivity(i);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(Home.this,"Authentication failed",Toast.LENGTH_SHORT).show();

            }
        });
        promptInfo=new BiometricPrompt.PromptInfo.Builder().setTitle("College app authentication").
                setSubtitle("Login using fingerprint").setNegativeButtonText("Exit").build();

        hmlogout=findViewById(R.id.hm2logout);
        hmlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(Home.this,Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                };
        });
        hmpunchin =findViewById(R.id.hm2punchin);
        hmpunchin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state="punch_in";
                biometricPrompt.authenticate(promptInfo);
            }
        });
         hmpunchout=findViewById(R.id.hm2punchout);
         hmpunchout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 state="punch_out";
                 biometricPrompt.authenticate(promptInfo);
             }
         });
        hmdetails=findViewById(R.id.hm2details);
        hmdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("USERIDss",FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                Intent i =new Intent(Home.this,UserInfo.class);
                startActivity(i);
            }
        });
       hmviewattendance=findViewById(R.id.hm2viewat);
       hmviewattendance.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View v) {
               hmprogressBar.setVisibility(View.VISIBLE);
               FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       Intent intent = new Intent(Home.this,ViewAttendance.class);
                       User user=snapshot.getValue(User.class);
                       String s[]={user.branch,user.scholarno};
                       intent.putExtra("currentuser1",s);
                       hmprogressBar.setVisibility(View.GONE);
                       startActivity(intent);

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });

           }
       });
       hmviewmessages=findViewById(R.id.hm2viewm);
       hmviewmessages.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               hmprogressBar.setVisibility(View.VISIBLE);
               FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       Intent intent = new Intent(Home.this,ViewMessages.class);
                       User user=snapshot.getValue(User.class);
                       String s[]={user.branch};
                       intent.putExtra("currentuser2",s);
                       hmprogressBar.setVisibility(View.GONE);
                       startActivity(intent);

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });

           }
       });
    }

  void authenticate(){


//        promptInfo=new BiometricPrompt.PromptInfo.Builder().setTitle("College app authentication").
//                setSubtitle("Login using fingerprint").
//                setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG|
//                        BiometricManager.Authenticators.DEVICE_CREDENTIAL).build();
    }

}
