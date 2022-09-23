package com.trio.picturewall.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
import com.trio.picturewall.responseBody.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private final String TAG = "RegisterActivity:";
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
            if (bPwdSwitch1) {
                ivPwdSwitch1.setImageResource(R.mipmap.eye);
                registerPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                ivPwdSwitch1.setImageResource(R.mipmap.eye_off);
                registerPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD |
                        InputType.TYPE_CLASS_TEXT);
                registerPassword.setTypeface(Typeface.DEFAULT);
            }
        });
        ivPwdSwitch2.setOnClickListener(view -> {//眼睛，密码查看
            bPwdSwitch2 = !bPwdSwitch2;
            if (bPwdSwitch2) {
                ivPwdSwitch2.setImageResource(R.mipmap.eye);
                verifyPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                ivPwdSwitch2.setImageResource(R.mipmap.eye_off);
                verifyPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD |
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
                if (password.equals(vPassword)) {
                    register(username, password);
                } else {
                    Toast.makeText(getApplicationContext(), "两次输入的密码不一样，请重新输入！", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void register(String username, String password) {
        // url路径
        String url = "http://47.107.52.7:88/member/photo/user/register";

        // 请求头
        Headers headers = new Headers.Builder()
                .add("Accept", "application/json, text/plain, */*")
                .add("appId", Api.appId)
                .add("appSecret", Api.appSecret)
                .add("Content-Type", "application/json")
                .build();
        // 请求体
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("password", password);
        bodyMap.put("username", username);
        // 将Map转换为字符串类型加入请求体中
        String body = new Gson().toJson(bodyMap);
        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
        //请求组合创建
        Request request = new Request.Builder()
                .url(url)
                // 将请求头加至请求中
                .headers(headers)
                .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                .build();
        try {
            OkHttpClient client = new OkHttpClient();
            //发起请求，传入callback进行回调
            client.newCall(request).enqueue(callback);
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 回调
     */
    public final Callback callback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "连接服务器出错", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            Type jsonType = new TypeToken<ResponseBody<Object>>() {
            }.getType();
            // 获取响应体的json串
            String body = Objects.requireNonNull(response.body()).string();
            Log.d(TAG, body);
            // 解析json串
            ResponseBody<Object> dataResponseBody = new Gson().fromJson(body, jsonType);
            Log.d(TAG, dataResponseBody.toString());
            if (dataResponseBody.getCode() == 200) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
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
    };
}