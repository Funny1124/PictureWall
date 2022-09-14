package com.trio.picturewall.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
import com.trio.picturewall.information.LoginData;

public class LoginActivity extends AppCompatActivity {
    private Boolean bPwdSwitch = false; //是否查看密码
    private ImageView ivPwdSwitch;
    private EditText username;
    private EditText password;
    private CheckBox cbRememberPwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ivPwdSwitch = findViewById(R.id.iv_pwd_switch);
        username = findViewById(R.id.account);
        password = findViewById(R.id.password);
        cbRememberPwd = findViewById(R.id.cb_remember_pwd);

        //记住密码？恢复
        restorePwd();

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

                String username = LoginActivity.this.username.getText().toString();
                String userpassword = password.getText().toString();

                //记住密码？
                rememberPwd(username,userpassword);

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

    public void rememberPwd(String username,String password){

        String spFileName = getResources()
                .getString(R.string.shared_preferences_file_name);
        String accountKey = getResources()
                .getString(R.string.login_account_name);
        String passwordKey = getResources()
                .getString(R.string.login_password);
        String rememberPasswordKey = getResources()
                .getString(R.string.login_remember_password);

        SharedPreferences spFile = getSharedPreferences(
                spFileName ,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spFile.edit();

        if (cbRememberPwd.isChecked()) {

            editor.putString(accountKey , username);
            editor.putString(passwordKey , password);
            editor.putBoolean(rememberPasswordKey , true);
            editor.apply();
        } else {
            editor.remove(accountKey);
            editor.remove(passwordKey);

            editor.remove(rememberPasswordKey);
            editor.apply();
        }
    }

    private void restorePwd(){
        String spFileName = getResources()
                .getString(R.string.shared_preferences_file_name);
        String accountKey = getResources()
                .getString(R.string.login_account_name);
        String passwordKey = getResources()
                .getString(R.string.login_password);
        String rememberPasswordKey = getResources()
                .getString(R.string.login_remember_password);

        SharedPreferences spFile = getSharedPreferences(
                spFileName ,
                MODE_PRIVATE);
        String re_account = spFile.getString(accountKey , null);
        String re_password = spFile.getString(passwordKey , null);
        boolean rememberPassword = spFile.getBoolean(

                rememberPasswordKey ,
                false);

        if (re_account != null && !TextUtils.isEmpty(re_account)) {
            username.setText(re_account);
        }

        if (re_password != null && !TextUtils.isEmpty(re_password)) {
            password.setText(re_password);
        }

        cbRememberPwd.setChecked(rememberPassword);
    }
}