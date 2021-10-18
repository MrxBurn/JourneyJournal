package com.example.journeyjournal;

import android.content.Context;
import android.content.pm.LabeledIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter extends ArrayAdapter<Data> {

    public Adapter(@NonNull Context context, ArrayList<Data> dataArrayList) {
        super(context, 0, dataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if(listitemView == null){
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        Data data = getItem(position);

        TextView title = listitemView.findViewById(R.id.journey_title);
        ImageView imageView = listitemView.findViewById(R.id.image);


        title.setText(data.getTitle());

        Picasso.get().load(data.getImgUrl()).into(imageView);


        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Item clicked is: " + data.getTitle() + " " + position, Toast.LENGTH_SHORT).show();
            }
        });

        return listitemView;
    }
}
