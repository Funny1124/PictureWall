package com.trio.picturewall.activity;

import static com.trio.picturewall.Http.Api.postAdd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
//import com.hitomi.tilibrary.transfer.TransferConfig;
//import com.hitomi.tilibrary.transfer.Transferee;
import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
import com.trio.picturewall.adapter.GridViewAdapter;
import com.trio.picturewall.entity.Picture;
import com.trio.picturewall.information.LoginData;
import com.trio.picturewall.responseBody.ResponseBody;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

    private EditText edit_img_name = null;
    private EditText edit_img_context = null;

    public static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    public static final int CROP_PHOTO = 2;// 裁剪
    public static final int ARRAY_PHOTO = 3;// 相册选取
    private Uri imageUri = null;
    //显示删除符号
    private boolean isShowDelete = false;

    //图片数组
    ArrayList<File> fileList = new ArrayList<>();
    //显示图片
    private GridView gridViewPhoto;
    //文件夹下所有图片的bitmap
    private List<Bitmap> listpath;
    //文件夹下图片的真实路径
    private String scanpath;
    //显示图片的适配器
    private GridViewAdapter adapter;
    String title = null;
    String content = null;
    //设置弹窗
    Dialog mDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        findViewById(R.id.publish).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);

        gridViewPhoto = (GridView) findViewById(R.id.gv);
        initData(fileList);
        //单击事件
        gridViewPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position<fileList.size()){
                    Toast.makeText(getApplicationContext(), "长按可删除", Toast.LENGTH_LONG).show();
                }else{
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(PublishActivity.this, Manifest.permission.CAMERA)||!ActivityCompat.shouldShowRequestPermissionRationale(PublishActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(PublishActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                    if (ContextCompat.checkSelfPermission(PublishActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        //申请WRITE_EXTERNAL_STORAGE权限
                        return;
                    }else showDialog();

                }
            }
        });
        //长按事件
        gridViewPhoto.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (isShowDelete) {
                    isShowDelete = false;
                } else {
                    isShowDelete = true;
                    adapter.setIsShowDelete(isShowDelete);
                    if (position<fileList.size()){
                        findViewById(R.id.delete_markView).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                delete(position);//删除选中项
                                initData(fileList);
                            }
                        });
                    }else{
                        showDialog();
                    }
                }
                adapter.setIsShowDelete(isShowDelete);//setIsShowDelete()方法用于传递isShowDelete值
                return true;
            }
        });

    }

    private void delete(int position) {//删除选中项方法
        if (isShowDelete) {
            fileList.remove(position);
            isShowDelete = false;
        }
        initData(fileList);
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

                if(title.equals("")||content.equals("")){
                    Toast toast = Toast.makeText(this,"请输入内容",Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    if(fileList.size()>0){
                        //上传并发布
                        post(fileList,title,content);
                        Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(this, "请上传图片", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
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
                            } else {
                                uri = data.getData();
                                String path = handleImageOkKitKat(uri);
                                File file = new File(path);   //图片地址
                                fileList.add(file);
                                System.out.println(path);
                            }
                        } else {
                            uri = data.getData();
                            String path = handleImageOkKitKat(uri);
                            File file = new File(path);   //图片地址
                            fileList.add(file);
                            System.out.println(path);
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
                break;

            case PHOTO_REQUEST_CAREMA:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        imageUri = data.getData();
                        String path = handleImageOkKitKat(imageUri);
                        File file = new File(path);   //图片地址
                        fileList.add(file);
                        System.out.println(path);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
        gridViewPhoto = (GridView) findViewById(R.id.gv);
        initData(fileList);
//        gridViewPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(), "Item"+position, Toast.LENGTH_LONG).show();
//                if (position<fileList.size()){
//                    //fileList.remove(position);
//                    //initData(fileList);
//                }else{
//                    showDialog();
//                }
//            }
//        });
    }

    //图片uri判断
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
    //uri转文件
    @SuppressLint("Range")
    public String getImagePath(Uri uri,String selection) {
        System.out.println("uri转file");
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
    //初始化
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


    /**
     * 底部弹出框
     */
    private void showDialog() {
        if (mDialog ==null){
            initShareDialog();
        }
        mDialog.show();
    }

    /**
     * dialog 初始化
     */
    private void initShareDialog() {
        mDialog = new Dialog(this, R.style.dialogStyle);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(true);            //点击框外，框退出
        Window window = mDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);      //位于底部
        window.setWindowAnimations(R.style.dialogStyle);    //弹出动画
        View inflate = View.inflate(this, R.layout.dialog_picture_camera, null);
        inflate.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (mDialog != null && mDialog.isShowing()){
                    mDialog.dismiss();      //消失，退出
                }
            }
        });
        inflate.findViewById(R.id.dialog_Picture).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (mDialog != null && mDialog.isShowing()){
                    mDialog.dismiss();      //消失，退出
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), 0);
                }
            }
        });
        inflate.findViewById(R.id.dialog_Camera).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (mDialog != null && mDialog.isShowing()){
                    mDialog.dismiss();      //消失，退出
                    openCamera(PublishActivity.this);
                }
            }
        });
        window.setContentView(inflate);
        //横向充满
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
    }


    public void openCamera(Activity activity) {
        //獲取系統版本
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // 激活相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File tempFile;

        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                    "yyyy_MM_dd_HH_mm_ss");
            String filename = timeStampFormat.format(new Date());
            tempFile = new File(Environment.getExternalStorageDirectory(), filename + ".jpg");
            if (currentapiVersion < 24) {
                // 从文件中创建uri
                imageUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            } else {
                //兼容android7.0 使用共享文件的形式
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());

                //检查是否有存储权限，以免崩溃
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    //Util.showToast(this,"请开启存储权限");
                    Toast.makeText(this,"请开启存储权限",Toast.LENGTH_SHORT).show();
                    return;
                }
                imageUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        activity.startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    /*
     * 判断sdcard是否被挂载
     */
    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
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

//            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                    .addFormDataPart("fileList", fileList.get(0).getName(), fileBody)
//                    .addFormDataPart("fileList", fileList.get(1).getName(), fileBody)
//                    .build();

            MediaType mediaType = MediaType.Companion.parse("text/x-markdown; charset=utf-8");

            MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            for (int i = 0; i <= length - 1; i++) {
                RequestBody fileBody = RequestBody.Companion.create(fileList.get(i), mediaType);
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