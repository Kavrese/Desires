package com.example.yourdesires;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int pos;
    boolean back_presed = false;
    String searchType;
    DesiresAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Desires> list;
    EditText desiresText,searchText;
    LinearLayout bottonSheet,lin;
    BottomSheetBehavior bottomSheetBehavior;
    TextView name_botton_sheet,text_first;
    EditText tag1,tag2,op;
    Button save;
    ImageView backIMG,filter,search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FlowManager.init(new FlowConfig.Builder(this).build());
        searchType = "def";
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
                                filter.setImageResource(R.drawable.tool_name);
                                break;
                            case R.id.menu_filter2:
                                searchText.setHint(R.string.tag_filter_str);
                                searchType = "tag";
                                filter.setImageResource(R.drawable.tool_tag);
                                break;
                            case R.id.menu_filter3:
                                searchText.setHint(R.string.status_filter_str);
                                searchType = "status";
                                filter.setImageResource(R.drawable.tool_status);

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
                    String.valueOf(listL.get(i).getTag2()),String.valueOf(listL.get(i).getData()),String.valueOf(listL.get(i).getDesires())));
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
                    list = onSearch(list, searchType,String.valueOf(searchText.getText()));
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        });
        getIntentMet();
        showFirstIMG();
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
                    showFirstIMG();
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
    private void showFirstIMG (){
        if(list.size() != 0){
            text_first.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
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
                        list.add(new Desires(String.valueOf(desiresText.getText()), status, tag1, tag2, data, op));
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
        if (list.size() == 0){
            recyclerView.setVisibility(View.GONE);
            text_first.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            text_first.setVisibility(View.GONE);
        }
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
                    }
                }
                break;
            case "tag":
                for(int i= 0;i<list.size();i++){
                    if(list.get(i).getTag1().equals(pole) || list.get(i).getTag2().equals(pole)){
                        buffer.add(list.get(i));
                    }
                }
                break;
            case "status":
                for(int i= 0;i<list.size();i++){
                    int num = 0;
                    switch (pole){
                        case "В ожидание":
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
                        }
                    }
                }
                break;
        }
        return buffer;
    }
}
