package com.example.attendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class AttendanceUserAdapter  extends FirebaseRecyclerAdapter<AttendanceUser, AttendanceUserAdapter.AttendanceUserViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AttendanceUserAdapter(@NonNull FirebaseRecyclerOptions<AttendanceUser> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AttendanceUserViewHolder holder, int position, @NonNull AttendanceUser model) {
       holder.date.setText(model.getDate());
        holder.pin.setText("PUNCHIN TIME     :  "+model.getPunchintime()+"\n"+"\n"+"\n"+"PUNCHOUT TIME :  "+model.getPunchoutime());
        Glide.with(holder.img.getContext()).load(model.getPunchoutimage()).into(holder.img);
    }

    @NonNull
    @Override
    public AttendanceUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_viewattendance_row, parent, false);
        return new AttendanceUserViewHolder(view);
    }

    class AttendanceUserViewHolder extends RecyclerView.ViewHolder{
        TextView date,pin;
        CircleImageView img;
        public AttendanceUserViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.viewatdate);
            pin=itemView.findViewById(R.id.viewapinval);
            img=itemView.findViewById(R.id.viewaimageView);
        }
    }
}
