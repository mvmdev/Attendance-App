package com.example.attendance;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class Login extends AppCompatActivity {
    TextView lgsignuptv, lgforgottv;
    EditText lgemail,lgpassword;
    Button lgbutton;
    ProgressBar lgpg;
    private FirebaseAuth mAuth;
    @Override
    protected void onStart() {
        super.onStart();
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!= null){
            Intent intent = new Intent(Login.this,Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Attendance);
        setContentView(R.layout.activity_login);
        lgbutton=findViewById(R.id.lgbutton);
        lgemail=findViewById(R.id.lgemail);
        lgpassword=findViewById(R.id.lgpassword);
        lgsignuptv=findViewById(R.id.lgsignuptv);
        lgpg=findViewById(R.id.lgprogressBar);
        lgforgottv=findViewById(R.id.lgforgottv);
        mAuth = FirebaseAuth.getInstance();
        lgsignuptv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Signup.class);
                v.getContext().startActivity(intent);
            }
        });
        lgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        lgforgottv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (!Patterns.EMAIL_ADDRESS.matcher(lgemail.getText().toString()).matches()) {
                    lgemail.setError("Enter valid email address");
                    lgemail.requestFocus();
                    }
                    else{
                        lgpg.setVisibility(View.VISIBLE);
                    mAuth.sendPasswordResetEmail(lgemail.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Login.this,"Reset Link sent to your email",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login.this,"Error ! Reset Link not sent, User may not be registered",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                lgpg.setVisibility(View.GONE);
            }
        });
    }
    private void login() {

        String email = lgemail.getText().toString();
        String pwd = lgpassword.getText().toString();
        if (email == null || email.length() == 0) {
            lgemail.setError("Please enter email");
            lgemail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            lgemail.setError("Enter valid email address");
            lgemail.requestFocus();
            return;
        }
        lgpg.setVisibility(View.VISIBLE);
        String currdID= Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
        mAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                           User curruser=snapshot.getValue(User.class);
                            String scholarno=curruser.getScholarno();
                            FirebaseDatabase.getInstance().getReference("DeviceID").child(scholarno).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.getValue(DeviceID.class).getdID().equals(currdID)){
                                        lgpg.setVisibility(View.GONE);
                                        FirebaseMessaging.getInstance().subscribeToTopic(curruser.branch);
                                        Intent intent = new Intent(Login.this, Home.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {
                                        mAuth.signOut();
                                        lgpg.setVisibility(View.GONE);
                                        Toast.makeText(Login.this,"Please login with your registered device. In case of device issue, contact admin",
                                                Toast.LENGTH_LONG).show();
                                    }

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
                else{
                    lgpg.setVisibility(View.GONE);
                    Toast.makeText(Login.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
//    public boolean checkdID(){
//        final boolean[] flag = {false};
//       String currdID= Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
//       User curruser=Login.getcurruser();
//       String branch=curruser.getBranch();
//       String scholarno=curruser.getScholarno();
//        FirebaseDatabase.getInstance().getReference("DeviceID").child(branch+"/"+scholarno).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.getValue(DeviceID.class).getdID()== currdID){
//                    flag[0] =true;
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        return flag[0];
//    }
//    public static User getcurruser(){
//        final User[] curruser = new User[1];
//        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                curruser[0] =snapshot.getValue(User.class);
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        return curruser[0];
//    }

}

