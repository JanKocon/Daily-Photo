package com.example.jan.praca;

import android.app.Activity;
import android.content.Context;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.jan.praca.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class GalleryAdapter extends RecyclerView.Adapter {
    List<String> galleryItems;
    Context context;

    public GalleryAdapter(Context context) {
        this.context = context;
        this.galleryItems = new ArrayList<>();
    }

    public void addGalleryItems(List<String> galleryItems) {
        int previousSize = this.galleryItems.size();
        this.galleryItems.addAll(galleryItems);
        notifyItemRangeInserted(previousSize, galleryItems.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.custom_row_gallery_item, parent, false);
        return new GalleryItemHolder(row);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        String currentItem = galleryItems.get(position);
        File imageViewThoumb = new File(currentItem);
        GalleryItemHolder galleryItemHolder = (GalleryItemHolder) holder;
        Picasso.get()
                .load(imageViewThoumb)
                .centerCrop()
                .resize(getScreenWidth(context) / 3,
                        getScreenHeight(context) / 4)
                .into(galleryItemHolder.imageViewThumbnail);

    }

    @Override
    public int getItemCount() {
        return galleryItems.size();
    }

    public class GalleryItemHolder extends RecyclerView.ViewHolder {
        ImageView imageViewThumbnail;

        public GalleryItemHolder(View itemView) {
            super(itemView);
            imageViewThumbnail = (ImageView) itemView.findViewById(R.id.imageViewThumbnail);
        }
    }
    public  int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }


    public  int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }



}
