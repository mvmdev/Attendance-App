package com.example.attendance;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewAttendance extends AppCompatActivity implements Serializable {
    private RecyclerView recyclerView;
    private AttendanceUserAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewattendance);
        recyclerView=findViewById(R.id.viewarecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CalendarUtil obj=CalendarUtil.getcalendar();
        String path;
        String[] s = getIntent().getStringArrayExtra("currentuser1");
        path= String.valueOf(obj.cYear + "/" + s[0] + "/" + s[1] + "/" + obj.month);
        FirebaseRecyclerOptions<AttendanceUser> options =
                new FirebaseRecyclerOptions.Builder<AttendanceUser>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("AttendanceUser").child(path), AttendanceUser.class)
                        .build();
        adapter=new AttendanceUserAdapter(options);
        recyclerView.setAdapter(adapter);
    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
