package com.trio.picturewall.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
public class RegisterActivity extends AppCompatActivity {

    private Boolean bPwdSwitch1 = false; //是否查看密码
    private ImageView ivPwdSwitch1;
    private Boolean bPwdSwitch2 = false; //是否查看密码
    private ImageView ivPwdSwitch2;
    private EditText registerUsername;
    private EditText registerPassword;
    private EditText verifyPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ivPwdSwitch1 = findViewById(R.id.iv_pwd_switch1);
        ivPwdSwitch2 = findViewById(R.id.iv_pwd_switch2);
        registerUsername = findViewById(R.id.registerUsername);
        registerPassword = findViewById(R.id.registerPassword);
        verifyPassword = findViewById(R.id.verifyPassword);

        ivPwdSwitch1.setOnClickListener(view -> {//第一个密码框 眼睛，密码查看
            bPwdSwitch1 = !bPwdSwitch1;
            if (bPwdSwitch1){
                ivPwdSwitch1.setImageResource(R.mipmap.eye);
                registerPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }else {
                ivPwdSwitch1.setImageResource(R.mipmap.eye_off);
                registerPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|
                        InputType.TYPE_CLASS_TEXT);
                registerPassword.setTypeface(Typeface.DEFAULT);
            }
        });
        ivPwdSwitch2.setOnClickListener(view -> {//眼睛，密码查看
            bPwdSwitch2 = !bPwdSwitch2;
            if (bPwdSwitch2){
                ivPwdSwitch2.setImageResource(R.mipmap.eye);
                verifyPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }else {
                ivPwdSwitch2.setImageResource(R.mipmap.eye_off);
                verifyPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|
                        InputType.TYPE_CLASS_TEXT);
                verifyPassword.setTypeface(Typeface.DEFAULT);
            }
        });
        findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = registerUsername.getText().toString();
                String password = registerPassword.getText().toString();
                String vPassword = verifyPassword.getText().toString();
                if (password.equals(vPassword)){
                    Api.register(username,password);
                    startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                    finish();
                }else {
                    Log.v("注册","两次密码不同");
                }

            }
        });
    }
}