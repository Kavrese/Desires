package com.example.yourdesires;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DesiresActivity extends AppCompatActivity {
String command,tag1S,tag2S;
int status,pos;
TextView data,time_des,time_des2;
EditText op,desires,tag1,tag2;
ImageView statusColor,back,plus,scrap;
ImageView menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desires);
        command = "new data";
        DateFormat time = new SimpleDateFormat("HH");
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
        tag1S = getIntent().getStringExtra("tag1");
        tag2S = getIntent().getStringExtra("tag2");
        data.setText(getIntent().getStringExtra("data"));
        status = getIntent().getIntExtra("status",1);
        if(!tag1S.equals("no") && !tag2S.equals("no")){
            tag1.setText(tag1S);
            tag2.setText(tag2S);
            showTag2();
        }else {
            if(tag1S.equals("no")) {
                tag1.setText(tag2S);
                hideTag2();
            }else {
                tag1.setText(tag1S);
            }
            if(tag2S.equals("no")){
                tag2.setText("");
                hideTag2();
            }else{
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
                PopupMenu pop = new PopupMenu(v.getContext(),v);
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
                PopupMenu pop_time_des1 = new PopupMenu(v.getContext(),v);
                pop_time_des1.inflate(R.menu.menu_time_des_1);
                pop_time_des1.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        boolean bool = false;
                        SharedPreferences sh = getSharedPreferences("0",0);
                        SharedPreferences.Editor ed;
                        switch (item.getItemId()){
                            case R.id.menu_next:
                                ed = sh.edit();
                                ed.putString("background","next");
                                ed.apply();
                                bool= true;
                                break;
                            case R.id.menu_data:
                                ed = sh.edit();
                                ed.putString("background","data");
                                ed.apply();
                                bool= true;
                                break;
                            case R.id.menu_return:
                                ed = sh.edit();
                                ed.putString("background","return");
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
    }
    private void inputIntent(){
        Intent in = new Intent(DesiresActivity.this,MainActivity.class);
        in.putExtra("command",command);
        pos = getIntent().getIntExtra("position",0);
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
}
