package com.trio.picturewall.activity;

import static com.trio.picturewall.Http.Api.post;
import static com.trio.picturewall.Http.Api.postAdd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.trio.picturewall.R;
import com.trio.picturewall.information.LoginData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PublishActivity extends AppCompatActivity implements View.OnClickListener {
    //uri对象
    private Uri imageUri;

    private EditText edit_img_name = null;
    private EditText edit_img_context = null;

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
                String title = edit_img_name.getText().toString().trim();

                edit_img_context = findViewById(R.id.edit_img_context);
                String content = edit_img_context.getText().toString().trim();

                postAdd(LoginData.picture.getImageCode(),LoginData.loginUser.getId(), title,content);

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
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(intent, 0);
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
                    Uri uri;
                    ArrayList<File> fileList = new ArrayList<>();
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
                                post(fileList);
                            } else {
                                uri = data.getData();
                                String path = handleImageOkKitKat(uri);
                                File file = new File(path);   //图片地址
                                fileList.add(file);
                                System.out.println(path);
                                post(fileList);

                            }
                        } else {
                            uri = data.getData();
                            String path = handleImageOkKitKat(uri);
                            File file = new File(path);   //图片地址
                            fileList.add(file);
                            System.out.println(path);
                            post(fileList);

                        }
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



}