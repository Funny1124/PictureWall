package com.trio.picturewall.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
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

import java.io.File;
import java.io.FileOutputStream;
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

public class DetailActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    public static MyPosts post;
    TextView user_name;
    TextView post_title;
    TextView post_context;
    TextView com_num;

    EditText com_edit_text;
    ImageView focus;
    ImageView photo;
    ImageView cancel;
    ImageView btn_like;
    ImageView btn_collect;
    ImageView btn_comment;
    ImageView forward_image;
    ImageView next_image;
    CommentAdapter commentAdapter;
    ImageView btn_close;
    ImageView com_post;
    private Dialog com_dialog;
    private RecyclerView comment_list; //????????????
    private SwipeRefreshLayout swipe_comment;
    private List<Comment> comments = new ArrayList<>(); //????????????list
    private int com_count = 0;

    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //view = inflater.inflate(R.layout.fragment_find, container, false);
        //swipe_comment = findViewById(R.id.swipe_comment);
//        getdetail();
        getComment();
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
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
                if (!post.getHasFocus()) {//detail.getHasFocus() == false????????????->?????????
                    hasfocus();
                    focus.setImageResource(R.mipmap.focus);
                    post.setHasFocus(true);
                } else {
                    cancelfocus();
                    focus.setImageResource(R.mipmap.unfocus);
                    post.setHasFocus(false);
                }
                break;

//            case R.id.image_detail://????????????
//                //
//                break;
            case R.id.btn_like:
                if (!post.getHasLike()) {//detail.getHasFocus() == false????????????->?????????
                    like();
                    btn_like.setImageResource(R.mipmap.good_fill);
                    post.setHasLike(true);
                } else {
                    cancelLike();
                    btn_like.setImageResource(R.mipmap.good);
                    post.setHasLike(false);
                }
                break;
            case R.id.btn_collect:
                if (!post.getHasCollect()) {//detail.getHasFocus() == false????????????->?????????
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
            case R.id.btn_close:
                com_dialog.dismiss();
                break;
            case R.id.forward_image: {
                if (post.getImageUrlList().length != 0 && i > 0) {//????????????????????????
                    Glide.with(this).load(post.getImageUrlList()[--i]).into(photo);
                } else if (i == 0) {//????????????????????????????????????????????????????????????
                    i = post.getImageUrlList().length - 1;
                    Glide.with(this).load(post.getImageUrlList()[i]).into(photo);
                }
                break;
            }
            case R.id.next_image: {
                if (post.getImageUrlList().length != 0 && i < post.getImageUrlList().length - 1) {//????????????????????????
                    Glide.with(this).load(post.getImageUrlList()[++i]).into(photo);
                } else if (i == post.getImageUrlList().length - 1) {//????????????????????????????????????????????????????????????
                    i = 0;
                    Glide.with(this).load(post.getImageUrlList()[i]).into(photo);
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.image_detail://????????????
                Log.d("Detail", "onLongClick");
                showListDialog();
                break;
            default:
                break;
        }
        return true;
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
        forward_image = findViewById(R.id.forward_image);
        next_image = findViewById(R.id.next_image);
        focus.setOnClickListener(this);
//        photo.setOnClickListener(this);
        photo.setOnLongClickListener(this);
        cancel.setOnClickListener(this);
        btn_like.setOnClickListener(this);
        btn_collect.setOnClickListener(this);
        btn_comment.setOnClickListener(this);
        forward_image.setOnClickListener(this);
        next_image.setOnClickListener(this);
    }

    public void initData() {
        if (!post.getHasFocus()) {//detail.getHasFocus() == false????????????->?????????
            focus.setImageResource(R.mipmap.unfocus);
        } else {
            focus.setImageResource(R.mipmap.focus);
        }
        if (!post.getHasLike()) {//detail.getHasFocus() == false????????????->?????????
            btn_like.setImageResource(R.mipmap.good);
        } else {
            btn_like.setImageResource(R.mipmap.good_fill);
        }
        if (!post.getHasCollect()) {//detail.getHasFocus() == false????????????->?????????
            btn_collect.setImageResource(R.mipmap.collect);
        } else {
            btn_collect.setImageResource(R.mipmap.collected);
        }
        user_name.setText(post.getUsername());
        post_title.setText(post.getTitle());
        post_context.setText(post.getContent());
        if (post.getImageUrlList().length != 0) {//????????????????????????
            Glide.with(this).load(post.getImageUrlList()[0]).into(photo);
        }
    }

    public void hasfocus() {
        // url??????
        String url = "http://47.107.52.7:88/member/photo/focus?focusUserId="
                + post.getpUserId() + "&userId="
                + LoginData.loginUser.getId();

        // ?????????
        Headers headers = new Headers.Builder()
                .add("Accept", "application/json, text/plain, */*")
                .add("appId", Api.appId)
                .add("appSecret", Api.appSecret)
                .add("Content-Type", "application/json")
                .build();
        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
        //??????????????????
        Request request = new Request.Builder()
                .url(url)
                // ???????????????????????????
                .headers(headers)
                .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                .build();

        try {
            OkHttpClient client = new OkHttpClient();
            //?????????????????????callback????????????
            client.newCall(request).enqueue(ResponseBody.callback);
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }

    }

    public void cancelfocus() {
        // url??????
        String url = "http://47.107.52.7:88/member/photo/focus/cancel?focusUserId="
                + post.getpUserId() + "&userId="
                + LoginData.loginUser.getId();

        // ?????????
        Headers headers = new Headers.Builder()
                .add("Accept", "application/json, text/plain, */*")
                .add("appId", Api.appId)
                .add("appSecret", Api.appSecret)
                .add("Content-Type", "application/json")
                .build();
        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
        //??????????????????
        Request request = new Request.Builder()
                .url(url)
                // ???????????????????????????
                .headers(headers)
                .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                .build();
        try {
            OkHttpClient client = new OkHttpClient();
            //?????????????????????callback????????????
            client.newCall(request).enqueue(ResponseBody.callback);
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    private void collect() {
        // url??????
        String url = "http://47.107.52.7:88/member/photo/collect?" +
                "shareId=" + post.getId() +
                "&userId=" + LoginData.loginUser.getId();

        // ?????????
        Headers headers = new Headers.Builder()
                .add("appId", Api.appId)
                .add("appSecret", Api.appSecret)
                .add("Accept", "application/json, text/plain, */*")
                .build();

        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
        //??????????????????
        Request request = new Request.Builder()
                .url(url)
                // ???????????????????????????
                .headers(headers)
                .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                .build();
        try {
            OkHttpClient client = new OkHttpClient();
            //?????????????????????callback????????????
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    //TODO ??????????????????
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    //TODO ??????????????????
                    Type jsonType = new TypeToken<ResponseBody<Object>>() {
                    }.getType();
                    // ??????????????????json???
                    String body = Objects.requireNonNull(response.body()).string();
                    Log.d("info", body);
                    // ??????json???????????????????????????
                    ResponseBody<Object> dataResponseBody = new Gson().fromJson(body, jsonType);
                    Log.d("info", dataResponseBody.toString());
                }
            });
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    private void cancelCollect() {
        // url??????
        String url = "http://47.107.52.7:88/member/photo/collect/cancel?" +
                "collectId=" + post.getCollectId();

        // ?????????
        Headers headers = new Headers.Builder()
                .add("appId", Api.appId)
                .add("appSecret", Api.appSecret)
                .add("Accept", "application/json, text/plain, */*")
                .build();

        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

        //??????????????????
        Request request = new Request.Builder()
                .url(url)
                // ???????????????????????????
                .headers(headers)
                .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                .build();
        try {
            OkHttpClient client = new OkHttpClient();
            //?????????????????????callback????????????
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    //TODO ??????????????????
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    //TODO ??????????????????
                    Type jsonType = new TypeToken<ResponseBody<Object>>() {
                    }.getType();
                    // ??????????????????json???
                    String body = Objects.requireNonNull(response.body()).string();
                    Log.d("info", body);
                    // ??????json???????????????????????????
                    ResponseBody<Object> dataResponseBody = new Gson().fromJson(body, jsonType);
                    Log.d("info", dataResponseBody.toString());
                }
            });
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    private void like() {
        // url??????
        String url = "http://47.107.52.7:88/member/photo/like?" +
                "shareId=" + post.getId() +
                "&userId=" + LoginData.loginUser.getId();

        // ?????????
        Headers headers = new Headers.Builder()
                .add("appId", Api.appId)
                .add("appSecret", Api.appSecret)
                .add("Accept", "application/json, text/plain, */*")
                .build();


        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

        //??????????????????
        Request request = new Request.Builder()
                .url(url)
                // ???????????????????????????
                .headers(headers)
                .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                .build();
        try {
            OkHttpClient client = new OkHttpClient();
            //?????????????????????callback????????????
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    //TODO ??????????????????
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    //TODO ??????????????????
                    Type jsonType = new TypeToken<ResponseBody<Object>>() {
                    }.getType();
                    // ??????????????????json???
                    String body = Objects.requireNonNull(response.body()).string();
                    Log.d("info", body);
                    // ??????json???????????????????????????
                    ResponseBody<Object> dataResponseBody = new Gson().fromJson(body, jsonType);
                    Log.d("info", dataResponseBody.toString());
                }
            });
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    private void cancelLike() {
        // url??????
        String url = "http://47.107.52.7:88/member/photo/like/cancel?likeId=" + post.getLikeId();
        // ?????????
        Headers headers = new Headers.Builder()
                .add("appId", Api.appId)
                .add("appSecret", Api.appSecret)
                .add("Accept", "application/json, text/plain, */*")
                .build();


        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

        //??????????????????
        Request request = new Request.Builder()
                .url(url)
                // ???????????????????????????
                .headers(headers)
                .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                .build();
        try {
            OkHttpClient client = new OkHttpClient();
            //?????????????????????callback????????????
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    //TODO ??????????????????
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    //TODO ??????????????????
                    Type jsonType = new TypeToken<ResponseBody<Object>>() {
                    }.getType();
                    // ??????????????????json???
                    String body = Objects.requireNonNull(response.body()).string();
                    Log.d("info", body);
                    // ??????json???????????????????????????
                    ResponseBody<Object> dataResponseBody = new Gson().fromJson(body, jsonType);
                    Log.d("info", dataResponseBody.toString());
                }
            });
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    private void getComment() {
        // url??????
        String url = "http://47.107.52.7:88/member/photo/comment/first?current=1&shareId=" +
                post.getId() +
                "&size=32";

        // ?????????
        Headers headers = new Headers.Builder()
                .add("appId", Api.appId)
                .add("appSecret", Api.appSecret)
                .add("Accept", "application/json, text/plain, */*")
                .build();

        //??????????????????
        Request request = new Request.Builder()
                .url(url)
                // ???????????????????????????
                .headers(headers)
                .get()
                .build();
        try {
            OkHttpClient client = new OkHttpClient();
            //?????????????????????callback????????????
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    //TODO ??????????????????
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    //TODO ??????????????????
                    Type jsonType = new TypeToken<ResponseBody<CommentRecords>>() {
                    }.getType();
                    // ??????????????????json???
                    String body = Objects.requireNonNull(response.body()).string();
                    Log.d("CommentRecords", body);
                    // ??????json???????????????????????????
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


    //????????????
    private void showCommentDialog() {
        //???????????????
        com_dialog = new Dialog(this, R.style.Comment_Dialog_Style);
        //??????????????????
        View com_view = View.inflate(this, R.layout.dialog_comment, null);
        //????????????padding???0????????????????????????
        Window window = com_dialog.getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        //???????????????????????????????????????????????????
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.share_animation;
        //????????????
        com_dialog.show();
        //????????????view??????View
        com_dialog.setContentView(com_view, lp);
        // ????????????????????????????????????????????????
        com_dialog.setCanceledOnTouchOutside(true);
        //??????view
        comment_list = com_view.findViewById(R.id.comment_list);
        btn_close = com_view.findViewById(R.id.btn_close);
        com_post = com_view.findViewById(R.id.com_post);
        com_num = com_view.findViewById(R.id.com_num);
        swipe_comment = com_view.findViewById(R.id.swipe_comment);
        btn_close.setOnClickListener(this);
        com_edit_text = com_view.findViewById(R.id.com_edit_text);
        com_post.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                System.out.println(com_edit_text.getText().toString());
                addComment(com_edit_text.getText().toString(), post.getId(),
                        LoginData.loginUser.getId(), LoginData.loginUser.getUsername());
                com_edit_text.setText("");//???????????????

            }
        });
        //??????UI????????????UI
        runOnUiThread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                //??????????????????
                comment_list.setLayoutManager(new StaggeredGridLayoutManager(1, RecyclerView.VERTICAL) {
                    @Override
                    public boolean canScrollVertically() {
                        return true;
                    }
                });
//                //???????????????
                commentAdapter = new CommentAdapter(DetailActivity.this, comments);
                comment_list.setAdapter(commentAdapter);
                com_num.setText("" + com_count);
            }
        });

        swipe_comment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();

                //???????????????
                commentAdapter = new CommentAdapter(DetailActivity.this, comments);
                comment_list.setAdapter(commentAdapter);
                comment_list.setLayoutManager(new StaggeredGridLayoutManager(1, RecyclerView.VERTICAL));
                com_num.setText("" + com_count);
                //?????????????????????????????????????????????false
                //isRefreshing() ???????????????????????????
                if (swipe_comment.isRefreshing()) {
                    swipe_comment.setRefreshing(false);
                }
            }
        });


    }

    private void addComment(String content, int sahreID, String userId, String userName) {
        // url??????
        String url = "http://47.107.52.7:88/member/photo/comment/first";

        // ?????????
        Headers headers = new Headers.Builder()
                .add("appId", Api.appId)
                .add("appSecret", Api.appSecret)
                .add("Accept", "application/json, text/plain, */*")
                .build();

        // ?????????
        // PS.??????????????????????????????????????????????????????????????????fastjson???????????????json???
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("content", content);
        bodyMap.put("shareId", sahreID);
        bodyMap.put("userId", userId);
        bodyMap.put("userName", userName);
        // ???Map??????????????????????????????????????????
        String body = new Gson().toJson(bodyMap);

        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

        //??????????????????
        Request request = new Request.Builder()
                .url(url)
                // ???????????????????????????
                .headers(headers)
                .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                .build();
        try {
            OkHttpClient client = new OkHttpClient();
            //?????????????????????callback????????????
            client.newCall(request).enqueue(ResponseBody.callback);
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    private void refreshData() {
        getComment();
    }

    /**
     * ????????????dialog
     */
    private void showListDialog() {
        final String[] listItems = new String[]{"??????????????????", "??????????????????"};

        AlertDialog.Builder listDialog = new AlertDialog.Builder(DetailActivity.this);
        listDialog.setTitle("????????????");
        listDialog.setIcon(R.mipmap.seraphine);

    /*
        ??????item ?????????setMessage()
        ???setItems
        items : listItems[] -> ???????????????
        listener -> ????????????
    */
        listDialog.setItems(listItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://??????????????????
                        Log.v("Save Image :", listItems[which]);
                        new Thread() {
                            public void run() {
                                Log.e("??????", "????????????");
                                try {
                                    Bitmap myBitmap = Glide.with(DetailActivity.this)
                                            .asBitmap()
                                            .skipMemoryCache(true)//??????????????????
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)//?????????disk?????????
                                            .load(post.getImageUrlList()[i])
                                            .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                                    Bitmap bitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
                                    Log.e("??????", bitmap.toString());
                                    saveImageToGallery(bitmap);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }.start();
                        break;
                    case 1://??????????????????
                        Log.v("Save Image :", listItems[which]);
                        new Thread() {
                            public void run() {
                                Log.e("??????", "????????????");
                                try {
                                    for (int index = 0; index < post.getImageUrlList().length; index++) {
                                        Bitmap myBitmap = Glide.with(DetailActivity.this)
                                                .asBitmap()
                                                .skipMemoryCache(true)//??????????????????
                                                .diskCacheStrategy(DiskCacheStrategy.NONE)//?????????disk?????????
                                                .load(post.getImageUrlList()[index])
                                                .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                                        Bitmap bitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
                                        Log.e("??????", bitmap.toString());
                                        saveImageToGallery(bitmap);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }.start();
                        break;
                }
            }
        });

        //????????????
        listDialog.setPositiveButton(getString(R.string.dialog_btn_confirm_text)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        listDialog.create().show();
    }

    public void saveImageToGallery(Bitmap bmp) {
        if (bmp == null) {
            Log.e("TAG", "bitmap---??????");
            return;
        }
        String galleryPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "Camera" + File.separator;
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(galleryPath, fileName);
        try {
            if (!Objects.requireNonNull(file.getParentFile()).exists()) {
                if (!file.getParentFile().mkdirs()) {
                    Log.e("??????", "??????????????????");
                }
            }
            FileOutputStream fos = new FileOutputStream(file);
            //??????io?????????????????????????????????
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
            //????????????????????????????????????????????????
            Uri uri = Uri.fromFile(file);
            DetailActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                Log.e("TAG", "?????????????????? ?????????:" + file.getPath());
            } else {
                Log.e("TAG", "??????????????????");
            }
        } catch (IOException e) {
            Log.e("TAG", "??????????????????????????????");
            e.printStackTrace();
        }
    }
}
