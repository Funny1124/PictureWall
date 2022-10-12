package com.trio.picturewall.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
import com.trio.picturewall.adapter.MyPostAdapter;
import com.trio.picturewall.entity.MyPosts;
import com.trio.picturewall.entity.Records;
import com.trio.picturewall.information.LoginData;
import com.trio.picturewall.responseBody.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class DraftBoxActivity extends AppCompatActivity {
    public List<MyPosts> drafBoxItems;
    public RecyclerView recyclerView;//定义RecyclerView
    private MyPostAdapter myPostsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft_box);
        drafBoxItems = new ArrayList<>();
        get();
        initRecyclerView();
    }

    private void initRecyclerView() {
        //获取RecyclerView
        recyclerView = findViewById(R.id.draft_box_items);
        //创建adapter
        myPostsAdapter = new MyPostAdapter(this, drafBoxItems);
        //设置layoutManager,可以设置显示效果，是线性布局、grid布局，还是瀑布流布局
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        //给RecyclerView设置adapter
        recyclerView.setAdapter(myPostsAdapter);
        //参数是：上下文、列表方向（横向还是纵向）、是否倒叙
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        //RecyclerView中没有item的监听事件，需要自己在适配器中写一个监听事件的接口。参数根据自定义
        myPostsAdapter.setOnItemClickListener(new MyPostAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, MyPosts data) {
                DetailActivity.post = data;
                startActivity(new Intent(DraftBoxActivity.this, DetailActivity.class));
            }
        });

        myPostsAdapter.setOnItemLongClickListener(new MyPostAdapter.OnItemLongClickListener() {
            @Override
            public void OnItemLongClick(View view, MyPosts data) {
                PopupMenu popupMenu = new PopupMenu(DraftBoxActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.change, popupMenu.getMenu());

                //弹出式菜单的菜单项点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        drafBoxItems.remove(data);
                        //此处回传点击监听事件
                        myPostsAdapter.notifyItemRemoved(myPostsAdapter.pos);
//                        deleteMyPost();
                        change(data);
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void get() {
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/share/save?current=1&size=5&" +
                    "userId=" + LoginData.loginUser.getId();

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", Api.appId)
                    .add("appSecret", Api.appSecret)
                    .add("Accept", "application/json, text/plain, */*")
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

                        // 获取响应体的json串
                        String body = Objects.requireNonNull(response.body()).string();
                        Log.d("drafBox：", body);


                        runOnUiThread(new Runnable() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void run() {
                                Type jsonType = new TypeToken<ResponseBody<Records>>() {
                                }.getType();
                                // 解析json串到自己封装的状态
                                ResponseBody<Records> dataResponseBody = new Gson().fromJson(body, jsonType);
                                if (dataResponseBody.getData() != null) {//判断当前用户是否有发布帖子
                                    Log.d("drafBox：", dataResponseBody.getData().getRecords().toString());
                                    drafBoxItems.addAll(dataResponseBody.getData().getRecords());
                                } else {
                                    Toast.makeText(DraftBoxActivity.this, "你没有保存任何作品！", Toast.LENGTH_SHORT).show();
                                }
                                myPostsAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                });
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private void change(MyPosts posts) {
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/share/change";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", Api.appId)
                    .add("appSecret", Api.appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            // 请求体
            // PS.用户也可以选择自定义一个实体类，然后使用类似fastjson的工具获取json串
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("content", posts.getContent());
            bodyMap.put("id", posts.getId());
            bodyMap.put("imageCode", posts.getImageCode());
            bodyMap.put("pUserId", posts.getpUserId());
            bodyMap.put("title", posts.getTitle());
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
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void run() {
                                Toast.makeText(DraftBoxActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

}