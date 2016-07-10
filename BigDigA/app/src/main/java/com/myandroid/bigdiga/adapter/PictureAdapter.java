package com.myandroid.bigdiga.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myandroid.bigdiga.R;
import com.myandroid.bigdiga.model.Picture;

import java.util.List;

public class PictureAdapter extends RecyclerView.Adapter<PictureHolder> {

    private List<Picture> pictures;
    private Context context;

    public PictureAdapter(Context context, List<Picture> pictures) {
        this.context = context;
        this.pictures = pictures;
    }

    @Override
    public PictureHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_item_picture, parent, false);
        return new PictureHolder(context, view);
    }

    @Override
    public void onBindViewHolder(PictureHolder holder, int position) {
        Picture crime = pictures.get(position);
        holder.bindCrime(crime);
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    public void updateList(List<Picture> data) {
        pictures = data;
        notifyDataSetChanged();
    }
}
