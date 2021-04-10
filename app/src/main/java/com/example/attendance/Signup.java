package com.example.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.Toast;
import java.util.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class Signup extends AppCompatActivity
{
  private   Button sgbutton;
    private TextView tv;
    private EditText sgemail,sgname, sgmobileno,sgscholar,sgpassword;
    private ProgressBar pg;
    private FirebaseAuth mAuth;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        sgbutton =findViewById(R.id.sgbutton);
        sgemail=findViewById(R.id.sgemail);
        sgname=findViewById(R.id.sgname);
        sgmobileno =findViewById(R.id.sgmobileno);
        sgpassword=findViewById(R.id.sgpassword);
        sgscholar=findViewById(R.id.sgscholarno);
        pg=findViewById(R.id.sgprogress);
        tv=findViewById(R.id.sgtv);
        mAuth = FirebaseAuth.getInstance();

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Signup.this, Login.class);
                startActivity(intent);
            }
        });

        spinner = (Spinner) findViewById(R.id.spinner);
        String[] Branch = new String[]{
                "Select branch...",
                "CSE",
                "ECE",
                "EE",
                "ME",
                "CE",
                "CHEM",
                "MME"
        };
        final List<String> Branchlist = new ArrayList<>(Arrays.asList(Branch));
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.support_simple_spinner_dropdown_item,Branchlist){
            @Override
            //this method below sets the "Select branch..." option disabled for spinner
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }

        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        final String[] selectedbranch = {"No branch selected"};
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);

                if(position > 0){
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected Branch : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                    selectedbranch[0] =selectedItemText;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText
                        (getApplicationContext(), "Select a branch", Toast.LENGTH_SHORT)
                        .show();

            }
        });
        sgbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                registeruser(selectedbranch[0]);
            }
        });

    }
    private  void registeruser(String branch) {

        final String email = sgemail.getText().toString();
        String pass = sgpassword.getText().toString();
        final String mobileno = sgmobileno.getText().toString();
        final String name = sgname.getText().toString();
        final String scholarno = sgscholar.getText().toString();
        User u=new User(name,email,mobileno,scholarno,branch);
        if (name == null || name.length() == 0) {
            sgname.setError("Please enter name");
            sgname.requestFocus();
            pg.setVisibility(View.INVISIBLE);
            return;
        }
        if (email == null || email.length() == 0) {
            sgemail.setError("Please enter email");
            sgemail.requestFocus();
            pg.setVisibility(View.INVISIBLE);
            return;
        }
        if (mobileno == null || mobileno.length() == 0) {
            sgmobileno.setError("Please enter password");
            sgmobileno.requestFocus();
            pg.setVisibility(View.INVISIBLE);
            return;
        }
        if (scholarno == null || scholarno.length() == 0) {
            sgscholar.setError("Please enter scholar number");
            sgscholar.requestFocus();
            pg.setVisibility(View.INVISIBLE);
            return;
        }
        if (branch.equals("No branch selected")) {
            Toast.makeText
                    (getApplicationContext(), "Please select branch", Toast.LENGTH_SHORT)
                    .show();
            pg.setVisibility(View.INVISIBLE);
            return;
        }
        if (pass == null || pass.length() == 0) {
            sgpassword.setError("Please enter password");
            sgpassword.requestFocus();
            pg.setVisibility(View.INVISIBLE);
            return;
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            sgemail.setError("Enter valid email");
            sgemail.requestFocus();
            pg.setVisibility(View.INVISIBLE);
            return;
        }
        if (mobileno.length() != 10) {
            sgmobileno.setError("Enter a valid mobile number");
            sgmobileno.requestFocus();
            pg.setVisibility(View.INVISIBLE);
            return;
        }


        if (pass.length() < 8) {
            sgpassword.setError("Password should not be less than 8 characters");
            sgpassword.requestFocus();
            pg.setVisibility(View.INVISIBLE);
            return;
        }
        pg.setVisibility(View.VISIBLE);
        boolean flag[] = {false};

        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //to check if the database has data node "Users" or not, if it doesn't has then create the first user of database
                if (snapshot.getValue() == null) {
                   addDeviceID(u,pass);
                }
                //since the database has "User" node and user data, so make flag[0]=true so that query of phone no and scholar no can be executed
                //by code below
                else {
                    flag[0] = true;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (flag[0] = true) {
            Query mobilenoq = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("mobileno").equalTo(mobileno);
            mobilenoq.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getChildrenCount() > 0) {
                        Toast.makeText(Signup.this, "Mobile no already taken", Toast.LENGTH_SHORT).show();
                        pg.setVisibility(View.GONE);
                        Log.d("Duplicate no", "No");
                    } else {
                        Query scholarnoq = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("scholarno").equalTo(scholarno);
                        scholarnoq.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getChildrenCount() > 0) {
                                    Toast.makeText(Signup.this, "Scholar no already exists", Toast.LENGTH_SHORT).show();
                                    pg.setVisibility(View.GONE);
                                    Log.d("Duplicate scholar no", "No");

                                } else {

                                    addDeviceID(u,pass);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {


                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {


                }
            });


        }

    }
        public void createuser(User u,String pass){
        String email=u.email;
            String branch=u.branch;
                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("User", "Hi");
                            FirebaseDatabase.getInstance().getReference("Users").
                                    child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        pg.setVisibility(View.GONE);
                                        addToGroup(branch);
                                        Toast.makeText(Signup.this, "User has been registered successfully", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(Signup.this, Home.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        FirebaseAuth.getInstance().getCurrentUser().delete();
                                        pg.setVisibility(View.GONE);
                                        Toast.makeText(Signup.this, "Failed to register user: " +
                                                task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                        } else {
                            Toast.makeText(Signup.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            pg.setVisibility(View.GONE);

                        }
                    }
                });

    }
    public void addToGroup(String branch){
        if(branch.equals("CSE")){
            FirebaseMessaging.getInstance().subscribeToTopic("CSE");
            Log.d("Subscribe","CSE");
        }
        else if(branch.equals("ECE")){
            FirebaseMessaging.getInstance().subscribeToTopic("ECE");
            Log.d("Subscribe","ECE");
        }
        else if(branch.equals("EE")){
            FirebaseMessaging.getInstance().subscribeToTopic("EE");
            Log.d("Subscribe","EE");

        }
        else if(branch.equals("ME")){
            FirebaseMessaging.getInstance().subscribeToTopic("ME");
            Log.d("Subscribe","ME");

        }
        else if(branch.equals("CE")){
            FirebaseMessaging.getInstance().subscribeToTopic("CE");
            Log.d("Subscribe","CE");
        }
        else if(branch.equals("CHEM")){
            FirebaseMessaging.getInstance().subscribeToTopic("CHEM");
            Log.d("Subscribe","CHEM");

        }
        else if(branch.equals("MME")){
            FirebaseMessaging.getInstance().subscribeToTopic("MME");

        }
    }
    public void addDeviceID(User u,String pass){

        DeviceID obj=new DeviceID(Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID));
        FirebaseDatabase.getInstance().getReference("DeviceID").orderByChild("dID").equalTo(obj.dID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    Toast.makeText(Signup.this, "Device Already Registered", Toast.LENGTH_SHORT).show();
                    pg.setVisibility(View.GONE);
                }
                else
                FirebaseDatabase.getInstance().getReference("DeviceID").child(u.scholarno).
                        setValue(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       createuser(u,pass);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        FirebaseDatabase.getInstance().getReference("DeviceID").child(u.scholarno).
//                setValue(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                createuser(u,pass);
//            }
//        });
    }
}

