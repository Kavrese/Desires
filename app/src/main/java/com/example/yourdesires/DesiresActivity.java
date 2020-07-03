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
import android.os.Handler;
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
import com.example.yourdesires.model.Notification_Table;
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
boolean light,loadDB,saveBD,recording,playback,deleteFM,back_presed;
MainActivity mainActivity = new MainActivity();
String command,tag1S,tag2S,click_dialog_share;
    List<Uri> bd;               //
    List<Uri> local;            //
    List<Uri> noLocal;          //  Для синхронизации локалки и бд
    List<Uri> noBd;             //
    List<Uri> yes;              //
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
File outputfileAudio;
RecyclerView rec;
Uri outputfileURI;
ArrayList<Media> arrayListMedia = new ArrayList<>();
Context wrapper;
MediaRecorder mediaRecorder;
int CAMERA_PHOTO = 1;
int GALLERY = 2;
int CAMERA_VIDEO = 3;
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
        add_media = findViewById(R.id.add_media);
        dialog_share.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {     //При выходе из диалога Share
                click_dialog_share = "no";
                editText_share.setVisibility(View.GONE);        //Прячем поле ввода
                editText_share.setText(standard_text_share);    //Ставим туда стандартный текст
            }
        });
        mediaRecorder = new MediaRecorder ();       //Recorder для записи audio

        //Настройка плеера аудио
        audio_recorder = new Dialog(DesiresActivity.this);
        audio_recorder.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        audio_recorder.setContentView(R.layout.dialog_maket_recorder_audio);

        //Настройка RecyclerView media
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
        command = "new data";       //Комманда для выхода в MainActivity
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
        //Загрузка данных
        saveBD = sh.getBoolean("saveBD",true);
        loadDB = sh.getBoolean("loadBD",false);
        deleteFM = sh.getBoolean("deleteMF",true);
        tag1S = getIntent().getStringExtra("tag1");
        tag2S = getIntent().getStringExtra("tag2");
        data.setText(getIntent().getStringExtra("data"));
        status = getIntent().getIntExtra("status",1);       //Сейчашний статус желаний

        light = switchColor(getIntent().getStringExtra("color"));       //Меняем темы активити
        wrapper = getWrapperStyle();     //Стиль всех PopurMenu

        //Настройка tag'ов
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

        statusColor = findViewById(R.id.statusColor);       //В toolbar'е, сейчашний статус желания
        editStatusColor(status);        //Устонавливаем сейчашний статус желания
        back.setOnClickListener(new View.OnClickListener() {        //Выход в MainActivity
            @Override
            public void onClick(View v) {
                inputIntent();
                mainActivity.addLogBD("close",mainActivity.createNewIdLog(),mainActivity.getNumEdit(desires.getText().toString()),desires.getText().toString());
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       //Меню выбора действия с желанием
                PopupMenu pop = new PopupMenu(wrapper,v);
                pop.inflate(R.menu.status_menu_share);
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        boolean bool = false;
                        switch (item.getItemId()) {
                            case R.id.menu_share:       //Поделиться
                                dialog_share.show();    //Показываем диалог поделиться
                                img_share.setOnClickListener(DesiresActivity.this);     //Клик на скриншот
                                audio_share.setOnClickListener(DesiresActivity.this);   //Пока не сделал
                                text_share.setOnClickListener(DesiresActivity.this);    //Клик на текст
                                break;
                            case R.id.menu_work_share:      //Изменить статус на "в процессе"
                                editStatusColor(1);
                                command = "yellow";     //Жёлтый
                                bool = true;
                                break;
                            case R.id.menu_great_share:     //Изменить статус на "сделанно"
                                editStatusColor(2);     //Зелённый
                                command = "green";
                                bool = true;
                                break;
                            case R.id.menu_time_share:      //Изменить статус на "отложенно на время"
                                editStatusColor(3);     //Оранжевый
                                command = "orange";
                                bool = true;
                                break;
                            case R.id.menu_sleep_share:     //Изменить статус на "отложенно"
                                editStatusColor(4);     //Красный
                                command = "red";
                                bool = true;
                                break;
                            case R.id.menu_delete_share:        //Удалить
                                command = "delete";
                                inputIntent();
                                mainActivity.addLogBD("delete",mainActivity.createNewIdLog(),mainActivity.getNumEdit(desires.getText().toString()),desires.getText().toString());
                                bool = false;
                                break;
                        }
                        if(bool)
                            mainActivity.addLogBD("update status",mainActivity.createNewIdLog(),mainActivity.getNumEdit(desires.getText().toString())+1,desires.getText().toString());
                        return bool;
                    }
                });
                pop.show();
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       //Добавляем Tag2
                tag2.setText("");
                showTag2();
            }
        });
        scrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       //Удаляем Tag2
                hideTag2();
                tag2.setText("");
            }
        });
        button_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       //Клик по кнопке поделиться
                final Intent in = new Intent(Intent.ACTION_SEND);       //Интент share
                in.putExtra(Intent.EXTRA_SUBJECT,editText_share.getText().toString());
                switch(click_dialog_share){
                    case "img":      //Делаем скриншот
                        //Путь до файла
                        final File fileIMG = new File(Environment.getExternalStorageDirectory() +"/Desires/ScreenAppsDesires/",createNameFile()+".png");
                        //Путь до папки скриншотов
                        File fileFolder = new File(Environment.getExternalStorageDirectory() +"/Desires/ScreenAppsDesires/");
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();    //Нужно для скриншота
                        StrictMode.setVmPolicy(builder.build());                                    //
                        View v1 = getWindow().getDecorView().getRootView();     //View экрана
                        v1.setDrawingCacheEnabled(true);
                        final Bitmap bit = Bitmap.createBitmap(v1.getDrawingCache());  //Делаем сам скриншот
                        v1.setDrawingCacheEnabled(false);
                        fileFolder.mkdirs();        //Создаём папку скринщотов (если она уже есть, то он её не сделает)
                        try {
                            saveFileScreen(bit,fileIMG);        //Сохраняем скриншот по uri файла
                        } catch (IOException e) {
                            Toast.makeText(DesiresActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        //Отправляем этот файл
                        Picasso.get()
                                .load(Uri.fromFile(fileIMG))
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        in.setType("image/*");      //Тип изображение
                                        Uri uri = Uri.fromFile(fileIMG);
                                        in.putExtra(Intent.EXTRA_STREAM,uri);
                                        startActivity(Intent.createChooser(in,"Share"));    //Запускаем
                                        mainActivity.addLogBD("share",mainActivity.createNewIdLog(),mainActivity.getNumEdit(desires.getText().toString()),desires.getText().toString());
                                        dialog_share.dismiss();     //Закрываем диалог
                                    }
                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {}
                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {}
                                });
                        break;
                    case "text":        //Отправить текст
                        in.setType("text/plain");   //Указываем тип текст
                        in.putExtra(Intent.EXTRA_TEXT,editText_share.getText().toString());     //Интент с тем что написал пользователь
                        startActivity(Intent.createChooser(in,"Share"));        //Запускаем
                        break;
                    case "audio":       //Пока отключенно, не знаю добавлю ли вообще
                        Snackbar.make(v,"soon",Snackbar.LENGTH_SHORT).show();
                        break;
                    case "no":
                        Snackbar.make(v,"Выберите тип share",Snackbar.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        time_des.setOnClickListener(new View.OnClickListener() {        //Клик по "напоминанию"
            @Override
            public void onClick(View v) {
                PopupMenu pop_time_des1 = new PopupMenu(wrapper,v);     //Меню выбора напоминания со стилем
                pop_time_des1.inflate(R.menu.menu_time_des_1);
                pop_time_des1.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        boolean bool = false;
                        switch (item.getItemId()){
                            case R.id.menu_next:        //Напомнить завтра
                                DateFormat dateFormatDay = new SimpleDateFormat("dd.MM.yyyy");
                                Calendar cal = Calendar.getInstance();      //Новый календарь
                                Date date = null;
                                try {
                                    //Сегоднешнюю дату трансформируем под dateFormat
                                    date = dateFormatDay.parse(getTodayDate());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                cal.setTime(date);      //Добавлем в календарь сегоднешнюю дату
                                cal.add(Calendar.DAY_OF_MONTH,1);   //+1 день в календаре
                                timePick(cal,"date_start");      //Открываем диалог выбора времени, а за ним создание уведомления
                                bool= true;
                                break;
                            case R.id.menu_data:        //Выбрать дату напоминания
                                datePick("date_start");     //Открыть диалог дату, за ним диалог выбора времени, а дальше создание уведомления
                                bool= true;
                                break;
                            case R.id.menu_return:       //Напомнить при запуске
                                ed = sh.edit();
                                ed.putString("next_start",desires.getText().toString());        //Добавляем SharedPreference "next_start" название желания
                                ed.apply();
                                Snackbar.make(time_des,"Напоминание созданно",Snackbar.LENGTH_SHORT).show();
                                saveNotificationBD("next_start",getTodayDate(),"no",0);     //Сохраняем напоминание в бд
                                bool= true;
                                break;
                        }
                        if(bool)
                            mainActivity.addLogBD("new notification",mainActivity.createNewIdLog(),mainActivity.getNumEdit(desires.getText().toString()),desires.getText().toString());
                        return bool;
                    }
                });
                pop_time_des1.show();       //Показываем меню напоминаний
            }
        });
        time_des2.setOnClickListener(new View.OnClickListener() {       //Выбор даты изменения статуса желания
            @Override
            public void onClick(View v) {
            //    saveNotificationBD("date_start_status",getTodayDate(),"no",0);
            }
        });
        if(!tag2.getText().toString().equals("")){      //Если во 2 тэге ничего нет - скрываем его
            scrap.setVisibility(View.VISIBLE);
        }
        add_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();//Без этого камера не запускается
                StrictMode.setVmPolicy(builder.build());                                //
                PopupMenu media_menu_pop = new PopupMenu(wrapper,v);        //Меню медиа со стилем
                media_menu_pop.inflate(R.menu.media_menu);
                media_menu_pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_photo:       //Фото из камеры
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    //Intent до встроенного приложения камеры
                                //Путь до файла медиа
                                final File file = new File(Environment.getExternalStorageDirectory() + "/" + "Desires"+"/"+desires.getText().toString()+"/",createNameFile()+".jpg");
                                outputfileURI = Uri.fromFile(file);     //Получаем uri
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,outputfileURI);     //Запускаем камеру с этим uri
                                startActivityForResult(intent,CAMERA_PHOTO);        //Ждём результата
                                break;
                            case R.id.gallery_menu:     //Выбор фото из галереи
                                Intent inGallery = new Intent(Intent.ACTION_PICK); //Intent до галереи
                                inGallery.setType("image/*");       //Тип выбранного файла
                                startActivityForResult(inGallery,GALLERY);      //Ждём результата
                                break;
                            case R.id.audio_menu:       //Запись аудио
                                editSettingsPlayerRec();       //Настраиваем плеер и там же запускаем
                                audio_recorder.show();      //Показываем плеер
                                break;
                            case R.id.video_menu:       //Запись видео
                                Intent intent_video = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);      //Intent до камеры
                                //Путь до файла
                                final File file_video = new File(Environment.getExternalStorageDirectory() + "/" + "Desires"+"/"+desires.getText().toString()+"/",createNameFile()+".mp4");
                                outputfileURI = Uri.fromFile(file_video);       //Получаем uri
                                intent_video.putExtra(MediaStore.EXTRA_OUTPUT,outputfileURI);   //Запускаем камеру с эти uri
                                startActivityForResult(intent_video,CAMERA_VIDEO);      //Ждём результат
                                break;
                        }
                        return false;
                    }
                });
                media_menu_pop.show();  //Показываем меню медиа
            }
        });
        if(sh.getBoolean("syn",false)){     //Если надо синхронизировать - синхронизирует
            synchronizedLocalMediaAndBD();
            ed = sh.edit();
            ed.putBoolean("syn",false);     //Меняем синхронизацию на false
            ed.apply();
        }
        if(!loadDB) {       //Если не надо загружать медиа из бд
            loadMediaFile();        //Загружаем из локалки
        }else{
            loadMediaFileBD();      //Загружаем из бд
        }

    }
    private String getTodayDate (){         //Метод получения текущей даты
        DateFormat dateFormatDay = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormatDay.format(Calendar.getInstance().getTime());
    }
    private void saveNotificationBD (String type,String date_created,String date_work,int status){      //Метод сохранения напоминания в бд
        switch (type){
            case "next_start":
                if(proverkaNotification(type,desires.getText().toString())) {
                    com.example.yourdesires.model.Notification notification = new com.example.yourdesires.model.Notification();
                    notification.setType(type);
                    notification.setDate_work(date_work);
                    notification.setDate_created(date_created);
                    notification.setStatus_edit(status);
                    notification.setId_desires(searchIDDesires());
                    notification.setId_notification(createNewIdNotification());
                    notification.setName(desires.getText().toString());
                    notification.save();
                }else
                    Snackbar.make(desires,"Напоминание, для это-го желание, уже существует",Snackbar.LENGTH_SHORT).show();
                break;
        }
        showAllBdNotification();
    }
    private void showAllBdNotification (){
        List<com.example.yourdesires.model.Notification> list = SQLite.select()
                .from(com.example.yourdesires.model.Notification.class)
                .queryList();
        list.size();
    }
    private int createNewIdNotification (){     //Метод создания нового id_notification
        List<com.example.yourdesires.model.Notification> list= SQLite.select()
                .from(com.example.yourdesires.model.Notification.class)
                .queryList();
        if(list.size() != 0)
            return list.get(list.size()-1).getId_notification()+1;

         return 1;
    }
    private boolean proverkaNotification (String type,String name){
        List<com.example.yourdesires.model.Notification> list;
        switch (type){
            case "next_start":
                list = SQLite.select()
                        .from(com.example.yourdesires.model.Notification.class)
                        .where(Notification_Table.name.is(name))
                        .and(Notification_Table.type.is(type))
                        .queryList();
                if(list.size() == 0){
                    return true;
                }else
                    return false;
        }
        return false;
    }
    private Calendar datePick (final String type){
        final Calendar date = Calendar.getInstance();   //Новый календырь для времени уведомления
        new DatePickerDialog(DesiresActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.set(Calendar.YEAR,year);       //Добавляем год
                date.set(Calendar.MONTH,month);     //Добавляем месяц
                date.set(Calendar.DAY_OF_MONTH,dayOfMonth); //Добавляем день
                timePick(date,type);     //Открывает диалог с выбором времени
            }
        },
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)).show();
        return date;
    }
    private Calendar timePick(final Calendar date,String type){
        new TimePickerDialog(DesiresActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                date.set(Calendar.MINUTE,minute);               //Добавляем минуты
                date.set(Calendar.HOUR_OF_DAY,hourOfDay);       //Добавляем часы
                createNotification(date);       //Создаём уведомление с добавленным временем
               // saveNotificationBD(type,getTodayDate(),date,0);
            }
        },
            date.get(Calendar.MINUTE),
            date.get(Calendar.HOUR_OF_DAY),true).show();

        return date;
    }
    private void createNotification (Calendar date){
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "101")
                        .setAutoCancel(false)
                        .setSmallIcon(R.drawable.icon)
                        .setWhen(date.getTimeInMillis())
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Напоминание")
                        .setContentText("Вы просили напомнить про желание")
                        .setPriority(Notification.PRIORITY_HIGH);

        createChannelIfNeeded(notificationManager);
        notificationManager.notify(101, notificationBuilder.build());
        Snackbar.make(time_des,"Уведомление будет отправленно",Snackbar.LENGTH_SHORT).show();
    }
    public static void createChannelIfNeeded(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("101", "101", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }
    private void saveFileScreen (Bitmap bit,File fileIMG) throws IOException{   //Метод сохранение скриншота через bitmap
        FileOutputStream fileOutputStream = new FileOutputStream(fileIMG);  //Указываем путь до файла
        bit.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);   //Сохраняем
        fileOutputStream.flush();
        fileOutputStream.close();
    }
    private void editSettingsPlayerRec (){
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        final ImageView button = audio_recorder.findViewById(R.id.button);
                if(light)
                    button.setImageResource(R.drawable.microphone);
                else
                    button.setImageResource(R.drawable.microphone_light);
                    button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(outputfileAudio == null)
                            outputfileAudio = new File(Environment.getExternalStorageDirectory() + "/Desires/" + desires.getText().toString() + "/", createNameFile() + "rec.mp3");
                        if(!recording && !playback) {
                            if (light)
                                button.setImageResource(R.drawable.off);
                            else
                                button.setImageResource(R.drawable.off_light);
                            mediaRecorder.setOutputFile(outputfileAudio);
                            startRecording();
                            if(light)
                                button.setImageResource(R.drawable.pause);
                            else
                                button.setImageResource(R.drawable.pause_light);
                        }else if(recording && !playback){
                            stopRecording();
                            audio_recorder.hide();
                            uploadUriMedia(Uri.fromFile(outputfileAudio),"audio");
                            arrayListMedia.add(new Media(Uri.fromFile(outputfileAudio),desires.getText().toString()));
                            rec.setVisibility(View.VISIBLE);
                            rec.getAdapter().notifyDataSetChanged();
                            mainActivity.addLogBD("create new media audio",mainActivity.createNewIdLog(),mainActivity.getNumEdit(desires.getText().toString()),desires.getText().toString());
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
        MediaLost mediaLost = new MediaLost();
        mediaLost.setId_desires(searchIDDesires());
        mediaLost.setMedia_id(createNewMediaId());
        mediaLost.setUri(uri.toString());
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
            arrayListMedia.add(new Media(outputfileURI,desires.getText().toString()));
            mainActivity.addLogBD("new media photo",mainActivity.createNewIdLog(),mainActivity.getNumEdit(desires.getText().toString()),desires.getText().toString());
            rec.getAdapter().notifyDataSetChanged();
            rec.setVisibility(View.VISIBLE);
            if(saveBD) {
                uploadUriMedia(outputfileURI,"img");
            }
        }
        if(requestCode == CAMERA_VIDEO && resultCode == RESULT_OK){
            arrayListMedia.add(new Media(outputfileURI,desires.getText().toString()));
            mainActivity.addLogBD("new media video",mainActivity.createNewIdLog(),mainActivity.getNumEdit(desires.getText().toString()),desires.getText().toString());
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

    private void inputIntent(){     //Выход обратно с сохранением данных для последующий ихменений в самом желании и в бд
        Intent in = new Intent(DesiresActivity.this,MainActivity.class);
        in.putExtra("command",command);     //Сохраняем комманду, что сделать с эти желанием в MainActivity
        pos = getIntent().getIntExtra("position",0);
        //Я не помню зачем это здесь
        if(sh.getString("search","false").equals("true")){
            ArrayList positions = getIntent().getIntegerArrayListExtra("positions");
            pos =((Integer) positions.get(pos));
            ed = sh.edit();
            ed.putString("search","false");
            ed.apply();
        }
        //Условия выхода
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
            in.putExtra("position",pos);  //Сохраняем позицию это-го желания для дальнейшего применения command
            in.putExtra("op",String.valueOf(op.getText())); //Сохраняем описание
            in.putExtra("tag1",String.valueOf(tag1.getText())); //Созраняем tag1
            in.putExtra("tag2",String.valueOf(tag2.getText())); //Созраняем tag2
            in.putExtra("name",String.valueOf(desires.getText()));  //Созраняем название желания
            startActivity(in);  //Запуск в MainActivity
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
                   arrayListMedia.add(new Media(listOfFiles.get(i),desires.getText().toString()));
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
                arrayListMedia.add(new Media(Uri.parse(mediaLost.get(i).getUri()),desires.getText().toString()));
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
    private int createNewMediaId (){        //Создаём новое media_id
        List<MediaLost> list = SQLite.select()  //
                .from(MediaLost.class)          //Получаем лист всех media_id (не их кол-во, а их самих)
                .queryList();                   //
        return list.size()+1;       //Потом просто прибавляем +1 к размеру листа - это и будет новое media_id
    }
    private List<Uri> getAllFile (){   //Метод получения всех медиа файлов в корне
        //Путь до папки желания
        File file = new File(Environment.getExternalStorageDirectory() + "/" + "Desires"+"/"+desires.getText().toString()+"/" );
        File[] files = file.listFiles();    //Берём в массив все файлы в папке
        List<Uri> URIsLocal = new ArrayList<>();    //Лист uri
        if(files != null ) {    //Если в паке есть файлы
            for (int i = 0; i < files.length; i++) {
                URIsLocal.add(Uri.fromFile(files[i]));      //Добавляем в лист uri файла поочереди из массива
            }
            return  URIsLocal;      //Возращяем лист uri
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
    private Context getWrapperStyle (){
        if(light){
            wrapper = new ContextThemeWrapper(text.getContext(),R.style.Pop_menu_light);
        }else{
            wrapper = new ContextThemeWrapper(text.getContext(),R.style.Pop_menu_dark);
        }
        return wrapper;
    }
    @Override
    public void onBackPressed() {
        if(!back_presed){
            back_presed = true;
            Toast.makeText(this, "Нажмите ещё раз что-бы выйти", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    back_presed = false;
                }
            },2000);
        }else{
            System.exit(1);
        }
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
