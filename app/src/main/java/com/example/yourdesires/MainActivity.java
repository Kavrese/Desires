package com.example.yourdesires;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yourdesires.model.Lost;
import com.example.yourdesires.model.Lost_Table;
import com.example.yourdesires.model.MyDataBase;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {
    boolean back_presed = false;
    DesiresAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Desires> list;
    EditText desiresText;
    LinearLayout bottonSheet;
    BottomSheetBehavior bottomSheetBehavior;
    TextView name_botton_sheet,text_first;
    EditText tag1,tag2,op;
    Button save;
    ImageView backIMG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name_botton_sheet = findViewById(R.id.name_desires_botton_sheet);
        save = findViewById(R.id.save);
        tag1 = findViewById(R.id.tag1_b);
        tag2 = findViewById(R.id.tag2_b);
        op = findViewById(R.id.op);
        backIMG = findViewById(R.id.back_img);
        text_first = findViewById(R.id.text_first);
        bottonSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottonSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        list = new ArrayList<Desires>();
        recyclerView = findViewById(R.id.recycler_view);
        desiresText = findViewById(R.id.text_plus);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DesiresAdapter(list);
        recyclerView.setAdapter(adapter);
        if(list.size() != 0){
            text_first.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newDesires (String.valueOf(desiresText.getText()),String.valueOf(tag1.getText()),String.valueOf(tag2.getText()),String.valueOf(op.getText()));
            }
        });
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if(i == BottomSheetBehavior.STATE_HIDDEN){
                    backIMG.setVisibility(View.GONE);
                    remove();
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
        //List<Lost> listL = getOrSetDataBase("getAll","0","0","0","0",0,"0");
    }

    public void openBottonSheet (View view){
        List <Lost> losts = SQLite.select()
                .from(Lost.class)
                .where(Lost_Table.id.is(2))
                .queryList();
        Toast.makeText(this, String.valueOf(losts.get(0)), Toast.LENGTH_SHORT).show();
        desiresText.setEnabled(false);
        hiddenKeyboard();
        String name = String.valueOf(desiresText.getText());
        if(!name.equals("")){
            name_botton_sheet.setText("Новое желание: "+name);
            backIMG.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else{
            Toast.makeText(this, "Введите заголовок желания", Toast.LENGTH_SHORT).show();
        }
    }

    public void newDesires (String name, String tag1, String tag2, String op){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        if(op.equals("")){
            Toast.makeText(MainActivity.this, "Заполните описание", Toast.LENGTH_SHORT).show();
        }else {
            if (tag1.equals("")) {
                Toast.makeText(MainActivity.this, "Заполните хотя-бы 1 tag", Toast.LENGTH_SHORT).show();
            } else {
                if(tag1.equals(tag2)){
                    Toast.makeText(this, "Повторяющееся tag'и ", Toast.LENGTH_SHORT).show();
                }else {
                    if (tag2.equals("")) {
                        tag2 = "no";
                    }
                    String data = dateFormat.format(new Date());
                    list.add(new Desires(String.valueOf(desiresText.getText()), 1, tag1, tag2, data));
                    recyclerView.getAdapter().notifyDataSetChanged();
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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
    public void onClick1 (View view){
        getOrSetDataBase("new","0","0","0","0",0,"0");
        getOrSetDataBase("show","0","0","0","0",0,"0");
    }
    public void onClick2 (View view){
        getOrSetDataBase("update","0","0","0","0",0,"0");
        getOrSetDataBase("show","0","0","0","0",0,"0");
    }
    public List<Lost> getOrSetDataBase (String command,String name,String op,String tag1,String tag2,int num,String data){
        FlowManager.init(new FlowConfig.Builder(this).build());
        if(command.equals("show")){
            List<Lost> lost = SQLite.select()
                    .from(Lost.class)
                    .where(Lost_Table.id.is(2))
                    .queryList();
            Toast.makeText(this,String.valueOf(lost.get(0)), Toast.LENGTH_SHORT).show();
        }
        if(command.equals("new")){
            Lost lost = new Lost();
            lost.setId(15);
            lost.setDesires("what");
            lost.setData("0");
            lost.setOp("0");
            lost.setTag2("0");
            lost.save();
        }
        if(command.equals("update")){
            Lost lost = new Lost();
            lost.setId(15);
            lost.setDesires("new what");
            lost.update();
        }
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
            lost.setData(data);
            lost.save();
        }
        return null;
    }

    public void hiddenKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
