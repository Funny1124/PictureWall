package com.trio.picturewall.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;

import com.trio.picturewall.R;

public class PhotoActivity extends AppCompatActivity {

    private ImageView imageView;
    Bitmap bitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        //显示在imageView_cen中
        imageView  = (ImageView)findViewById(R.id.imageView_cen);

        Intent intent=getIntent();
        if(intent!=null)
        {
            bitmap=intent.getParcelableExtra("bitmap");
            imageView.setImageBitmap(bitmap);
        }
    }

}