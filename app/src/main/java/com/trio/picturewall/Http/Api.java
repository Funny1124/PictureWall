package com.trio.picturewall.Http;

import android.os.NetworkOnMainThreadException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trio.picturewall.entity.Picture;
import com.trio.picturewall.entity.User;
import com.trio.picturewall.information.LoginData;
import com.trio.picturewall.responseBody.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Api {

//    public static String appId = "f3d10b15acaf4ed0a0cee98adc03b447";
//    public static String appSecret = "545153dc74d28165d46f9833b9e7282fb20ce";

    public static String appId = "036c2739697b4e89997e5897849d2975";
    public static String appSecret = "21695a53223293e7b4b64bef4935133f57af3";

//    public static String appId = "10f623a5dc0345e0ade966247f1c7a24";
//    public static String appSecret = "48335c92b7b6fbf374899af0d381708a379fe";

//    public static String appId = "71c1c3d47205488eab5be23dc03b447d";
//    public static String appSecret = "073692b705b0437da4203b663467a62d8df70";


    static Gson gson = new Gson();

    public static void alter(User alterUser) {
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/user/update";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", appId)
                    .add("appSecret", appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            // 请求体
            // PS.用户也可以选择自定义一个实体类，然后使用类似fastjson的工具获取json串
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("avatar", alterUser.getAvatar());
            bodyMap.put("id", alterUser.getId());
            bodyMap.put("introduce", alterUser.getIntroduce());
            bodyMap.put("sex", alterUser.getSex());
            bodyMap.put("username", alterUser.getUsername());
            // 将Map转换为字符串类型加入请求体中
            String body = gson.toJson(bodyMap);

            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(ResponseBody.callback);

            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public static void avatarpost(File fileList) {
        new Thread(() -> {


            // url路径
            String url = "http://47.107.52.7:88/member/photo/image/upload";

            Log.d("DialogActivity", "upload-run: 上传照片！");

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", appId)
                    .add("appSecret", appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            MediaType mediaType = MediaType.Companion.parse("text/x-markdown; charset=utf-8");
            RequestBody fileBody = RequestBody.Companion.create(fileList, mediaType);
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("fileList", fileList.getName(), fileBody)
                    .build();

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
                        LoginData.avater = dataResponseBody.getData();
                        LoginData.loginUser.setAvatar(LoginData.avater.getImageUrlList().get(0));
                        Log.d("info", dataResponseBody.toString());
                        Log.d("User:", LoginData.avater.getImageUrlList().toString());
                        Log.d("User:", LoginData.loginUser.getAvatar());
                    }
                });

            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public static void postAdd(String imageCode, String pUserId, String title, String content) {
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/share/add";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", appId)
                    .add("appSecret", appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            // 请求体
            // PS.用户也可以选择自定义一个实体类，然后使用类似fastjson的工具获取json串
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("content", content);
            bodyMap.put("imageCode", imageCode);
            bodyMap.put("pUserId", pUserId);
            bodyMap.put("title", title);
            // 将Map转换为字符串类型加入请求体中
            String body = gson.toJson(bodyMap);

            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(ResponseBody.callback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }


}
