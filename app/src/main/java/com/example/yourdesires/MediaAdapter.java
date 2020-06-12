package com.example.yourdesires;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {
    boolean mediaIsNull = true;
    ArrayList<Media> arrayList;
    public MediaAdapter(ArrayList<Media> arrayList){
        this.arrayList = arrayList;
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
        }
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.maket_recyclerview_media,parent,false);
        final MediaViewHolder mediaViewHolder = new MediaViewHolder(view);
        return mediaViewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull final MediaViewHolder holder, final int position) {
        Uri uri = arrayList.get(position).getImg();
        if(uri != null){
            mediaIsNull = false;
            Picasso.get()
            .load(uri)
            .placeholder(android.R.drawable.stat_sys_download_done)
            .error(android.R.drawable.ic_menu_close_clear_cancel)
            .into(holder.img);
        }else{
            holder.img.setImageResource(android.R.drawable.ic_menu_camera);
            mediaIsNull = true;
        }
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(holder.img.getContext());
                dialog.setContentView(R.layout.dialog_maket);
                ImageView img = dialog.findViewById(R.id.img);
                Picasso.get()
                        .load(arrayList.get(position).getImg())
                        .placeholder(android.R.drawable.stat_sys_download)
                        .error(android.R.drawable.ic_menu_close_clear_cancel)
                        .into(img);
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
