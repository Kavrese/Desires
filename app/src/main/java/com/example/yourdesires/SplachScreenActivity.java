package com.example.yourdesires;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplachScreenActivity extends AppCompatActivity {
ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splach_screen);
        img = findViewById(R.id.imageView);
        Animation anim = AnimationUtils.loadAnimation(this,R.anim.anim_splach_screen);
        img.startAnimation(anim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent in = new Intent(SplachScreenActivity.this,MainActivity.class);
                startActivity(in);
                finish();
            }
        },1300);
    }
}
