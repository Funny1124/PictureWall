package com.trio.picturewall.activity;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
import com.trio.picturewall.entity.User;
import com.trio.picturewall.information.LoginData;
import com.trio.picturewall.ui.profiles.ProfilesFragment;

import java.io.EOFException;
import java.io.File;

public class AlterActivity extends AppCompatActivity {

    private ImageView iv_back;
    private ImageView temp_avatar;
    private EditText temp_username;
//    private EditText temp_sex;
    private EditText temp_introduce;
    private Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter);

        iv_back = findViewById(R.id.iv_back);
        temp_avatar = findViewById(R.id.iv_avatar);
        temp_username = findViewById(R.id.temp_username);
//        temp_sex = findViewById(R.id.temp_sex);
        temp_introduce = findViewById(R.id.temp_introduce);
        btn_save = findViewById(R.id.btn_save);

        autoInput();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Api.alter(alterInfo());
                finish();
            }
        });
        temp_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK){
                    try {
                    Uri uri = null;
                    if (data != null){
                        uri = data.getData();
                        String path = handleImageOkKitKat(uri);
                        File file = new File(path);   //图片地址
                        Api.avatarpost(file);
                        System.out.println(path);
                    }
                }catch (Exception e){
                        System.out.println(e);
                    }
        }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    //判断图片文件
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

    private void autoInput() {
        String url = LoginData.loginUser.getAvatar();
        //加载头像
        Glide.with(this).load(url).into(temp_avatar);

        temp_username.setText(LoginData.loginUser.getUsername());
//        temp_sex.setText(LoginData.loginUser.getSex());
        temp_introduce.setText(LoginData.loginUser.getIntroduce());
    }

    private User alterInfo() {

        User alterUser = new User();
        alterUser.setId(LoginData.loginUser.getId());
        alterUser.setAvatar(LoginData.loginUser.getAvatar());
        alterUser.setUsername(temp_username.getText().toString().trim());
//        alterUser.setSex(temp_sex.getText().toString().trim());
        alterUser.setSex("0");
        alterUser.setIntroduce(temp_introduce.getText().toString().trim());
        return alterUser;
    }


}