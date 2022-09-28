package com.trio.picturewall.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
import com.trio.picturewall.adapter.CommentAdapter;
import com.trio.picturewall.entity.Comment;
import com.trio.picturewall.entity.CommentRecords;
import com.trio.picturewall.entity.MyPosts;
import com.trio.picturewall.information.LoginData;
import com.trio.picturewall.responseBody.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
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
    public static MyPosts post;
    TextView user_name;
    TextView post_title;
    TextView post_context;
    TextView com_num;
    ImageView focus;
    ImageView photo;
    ImageView cancel;
    ImageView btn_like;
    ImageView btn_collect;
    ImageView btn_comment;
    CommentAdapter commentAdapter;
    ImageView btn_close;
    private Dialog com_dialog;
    private RecyclerView comment_list; //评论列表
    private List<Comment> comments = new ArrayList<>(); //评论数据list
    int com_count;
    public static int shareId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getdetail();
        getComment();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        initView();
        initData();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.focus:
                if (!post.getHasFocus()) {//detail.getHasFocus() == false：未关注->已关注
                    hasfocus();
                    focus.setImageResource(R.mipmap.focus);
                    post.setHasFocus(true);
                } else {
                    cancelfocus();
                    focus.setImageResource(R.mipmap.unfocus);
                    post.setHasFocus(false);
                }
                break;

            case R.id.image_detail:
                //TODO
                break;
            case R.id.btn_like:
                if (!post.getHasLike()) {//detail.getHasFocus() == false：未关注->已关注
                    like();
                    btn_like.setImageResource(R.mipmap.like);
                    post.setHasLike(true);
                } else {
                    cancelLike();
                    btn_like.setImageResource(R.mipmap.unlike);
                    post.setHasLike(false);
                }
                break;
            case R.id.btn_collect:
                if (!post.getHasCollect()) {//detail.getHasFocus() == false：未关注->已关注
                    collect();
                    btn_collect.setImageResource(R.mipmap.collected);
                    post.setHasCollect(true);
                } else {
                    cancelCollect();
                    btn_collect.setImageResource(R.mipmap.collect);
                    post.setHasCollect(false);
                }
                break;
            case R.id.btn_comment:
                showCommentDialog();
                break;
            default:
                break;
        }
    }

    public void initView() {
        user_name = findViewById(R.id.user_name);
        post_title = findViewById(R.id.post_title);
        post_context = findViewById(R.id.post_context);
        focus = findViewById(R.id.focus);
        photo = findViewById(R.id.image_detail);
        cancel = findViewById(R.id.cancel);
        btn_like = findViewById(R.id.btn_like);
        btn_collect = findViewById(R.id.btn_collect);
        btn_comment = findViewById(R.id.btn_comment);

        focus.setOnClickListener(this);
        photo.setOnClickListener(this);
        cancel.setOnClickListener(this);
        btn_like.setOnClickListener(this);
        btn_collect.setOnClickListener(this);
        btn_comment.setOnClickListener(this);
    }

    public void initData() {
        if (!post.getHasFocus()) {//detail.getHasFocus() == false：未关注->已关注
            focus.setImageResource(R.mipmap.unfocus);
        } else {
            focus.setImageResource(R.mipmap.focus);
        }
        if (!post.getHasLike()) {//detail.getHasFocus() == false：未关注->已关注
            btn_like.setImageResource(R.mipmap.unlike);
        } else {
            btn_like.setImageResource(R.mipmap.like);
        }
        if (!post.getHasCollect()) {//detail.getHasFocus() == false：未关注->已关注
            btn_collect.setImageResource(R.mipmap.collect);
        } else {
            btn_collect.setImageResource(R.mipmap.collected);
        }
        user_name.setText(post.getUsername());
        post_title.setText(post.getTitle());
        post_context.setText(post.getContent());
        if (post.getImageUrlList().length != 0) {//解决没有图片闪退
            Glide.with(this).load(post.getImageUrlList()[0]).into(photo);
        }
    }

    public void getdetail() {
        // url路径
        String url = "http://47.107.52.7:88/member/photo/share/detail?shareId="
                + shareId + "&userId="
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
                    post = dataResponseBody.getData();
                }
            });
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    public void hasfocus() {
        // url路径
        String url = "http://47.107.52.7:88/member/photo/focus?focusUserId="
                + post.getpUserId() + "&userId="
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
                + post.getpUserId() + "&userId="
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

    private void collect() {
        // url路径
        String url = "http://47.107.52.7:88/member/photo/collect?" +
                "shareId=" + shareId +
                "&userId=" + LoginData.loginUser.getId();

        // 请求头
        Headers headers = new Headers.Builder()
                .add("appId", "83cf5e533f8e47d5961d64e2831516e9")
                .add("appSecret", "71291d9a048ed12e242c1916d79f55209f573")
                .add("Accept", "application/json, text/plain, */*")
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
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    //TODO 请求失败处理
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    //TODO 请求成功处理
                    Type jsonType = new TypeToken<ResponseBody<Object>>() {
                    }.getType();
                    // 获取响应体的json串
                    String body = Objects.requireNonNull(response.body()).string();
                    Log.d("info", body);
                    // 解析json串到自己封装的状态
                    ResponseBody<Object> dataResponseBody = new Gson().fromJson(body, jsonType);
                    Log.d("info", dataResponseBody.toString());
                }
            });
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    private void cancelCollect() {
        // url路径
        String url = "http://47.107.52.7:88/member/photo/collect/cancel?" +
                "collectId=" + post.getCollectId();

        // 请求头
        Headers headers = new Headers.Builder()
                .add("appId", "83cf5e533f8e47d5961d64e2831516e9")
                .add("appSecret", "71291d9a048ed12e242c1916d79f55209f573")
                .add("Accept", "application/json, text/plain, */*")
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
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    //TODO 请求失败处理
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    //TODO 请求成功处理
                    Type jsonType = new TypeToken<ResponseBody<Object>>() {
                    }.getType();
                    // 获取响应体的json串
                    String body = Objects.requireNonNull(response.body()).string();
                    Log.d("info", body);
                    // 解析json串到自己封装的状态
                    ResponseBody<Object> dataResponseBody = new Gson().fromJson(body, jsonType);
                    Log.d("info", dataResponseBody.toString());
                }
            });
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    private void like() {
        // url路径
        String url = "http://47.107.52.7:88/member/photo/like?" +
                "shareId=" + shareId +
                "&userId=" + LoginData.loginUser.getId();

        // 请求头
        Headers headers = new Headers.Builder()
                .add("appId", "83cf5e533f8e47d5961d64e2831516e9")
                .add("appSecret", "71291d9a048ed12e242c1916d79f55209f573")
                .add("Accept", "application/json, text/plain, */*")
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
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    //TODO 请求失败处理
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    //TODO 请求成功处理
                    Type jsonType = new TypeToken<ResponseBody<Object>>() {
                    }.getType();
                    // 获取响应体的json串
                    String body = Objects.requireNonNull(response.body()).string();
                    Log.d("info", body);
                    // 解析json串到自己封装的状态
                    ResponseBody<Object> dataResponseBody = new Gson().fromJson(body, jsonType);
                    Log.d("info", dataResponseBody.toString());
                }
            });
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    private void cancelLike() {
        // url路径
        String url = "http://47.107.52.7:88/member/photo/like/cancel?likeId=" + post.getLikeId();
        // 请求头
        Headers headers = new Headers.Builder()
                .add("appId", "83cf5e533f8e47d5961d64e2831516e9")
                .add("appSecret", "71291d9a048ed12e242c1916d79f55209f573")
                .add("Accept", "application/json, text/plain, */*")
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
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    //TODO 请求失败处理
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    //TODO 请求成功处理
                    Type jsonType = new TypeToken<ResponseBody<Object>>() {
                    }.getType();
                    // 获取响应体的json串
                    String body = Objects.requireNonNull(response.body()).string();
                    Log.d("info", body);
                    // 解析json串到自己封装的状态
                    ResponseBody<Object> dataResponseBody = new Gson().fromJson(body, jsonType);
                    Log.d("info", dataResponseBody.toString());
                }
            });
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    private void getComment() {
        // url路径
        String url = "http://47.107.52.7:88/member/photo/comment/first?current=1&shareId=805&size=2";

        // 请求头
        Headers headers = new Headers.Builder()
                .add("appId", "83cf5e533f8e47d5961d64e2831516e9")
                .add("appSecret", "71291d9a048ed12e242c1916d79f55209f573")
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
                    //TODO 请求失败处理
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    //TODO 请求成功处理
                    Type jsonType = new TypeToken<ResponseBody<CommentRecords>>() {
                    }.getType();
                    // 获取响应体的json串
                    String body = Objects.requireNonNull(response.body()).string();
                    Log.d("CommentRecords", body);
                    // 解析json串到自己封装的状态
                    ResponseBody<CommentRecords> dataResponseBody = new Gson().fromJson(body, jsonType);
                    Log.d("CommentRecords", dataResponseBody.toString());
                    comments = dataResponseBody.getData().getRecords();
                    com_count = dataResponseBody.getData().getRecords().size();
                    Log.d("comments", comments.toString());
                }
            });
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }


    //评论弹窗
    private void showCommentDialog() {
        //初始化弹窗
        com_dialog = new Dialog(this, R.style.Comment_Dialog_Style);
        //设置弹窗布局
        View com_view = View.inflate(this, R.layout.dialog_comment, null);
        //设置弹窗padding为0，可宽度沾满屏幕
        Window window = com_dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        //初始化弹窗大小、位置、弹出关闭动画
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.share_animation;
        //展示弹窗
        com_dialog.show();
        //添加弹窗view到主View
        com_dialog.setContentView(com_view, lp);
        // 设置点击对话框外部是否关闭对话框
        com_dialog.setCanceledOnTouchOutside(true);
        //绑定view
        comment_list = com_view.findViewById(R.id.comment_list);
        btn_close = com_view.findViewById(R.id.btn_close);
        com_num = com_view.findViewById(R.id.com_num);
        btn_close.setOnClickListener(this);
        //开启UI线程更新UI
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //设置布局管理
                comment_list.setLayoutManager(new StaggeredGridLayoutManager(1, RecyclerView.VERTICAL) {
                    @Override
                    public boolean canScrollVertically() {
                        return true;
                    }
                });
                //设置适配器
                commentAdapter = new CommentAdapter(DetailActivity.this, comments);
                comment_list.setAdapter(commentAdapter);
                com_num.setText("" + com_count);
            }
        });

    }
}
