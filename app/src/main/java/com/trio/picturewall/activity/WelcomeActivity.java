package com.trio.picturewall.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.trio.picturewall.R;
import com.trio.picturewall.widget.CircleButton;

public class WelcomeActivity extends AppCompatActivity {

    private CircleButton enter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        init();
    }

    private void init() {
        /*
        动画
         */
        enter = findViewById(R.id.enter);
//        Animation enter_ani = AnimationUtils.loadAnimation(WelcomeActivity.this,R.anim.wel_translate_left);
//        enter.startAnimation(enter_ani);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}