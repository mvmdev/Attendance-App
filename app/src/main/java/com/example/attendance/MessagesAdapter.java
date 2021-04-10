package com.example.attendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessagesAdapter extends FirebaseRecyclerAdapter<Messages, MessagesAdapter.MessagesAdapterViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MessagesAdapter(@NonNull FirebaseRecyclerOptions<Messages> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MessagesAdapter.MessagesAdapterViewHolder holder, int position, @NonNull Messages model) {
        holder.date.setText(model.getDate());
        holder.title.setText(model.getTitle());
        holder.body.setText(model.getBody());
    }

    @NonNull
    @Override
    public MessagesAdapter.MessagesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_viewmessages_row, parent, false);
        return new MessagesAdapter.MessagesAdapterViewHolder(view);

    }
    class  MessagesAdapterViewHolder extends RecyclerView.ViewHolder{
        TextView date,title,body;
        public MessagesAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.viewmsdate);
            title=itemView.findViewById(R.id.viewmstitle);
            body=itemView.findViewById(R.id.viewmsbody);
        }
    }
}
