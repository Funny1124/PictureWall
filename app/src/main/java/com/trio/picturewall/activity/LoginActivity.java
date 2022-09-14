package com.trio.picturewall.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
import com.trio.picturewall.information.LoginData;

public class LoginActivity extends AppCompatActivity {
    private Boolean bPwdSwitch = false; //是否查看密码
    private ImageView ivPwdSwitch;
    private EditText account;
    private EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ivPwdSwitch = findViewById(R.id.iv_pwd_switch);
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);

        ivPwdSwitch.setOnClickListener(view -> {//眼睛，密码查看
            bPwdSwitch = !bPwdSwitch;
            if (bPwdSwitch){
                ivPwdSwitch.setImageResource(R.mipmap.eye);
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }else {
                ivPwdSwitch.setImageResource(R.mipmap.eye_off);
                password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|
                        InputType.TYPE_CLASS_TEXT);
                password.setTypeface(Typeface.DEFAULT);
            }
        });
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