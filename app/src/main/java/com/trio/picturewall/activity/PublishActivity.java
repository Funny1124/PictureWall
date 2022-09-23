package com.trio.picturewall.activity;

import static com.trio.picturewall.Http.Api.post;
import static com.trio.picturewall.Http.Api.postAdd;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                //intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 0);


        }
    }


    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
//                if (resultCode == RESULT_OK) {
//                    try {
//                        if(data != null) {
//                            //获取uri
//                            Uri uri = data.getData();
//                            imageUri = uri;
//                            //uri转文件
//                            String path = getImagePath(imageUri,null);
//                            File file = new File(path);   //图片地址
//                            //上传到服务器
//                            post(file);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
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
                                    String path = getImagePath(imageUri, null);
                                    File file = new File(path);   //图片地址
                                    fileList.add(file);
                                    System.out.println(file);
                                }
                                post(fileList);
                                //uri = imageNames.getItemAt(0).getUri();
                            } else {
                                uri = data.getData();
                                String path = getImagePath(uri, null);
                                File file = new File(path);   //图片地址
                                //post(file);
                                //fileList.add(uri.toString());
                            }
                        } else {
                            uri = data.getData();
                            String path = getImagePath(uri, null);
                            File file = new File(path);   //图片地址
                            //post(file);
                            //fileList.add(uri.toString());
                        }
                    } catch (Exception e) {

                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


}