package com.trio.picturewall.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
import com.trio.picturewall.information.LoginData;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText account = findViewById(R.id.account);
        EditText password = findViewById(R.id.password);

        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = account.getText().toString();
                String userpassword = password.getText().toString();
                //调用登录方法
                Api.login(username,userpassword);
                try {//等待1秒
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(LoginData.loginUser != null){
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }
            }
        });

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });
    }
}