package com.example.yourdesires;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.StrictMode;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yourdesires.model.Lost;
import com.example.yourdesires.model.Lost_Table;
import com.example.yourdesires.model.MediaLost;
import com.example.yourdesires.model.MediaLost_Table;
import com.raizlabs.android.dbflow.sql.language.Operator;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DesiresActivity extends AppCompatActivity {
boolean light,loadDB,saveBD,recording,playback;
String command,tag1S,tag2S;
int status,pos;
TextView data,time_des,time_des2,text;
EditText op,desires,tag1,tag2;
ImageView statusColor,back,plus,scrap;
ImageView menu,add_media;
androidx.appcompat.widget.Toolbar toolbar;
LinearLayout lin,lin_tag,lin_time,lin_media;
SharedPreferences sh;
SharedPreferences.Editor ed;
RecyclerView rec;
Uri outputfileURI;
ArrayList<Media> arrayListMedia = new ArrayList<>();
Context wrapper;
MediaRecorder mediaRecorder;
int CAMERA_PHOTO = 1;
int GALLERY = 2;
int CAMERA_VIDEO = 3;
int AUDIO = 4;
Dialog audio_recorder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desires);
        mediaRecorder = new MediaRecorder ();
        audio_recorder = new Dialog(DesiresActivity.this);
        audio_recorder.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        audio_recorder.setContentView(R.layout.dialog_maket_recorder_audio);
        add_media = findViewById(R.id.add_media);
        rec = findViewById(R.id.recycler_media);
        MediaAdapter adapter = new MediaAdapter(arrayListMedia);
        rec.setAdapter(adapter);
        rec.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        sh = getSharedPreferences("0",0);
        lin = findViewById(R.id.lin_des);
        lin_tag = findViewById(R.id.lin_tag);
        lin_time = findViewById(R.id.lin_time);
        lin_media = findViewById(R.id.lin_media);
        text = findViewById(R.id.text);
        toolbar = findViewById(R.id.des_toolbar);
        command = "new data";
        op = findViewById(R.id.op);
        scrap = findViewById(R.id.scrap);
        time_des = findViewById(R.id.time_des);
        time_des2 = findViewById(R.id.time_des_2);
        tag1 = findViewById(R.id.tag1_des);
        tag2 = findViewById(R.id.tag2_des);
        data = findViewById(R.id.data_des);
        plus = findViewById(R.id.plus);
        back = findViewById(R.id.back);
        menu = findViewById(R.id.menu_tool);
        desires = findViewById(R.id.name);
        desires.setText(getIntent().getStringExtra("name"));
        op.setText(getIntent().getStringExtra("op"));
        saveBD = sh.getBoolean("saveBD",true);
        loadDB = sh.getBoolean("loadBD",false);
        tag1S = getIntent().getStringExtra("tag1");
        tag2S = getIntent().getStringExtra("tag2");
        data.setText(getIntent().getStringExtra("data"));
        status = getIntent().getIntExtra("status",1);
        light = switchColor(getIntent().getStringExtra("color"));
        wrapper = getWrapperStyle(wrapper);
        if(tag2S.equals("") || tag2S.equals("no")){
            hideTag2();
            tag1.setText(tag1S);
        }else {
            if (tag1S.equals("no") || tag1S.equals("")) {
                tag1.setText(tag2S);
                tag2.setText("");
                hideTag2();
            } else {
                tag1.setText(tag1S);
                tag2.setText(tag2S);
            }
        }

        statusColor = findViewById(R.id.statusColor);
        editStatusColor(status);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputIntent();
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pop = new PopupMenu(wrapper,v);
                pop.inflate(R.menu.status_menu);
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        boolean bool = false;
                        switch (item.getItemId()) {
                            case R.id.menu_work:
                                editStatusColor(1);
                                command = "yellow";
                                bool = true;
                                break;
                            case R.id.menu_great:
                                editStatusColor(2);
                                command = "green";
                                bool = true;
                                break;
                            case R.id.menu_time:
                                editStatusColor(3);
                                command = "orange";
                                bool = true;
                                break;
                            case R.id.menu_sleep:
                                editStatusColor(4);
                                command = "red";
                                bool = true;
                                break;
                            case R.id.menu_delete:
                                command = "delete";
                                inputIntent();
                                bool = true;
                                break;
                        }
                        return bool;
                    }
                });
                pop.show();
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag2.setText("");
                showTag2();
            }
        });
        scrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideTag2();
                tag2.setText("");
            }
        });
        time_des.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pop_time_des1 = new PopupMenu(wrapper,v);
                pop_time_des1.inflate(R.menu.menu_time_des_1);
                pop_time_des1.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        boolean bool = false;
                        SharedPreferences sh = getSharedPreferences("0",0);
                        SharedPreferences.Editor ed;
                        String str = desires.getText().toString();
                        Intent in_clock = new Intent(AlarmClock.ACTION_SET_ALARM);
                        switch (item.getItemId()){
                            case R.id.menu_next:
                                DateFormat dateFormat = new SimpleDateFormat("HH");
                                String hour_str = dateFormat.format(new Date());
                                int hour = Integer.parseInt(hour_str);
                                dateFormat = new SimpleDateFormat("mm");
                                String minutesStr = dateFormat.format(new Date());
                                int minutes = Integer.parseInt(minutesStr);
                                in_clock.putExtra(AlarmClock.EXTRA_MESSAGE,str);
                                in_clock.putExtra(AlarmClock.EXTRA_HOUR,hour);
                                in_clock.putExtra(AlarmClock.EXTRA_MINUTES,minutes);
                                startActivity(in_clock);
                                bool= true;
                                break;
                            case R.id.menu_data:

                                bool= true;
                                break;
                            case R.id.menu_return:
                                ed = sh.edit();
                                ed.putString("next_start",desires.getText().toString());
                                ed.apply();
                                bool= true;
                                break;
                        }
                        return bool;
                    }
                });
                pop_time_des1.show();
            }
        });
        time_des2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        if(!tag2.getText().toString().equals("")){
            scrap.setVisibility(View.VISIBLE);
        }
        add_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu media_menu_pop = new PopupMenu(wrapper,v);
                media_menu_pop.inflate(R.menu.media_menu);
                media_menu_pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_photo:
                                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();//Без этого камера не запускается
                                StrictMode.setVmPolicy(builder.build());                                //
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                String name = createNameFile();
                                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "Желание"+"/"+desires.getText().toString()+"/",name+".jpg");
                                outputfileURI = Uri.fromFile(file);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,outputfileURI);
                                startActivityForResult(intent,CAMERA_PHOTO);
                                break;
                            case R.id.gallery_menu:
                                Intent inGallery = new Intent(Intent.ACTION_PICK);
                                inGallery.setType("image/*");
                                startActivityForResult(inGallery,GALLERY);
                                break;
                            case R.id.audio_menu:
                                editSettingsPlayerRec();
                                audio_recorder.show();
                                break;
                        }
                        return false;
                    }
                });
                media_menu_pop.show();
            }
        });
        if(sh.getBoolean("noAlert",false)){
            synchronizedLocalMediaAndBD();
        }
        if(!loadDB) {
            loadMediaFile();
        }else{
            loadMediaFileBD();
        }

    }
    private void editSettingsPlayerRec (){
        final ImageView button = audio_recorder.findViewById(R.id.button);
                if(light)
                    button.setImageResource(R.drawable.microphone);
                else
                    button.setImageResource(R.drawable.microphone_light);
                    button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File outputfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Желание/" + desires.getText().toString() + "/", createNameFile() + "rec.mp3");
                        if(!recording && !playback) {
                            if (light)
                                button.setImageResource(R.drawable.off);
                            else
                                button.setImageResource(R.drawable.off_light);

                            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                            mediaRecorder.setOutputFile(outputfile);
                            startRecording();
                            if(light)
                                button.setImageResource(R.drawable.pause);
                            else
                                button.setImageResource(R.drawable.pause_light);
                        }else if(recording && !playback){
                            stopRecording();
                            audio_recorder.hide();
                            uploadUriMedia(Uri.fromFile(outputfile),"audio");
                            arrayListMedia.add(new Media(Uri.fromFile(outputfile)));
                            rec.getAdapter().notifyDataSetChanged();
                        }
                    }
                });
        }

    private void startRecording (){
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            recording = true;
            Toast.makeText(DesiresActivity.this, "Идёт запись", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void stopRecording (){
        if(recording){
            mediaRecorder.stop();
            mediaRecorder.release();
            recording = false;
        }
    }
    private void uploadUriMedia(Uri uri,String type){       //Сохранение медиа файлов в бд
        String str = uri.toString();
        int id_desires = searchIDDesires();
        int id_media = getNumFileMedia();   //Получаем кол-во существующих медиа файлов
        id_media++; //Добавляем +1 - это новый файл
        MediaLost mediaLost = new MediaLost();
        mediaLost.setId_desires(id_desires);
        mediaLost.setMedia_id(id_media);
        mediaLost.setUri(str);
        mediaLost.setType(type);
        mediaLost.save();
    }
    private int searchIDDesires(){      //Метод поиска id желания по названию
        Lost Lost = SQLite.select()
        .from(Lost.class)
        .where(Lost_Table.desires.is(desires.getText().toString()))
        .querySingle();
        int id = Lost.getId();
        return id;
    }
    private int getNumFileMedia (){     //Метод получения кол-во файлов в бд
        List<MediaLost> list;
        list = SQLite.select()
                .from(MediaLost.class)
                .where(MediaLost_Table.id_desires.is(searchIDDesires()))
                .queryList();
        return list.size();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PHOTO && resultCode == RESULT_OK) {
            arrayListMedia.add(new Media(outputfileURI));
            rec.getAdapter().notifyDataSetChanged();
            rec.setVisibility(View.VISIBLE);
            if(saveBD) {
                uploadUriMedia(outputfileURI,"img");
            }
        }
        if(requestCode == GALLERY && resultCode == RESULT_OK){
 /*           Uri uriGallery = data.getData();
            File imgGallery = new File(uriGallery.toString());
            File imgFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Желание/"+desires.getText().toString()+"/",createNameFile()+".jpg");
*/
        }
    }
    public String createNameFile(){
        DateFormat db = new SimpleDateFormat("ddMMyyyyHHmmss");
        String dateText = db.format(new Date());
        return dateText;
    }

    private void inputIntent(){
        Intent in = new Intent(DesiresActivity.this,MainActivity.class);
        in.putExtra("command",command);
        pos = getIntent().getIntExtra("position",0);

        if(sh.getString("search","false").equals("true")){
            ArrayList positions = getIntent().getIntegerArrayListExtra("positions");
            pos =((Integer) positions.get(pos));
            ed = sh.edit();
            ed.putString("search","false");
            ed.apply();
        }
        in.putExtra("position",pos);
        in.putExtra("op",String.valueOf(op.getText()));
        if(String.valueOf(tag1.getText()).equals("") && String.valueOf(tag2.getText()).equals("")) {
            Toast.makeText(this, "Заполните хотя-бы один тэг", Toast.LENGTH_SHORT).show();
        }else if(String.valueOf(tag1.getText()).equals(String.valueOf(tag2.getText()))){
            Toast.makeText(this, "Одинаковые тэги", Toast.LENGTH_SHORT).show();
        }else if (String.valueOf(op.getText()).equals("")){
            Toast.makeText(this, "Заполните описание желания", Toast.LENGTH_SHORT).show();
        }else if(String.valueOf(desires.getText()).equals("")) {
            Toast.makeText(this, "Заполните заголовок желания", Toast.LENGTH_SHORT).show();
        }else{
            if(tag2.getText().toString().equals("")){
                tag2S = "no";
            }
            in.putExtra("tag1",String.valueOf(tag1.getText()));
            in.putExtra("tag2",String.valueOf(tag2.getText()));
            in.putExtra("op",String.valueOf(op.getText()));
            in.putExtra("name",String.valueOf(desires.getText()));
            startActivity(in);
            finish();
        }
      }
    private void editStatusColor (int status){
        switch (status){
            case 1:
                statusColor.setImageResource(R.color.yellow);
                break;
            case 2:
                statusColor.setImageResource(R.color.green);
                break;
            case 3:
                statusColor.setImageResource(R.color.orange);
                break;
            case 4:
                statusColor.setImageResource(R.color.red);
                break;
        }
    }
    private void loadMediaFile (){                  //Загрузка медиа файлов из файловой системы
       List<Uri> listOfFiles = getAllFile();
       if(listOfFiles != null) {
           for (int i = 0; i < listOfFiles.size(); i++) {
               arrayListMedia.add(new Media(listOfFiles.get(i)));
           }
           rec.getAdapter().notifyDataSetChanged();
           rec.setVisibility(View.VISIBLE);
       }
    }
    private int getNumMediaFileLocal (){        // //Метод получения кол-во файлов из файловой системы
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "Желание"+"/"+desires.getText().toString()+"/" );
        File[] listOfFiles = file.listFiles();
        if(listOfFiles != null) {
            return listOfFiles.length;
        }
        return 0;
    }
    private void loadMediaFileBD (){            //Загрузка медиа файлов с бд
        int id_desires = searchIDDesires();
        List<MediaLost> mediaLost = SQLite.select()
                .from(MediaLost.class)
                .where(MediaLost_Table.id_desires.is(id_desires))
                .queryList();
        if(mediaLost != null) {
            for (int i = 0; i < mediaLost.size(); i++) {
                arrayListMedia.add(new Media(Uri.parse(mediaLost.get(i).getUri())));
                rec.getAdapter().notifyDataSetChanged();
                rec.setVisibility(View.VISIBLE);
            }
        }
    }
    private List<Uri> getAllFileBD (){  //Метод получения всех медиа файлов в бд
        int id_desires = searchIDDesires();
        List<Uri> URIsBD = new ArrayList<>();
        List<MediaLost> mediaLost = SQLite.select()
                .from(MediaLost.class)
                .where(MediaLost_Table.id_desires.is(id_desires))
                .queryList();
        for (int i = 0;i<mediaLost.size();i++){
            URIsBD.add(Uri.parse(mediaLost.get(i).getUri()));
        }
        return URIsBD;
    }
    private List<Uri> getAllFile (){   //Метод получения всех медиа файлов в корне
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "Желание"+"/"+desires.getText().toString()+"/" );
        File[] files = file.listFiles();
        List<Uri> URIsLocal = new ArrayList<>();
        if(files != null ) {
            for (int i = 0; i < files.length; i++) {
                URIsLocal.add(Uri.fromFile(files[i]));
            }
            return  URIsLocal;
        }
        return null;
    }
    private void synchronizedLocalMediaAndBD(){
        int bd_file = getNumFileMedia();
        int local_file = getNumMediaFileLocal();
        List<Uri> bd = getAllFileBD();
        List<Uri> local = getAllFile();
        List<Uri> no = new ArrayList<>();
        if(bd_file < local_file  && local.size() != 0 ){

            for(int i = 0;i<local.size();i++){
                for(int k = 0;k<bd.size();k++){
                    if(String.valueOf(local.get(i)).equals(String.valueOf(bd.get(k)))){
                        break;
                    }else{
                        no.add(local.get(i));       //Лист с отсутсвующями медиа файлами в бд
                    }
                }
            }
            if(bd_file == 0 && local_file != 0){
                for (int i =0;i<local_file;i++){
                    no.add(local.get(i));
                }
            }
            for(int i = 0;i<no.size();i++){     //Сохраняем недостающие файлы в бд
                int id = getNumFileMedia();
                id++;
                MediaLost mediaLost = new MediaLost();
                mediaLost.setMedia_id(id);
                mediaLost.setId_desires(searchIDDesires());
                mediaLost.setUri(no.get(i).toString());
                mediaLost.save();
            }
        }else if(bd_file > local_file){
            Toast.makeText(wrapper, "Error: В БД больше файлов", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean switchColor (String color){
        TextView textMedia = audio_recorder.findViewById(R.id.mediaText);
        LinearLayout block_media = audio_recorder.findViewById(R.id.block_media);
        LinearLayout dialog_lin_media = audio_recorder.findViewById(R.id.dialog_lin_media);
        boolean light = true;
        if(color.equals("light")){
            textMedia.setTextColor(getResources().getColor(R.color.dark));
            block_media.setBackgroundResource(R.drawable.maket_block);
            dialog_lin_media.setBackgroundColor(getResources().getColor(R.color.white));
            toolbar.setBackgroundColor(getResources().getColor(R.color.white));
            desires.setTextColor(getResources().getColor(R.color.dark));
            desires.setBackgroundResource(R.drawable.maket_up);
            lin.setBackgroundColor(getResources().getColor(R.color.white_back));
            lin_tag.setBackgroundResource(R.drawable.maket_block);
            lin_time.setBackgroundResource(R.drawable.maket_block);
            lin_media.setBackgroundResource(R.drawable.maket_block);
            text.setTextColor(getResources().getColor(R.color.dark));
            op.setTextColor(getResources().getColor(R.color.dark));
            op.setBackgroundResource(R.drawable.maket_block);
            tag1.setBackgroundResource(R.color.white);
            tag2.setBackgroundResource(R.color.white);
            time_des.setTextColor(getResources().getColor(R.color.dark));
            time_des2.setTextColor(getResources().getColor(R.color.dark));
            time_des.setBackgroundResource(R.drawable.maket_up);
            time_des2.setBackgroundResource(R.color.white);
            data.setTextColor(getResources().getColor(R.color.dark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            back.setImageResource(R.drawable.back);
            plus.setImageResource(R.drawable.plus);
            menu.setImageResource(R.drawable.menu_toolbar);
            scrap.setImageResource(R.drawable.mys);
            light = true;
        }else if (color.equals("dark")){
            textMedia.setTextColor(getResources().getColor(R.color.white));
            block_media.setBackgroundResource(R.drawable.maket_block_dark);
            dialog_lin_media.setBackgroundColor(getResources().getColor(R.color.dark));
            toolbar.setBackgroundColor(getResources().getColor(R.color.dark_2));
            desires.setTextColor(getResources().getColor(R.color.white));
            desires.setBackgroundResource(R.color.dark_2);
            lin.setBackgroundColor(getResources().getColor(R.color.dark_back));
            lin_tag.setBackgroundResource(R.drawable.maket_block_dark);
            lin_media.setBackgroundResource(R.drawable.maket_block_dark);
            lin_time.setBackgroundResource(R.drawable.maket_block_dark);
            text.setTextColor(getResources().getColor(R.color.white));
            op.setTextColor(getResources().getColor(R.color.white));
            op.setBackgroundResource(R.drawable.maket_block_dark);
            tag1.setBackgroundResource(R.color.dark_2);
            tag2.setBackgroundResource(R.color.dark_2);
            tag1.setTextColor(getResources().getColor(R.color.white));
            tag2.setTextColor(getResources().getColor(R.color.white));
            time_des.setTextColor(getResources().getColor(R.color.white));
            time_des2.setTextColor(getResources().getColor(R.color.white));
            time_des.setBackgroundResource(R.drawable.maket_up_dark);
            time_des2.setBackgroundResource(R.color.dark_2);
            data.setBackgroundResource(R.color.dark_2);
            data.setTextColor(getResources().getColor(R.color.white));
            getWindow().setStatusBarColor(getResources().getColor(R.color.dark_3));
            back.setImageResource(R.drawable.arrow_light);
            plus.setImageResource(R.drawable.plus_light);
            menu.setImageResource(R.drawable.tri_light);
            scrap.setImageResource(R.drawable.trash_light);
            light =false;
        }
        return light;
    }
    private void showTag2 (){
        tag2.setVisibility(View.VISIBLE);
        plus.setVisibility(View.GONE);
        scrap.setVisibility(View.VISIBLE);
    }
    private void hideTag2 (){
        tag2.setVisibility(View.GONE);
        plus.setVisibility(View.VISIBLE);
        scrap.setVisibility(View.GONE);
    }
    private Context getWrapperStyle (Context wrapper){
        if(light){
            wrapper = new ContextThemeWrapper(text.getContext(),R.style.Pop_menu_light);
        }else{
            wrapper = new ContextThemeWrapper(text.getContext(),R.style.Pop_menu_dark);
        }
        return wrapper;
    }
}
