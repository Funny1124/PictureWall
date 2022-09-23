package com.trio.picturewall.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
import com.trio.picturewall.entity.User;
import com.trio.picturewall.information.LoginData;
import com.trio.picturewall.responseBody.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private String TAG = "LoginActivity";
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
            if (bPwdSwitch) {
                ivPwdSwitch.setImageResource(R.mipmap.eye);
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                ivPwdSwitch.setImageResource(R.mipmap.eye_off);
                password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD |
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
                rememberPwd(username, userpassword);

                //调用登录方法
                login(username, userpassword);

            }
        });

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    public void rememberPwd(String username, String password) {

        String spFileName = getResources()
                .getString(R.string.shared_preferences_file_name);
        String accountKey = getResources()
                .getString(R.string.login_account_name);
        String passwordKey = getResources()
                .getString(R.string.login_password);
        String rememberPasswordKey = getResources()
                .getString(R.string.login_remember_password);

        SharedPreferences spFile = getSharedPreferences(
                spFileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spFile.edit();

        if (cbRememberPwd.isChecked()) {
            editor.putString(accountKey, username);
            editor.putString(passwordKey, password);
            editor.putBoolean(rememberPasswordKey, true);
            editor.apply();
        } else {
            editor.remove(accountKey);
            editor.remove(passwordKey);
            editor.remove(rememberPasswordKey);
            editor.apply();
        }
    }

    private void restorePwd() {
        String spFileName = getResources()
                .getString(R.string.shared_preferences_file_name);
        String accountKey = getResources()
                .getString(R.string.login_account_name);
        String passwordKey = getResources()
                .getString(R.string.login_password);
        String rememberPasswordKey = getResources()
                .getString(R.string.login_remember_password);

        SharedPreferences spFile = getSharedPreferences(
                spFileName,
                MODE_PRIVATE);
        String re_account = spFile.getString(accountKey, null);
        String re_password = spFile.getString(passwordKey, null);
        boolean rememberPassword = spFile.getBoolean(
                rememberPasswordKey,
                false);

        if (re_account != null && !TextUtils.isEmpty(re_account)) {
            username.setText(re_account);
        }

        if (re_password != null && !TextUtils.isEmpty(re_password)) {
            password.setText(re_password);
        }

        cbRememberPwd.setChecked(rememberPassword);
    }

    public void login(String username, String password) {
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/user/login";
            // 请求头
            Headers headers = new Headers.Builder()
                    .add("Accept", "application/json, text/plain, */*")
                    .add("appId", Api.appId)
                    .add("appSecret", Api.appSecret)
                    .build();
            FormBody.Builder params = new FormBody.Builder();
            params.add("username", username); //添加url参数
            params.add("password", password); //添加url参数
            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(params.build())
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Type jsonType = new TypeToken<ResponseBody<User>>() {
                        }.getType();
                        // 获取响应体的json串
                        String body = Objects.requireNonNull(response.body()).string();
                        Log.d("info", body);
                        // 解析json串到自己封装的状态
                        ResponseBody<User> dataResponseBody = new Gson().fromJson(body, jsonType);
                        if (dataResponseBody.getCode() == 200) {
                            Log.d(TAG, dataResponseBody.toString());
                            Log.d(TAG + "UserId:", dataResponseBody.getData().getId());
                            Log.d(TAG + "UserName:", dataResponseBody.getData().getUsername());
                            LoginData.loginUser = dataResponseBody.getData();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), dataResponseBody.getMsg(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}