package com.example.yourdesires;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yourdesires.model.Lost;
import com.example.yourdesires.model.Lost_Table;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Operator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    int pos;
    boolean back_presed = false;
    boolean light = true;
    boolean searchB = false;
    boolean text_sear = false;
    String searchType;
    DesiresAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Desires> list;
    ArrayList<Desires> listSearch;
    ArrayList<Integer> position;
    EditText desiresText,searchText;
    LinearLayout bottonSheet,lin;
    BottomSheetBehavior bottomSheetBehavior;
    TextView name_botton_sheet,text_first,text_start;
    EditText tag1,tag2,op;
    Button save,next_start;
    ImageView backIMG,filter,search,rgb,plus;
    LinearLayout con,lin_next;
    androidx.appcompat.widget.Toolbar up,dawn;
    SharedPreferences sh;
    SharedPreferences.Editor ed;
    FloatingActionButton sbros;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FlowManager.init(new FlowConfig.Builder(this).build());
        listSearch = new ArrayList<>();
        position = new ArrayList<>();
        text_start = findViewById(R.id.text_next_start);
        lin_next = findViewById(R.id.lin_next_start);
        next_start = findViewById(R.id.button_next_start);
        next_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHiddenNextWindow();
            }
        });
        sh = getSharedPreferences("0",0);
        String start = sh.getString("next_start","false");
        String prover = sh.getString("proverka","false");
        if(start.equals("false") && prover.equals("true")){
            ed = sh.edit();
            ed.putString("proverka","false");
            ed.apply();
        }else if(!start.equals("false") && prover.equals("true")){
            onShowNextWindow();
            Toast.makeText(this, text_start.getText().toString() + sh.getString("next_start","error"), Toast.LENGTH_SHORT).show();
            text_start.setText(text_start.getText().toString() + sh.getString("next_start","error"));
            ed = sh.edit();
            ed.putString("next_start","false");
            ed.putString("proverka","false");
            ed.apply();
        }
        searchType = "def";
        ed = sh.edit();
        ed.putString("search","false");
        ed.apply();
        sbros = findViewById(R.id.sbros);
        sbros.hide();
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
        bottonSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottonSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        desiresText = findViewById(R.id.text_plus);
        searchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchType.equals("status")){
                    PopupMenu search_status = new PopupMenu(v.getContext(),v);
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
                PopupMenu popupMenu2 = new PopupMenu(v.getContext(),v);
                popupMenu2.inflate(R.menu.filter_menu);
                popupMenu2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_filter1:
                                searchText.setHint(R.string.name_filter_str);
                                searchType = "name";
                                switchFilter(searchType);
                                break;
                            case R.id.menu_filter2:
                                searchText.setHint(R.string.tag_filter_str);
                                searchType = "tag";
                                switchFilter(searchType);
                                break;
                            case R.id.menu_filter3:
                                searchText.setHint(R.string.status_filter_str);
                                searchType = "status";
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
        final PopupMenu search_status = new PopupMenu(MainActivity.this,searchText);
        search_status.inflate(R.menu.search_status_menu);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String str = searchText.getText().toString();
                if(str.equals(getResources().getString(R.string.status_1)) && str.equals(getResources().getString(R.string.status_2)) && str.equals(getResources().getString(R.string.status_3))&&str.equals(getResources().getString(R.string.status_4))){
                    text_sear = true;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(searchType.equals("status")) {
                    if (!searchText.getText().toString().equals("") && !text_sear) {
                        searchText.setText("");
                    }
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
                            text_sear = true;
                            return true;
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
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
    }
    public void switchColor (String color){
        text_first = findViewById(R.id.text_first);
        plus = findViewById(R.id.plus);
        con = findViewById(R.id.con);
        up = findViewById(R.id.up_toolbar);
        dawn = findViewById(R.id.dawn_toolbar);
        rgb = findViewById(R.id.rgb);
        search = findViewById(R.id.search);
        filter = findViewById(R.id.filter);
        desiresText = findViewById(R.id.text_plus);
        switch (color){
            case "dark":
                text_first.setTextColor(getResources().getColor(R.color.white));
                up.setBackgroundResource(R.color.dark_2);
                dawn.setBackgroundResource(R.color.dark_2);
                rgb.setImageResource(R.drawable.rgb_light);
                search.setImageResource(R.drawable.search_light);
                con.setBackgroundResource(R.color.dark_back);
                desiresText.setBackgroundResource(R.color.dark_2);
                desiresText.setTextColor(getResources().getColor(R.color.white_text));
                getWindow().setStatusBarColor(getResources().getColor(R.color.dark_3));
                plus.setImageResource(R.drawable.plus_light);
                light = false;
                switchFilter(searchType);
                break;
            case "light":
                text_first.setTextColor(getResources().getColor(R.color.dark));
                up.setBackgroundResource(R.drawable.maket_up);
                dawn.setBackgroundResource(R.drawable.maket_dawn);
                rgb.setImageResource(R.drawable.rgb);
                search.setImageResource(R.drawable.search);
                con.setBackgroundResource(R.color.white_back);
                desiresText.setBackgroundResource(R.color.white);
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                desiresText.setTextColor(getResources().getColor(R.color.dark));
                plus.setImageResource(R.drawable.plus);
                light = true;
                switchFilter(searchType);
                break;
        }
        ed = sh.edit();
        ed.putString("color",color);
        ed.apply();
        editMaketRecyclerView(light,list);
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
                case "def":
                    filter.setImageResource(R.drawable.filter);
                    break;
            }
        }
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
            text_first.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
    }

    private void showFirstIMG (){
        text_first.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
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
                        list.add(new Desires(String.valueOf(desiresText.getText()), status, tag1, tag2, data, op,light,searchB,position));
                        recyclerView.getAdapter().notifyDataSetChanged();
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        getOrSetDataBase("setAll", name, op, tag1, tag2, list.size(), data, 1);

                }
            }
        }
    }

    public void remove (){
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
        }
        if(buffer.size() == 0){
            Snackbar.make(search,"Ничего не найденно",Snackbar.LENGTH_SHORT).show();
        }
        return buffer;
    }
    public void onShowNextWindow (){
        lin_next.setVisibility(View.VISIBLE);
    }
    public void onHiddenNextWindow (){
        lin_next.setVisibility(View.GONE);
    }
}
