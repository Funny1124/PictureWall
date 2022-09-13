package com.trio.picturewall.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
import com.trio.picturewall.entity.User;
import com.trio.picturewall.information.LoginData;

public class AlterActivity extends AppCompatActivity {

    private ImageView temp_avatar;
    private EditText temp_username;
    private EditText temp_sex;
    private EditText temp_introduce;
    private Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter);

        btn_save=findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Api.alter(alterInfo());
            }
        });
    }

    private User alterInfo(){
        temp_avatar=findViewById(R.id.iv_avatar);
        temp_username=findViewById(R.id.temp_username);
        temp_sex=findViewById(R.id.temp_sex);
        temp_introduce=findViewById(R.id.temp_introduce);
        User alterUser = new User();
        alterUser.setId(LoginData.loginUser.getId());
//        alterUser.setAvatar(temp_avatar.);
        alterUser.setUsername(temp_username.getText().toString().trim());
        alterUser.setSex(temp_sex.getText().toString().trim());
        alterUser.setIntroduce(temp_introduce.getText().toString().trim());
        return alterUser;
    }
}