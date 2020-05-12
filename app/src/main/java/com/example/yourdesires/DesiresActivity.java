package com.example.yourdesires;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yourdesires.R;
import com.example.yourdesires.model.Lost;
import com.example.yourdesires.model.Lost_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

public class DesiresActivity extends AppCompatActivity {
String command;
int status,pos;
TextView desires;
ImageView statusColor,back;
ImageView menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desires);
        command = null;
        back = findViewById(R.id.back);
        menu = findViewById(R.id.menu_tool);
        desires = findViewById(R.id.name);
        desires.setText(getIntent().getStringExtra("name"));
        status =getIntent().getIntExtra("status",1);
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
    }
    private void inputIntent(){
        Intent in = new Intent(DesiresActivity.this,MainActivity.class);
        in.putExtra("command",command);
        pos = getIntent().getIntExtra("position",0);
        in.putExtra("position",pos);
        startActivity(in);
        finish();
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
}
