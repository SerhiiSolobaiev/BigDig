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
    private Picture picture;

    private TextView textViewUrl;

    public PictureHolder(Context context, View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        this.context = context;
        textViewUrl = (TextView) itemView.findViewById(R.id.listItemTextViewUrl);
    }

    public void bindCrime(Picture pictureBind) {
        picture = pictureBind;
        textViewUrl.setText(picture.getUrl());
        textViewUrl.setBackgroundColor(pictureBind.getStatus().getColor());
    }

    @Override
    public void onClick(View v) {
        Utility.openApp(context, HistoryFragment.class.getSimpleName(), picture.getUrl(),
                picture.getId());
    }
}
