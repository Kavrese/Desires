package com.example.yourdesires;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yourdesires.model.LogData;
import com.example.yourdesires.model.LogData_Table;
import com.example.yourdesires.model.Lost;
import com.example.yourdesires.model.Lost_Table;
import com.example.yourdesires.model.Notification;
import com.example.yourdesires.model.Notification_Table;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    int pos;
    boolean back_presed = false;
    boolean light = true;
    boolean searchB = false;
    boolean deleteMF = false;
    boolean saveBD = true;
    boolean loadBD = false;
    boolean noAlert = false;
    boolean syn = false;
    boolean random,edit,search_generator,notifications;    //Для генерирования жедания
    String searchType;
    Dialog dialog_next;
    DesiresAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Desires> list;
    ArrayList<Desires> listSearch;
    ArrayList<Integer> position;
    EditText desiresText,searchText,fake;
    Dialog dialog_setting,dialog_show_gen;
    LinearLayout bottonSheet,lin,bottonSheetGenerator;
    BottomSheetBehavior bottomSheetBehavior,bottomSheetBehaviorGeneratot;
    TextView name_botton_sheet,text_first;
    EditText tag1,tag2,op;
    Button save;
    ImageView backIMG,filter,search,rgb,plus,settings;
    LinearLayout con;
    androidx.appcompat.widget.Toolbar up,dawn;
    SharedPreferences sh;
    SharedPreferences.Editor ed;
    FloatingActionButton sbros;
    Context wrapper;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO}; //Массив с разрешениями
        requestPermissions(permissions,1);  //Запрашиваем эти разрешения
        FlowManager.init(new FlowConfig.Builder(this).build());
        wrapper = getWrapperStyle(wrapper);
        listSearch = new ArrayList<>();
        position = new ArrayList<>();
        fake = findViewById(R.id.fake);
        settings = findViewById(R.id.settings);
        dialog_next = new Dialog(MainActivity.this);
        dialog_next.setContentView(R.layout.dialog_next_window);
        dialog_next.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog_show_gen = new Dialog(MainActivity.this);
        dialog_show_gen.setContentView(R.layout.dialog_show_generator);
        dialog_show_gen.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        sh = getSharedPreferences("0",0);
        String start = sh.getString("next_start","false");
        String prover = sh.getString("proverka","false");
        //Обработка диалога "следующего запуска"
        if(start.equals("false") && prover.equals("true")){
            ed = sh.edit();
            ed.putString("proverka","false");
            ed.apply();
        }else if(!start.equals("false") && prover.equals("true")){
            List<Notification> list = getNotificationNextStart();
            if(list.size() != 0) {
                nextWindow(list);
                for (int i = 0; i < list.size(); i++)
                    deleteNotification(list.get(i).getId_notification());
            }
            ed = sh.edit();
            ed.putString("proverka","false");
            ed.putString("next_start","false");
            ed.apply();
        }
        searchType = "def";
        ed = sh.edit();
        ed.putString("search","false");
        ed.apply();
        sbros = findViewById(R.id.sbros);
        sbros.hide();
        dialog_setting = new Dialog(this);
        dialog_setting.setContentView(R.layout.dialog_settings_maket);
        dialog_setting.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        rgb = findViewById(R.id.rgb);
        up = findViewById(R.id.up_toolbar);
        con = findViewById(R.id.con);
        dawn = findViewById(R.id.dawn_toolbar);
        setSupportActionBar(up);
        setSupportActionBar(dawn);
        search = findViewById(R.id.search);
        searchText = findViewById(R.id.search_text);
        filter = findViewById(R.id.filter);
        name_botton_sheet = findViewById(R.id.name_desires_botton_sheet);
        save = findViewById(R.id.save);
        tag1 = findViewById(R.id.tag1_b);
        tag2 = findViewById(R.id.tag2_b);
        op = findViewById(R.id.op);
        backIMG = findViewById(R.id.back_img);
        lin = findViewById(R.id.lin);
        text_first = findViewById(R.id.text_first);
        //Настройка bottonSheet'ов
        bottonSheet = findViewById(R.id.bottom_sheet);
        bottonSheetGenerator = findViewById(R.id.bottom_sheet_generator_core);
        bottomSheetBehavior = BottomSheetBehavior.from(bottonSheet);
        bottomSheetBehaviorGeneratot = BottomSheetBehavior.from(bottonSheetGenerator);
        bottomSheetBehaviorGeneratot.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        generatorDesires();
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        desiresText = findViewById(R.id.text_plus);
        searchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchType.equals("status")){
                    PopupMenu search_status = new PopupMenu(wrapper,v);
                    search_status.inflate(R.menu.search_status_menu);
                    search_status.show();
                    search_status.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.menu_work_search:
                                    searchText.setText(R.string.status_1);
                                    break;
                                case R.id.menu_great_search:
                                    searchText.setText(R.string.status_2);
                                    break;
                                case R.id.menu_time_search:
                                    searchText.setText(R.string.status_4);
                                    break;
                                case R.id.menu_sleep_search:
                                    searchText.setText(R.string.status_3);
                                    break;
                            }
                            return false;
                        }
                    });
                }
            }
        });
        sbros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new DesiresAdapter(list);
                recyclerView.setAdapter(adapter);
                recyclerView.getAdapter().notifyDataSetChanged();
                sbros.hide();
            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fake.requestFocus();
                hiddenKeyboardEditText(searchText,1);
                PopupMenu popupMenu2 = new PopupMenu(wrapper,v);
                popupMenu2.inflate(R.menu.filter_menu);
                popupMenu2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_filter1:
                                searchText.setHint(R.string.name_filter_str);
                                searchType = "name";
                                searchText.setText("");
                                switchFilter(searchType);
                                break;
                            case R.id.menu_filter2:
                                searchText.setHint(R.string.tag_filter_str);
                                searchType = "tag";
                                searchText.setText("");
                                switchFilter(searchType);
                                break;
                            case R.id.menu_filter3:
                                searchText.setHint(R.string.status_filter_str);
                                searchType = "status";
                                searchText.setText("");
                                hiddenKeyboardEditText(searchText,0);
                                switchFilter(searchType);
                                break;
                            case R.id.menu_filter4:
                                searchText.setHint(R.string.data_filter_str);
                                searchType = "date";
                                searchText.setText("");
                                switchFilter(searchType);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu2.show();
            }
        });
        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(searchType.equals("date") && hasFocus){
                    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    final Calendar dateAndTime= Calendar.getInstance();
                    hiddenKeyboardEditText(searchText,0);
                    new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            dateAndTime.set(Calendar.YEAR, year);
                            dateAndTime.set(Calendar.MONTH, month);
                            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            searchText.setText(simpleDateFormat.format(dateAndTime.getTime()));
                        }
                    },
                            dateAndTime.get(Calendar.YEAR),
                            dateAndTime.get(Calendar.MONTH),
                            dateAndTime.get(Calendar.DAY_OF_MONTH))
                            .show();
                    fake.requestFocus();
                }else if(hasFocus && searchType.equals("status")){
                    final PopupMenu search_status = new PopupMenu(wrapper,searchText);
                    search_status.inflate(R.menu.search_status_menu);
                    search_status.show();
                    search_status.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_work_search:
                                    searchText.setText(R.string.status_1);
                                    break;
                                case R.id.menu_great_search:
                                    searchText.setText(R.string.status_2);
                                    break;
                                case R.id.menu_time_search:
                                    searchText.setText(R.string.status_4);
                                    break;
                                case R.id.menu_sleep_search:
                                    searchText.setText(R.string.status_3);
                                    break;
                            }
                            return true;
                        }
                    });
                }

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newDesires (String.valueOf(desiresText.getText()),String.valueOf(tag1.getText()),String.valueOf(tag2.getText()),String.valueOf(op.getText()),1);
            }
        });
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if(i == BottomSheetBehavior.STATE_HIDDEN){
                    backIMG.setVisibility(View.GONE);
                    remove();
                    hiddenKeyboard();
                }
                if(i == BottomSheetBehavior.STATE_EXPANDED){
                    backIMG.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        backIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        List<Lost> listL = getOrSetDataBase("getAll","0","0","0","0",0,"0",0);
        for(int i = 0; i<listL.size();i++){
            list.add(new Desires(String.valueOf(listL.get(i).getDesires()),
                    listL.get(i).getStatus(),String.valueOf(listL.get(i).getTag1()),
                    String.valueOf(listL.get(i).getTag2()),String.valueOf(listL.get(i).getData()),String.valueOf(listL.get(i).getDesires()),light,searchB,position));
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DesiresAdapter(list);
        recyclerView.setAdapter(adapter);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("def".equals(searchType)) {
                    Toast.makeText(MainActivity.this, "Выберите фильтр", Toast.LENGTH_SHORT).show();
                } else if (list.size() == 0){
                    Toast.makeText(MainActivity.this, "Создайте хоть одно желание", Toast.LENGTH_SHORT).show();
                } else if (String.valueOf(searchText.getText()).equals("")){
                    Toast.makeText(MainActivity.this, "Заполните поле для поиска", Toast.LENGTH_SHORT).show();
                }else{
                    listSearch = onSearch(list, searchType,String.valueOf(searchText.getText()));
                    for(int i = 0;i<listSearch.size();i++){     //Добавляем гайденные желания в логи бд
                        addLogBD("in search",createNewIdLog(),getNumEdit(listSearch.get(i).getName()),listSearch.get(i).getName());
                    }
                    if(listSearch.size() != 0) {
                        ed = sh.edit();
                        ed.putString("search","true");
                        ed.apply();
                        adapter = new DesiresAdapter(listSearch);
                        recyclerView.setAdapter(adapter);
                        recyclerView.getAdapter().notifyDataSetChanged();
                        sbros.show();
                        Animation sbros_anim = AnimationUtils.loadAnimation(MainActivity.this,R.anim.anim_sbros);
                        sbros.startAnimation(sbros_anim);
                    }
                }
            }
        });

        rgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(light)
                    switchColor("dark");
                else
                    switchColor("light");
            }
        });
        getIntentMet();
        getOrSetDataBase("num media","0","0","0","0",pos,"0",1);
        if(sh.getString("color","light").equals("light")){
            switchColor("light");
        }else{
            switchColor("dark");
        }
        onFirstIMG();
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLog();  //Для отладки
                final Switch bd = dialog_setting.findViewById(R.id.bd_switch);
                final Switch file = dialog_setting.findViewById(R.id.file_switch);
                final Switch delete = dialog_setting.findViewById(R.id.delete_switch);
                final LinearLayout alert = dialog_setting.findViewById(R.id.lin_alert);
                TextView alertText = dialog_setting.findViewById(R.id.text_alert);
                loadBooleans();
                bd.setChecked(saveBD);
                file.setChecked(loadBD);
                delete.setChecked(deleteMF);
                syn = sh.getBoolean("syn",false);
                if(!loadBD && !saveBD) {
                    file.setTextColor(getResources().getColor(R.color.grey));
                }else if(!noAlert && saveBD && loadBD){
                    alert.setVisibility(View.VISIBLE);
                }
                alertText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        noAlert = true;
                        alert.setVisibility(View.GONE);
                        syn = true;
                        saveBooleans(saveBD,loadBD,noAlert,deleteMF,syn);
                    }
                });
                bd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            saveBD = true;
                            setActivTextColor(file);
                        }else{
                            saveBD = false;
                            file.setTextColor(getResources().getColor(R.color.grey));
                            file.setChecked(false);
                        }
                        saveBooleans(saveBD,loadBD,noAlert,deleteMF,syn);
                    }
                });

                file.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked && saveBD){
                            loadBD = true;
                            if(!noAlert) {
                                alert.setVisibility(View.VISIBLE);
                            }
                        }else{
                            file.setChecked(false);
                            alert.setVisibility(View.GONE);
                            loadBD = false;
                            noAlert = false;
                        }
                        saveBooleans(saveBD,loadBD,noAlert,deleteMF,syn);
                    }
                });
                delete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked)
                            deleteMF = true;
                        else
                            deleteMF = false;

                        saveBooleans(saveBD,loadBD,noAlert,deleteMF,syn);
                    }
                });
                dialog_setting.show();
            }
        });
    }
    private List<Notification> getNotificationNextStart(){
        List<Notification> list = SQLite.select()
                .from(Notification.class)
                .where(Notification_Table.type.is("next_start"))
                .queryList();
        return list;
    }
    private void deleteNotification (int id_notification){
        SQLite.delete(Notification.class)
                .where(Notification_Table.id_notification.is(id_notification))
                .execute();
    }
    private void nextWindow(List<Notification> list) {      //Показ диалога напоминания
        boolean bool = false;
        dialog_next.show();
        Button button_next_window = dialog_next.findViewById(R.id.button_next_window);
        TextView text_next_window = dialog_next.findViewById(R.id.text_next_window);
        if(list.size() == 1)
            text_next_window.setText("Напоминание про желание: " + list.get(0).getName());
        else
        for(int i =0;i<list.size();i++) {
            if(!bool) {
                text_next_window.setText("Напоминание про желания:" + " " + list.get(i).getName());
                bool = true;
            }else{
                text_next_window.setText(text_next_window.getText().toString() + ", " + list.get(i).getName());
            }
        }
        button_next_window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_next.dismiss();
            }
        });
    }

    private void setActivTextColor (Switch v){
        if(light){
            v.setTextColor(getResources().getColor(R.color.dark));
        }else{
            v.setTextColor(getResources().getColor(R.color.white));
        }
    }
    private void saveBooleans (boolean saveBD,boolean loadBD,boolean noAlert,boolean deleteMF,boolean syn){
        ed = sh.edit();
        ed.putBoolean("saveBD",saveBD);
        ed.putBoolean("loadBD",loadBD);
        ed.putBoolean("noAlert",noAlert);
        ed.putBoolean("deleteMF",deleteMF);
        ed.putBoolean("syn",syn);
        ed.apply();
    }
    private void loadBooleans (){
        saveBD = sh.getBoolean("saveBD",true);
        loadBD = sh.getBoolean("loadBD",false);
        noAlert = sh.getBoolean("noAlert",false);
        deleteMF = sh.getBoolean("deleteMF",true);
    }
    private Context getWrapperStyle (Context wrapper){
        if(light){
            wrapper = new ContextThemeWrapper(getApplicationContext(),R.style.Pop_menu_light);
        }else{
            wrapper = new ContextThemeWrapper(getApplicationContext(),R.style.Pop_menu_dark);
        }
        return wrapper;
    }
    private void switchColor (String color){
        LinearLayout botton_sheet_generator = findViewById(R.id.bottom_sheet_generator);
        LinearLayout lin_next_window = dialog_next.findViewById(R.id.lin_next_window);
        LinearLayout lin_alert = dialog_setting.findViewById(R.id.lin_alert);
        LinearLayout dialog_lin = dialog_setting.findViewById(R.id.dialog_lin);
        LinearLayout lin_file = dialog_setting.findViewById(R.id.lin_file);
        LinearLayout lin_gen = dialog_show_gen.findViewById(R.id.main_gen);
        Button close_gen = dialog_show_gen.findViewById(R.id.close_gen);
        Button open_gen = dialog_show_gen.findViewById(R.id.open_gen);
        TextView name_gen = dialog_show_gen.findViewById(R.id.name_desires_gen);
        TextView text = dialog_show_gen.findViewById(R.id.text_gen);
        Switch bd = dialog_setting.findViewById(R.id.bd_switch);
        Switch file = dialog_setting.findViewById(R.id.file_switch);
        Switch delete = dialog_setting.findViewById(R.id.delete_switch);
        TextView text_generator = findViewById(R.id.text_botton_sheet_generatot);
        TextView text_module_2 = findViewById(R.id.text_module_2);
        CheckBox checkBox_module_2 = findViewById(R.id.checkBox_module_2);
        CheckBox checkBox_edit = findViewById(R.id.checkBox_edit);
        CheckBox checkBox_search = findViewById(R.id.checkBox_search);
        CheckBox checkBox_not = findViewById(R.id.checkBox_notifocations);
        TextView settingsText = dialog_setting.findViewById(R.id.settingsText);
        TextView text_alert = dialog_setting.findViewById(R.id.text_alert);
        ImageView alert = dialog_setting.findViewById(R.id.alert);
        Button button_next_window = dialog_next.findViewById(R.id.button_next_window);
        TextView text_next_window = dialog_next.findViewById(R.id.text_next_window);
        text_first = findViewById(R.id.text_first);
        plus = findViewById(R.id.plus);
        con = findViewById(R.id.con);
        up = findViewById(R.id.up_toolbar);
        dawn = findViewById(R.id.dawn_toolbar);
        rgb = findViewById(R.id.rgb);
        search = findViewById(R.id.search);
        filter = findViewById(R.id.filter);
        desiresText = findViewById(R.id.text_plus);
        ImageView small_line = findViewById(R.id.small_line);
        LinearLayout button_sheet = findViewById(R.id.bottom_sheet);
        TextView text_desires_sheet = findViewById(R.id.name_desires_botton_sheet);
        TextView text_botton_sheet_generator = findViewById(R.id.text_botton_sheet_generatot);
        EditText op = findViewById(R.id.op);
        EditText tag1Text = findViewById(R.id.tag1_b);
        EditText tag2Text = findViewById(R.id.tag2_b);
        Button create_generator = findViewById(R.id.create_botton_sheet);
        switch (color){
            case "dark":
                text.setTextColor(getColor(R.color.white));
                lin_gen.setBackgroundResource(R.color.dark_2);
                close_gen.setBackgroundResource(R.drawable.maket_button_sheet_dark);
                close_gen.setTextColor(getColor(R.color.white));
                open_gen.setTextColor(getColor(R.color.white));
                open_gen.setBackgroundResource(R.drawable.maket_button_sheet_dark);
                name_gen.setTextColor(getColor(R.color.white));
                text_module_2.setTextColor(getColor(R.color.white));
                checkBox_module_2.setTextColor(getColor(R.color.white));
                if(!random) {
                    checkBox_not.setTextColor(getColor(R.color.white));
                    checkBox_edit.setTextColor(getColor(R.color.white));
                    checkBox_search.setTextColor(getColor(R.color.white));
                }else {
                    checkBox_not.setTextColor(getColor(R.color.grey));
                    checkBox_edit.setTextColor(getColor(R.color.grey));
                    checkBox_search.setTextColor(getColor(R.color.grey));
                }
                text_botton_sheet_generator.setTextColor(getResources().getColor(R.color.white));
                text_generator.setTextColor(getColor(R.color.white));
                text_generator.setBackgroundResource(R.drawable.maket_up_dark);
                create_generator.setBackgroundResource(R.drawable.maket_button_sheet_dark);
                create_generator.setTextColor(getResources().getColor(R.color.white));
                botton_sheet_generator.setBackgroundResource(R.drawable.maket_butto_sheet_dark);
                small_line.setImageResource(R.drawable.small_line_dark);
                button_next_window.setTextColor(getResources().getColor(R.color.white));
                button_next_window.setBackgroundResource(R.drawable.maket_block_dark);
                text_next_window.setTextColor(getResources().getColor(R.color.white));
                lin_next_window.setBackgroundResource(R.drawable.maket_block_dark);
                recyclerView.setBackgroundColor(getResources().getColor(R.color.dark_back));
                alert.setImageResource(R.drawable.alert_light);
                text_alert.setTextColor(getResources().getColor(R.color.white));
                lin_alert.setBackgroundResource(R.drawable.maket_left_light);
                dialog_lin.setBackgroundColor(getResources().getColor(R.color.dark));
                settingsText.setTextColor(getResources().getColor(R.color.white));
                lin_file.setBackgroundResource(R.drawable.maket_block_dark);
                bd.setTextColor(getResources().getColor(R.color.white));
                bd.setBackgroundResource(R.drawable.maket_up_dark);
                file.setTextColor(getResources().getColor(R.color.white));
                file.setBackgroundResource(R.drawable.maket_up_dark);
                delete.setTextColor(getResources().getColor(R.color.white));
                settings.setImageResource(R.drawable.settings_light);
                text_first.setTextColor(getResources().getColor(R.color.white));
                up.setBackgroundResource(R.color.dark_4);
                dawn.setBackgroundResource(R.color.dark_4);
                rgb.setImageResource(R.drawable.rgb_light);
                search.setImageResource(R.drawable.search_light);
                con.setBackgroundResource(R.color.dark_back);
                desiresText.setBackgroundResource(R.color.dark_2);
                desiresText.setTextColor(getResources().getColor(R.color.white_text));
                getWindow().setStatusBarColor(getResources().getColor(R.color.dark_3));
                plus.setImageResource(R.drawable.plus_light);
                button_sheet.setBackgroundResource(R.drawable.maket_butto_sheet_dark);
                text_desires_sheet.setTextColor(getResources().getColor(R.color.white));
                op.setTextColor(getResources().getColor(R.color.white));
                tag1Text.setTextColor(getResources().getColor(R.color.white));
                tag2Text.setTextColor(getResources().getColor(R.color.white));
                save.setBackgroundResource(R.drawable.maket_button_sheet_dark);
                light = false;
                switchFilter(searchType);
                break;
            case "light":
                text.setTextColor(getColor(R.color.dark));
                lin_gen.setBackgroundResource(R.color.dark_2);
                close_gen.setBackgroundResource(R.drawable.maket_button_sheet);
                close_gen.setTextColor(getColor(R.color.dark));
                open_gen.setTextColor(getColor(R.color.dark));
                open_gen.setBackgroundResource(R.drawable.maket_button_sheet);
                name_gen.setTextColor(getColor(R.color.dark));
                text_module_2.setTextColor(getColor(R.color.dark));
                checkBox_module_2.setTextColor(getColor(R.color.dark));
                if(!random) {
                    checkBox_not.setTextColor(getColor(R.color.dark));
                    checkBox_edit.setTextColor(getColor(R.color.dark));
                    checkBox_search.setTextColor(getColor(R.color.dark));
                }else {
                    checkBox_not.setTextColor(getColor(R.color.grey));
                    checkBox_edit.setTextColor(getColor(R.color.grey));
                    checkBox_search.setTextColor(getColor(R.color.grey));
                }
                text_botton_sheet_generator.setTextColor(getResources().getColor(R.color.dark));
                text_generator.setTextColor(getColor(R.color.dark));
                text_generator.setBackgroundResource(R.drawable.maket_up);
                create_generator.setBackgroundResource(R.drawable.maket_button_sheet);
                create_generator.setTextColor(getResources().getColor(R.color.dark));
                botton_sheet_generator.setBackgroundResource(R.drawable.maket_butto_sheet);
                create_generator.setTextColor(getResources().getColor(R.color.dark));
                text_botton_sheet_generator.setTextColor(getResources().getColor(R.color.dark));
                create_generator.setBackgroundResource(R.drawable.maket_button_sheet);
                botton_sheet_generator.setBackgroundResource(R.drawable.maket_butto_sheet);
                small_line.setImageResource(R.drawable.small_line);
                button_next_window.setTextColor(getResources().getColor(R.color.dark));
                button_next_window.setBackgroundResource(R.drawable.maket_block);
                text_next_window.setTextColor(getResources().getColor(R.color.dark));
                lin_next_window.setBackgroundResource(R.drawable.maket_block);
                recyclerView.setBackgroundColor(getResources().getColor(R.color.white_back));
                alert.setImageResource(R.drawable.alert);
                text_alert.setTextColor(getResources().getColor(R.color.dark));
                lin_alert.setBackgroundResource(R.drawable.maket_left);
                dialog_lin.setBackgroundColor(getResources().getColor(R.color.white));
                settingsText.setTextColor(getResources().getColor(R.color.dark));
                lin_file.setBackgroundResource(R.drawable.maket_block);
                bd.setTextColor(getResources().getColor(R.color.dark));
                bd.setBackgroundResource(R.drawable.maket_up);
                file.setTextColor(getResources().getColor(R.color.dark));
                file.setBackgroundResource(R.drawable.maket_up);
                delete.setTextColor(getResources().getColor(R.color.dark));
                settings.setImageResource(R.drawable.settings);
                text_first.setTextColor(getResources().getColor(R.color.dark));
                up.setBackgroundColor(getColor(R.color.white));
                dawn.setBackgroundColor(getColor(R.color.white));
                rgb.setImageResource(R.drawable.rgb);
                search.setImageResource(R.drawable.search);
                con.setBackgroundResource(R.color.white_back);
                desiresText.setBackgroundResource(R.color.white);
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                desiresText.setTextColor(getResources().getColor(R.color.dark));
                plus.setImageResource(R.drawable.plus);
                button_sheet.setBackgroundResource(R.drawable.maket_butto_sheet);
                text_desires_sheet.setTextColor(getResources().getColor(R.color.dark));
                op.setTextColor(getResources().getColor(R.color.dark));
                tag1Text.setTextColor(getResources().getColor(R.color.dark));
                tag2Text.setTextColor(getResources().getColor(R.color.dark));
                save.setBackgroundResource(R.drawable.maket_button_sheet);
                light = true;
                switchFilter(searchType);
                break;
        }
        ed = sh.edit();
        ed.putString("color",color);
        ed.apply();
        editMaketRecyclerView(light,list);
        wrapper = getWrapperStyle(wrapper);
    }
    private void generatorDesires (){
        CheckBox checkBox_module_2 = findViewById(R.id.checkBox_module_2);
        final CheckBox checkBox_edit = findViewById(R.id.checkBox_edit);
        final CheckBox checkBox_search = findViewById(R.id.checkBox_search);
        final CheckBox checkBox_not = findViewById(R.id.checkBox_notifocations);
        final Button create = findViewById(R.id.create_botton_sheet);
        checkBox_edit.setOnCheckedChangeListener(this);
        checkBox_not.setOnCheckedChangeListener(this);
        checkBox_search.setOnCheckedChangeListener(this);
        checkBox_module_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    random = true;
                    checkBox_edit.setTextColor(getColor(R.color.grey));
                    checkBox_not.setTextColor(getColor(R.color.grey));
                    checkBox_search.setTextColor(getColor(R.color.grey));
                    checkBox_edit.setClickable(false);
                    checkBox_not.setClickable(false);
                    checkBox_search.setClickable(false);
                }else{
                    checkBox_edit.setClickable(true);
                    checkBox_not.setClickable(true);
                    checkBox_search.setClickable(true);
                    random = false;
                    if(light){
                        checkBox_edit.setTextColor(getColor(R.color.dark));
                        checkBox_not.setTextColor(getColor(R.color.dark));
                        checkBox_search.setTextColor(getColor(R.color.dark));
                    }else{
                        checkBox_edit.setTextColor(getColor(R.color.white));
                        checkBox_not.setTextColor(getColor(R.color.white));
                        checkBox_search.setTextColor(getColor(R.color.white));
                    }
                }
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(random || search_generator || notifications || edit)
                    algorithmGenerator();
                else
                    Snackbar.make(create,"Выберите хоть один пункт",Snackbar.LENGTH_LONG).show();
            }
        });
    }
    private void algorithmGenerator (){
        Random ran = new Random();
        if(list.size() != 0) {
            if (random) {         //Если выбранно "случайные критерии"
                showGeneratorResult(ran.nextInt(list.size()),list);     //Рандом число в зависимости от кол-во желаний
            } else {
                int index_1 = -1, index_2 = -1, index_3 = -1;
                if (edit) {           //Если выбранно "Учитывать кол-во измений"
                    int num_edit = 0;       //Доп переменная
                    for (int i = 0; i < list.size(); i++) {
                        if (num_edit < search_edit_desires(list.get(i).getName())) {      //Если кол-во изменений в желании больше чем в другом
                            index_1 = i;        //То изменяем индекс
                            num_edit = search_edit_desires(list.get(i).getName());
                        }
                    }
                }
                if (search_generator) {       //Если выбранно "Учитывать результаты поиска"
                    int num_search = 0;
                    for (int i = 0; i < list.size(); i++) {
                        if (num_search < search_search_desires(list.get(i).getName())) {
                            index_2 = i;
                            num_search = search_search_desires(list.get(i).getName());
                        }
                    }
                }
                if (notifications) {          //Если выбранно "Учитывать напоминания желания"
                    int num_notification = 0;
                    for (int i = 0; i < list.size(); i++) {
                        if (num_notification < search_notifications_desires(list.get(i).getName())) {
                            index_3 = i;
                            num_notification = search_notifications_desires(list.get(i).getName());
                        }
                    }
                }
                int result = 0;
                boolean bool = true;
                while (bool) {
                    switch (ran.nextInt(3)) {
                        case 0:
                            result = index_1;
                            break;
                        case 1:
                            result = index_2;
                            break;
                        case 2:
                            result = index_3;
                            break;
                    }
                    if(result != -1){
                        bool = false;
                    }
                }
                showGeneratorResult(result, list);
            }
        }else {
            Snackbar.make(desiresText, "Не обнаруженно ни одного желания! Создайте их!", Snackbar.LENGTH_LONG).show();
        }
    }
    private void showGeneratorResult (final int index, final List<Desires> list){
        bottomSheetBehaviorGeneratot.setState(BottomSheetBehavior.STATE_COLLAPSED);        //Закрываем сам генератор
        Button close = dialog_show_gen.findViewById(R.id.close_gen);
        Button open = dialog_show_gen.findViewById(R.id.open_gen);
        TextView name = dialog_show_gen.findViewById(R.id.name_desires_gen);
        name.setText(list.get(index).getName());
        dialog_show_gen.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_show_gen.dismiss();
            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Отправляем данные в Intent и запускаем
                Intent in = new Intent(MainActivity.this, DesiresActivity.class);
                in.putExtra("name", list.get(index).getName());
                in.putExtra("status", list.get(index).getStatus());
                in.putExtra("position", index);
                in.putExtra("data", list.get(index).getData());
                in.putExtra("op", list.get(index).getOp());
                in.putExtra("tag1", list.get(index).getTag1());
                in.putExtra("tag2", list.get(index).getTag2());
                in.putExtra("search",list.get(index).getSearch());
                in.putExtra("positions",list.get(index).getPosition());
                if(light)
                    in.putExtra("color","light");
                else
                    in.putExtra("color","dark");
                startActivity(in);
                finish();
            }
        });
    }
    private int search_search_desires (String name_desires){
        List<LogData> list = getLogDesires("get_desires",name_desires);
        int num_search = 0;
        for(int i=0;i<list.size();i++){
            if(list.get(i).getMessage().equals("in search"))
                num_search++;
        }
        return num_search;
    }
    private int search_notifications_desires (String name_desires){
        List<Notification> list = SQLite.select()
                .from(Notification.class)
                .where(Notification_Table.name.is(name_desires))
                .queryList();
        return list.size();
    }
    private int search_edit_desires (String name_desires){
        List<LogData> list = getLogDesires("get_desires",name_desires);
        return list.get(list.size()-1).getNum_edit();
    }
    public List<LogData> getLogDesires(String mode,String name){
        List<LogData> list = new ArrayList<>();
        switch(mode){
            case "get_desires":
                list = SQLite.select()
                        .from(LogData.class)
                        .where(LogData_Table.name_desires.is(name))
                        .queryList();
                return list;
            case "get_all":
                list = SQLite.select()
                        .from(LogData.class)
                        .queryList();
                return list;
        }
        return list;
    }
    public void editMaketRecyclerView (boolean light,ArrayList<Desires> list){
        for(int i = 0;i<list.size();i++){
            list.get(i).setLight(light);
        }
        recyclerView.getAdapter().notifyDataSetChanged();
    }
    public void getIntentMet (){
        String command = getIntent().getStringExtra("command");
        pos = getIntent().getIntExtra("position", 0);
        if(command != null && command.equals("new data")) {
            getOrSetDataBase("update name", getIntent().getStringExtra("name"), "0", "0", "0", pos, "0", 0);
            list.get(pos).setName(getIntent().getStringExtra("name"));
            getOrSetDataBase("update op", "0", getIntent().getStringExtra("op"), "0", "0", pos, "0", 0);
            list.get(pos).setOp(getIntent().getStringExtra("op"));
            getOrSetDataBase("update tag1", "0", "0", getIntent().getStringExtra("tag1"), "0", pos, "0", 0);
            list.get(pos).setTag1(getIntent().getStringExtra("tag1"));
            getOrSetDataBase("update tag2", "0", "0", "0", getIntent().getStringExtra("tag2"), pos, "0", 0);
            list.get(pos).setTag2(getIntent().getStringExtra("tag2"));
            recyclerView.getAdapter().notifyDataSetChanged();
        }
        if(command != null) {
            switch (command){
                case "yellow":
                    getOrSetDataBase("update status","0","0","0","0",pos,"0",1);
                    list.get(pos).setStatus(1);
                    break;
                case "green":
                    getOrSetDataBase("update status","0","0","0","0",pos,"0",2);
                    list.get(pos).setStatus(2);
                    break;
                case "orange":
                    getOrSetDataBase("update status","0","0","0","0",pos,"0",3);
                    list.get(pos).setStatus(3);
                    break;
                case "red":
                    getOrSetDataBase("update status","0","0","0","0",pos,"0",4);
                    list.get(pos).setStatus(4);
                    break;
                case "delete":
                    getOrSetDataBase("delete","0","0","0","0",pos,"0",4);
                    list.remove(pos);
                    recyclerView.getAdapter().notifyDataSetChanged();
                    onFirstIMG();
                    break;
            }
        }
    }
    public void switchFilter (String searchType2){
        if(!light) {
            switch (searchType2) {
                case "name":
                    filter.setImageResource(R.drawable.tool_name_light);
                    break;
                case "tag":
                    filter.setImageResource(R.drawable.tool_tag_light);
                    break;
                case "status":
                    filter.setImageResource(R.drawable.tool_status_light);
                    break;
                case "date":
                    filter.setImageResource(R.drawable.tool_date_light);
                    break;
                case "def":
                    filter.setImageResource(R.drawable.tool_light);
                    break;
            }
        }else{
            switch (searchType2) {
                case "name":
                    filter.setImageResource(R.drawable.tool_name);
                    break;
                case "tag":
                    filter.setImageResource(R.drawable.tool_tag);
                    break;
                case "status":
                    filter.setImageResource(R.drawable.tool_status);
                    break;
                case "date":
                    filter.setImageResource(R.drawable.tool_date);
                    break;
                case "def":
                    filter.setImageResource(R.drawable.filter);
                    break;
            }
        }
    }
    private void showLog (){
        List<LogData> list = SQLite.select()
                .from(LogData.class)
                .queryList();
        list.size();
    }
    public void openBottonSheet (View view){
        boolean bool = true;
        for(int i = 0;i<list.size();i++){
            if(list.get(i).getName().equals(String.valueOf(desiresText.getText()))){
                Toast.makeText(MainActivity.this, "Желание с таким заголовком уже есть", Toast.LENGTH_LONG).show();
                bool = false;
            }
        }
        if(bool) {
            desiresText.setEnabled(false);
            hiddenKeyboard();
            String name = String.valueOf(desiresText.getText());
            if (!name.equals("")) {
                name_botton_sheet.setText("Новое желание: " + name);
                backIMG.setVisibility(View.VISIBLE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                Toast.makeText(this, "Введите заголовок желания", Toast.LENGTH_SHORT).show();
                desiresText.setEnabled(true);
            }
        }
    }
    private void hiddenFirstIMG (){
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showFirstIMG (){
        recyclerView.setVisibility(View.INVISIBLE);
    }

    public void onFirstIMG(){
        if(list.size() == 0){
            showFirstIMG();
        }else{
            hiddenFirstIMG();
        }
    }

    public void newDesires (String name, String tag1, String tag2, String op,int status){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        if(op.equals("")){
            Toast.makeText(MainActivity.this, "Заполните описание", Toast.LENGTH_SHORT).show();
        }else {
            if (tag1.equals("") && tag2.equals("")) {
                Toast.makeText(MainActivity.this, "Заполните хотя-бы 1 tag", Toast.LENGTH_SHORT).show();
            } else {
                if(tag1.equals(tag2)){
                    Toast.makeText(this, "Повторяющееся tag'и ", Toast.LENGTH_SHORT).show();
                }else {
                    if (tag2.equals("")) {
                        tag2 = "no";
                    }
                    if (tag1.equals("")) {
                        tag1 = "no";
                    }
                    String data = dateFormat.format(new Date());
                    list.add(new Desires(desiresText.getText().toString(), status, tag1, tag2, data, op,light,searchB,position));
                    recyclerView.getAdapter().notifyDataSetChanged();
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    getOrSetDataBase("setAll", name, op, tag1, tag2, list.size(), data, 1);
                    File newFileDir = new File(Environment.getExternalStorageDirectory() + "/Desires/"+name);
                    if(!newFileDir.exists()){
                        newFileDir.mkdirs();
                    }else{
                        Toast.makeText(this, "Error: New folder already exist", Toast.LENGTH_SHORT).show();
                    }
                    addLogBD("create",createNewIdLog(),0,desiresText.getText().toString());
                }
            }
        }
    }
    public int getNumEdit (String name){
        List<LogData> list = SQLite.select()
                .from(LogData.class)
                .where(LogData_Table.name_desires.is(name))
                .queryList();
        if(list.size() == 0)
            return 0;
        return list.get(list.size()-1).getNum_edit();
    }
    public int createNewIdLog(){
        List<LogData> list = SQLite.select()
                .from(LogData.class)
                .queryList();
        return list.size()+1;
    }
    private void remove (){
        desiresText.setText("");
        this.tag1.setText("");
        this.tag2.setText("");
        this.op.setText("");
        onFirstIMG();
        desiresText.setEnabled(true);
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
    public List<Lost> getOrSetDataBase (String command,String name,String op,String tag1,String tag2,int num,String data,int status){
        FlowManager.init(new FlowConfig.Builder(this).build());
        List<Lost> losts;
        if(command.equals("getAll")){
            losts = SQLite.select()
                    .from(Lost.class)
                    .queryList();
            return losts;
        }
        if(command.equals("setAll")){
            Lost lost = new Lost();
            lost.setId(num);
            lost.setDesires(name);
            lost.setTag1(tag1);
            lost.setTag2(tag2);
            lost.setOp(op);
            lost.setStatus(status);
            lost.setData(data);
            lost.save();
        }
        if(command.equals("update status")){
            SQLite.update(Lost.class)
                    .set(Lost_Table.status.is(status))
                    .where(Lost_Table.desires.is(String.valueOf(list.get(num).getName())))
                    .execute();
        }
        if(command.equals("update name")){
            SQLite.update(Lost.class)
                    .set(Lost_Table.desires.is(name))
                    .where(Lost_Table.desires.is(String.valueOf(list.get(num).getName())))
                    .execute();
        }
        if(command.equals("update tag1")){
            SQLite.update(Lost.class)
                    .set(Lost_Table.tag1.is(tag1))
                    .where(Lost_Table.desires.is(String.valueOf(list.get(num).getName())))
                    .execute();
        }
        if(command.equals("update tag2")){
            SQLite.update(Lost.class)
                    .set(Lost_Table.tag2.is(tag2))
                    .where(Lost_Table.desires.is(String.valueOf(list.get(num).getName())))
                    .execute();
        }
        if(command.equals("update op")){
            SQLite.update(Lost.class)
                    .set(Lost_Table.op.is(op))
                    .where(Lost_Table.desires.is(String.valueOf(list.get(num).getName())))
                    .execute();
        }
        if(command.equals("delete")){
            SQLite.delete(Lost.class)
                    .where(Lost_Table.desires.is(String.valueOf(list.get(num).getName())))
                    .execute();
        }
        return null;
    }

    public void hiddenKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }
    private void hiddenKeyboardEditText (EditText text, int mode){
        text.setInputType(mode);
        text.setTextIsSelectable(true);
    }

    public ArrayList<Desires> onSearch (ArrayList<Desires> list,String command,String pole){
        ArrayList<Desires> buffer = new ArrayList<>();
        switch (command){
            case "name":
                for(int i= 0;i<list.size();i++){
                    if(list.get(i).getName().equals(pole)){
                        buffer.add(list.get(i));
                        position.add(i);
                    }
                }
                break;
            case "tag":
                for(int i= 0;i<list.size();i++){
                    if(list.get(i).getTag1().equals(pole) || list.get(i).getTag2().equals(pole)){
                        buffer.add(list.get(i));
                        position.add(i);
                    }
                }
                break;
            case "status":
                for(int i= 0;i<list.size();i++){
                    int num = 0;
                    switch (pole){
                        case "В ожидании":
                            num = 1;
                            break;
                        case "Отложено на время":
                            num = 3;
                            break;
                        case "Отложено":
                            num = 4;
                            break;
                        case "Выполнено":
                            num = 2;
                            break;
                    }
                    if( num == 0){
                        Toast.makeText(this, "Такого состояния нет", Toast.LENGTH_SHORT).show();
                    }else{
                        if(list.get(i).getStatus() == num){
                            buffer.add(list.get(i));
                            position.add(i);
                        }
                    }
                }
                break;
            case "date":
                for(int i= 0;i<list.size();i++){
                    if(list.get(i).getData().equals(pole)){
                        buffer.add(list.get(i));
                        position.add(i);
                    }
                }
                break;
        }
        if(buffer.size() == 0){
            Snackbar.make(search,"Ничего не найденно",Snackbar.LENGTH_SHORT).show();
        }
        return buffer;
    }
    public void addLogBD (String message,int id_log,int num_edit,String name_desires){
        LogData logData = new LogData();
        logData.setId_log(id_log);
        logData.setMessage(message);
        logData.setName_desires(name_desires);
        logData.setNum_edit(num_edit);
        logData.save();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.checkBox_edit:
                edit = isChecked;
                break;
            case R.id.checkBox_notifocations:
                notifications = isChecked;
                break;
            case R.id.checkBox_search:
                search_generator = isChecked;
                break;
        }
    }
}