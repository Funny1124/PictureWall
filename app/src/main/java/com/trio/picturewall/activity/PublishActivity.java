package com.trio.picturewall.activity;

import static com.trio.picturewall.Http.Api.postAdd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hitomi.tilibrary.transfer.TransferConfig;
import com.hitomi.tilibrary.transfer.Transferee;
import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
import com.trio.picturewall.adapter.GridViewAdapter;
import com.trio.picturewall.entity.Picture;
import com.trio.picturewall.information.LoginData;
import com.trio.picturewall.responseBody.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PublishActivity extends AppCompatActivity implements View.OnClickListener {
    //uri对象
    private Uri imageUri;

    private EditText edit_img_name = null;
    private EditText edit_img_context = null;
    private GridView gridView;
    ArrayList<File> fileList = new ArrayList<>();

    private GridView gridViewPhoto;
    //文件夹下所有图片的bitmap
    private List<Bitmap> listpath;
    //文件夹下图片的真实路径
    private String scanpath;
    //显示图片的适配器
    private GridViewAdapter adapter;
    String title = null;
    String content = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        findViewById(R.id.publish).setOnClickListener(this);
        findViewById(R.id.null_view).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.publish:
                edit_img_name = findViewById(R.id.edit_img_name);
                title = edit_img_name.getText().toString().trim();

                edit_img_context = findViewById(R.id.edit_img_context);
                content = edit_img_context.getText().toString().trim();
                //上传并发布
                post(fileList,title,content);
                finish();
                break;
            case R.id.null_view:
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 0);

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Uri uri = null;

                    try {
                        if (data != null) {
                            ClipData imageNames = data.getClipData();
                            if (imageNames != null) {
                                for (int i = 0; i < imageNames.getItemCount(); i++) {
                                    Uri imageUri = imageNames.getItemAt(i).getUri();
                                    //uri转文件
                                    String path = handleImageOkKitKat(imageUri);
                                    File file = new File(path);   //图片地址
                                    fileList.add(file);
                                    System.out.println(file);
                                }
                                //post(fileList);
                            } else {
                                uri = data.getData();
                                String path = handleImageOkKitKat(uri);
                                File file = new File(path);   //图片地址
                                fileList.add(file);
                                System.out.println(path);
                                //post(fileList);

                            }
                        } else {
                            uri = data.getData();
                            String path = handleImageOkKitKat(uri);
                            File file = new File(path);   //图片地址
                            fileList.add(file);
                            System.out.println(path);
                            //post(fileList);

                        }
                        gridViewPhoto = (GridView) findViewById(R.id.gv);
                        initData(fileList);
                        gridViewPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(getApplicationContext(), "Item"+position, Toast.LENGTH_LONG).show();
                                //跳转至显示页面
//                                Intent intent = new Intent(PublishActivity.this, PhotoActivity.class);
//                                //获取文件路径
//                                Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(fileList.get(position)));
//                                intent.putExtra("bitmap", bitmap);
//                                //启动intent
//                                startActivity(intent);

                                fileList.remove(position);
                                initData(fileList);
                            }
                        });

                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }

                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private String handleImageOkKitKat(Uri uri) {
        String imagePath=null;
        Log.d("uri=intent.getData :",""+uri);
        if (DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);        //数据表里指定的行
            Log.d("getDocumentId(uri) :",""+docId);
            Log.d("uri.getAuthority() :",""+uri.getAuthority());
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }
            else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri,null);
        }
        return imagePath;
    }

    @SuppressLint("Range")
    public String getImagePath(Uri uri,String selection) {
        System.out.println("异常0");
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);   //内容提供器
        if (cursor!=null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));   //获取路径
            }
        }
        cursor.close();
        return path;
    }

    private void initData(ArrayList<File> fileList) {
        listpath = new ArrayList<>();
        /**遍历数组*/
        for (int i = 0; i < fileList.size(); i++) {
            /**将文件转为bitmap如果为空则不是图片文件*/
            Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(fileList.get(i)));
            if (bitmap != null) {
                listpath.add(bitmap);
            }
        }
        /** 图片写入适配器*/
        adapter = new GridViewAdapter(listpath, this);
        gridViewPhoto.setAdapter(adapter);
    }

    public static void post(ArrayList<File> fileList, String title, String content) {
        new Thread(() -> {
            Gson gson = new Gson();
            int length = fileList.size();

            // url路径
            String url = "http://47.107.52.7:88/member/photo/image/upload";

            Log.d("DialogActivity", "upload-run: 上传照片！");
            Log.d("fileList.size()", String.valueOf(length));
            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", Api.appId)
                    .add("appSecret", Api.appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            MediaType mediaType = MediaType.Companion.parse("text/x-markdown; charset=utf-8");
            RequestBody fileBody = RequestBody.Companion.create(fileList.get(0), mediaType);
//            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                    .addFormDataPart("fileList", fileList.get(0).getName(), fileBody)
//                    .build();

            MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            for (int i = 0; i <= length - 1; i++) {
                requestBody.addFormDataPart("fileList", fileList.get(i).getName(), fileBody);
            }
            RequestBody body = requestBody.build();


            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(body)
                    .build();
            System.out.println(request);
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
                        Type jsonType = new TypeToken<ResponseBody<Picture>>() {
                        }.getType();
                        // 获取响应体的json串
                        String body = Objects.requireNonNull(response.body()).string();
                        Log.d("info", body);
                        // 解析json串到自己封装的状态
                        ResponseBody<Picture> dataResponseBody = gson.fromJson(body, jsonType);
                        LoginData.picture = dataResponseBody.getData();
                        Log.d("info", dataResponseBody.toString());
                        Log.d("Picture:", LoginData.picture.getImageCode());
                        Log.d("Picture:", String.valueOf(LoginData.picture.getImageUrlList()));
                        postAdd(LoginData.picture.getImageCode(),LoginData.loginUser.getId(), title,content);
                    }
                });

            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

}