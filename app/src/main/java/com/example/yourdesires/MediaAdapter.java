package com.example.yourdesires;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> implements  View.OnLongClickListener, View.OnTouchListener {
    boolean mediaIsNull = true;
    boolean isSelects,isSelect,isLongClick;     //isSelects - был ли включен режим выбора  isSelect - есть ли в выборе это желание
    private MediaPlayer mediaPlayer;
    private String type;
    ArrayList<Media> arrayList;
    ArrayList selected_list = new ArrayList();
    private boolean playing;
    public SharedPreferences sh;
    private SharedPreferences.Editor ed;
    MediaViewHolder holder;
    public MediaAdapter(ArrayList<Media> arrayList){
        this.arrayList = arrayList;
    }


    @Override
    public boolean onLongClick(View v) {

        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Toast.makeText(v.getContext(), "up", Toast.LENGTH_SHORT).show();
        return false;
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView img,selected_img;
        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            selected_img = itemView.findViewById(R.id.selected_img);
        }
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.maket_recyclerview_media,parent,false);
        final MediaViewHolder mediaViewHolder = new MediaViewHolder(view);
        sh = mediaViewHolder.img.getContext().getSharedPreferences("0",0);
        mediaViewHolder.img.setOnLongClickListener(this);
      //  mediaViewHolder.img.setOnTouchListener(this);
        holder = mediaViewHolder;
        return mediaViewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull final MediaViewHolder holder, final int position) {
        final Uri uri = arrayList.get(position).getImg();
        final String str = uri.toString();
        //Определяем како-го типа файл
        if(str.contains(".jpg") && uri != null)
            type = "img";
        else if(str.contains(".mp3") && uri != null)
            type = "audio";

        if(uri != null )
        if(type.equals("img")){         //Если это изображение - загружаем его на превью
            mediaIsNull = false;
            Picasso.get()
            .load(uri)
            .placeholder(android.R.drawable.stat_sys_download_done)
            .error(android.R.drawable.ic_menu_close_clear_cancel)
            .into(holder.img);
        }else if(uri != null && type.equals("audio")){      //Если это аудио - ставим на превью сторонюю картинку
            mediaIsNull = false;
            Picasso.get()
                    .load(R.drawable.wave)
                    .error(android.R.drawable.ic_menu_close_clear_cancel)
                    .into(holder.img);
        }else {             //Если файла нет ставим другую картинку на превью
            holder.img.setImageResource(android.R.drawable.ic_menu_camera);
            mediaIsNull = true;
        }
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //Обычный клик
                    if (type.equals("img")) {            //Если открытый файл картинка - открываем его на весь экран через диалог
                        Dialog dialog = new Dialog(holder.img.getContext());
                        dialog.setContentView(R.layout.dialog_maket);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        ImageView img = dialog.findViewById(R.id.img);
                        Picasso.get()       //Загружаем главное изображение
                                .load(arrayList.get(position).getImg())
                                .placeholder(android.R.drawable.stat_sys_download)
                                .error(android.R.drawable.ic_menu_close_clear_cancel)
                                .into(img);
                        dialog.show();
                    } else if (type.equals("audio")) {         //Если файл аудио - открываем диалог с плеером
                        Dialog dialog = new Dialog(holder.img.getContext());
                        dialog.setContentView(R.layout.dialog_maket_recorder_audio);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        final ImageView button = dialog.findViewById(R.id.button);  //Кнопка пуска/паузы
                        button.setImageResource(R.drawable.resume_light);       //Меняем изображение на пуск
                        dialog.show();
                        mediaPlayer = MediaPlayer.create(holder.img.getContext(), uri);      //Создаём Медиа плеер с uri файла
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {        //По завершению аудио файла
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                stopRecord(mediaPlayer);        //Останавливаем
                                button.setImageResource(R.drawable.resume_light);//Меняем картику на пуск
                                playing = false;
                            }
                        });
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!playing) {      //Если не играет
                                    playRecord(mediaPlayer);        //Начинаем играть
                                    button.setImageResource(R.drawable.pause_light);//Меняем картику на паузу
                                    playing = true;
                                } else {
                                    playing = false;
                                    stopRecord(mediaPlayer);        //Останавливаем если играло
                                    button.setImageResource(R.drawable.resume_light);//Меняем картику на пуск
                                }
                            }
                        });
                    }


            }
        });
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    private void playRecord (MediaPlayer mediaPlayer){
        mediaPlayer.start();
    }
    private void stopRecord (MediaPlayer mediaPlayer){
        mediaPlayer.stop();     //Останавливаем плеер
        try {
            mediaPlayer.prepare();      //Подготавливаем плеер
            mediaPlayer.seekTo(0);      //Мотаем на начало
        }
        catch (Throwable t) {
        }
    }

}
