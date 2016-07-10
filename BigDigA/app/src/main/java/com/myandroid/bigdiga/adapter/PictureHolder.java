package com.myandroid.bigdiga.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myandroid.bigdiga.R;
import com.myandroid.bigdiga.Utility;
import com.myandroid.bigdiga.fragment.HistoryFragment;
import com.myandroid.bigdiga.model.Picture;

public class PictureHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context context;

    private TextView textViewUrl;
    private ImageView imageView;

    private Picture picture;

    public PictureHolder(Context context, View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        this.context = context;
        textViewUrl = (TextView) itemView.findViewById(R.id.listItemTextViewUrl);
        imageView = (ImageView) itemView.findViewById(R.id.listItemImageView);
    }

    public void bindCrime(Picture pictureBind) {
        picture = pictureBind;
        textViewUrl.setText(picture.getUrl());
        textViewUrl.setBackgroundColor(pictureBind.getStatus().getColor());

        //Picasso.with(getActivity()).load(picture.getUrl()).into(imageView);
    }

    @Override
    public void onClick(View v) {
//        Toast.makeText(context, picture.getUrl() + " clicked!", Toast.LENGTH_SHORT).show();
        Utility.openApp(context, HistoryFragment.class.getSimpleName(), picture.getUrl());
    }
}
