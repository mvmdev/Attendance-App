package com.example.attendance;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInfo extends AppCompatActivity{
    DatabaseReference reff;
    FirebaseUser u;
    TextView t1,t2,t3,t4,t5;
   ProgressBar uipg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        t1=findViewById(R.id.uinameval);
        t2=findViewById(R.id.uiemailval);
        t3=findViewById(R.id.uischolarval);
        t4=findViewById(R.id.uimobileval);
        t5=findViewById(R.id.uibranchval);
        uipg=findViewById(R.id.uiprogressBar);
        uipg.setVisibility(View.VISIBLE);
        u=FirebaseAuth.getInstance().getCurrentUser();
       t1.setText(u.getUid().toString());
        reff=FirebaseDatabase.getInstance().getReference("Users");
        reff.child(u.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User unifo=snapshot.getValue(User.class);
                t1.setText(unifo.name);
                t2.setText(unifo.email);
                t3.setText(unifo.scholarno.toString());
                t4.setText(unifo.mobileno.toString());
                t5.setText(unifo.branch.toString());
                 uipg.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                uipg.setVisibility(View.GONE);
            }
        });
    }




}
