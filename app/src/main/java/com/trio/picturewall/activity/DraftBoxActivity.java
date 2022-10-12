package com.trio.picturewall.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trio.picturewall.R;
import com.trio.picturewall.adapter.MyPostAdapter;
import com.trio.picturewall.entity.MyPosts;
import com.trio.picturewall.entity.Records;
import com.trio.picturewall.responseBody.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DraftBoxActivity extends AppCompatActivity {
    public List<MyPosts> drafBoxItems;
    public RecyclerView recyclerView;//定义RecyclerView
    private MyPostAdapter myPostsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft_box);
        drafBoxItems=new ArrayList<>();
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
//                startActivity(new Intent(DraftBoxActivity.this, DetailActivity.class));
            }
        });
    }

    private void get() {
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/share/save?current=1&size=5&userId=1570327632636153856";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", "f3d10b15acaf4ed0a0cee98adc03b447")
                    .add("appSecret", "545153dc74d28165d46f9833b9e7282fb20ce")
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
                        Log.d("动态：", body);


                        runOnUiThread(new Runnable() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void run() {
                                Type jsonType = new TypeToken<ResponseBody<Records>>() {
                                }.getType();
                                // 解析json串到自己封装的状态
                                ResponseBody<Records> dataResponseBody = new Gson().fromJson(body, jsonType);
                                if (dataResponseBody.getData() != null) {//判断当前用户是否有发布帖子
                                    Log.d("动态：", dataResponseBody.getData().getRecords().toString());
                                    drafBoxItems.addAll(dataResponseBody.getData().getRecords());
                                } else {
                                    Toast.makeText(DraftBoxActivity.this, "你没有发布任何作品！", Toast.LENGTH_SHORT).show();
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
}