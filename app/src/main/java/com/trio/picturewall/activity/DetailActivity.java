package com.trio.picturewall.activity;

import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
import com.trio.picturewall.entity.MyPosts;
import com.trio.picturewall.information.LoginData;
import com.trio.picturewall.responseBody.ResponseBody;
import com.trio.picturewall.ui.home.find.FindFragment;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    public static MyPosts detail;
    TextView name;
    TextView focus;
    ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_detail);

        findViewById(R.id.focus).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);

        getdetail();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        initView();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.focus:
                if (!detail.getHasFocus()) {//detail.getHasFocus() == false：未关注->已关注
                    hasfocus();
                    focus.setText("已关注");
                    detail.setHasFocus(true);
                } else {
                    cancelfocus();
                    focus.setText("未关注");
                    detail.setHasFocus(false);
                }
                break;
            case R.id.cancel:
                finish();
            default:
                break;
        }
    }

    public void initView() {
        name = findViewById(R.id.user_name);
        focus = findViewById(R.id.focus);
        photo = findViewById(R.id.image_detail);
        if (detail.getHasFocus()==false) {//detail.getHasFocus() == false：未关注->已关注
            focus.setText("未关注");

        } else {
            focus.setText("已关注");
        }
        name.setText(detail.getUsername());
        if (detail.getImageUrlList().length != 0){//解决没有图片闪退
            Glide.with(this).load(detail.getImageUrlList()[0]).into(photo);
        }
    }

    public void getdetail() {
        // url路径
        String url = "http://47.107.52.7:88/member/photo/share/detail?shareId="
                + FindFragment.shareId + "&userId="
                + LoginData.loginUser.getId();

        // 请求头
        Headers headers = new Headers.Builder()
                .add("Accept", "application/json, text/plain, */*")
                .add("appId", Api.appId)
                .add("appSecret", Api.appSecret)
                .build();

        //请求组合创建
        Request request = new Request.Builder()
                .url(url)
                // 将请求头加至请求中
                .headers(headers)
                .get()
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
                    Type jsonType = new TypeToken<ResponseBody<MyPosts>>() {
                    }.getType();
                    Gson gson = new Gson();
                    // 获取响应体的json串
                    String body = Objects.requireNonNull(response.body()).string();
                    Log.d("DetailActivity", body);
                    // 解析json串到自己封装的状态
                    ResponseBody<MyPosts> dataResponseBody = gson.fromJson(body, jsonType);
                    Log.d("DetailActivity", dataResponseBody.toString());
                    detail = dataResponseBody.getData();
                }
            });
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    public void hasfocus() {
        // url路径
        String url = "http://47.107.52.7:88/member/photo/focus?focusUserId="
                + detail.getpUserId() + "&userId="
                + LoginData.loginUser.getId();

        // 请求头
        Headers headers = new Headers.Builder()
                .add("Accept", "application/json, text/plain, */*")
                .add("appId", Api.appId)
                .add("appSecret", Api.appSecret)
                .add("Content-Type", "application/json")
                .build();
        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
        //请求组合创建
        Request request = new Request.Builder()
                .url(url)
                // 将请求头加至请求中
                .headers(headers)
                .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                .build();

        try {
            OkHttpClient client = new OkHttpClient();
            //发起请求，传入callback进行回调
            client.newCall(request).enqueue(ResponseBody.callback);
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }

    }

    public void cancelfocus() {

        // url路径
        String url = "http://47.107.52.7:88/member/photo/focus/cancel?focusUserId="
                + detail.getpUserId() + "&userId="
                + LoginData.loginUser.getId();

        // 请求头
        Headers headers = new Headers.Builder()
                .add("Accept", "application/json, text/plain, */*")
                .add("appId", Api.appId)
                .add("appSecret", Api.appSecret)
                .add("Content-Type", "application/json")
                .build();
        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
        //请求组合创建
        Request request = new Request.Builder()
                .url(url)
                // 将请求头加至请求中
                .headers(headers)
                .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                .build();

        try {
            OkHttpClient client = new OkHttpClient();
            //发起请求，传入callback进行回调
            client.newCall(request).enqueue(ResponseBody.callback);
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }
}
