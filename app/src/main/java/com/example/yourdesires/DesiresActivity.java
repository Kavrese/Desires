package com.example.yourdesires;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.yourdesires.model.Lost;
import com.example.yourdesires.model.Lost_Table;
import com.example.yourdesires.model.MediaLost;
import com.example.yourdesires.model.MediaLost_Table;
import com.google.android.material.snackbar.Snackbar;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DesiresActivity extends AppCompatActivity implements View.OnClickListener {
boolean light,loadDB,saveBD,recording,playback,deleteFM;
String command,tag1S,tag2S,click_dialog_share;
    List<Uri> bd;
    List<Uri> local;
    List<Uri> noLocal;
    List<Uri> noBd;
    List<Uri> yes;
int status,pos;
Button button_share;
TextView data,time_des,time_des2,text;
EditText op,desires,tag1,tag2,editText_share;
ImageView statusColor,back,plus,scrap;
ImageView menu,add_media;
ImageView img_share,audio_share,text_share;
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
Dialog audio_recorder,dialog_share;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desires);
        desires = findViewById(R.id.name);
        desires.setText(getIntent().getStringExtra("name"));
        click_dialog_share = "no";
        dialog_share = new Dialog(DesiresActivity.this);
        dialog_share.setContentView(R.layout.dialog_share_maket);
        dialog_share.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        button_share = dialog_share.findViewById(R.id.button_share);
        img_share = dialog_share.findViewById(R.id.img_share);
        audio_share = dialog_share.findViewById(R.id.audio_share);
        text_share = dialog_share.findViewById(R.id.text_share);
        editText_share = dialog_share.findViewById(R.id.editText_share);
        final String standard_text_share = getResources().getString(R.string.share_text_1) +"'"+ desires.getText().toString() +"'"+ getResources().getText(R.string.share_text_2) +" "+ getResources().getString(R.string.app_name) + "! Скачай и попробуй тоже google.com";
        editText_share.setText(standard_text_share);
        dialog_share.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                click_dialog_share = "no";
                editText_share.setVisibility(View.GONE);
                editText_share.setText(standard_text_share);
            }
        });
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
        op.setText(getIntent().getStringExtra("op"));
        saveBD = sh.getBoolean("saveBD",true);
        loadDB = sh.getBoolean("loadBD",false);
        deleteFM = sh.getBoolean("deleteMF",true);
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
                pop.inflate(R.menu.status_menu_share);
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        boolean bool = false;
                        switch (item.getItemId()) {
                            case R.id.menu_share:
                                dialog_share.show();
                                img_share.setOnClickListener(DesiresActivity.this);
                                audio_share.setOnClickListener(DesiresActivity.this);
                                text_share.setOnClickListener(DesiresActivity.this);
                                break;
                            case R.id.menu_work_share:
                                editStatusColor(1);
                                command = "yellow";
                                bool = true;
                                break;
                            case R.id.menu_great_share:
                                editStatusColor(2);
                                command = "green";
                                bool = true;
                                break;
                            case R.id.menu_time_share:
                                editStatusColor(3);
                                command = "orange";
                                bool = true;
                                break;
                            case R.id.menu_sleep_share:
                                editStatusColor(4);
                                command = "red";
                                bool = true;
                                break;
                            case R.id.menu_delete_share:
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
        button_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent in = new Intent(Intent.ACTION_SEND);
                in.putExtra(Intent.EXTRA_SUBJECT,editText_share.getText().toString());
                switch(click_dialog_share){
                    case "img":
                        //Делаем скриншот
                        final File fileIMG = new File(Environment.getExternalStorageDirectory() +"/Desires/ScreenAppsDesires/",createNameFile()+".png");
                        File fileFolder = new File(Environment.getExternalStorageDirectory() +"/Desires/ScreenAppsDesires/");
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        View v1 = getWindow().getDecorView().getRootView();
                        v1.setDrawingCacheEnabled(true);
                        final Bitmap bit = Bitmap.createBitmap(v1.getDrawingCache());  //Делаем сам скриншот
                        v1.setDrawingCacheEnabled(false);
                        fileFolder.mkdirs();
                        try {
                            saveFileScreen(bit,fileIMG);
                        } catch (IOException e) {
                            Toast.makeText(DesiresActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        //Отправляем этот файл
                        Picasso.get()
                                .load(Uri.fromFile(fileIMG))
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        in.setType("image/*");
                                        Uri uri = Uri.fromFile(fileIMG);
                                        in.putExtra(Intent.EXTRA_STREAM,uri);
                                        startActivity(Intent.createChooser(in,"Share"));
                                        dialog_share.dismiss();
                                    }
                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {}
                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {}
                                });
                        break;
                    case "text":
                        in.setType("text/plain");
                        in.putExtra(Intent.EXTRA_TEXT,editText_share.getText().toString());
                        startActivity(Intent.createChooser(in,"Share"));
                        break;
                    case "audio":
                        Snackbar.make(v,"soon",Snackbar.LENGTH_SHORT).show();
                        break;
                    case "no":
                        Snackbar.make(v,"Выберите тип share",Snackbar.LENGTH_SHORT).show();
                        break;
                }
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
                        switch (item.getItemId()){
                            case R.id.menu_next:        //Напомнить завтра
                                DateFormat dateFormatDay = new SimpleDateFormat("dd.MM.yyyy");
                                Calendar cal = Calendar.getInstance();
                                Date date = null;
                                try {
                                    date = dateFormatDay.parse(dateFormatDay.format(Calendar.getInstance().getTime()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                cal.setTime(date);
                                cal.add(Calendar.DAY_OF_MONTH,1);   //+1 день
                                timePick(cal);
                                bool= true;
                                break;
                            case R.id.menu_data:        //Выбрать дату напоминания
                                datePick();
                                bool= true;
                                break;
                            case R.id.menu_return:       //Напомнить при запуске
                                ed = sh.edit();
                                ed.putString("next_start",desires.getText().toString());
                                ed.apply();
                                Snackbar.make(time_des,"Напоминание созданно",Snackbar.LENGTH_SHORT).show();
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
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();//Без этого камера не запускается
                StrictMode.setVmPolicy(builder.build());                                //
                PopupMenu media_menu_pop = new PopupMenu(wrapper,v);
                media_menu_pop.inflate(R.menu.media_menu);
                media_menu_pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_photo:
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                String name = createNameFile();
                                final File file = new File(Environment.getExternalStorageDirectory() + "/" + "Desires"+"/"+desires.getText().toString()+"/",name+".jpg");
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
                            case R.id.video_menu:
                                Intent intent_video = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                String name_video = createNameFile();
                                final File file_video = new File(Environment.getExternalStorageDirectory() + "/" + "Desires"+"/"+desires.getText().toString()+"/",name_video+".mp4");
                                outputfileURI = Uri.fromFile(file_video);
                                intent_video.putExtra(MediaStore.EXTRA_OUTPUT,outputfileURI);
                                startActivityForResult(intent_video,CAMERA_VIDEO);
                                break;
                        }
                        return false;
                    }
                });
                media_menu_pop.show();
            }
        });
        if(sh.getBoolean("syn",false)){
            synchronizedLocalMediaAndBD();
            ed = sh.edit();
            ed.putBoolean("syn",false);
            ed.apply();
        }
        if(!loadDB) {
            loadMediaFile();
        }else{
            loadMediaFileBD();
        }

    }
    private Calendar datePick (){
        final Calendar date = Calendar.getInstance();
        new DatePickerDialog(DesiresActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.set(Calendar.YEAR,year);
                date.set(Calendar.MONTH,month);
                date.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                timePick(date);
            }
        },
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)).show();
        return date;
    }
    private Calendar timePick(final Calendar date){
        new TimePickerDialog(DesiresActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                date.set(Calendar.MINUTE,minute);
                date.set(Calendar.HOUR_OF_DAY,hourOfDay);
                createNotification(date);
            }
        },
            date.get(Calendar.MINUTE),
            date.get(Calendar.HOUR_OF_DAY),true).show();

        return date;
    }
    private void createNotification (Calendar date){
        Intent notificationIntent = new Intent(DesiresActivity.this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(DesiresActivity.this,0, notificationIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(DesiresActivity.this, "CHANNEL_ID")
                        .setContentTitle("Напоминание")
                        .setContentText("Вы просили вам напомнить про желание "+desires.getText().toString())
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.icon)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true)
                        .setWhen(date.getTime().getTime());

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(DesiresActivity.this);
        notificationManager.notify(101, builder.build());
        Snackbar.make(time_des,"Уведомление будет отправленно",Snackbar.LENGTH_SHORT).show();
    }
    private void saveFileScreen (Bitmap bit,File fileIMG) throws IOException{
        FileOutputStream fileOutputStream = new FileOutputStream(fileIMG);  //Указываем путь до файла
        bit.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);   //Сохраняем
        fileOutputStream.flush();
        fileOutputStream.close();
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
                        File outputfile = new File(Environment.getExternalStorageDirectory() + "/Desires/" + desires.getText().toString() + "/", createNameFile() + "rec.mp3");
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
        if(requestCode == CAMERA_VIDEO && resultCode == RESULT_OK){
            arrayListMedia.add(new Media(outputfileURI));
            rec.getAdapter().notifyDataSetChanged();
            rec.setVisibility(View.VISIBLE);
            if(saveBD) {
                uploadUriMedia(outputfileURI,"video");
            }
        }
        if(requestCode == GALLERY && resultCode == RESULT_OK){
 /*           Uri uriGallery = data.getData();
            File imgGallery = new File(uriGallery.toString());
            File imgFolder = new File(Environment.getExternalStorageDirectory() + "/Desires/"+desires.getText().toString()+"/",createNameFile()+".jpg");
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
       if (listOfFiles != null) {
           if (listOfFiles.size() != 0) {
               for (int i = 0; i < listOfFiles.size(); i++) {
                   arrayListMedia.add(new Media(listOfFiles.get(i)));
               }
               rec.getAdapter().notifyDataSetChanged();
               rec.setVisibility(View.VISIBLE);
           } else {
               rec.setVisibility(View.GONE);
           }
       }
    }
    private int getNumMediaFileLocal (){        // //Метод получения кол-во файлов из файловой системы
        File file = new File(Environment.getExternalStorageDirectory() + "/" + "Desires"+"/"+desires.getText().toString()+"/" );
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
    private int createNewMediaId (){
        int id = 1;
        List<MediaLost> list = SQLite.select()
                .from(MediaLost.class)
                .where(MediaLost_Table.id_desires.is(searchIDDesires()))
                .queryList();
        int max = list.size()-1;
        if(list.size() != 0)
            id = list.get(max).getMedia_id() + 1;
        return id;
    }
    private List<Uri> getAllFile (){   //Метод получения всех медиа файлов в корне
        File file = new File(Environment.getExternalStorageDirectory() + "/" + "Desires"+"/"+desires.getText().toString()+"/" );
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
        bd = getAllFileBD();
        local = getAllFile();
        yes = differenceFileMediaBdLocal();
        if(yes.size() != 0) {       //Если есть одинаковае медиа файлы в бд и в локале, то находим лишние файлы
            noBd = searchNoFile("bd");
            noLocal = searchNoFile("local");
        }else{      //Если нет одинаковах файлов, то ...
            noBd = bd;      //Делаем все файлы в бд это-го желания лишними
            noLocal = local;    //Делаем все файлы в локале не сохранёнными в бд
        }
        if(local_file != 0 && bd != null && local != null){
                if(deleteFM)  //Если вкл авто удаление
                    deleteFileDB(noBd);     //Удаляем не существующие файлы из бд
                if(noLocal.size() != 0)     //Если есть что сохранять
                    saveFileDB(noLocal);    //Сохраняем недостоющие файлы в бд
        }else{
            Toast.makeText(this, "Error:Synchronized", Toast.LENGTH_SHORT).show();
        }
    }
    private List<Uri> differenceFileMediaBdLocal(){     //Метод нахождения одинаковых файлов в бд и локале
        List<Uri> yes = new ArrayList<>();
        for(int i =0;i<local.size();i++){
            for(int k =0;k<bd.size();k++){
                if(local.get(i).equals(bd.get(k))){     //Находим одинаковый файлы uri и добавляем их
                    yes.add(local.get(i));
                    break;
                }
            }
        }
        return yes;
    }
    private List<Uri> searchNoFile (String whereSearch){        //Метод поиска недостоющих/лишних (взависимости от whereSearch) файлов в бд
        List<Uri> list = new ArrayList<>();
        if(whereSearch.equals("bd")){
           list = bd;
        }else if(whereSearch.equals("local")){
            list = local;
        }
        for(int i=0;i<yes.size();i++){
            for(int k = 0;k<list.size();k++){
                if(list.get(k).equals(yes.get(i))){
                    list.remove(k);
                    break;
                }
            }
        }
       return list;
    }
    private void deleteFileDB (List<Uri> no){       //Метод удаления медиа фалов из бд
        for(int i=0;i<no.size();i++) {
            SQLite.delete(MediaLost.class)
                    .where(MediaLost_Table.uri.is(no.get(i).toString()))
                    .execute();
        }
    }
    private void saveFileDB (List<Uri> no){     //Метод сохранения файлов в бд
        for(int i=0;i<no.size();i++) {
            MediaLost mediaLost = new MediaLost();
            mediaLost.setUri(no.get(i).toString());
            mediaLost.setId_desires(searchIDDesires());
            mediaLost.setMedia_id(createNewMediaId());
            mediaLost.setType(determineFormatFileMedia(no.get(i).toString()));
            mediaLost.save();
        }
    }
    private String determineFormatFileMedia (String uri){       //Метод определения формата файла
        if(uri.contains(".jpg"))
            return "img";
        if(uri.contains(".mp3"))
            return "audio";
        if(uri.contains(".mp4"))
            return "video";
        return "img";
    }
    private boolean switchColor (String color){
        TextView textMedia = audio_recorder.findViewById(R.id.mediaText);
        LinearLayout block_media = audio_recorder.findViewById(R.id.block_media);
        LinearLayout block_share = dialog_share.findViewById(R.id.lin_share);
        LinearLayout dialog_lin_media = audio_recorder.findViewById(R.id.dialog_lin_media);
        LinearLayout dialog_lin_share = dialog_share.findViewById(R.id.dialog_lin_share);
        TextView text_share_static = dialog_share.findViewById(R.id.shareText);
        boolean light = true;
        if(color.equals("light")){
            button_share.setBackgroundResource(R.drawable.maket_block);
            button_share.setTextColor(getResources().getColor(R.color.dark));
            dialog_lin_share.setBackgroundColor(getResources().getColor(R.color.white));
            text_share_static.setTextColor(getResources().getColor(R.color.dark));
            block_share.setBackgroundResource(R.drawable.maket_block);
            editText_share.setBackgroundResource(R.drawable.maket_block);
            editText_share.setTextColor(getResources().getColor(R.color.dark));
            img_share.setImageResource(R.drawable.photo);
            audio_share.setImageResource(R.drawable.music);
            text_share.setImageResource(R.drawable.text);
            audio_share.setBackgroundResource(R.drawable.maket_left);
            text_share.setBackgroundResource(R.drawable.maket_left);
            add_media.setImageResource(R.drawable.clip);
            rec.setBackgroundResource(R.drawable.maket_right);
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
            button_share.setBackgroundResource(R.drawable.maket_block_dark);
            button_share.setTextColor(getResources().getColor(R.color.white));
            dialog_lin_share.setBackgroundColor(getResources().getColor(R.color.dark));
            text_share_static.setTextColor(getResources().getColor(R.color.white));
            block_share.setBackgroundResource(R.drawable.maket_block_dark);
            editText_share.setBackgroundResource(R.drawable.maket_block_dark);
            editText_share.setTextColor(getResources().getColor(R.color.white));
            img_share.setImageResource(R.drawable.photo_light);
            audio_share.setImageResource(R.drawable.music_light);
            text_share.setImageResource(R.drawable.text_light);
            audio_share.setBackgroundResource(R.drawable.maket_left_light);
            text_share.setBackgroundResource(R.drawable.maket_left_light);
            add_media.setImageResource(R.drawable.clip_light);
            rec.setBackgroundResource(R.drawable.maket_right_dark);
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.img_share:
                editText_share.setVisibility(View.GONE);
                click_dialog_share = "img";
                img_share.setImageResource(R.drawable.photo_click);
                if(light){
                     audio_share.setImageResource(R.drawable.music);
                     text_share.setImageResource(R.drawable.text);
                }else{
                     audio_share.setImageResource(R.drawable.music_light);
                     text_share.setImageResource(R.drawable.text_light);
                }
                break;
            case R.id.audio_share:
                editText_share.setVisibility(View.GONE);
                click_dialog_share = "audio";
                audio_share.setImageResource(R.drawable.music_click);
                if(light){
                    img_share.setImageResource(R.drawable.music);
                    text_share.setImageResource(R.drawable.text);
                }else{
                    img_share.setImageResource(R.drawable.photo_light);
                    text_share.setImageResource(R.drawable.text_light);
                }
                break;
            case R.id.text_share:
                editText_share.setVisibility(View.VISIBLE);
                click_dialog_share = "text";
                text_share.setImageResource(R.drawable.text_click);
                if(light){
                    img_share.setImageResource(R.drawable.photo);
                    text_share.setImageResource(R.drawable.text);
                }else{
                    img_share.setImageResource(R.drawable.photo_light);
                    audio_share.setImageResource(R.drawable.music_light);
                }
                break;
        }
    }
}
