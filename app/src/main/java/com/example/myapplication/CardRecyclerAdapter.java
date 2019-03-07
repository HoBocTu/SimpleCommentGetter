package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.annotations.NonNull;

import java.util.ArrayList;


public class CardRecyclerAdapter extends RecyclerView.Adapter<CardRecyclerAdapter.CardViewHolder> {

    private RecyclerView.ViewHolder holder;
    private ArrayList<Comment> comments;



    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_card_layout, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, final int position) {
        final Comment comment = comments.get(position);
        holder.bind(comment.getName(), comment.getBody());

        this.holder = holder;
    }

    @Override
    public int getItemCount() {
        try {
            return comments.size();
        }
        catch (Exception e){
            return 0;
        }
    }

    public void setItems(ArrayList<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    class CardViewHolder extends RecyclerView.ViewHolder{
        private TextView body, name;

        CardViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.nameText);
            body = view.findViewById(R.id.bodyText);

        }

        private void bind(String name, String body){
            this.body.setText(body);
            this.name.setText(name);
        }
    }

}
