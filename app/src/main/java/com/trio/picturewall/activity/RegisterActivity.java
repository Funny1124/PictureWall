package com.trio.picturewall.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText registerUsername = findViewById(R.id.registerUsername);
        EditText registerPassword = findViewById(R.id.registerPassword);
        EditText verifyPassword = findViewById(R.id.verifyPassword);


        findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = registerUsername.getText().toString();
                String password = registerPassword.getText().toString();
                String vPassword = verifyPassword.getText().toString();
                if (password.equals(vPassword)){
                    Api.register(username,password);
                    startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                    finish();
                }else {
                    Log.v("注册","两次密码不同");
                }

            }
        });
    }
}