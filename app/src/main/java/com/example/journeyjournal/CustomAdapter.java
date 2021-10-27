package com.example.journeyjournal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    Context context;
    ArrayList<Data> dataArrayList;

    public CustomAdapter(Context context, ArrayList<Data> dataArrayList) {
        this.context = context;
        this.dataArrayList = dataArrayList;
    }

    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {

        Data data = dataArrayList.get(position);

        holder.jTitle.setText(data.title);



        Picasso.get().load(data.imgUrl).into(holder.jImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(v.getContext(), EditJourney.class);
                i.putExtra("title", data.getTitle());
                i.putExtra("description", data.getDescription());
                i.putExtra("journeyID", data.getJourneyID());
                i.putExtra("imgUrl", data.getImgUrl());
                v.getContext().startActivity(i);


            }
        });


    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView jTitle;
        ImageView jImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            jTitle = itemView.findViewById(R.id.journey_title);
            jImage = itemView.findViewById(R.id.image);


        }
    }
}
