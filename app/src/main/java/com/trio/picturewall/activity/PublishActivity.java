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
import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
import com.trio.picturewall.adapter.GridViewAdapter;
import com.trio.picturewall.entity.Picture;
import com.trio.picturewall.information.LoginData;
import com.trio.picturewall.responseBody.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static final int PHOTO_REQUEST_CAREMA = 1;// ??????
    public static final int CROP_PHOTO = 2;// ??????
    public static final int ARRAY_PHOTO = 3;// ????????????
    private Uri imageUri = null;
    //??????????????????
    private boolean isShowDelete = false;

    //????????????
    ArrayList<File> fileList = new ArrayList<>();
    //????????????
    private GridView gridViewPhoto;
    //???????????????????????????bitmap
    private List<Bitmap> listpath;
    //?????????????????????????????????
    private String scanpath;
    //????????????????????????
    private GridViewAdapter adapter;
    String title = null;
    String content = null;
    //????????????
    Dialog mDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        findViewById(R.id.publish).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);

        gridViewPhoto = (GridView) findViewById(R.id.gv);
        initData(fileList);
        //????????????
        gridViewPhoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < fileList.size()) {
                    Toast.makeText(getApplicationContext(), "???????????????", Toast.LENGTH_LONG).show();
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(PublishActivity.this, Manifest.permission.CAMERA) || !ActivityCompat.shouldShowRequestPermissionRationale(PublishActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(PublishActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                    if (ContextCompat.checkSelfPermission(PublishActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        //??????WRITE_EXTERNAL_STORAGE??????
                        return;
                    } else showDialog();

                }
            }
        });
        //????????????
        gridViewPhoto.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (isShowDelete) {
                    isShowDelete = false;
                } else {
                    isShowDelete = true;
                    adapter.setIsShowDelete(isShowDelete);
                    if (position < fileList.size()) {
                        findViewById(R.id.delete_markView).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                delete(position);//???????????????
                                initData(fileList);
                            }
                        });
                    } else {
                        showDialog();
                    }
                }
                adapter.setIsShowDelete(isShowDelete);//setIsShowDelete()??????????????????isShowDelete???
                return true;
            }
        });

    }

    private void delete(int position) {//?????????????????????
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

                if (title.equals("") || content.equals("")) {
                    Toast toast = Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    if (fileList.size() > 0) {
                        //???????????????
                        post(fileList, title, content, 1);
                        Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.save:
                edit_img_name = findViewById(R.id.edit_img_name);
                title = edit_img_name.getText().toString().trim();

                edit_img_context = findViewById(R.id.edit_img_context);
                content = edit_img_context.getText().toString().trim();

                if (title.equals("") || content.equals("")) {
                    Toast toast = Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    if (fileList.size() > 0) {
                        //???????????????
                        post(fileList, title, content, 0);
                        Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT).show();
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
                                    //uri?????????
                                    String path = handleImageOkKitKat(imageUri);
                                    File file = new File(path);   //????????????
                                    fileList.add(file);
                                    System.out.println(file);
                                }
                            } else {
                                uri = data.getData();
                                String path = handleImageOkKitKat(uri);
                                File file = new File(path);   //????????????
                                fileList.add(file);
                                System.out.println(path);
                            }
                        } else {
                            uri = data.getData();
                            String path = handleImageOkKitKat(uri);
                            File file = new File(path);   //????????????
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
                    startActivityForResult(intent, CROP_PHOTO); // ??????????????????
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        imageUri = data.getData();
                        String path = handleImageOkKitKat(imageUri);
                        File file = new File(path);   //????????????
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

    //??????uri??????
    private String handleImageOkKitKat(Uri uri) {
        String imagePath = null;
        Log.d("uri=intent.getData :", "" + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);        //????????????????????????
            Log.d("getDocumentId(uri) :", "" + docId);
            Log.d("uri.getAuthority() :", "" + uri.getAuthority());
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        }
        return imagePath;
    }

    //uri?????????
    @SuppressLint("Range")
    public String getImagePath(Uri uri, String selection) {
        System.out.println("uri???file");
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);   //???????????????
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));   //????????????
            }
        }
        cursor.close();
        return path;
    }

    //??????????????????????????????
    private void initData(ArrayList<File> fileList) {
        listpath = new ArrayList<>();
        /**????????????*/
        for (int i = 0; i < fileList.size(); i++) {
            /**???????????????bitmap?????????????????????????????????*/
            Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(fileList.get(i)));
            if (bitmap != null) {
                listpath.add(bitmap);
            }
        }
        /** ?????????????????????*/
        adapter = new GridViewAdapter(listpath, this);
        gridViewPhoto.setAdapter(adapter);
    }


    //???????????????
    private void showDialog() {
        if (mDialog == null) {
            initShareDialog();
        }
        mDialog.show();
    }

    //dialog ?????????
    private void initShareDialog() {
        mDialog = new Dialog(this, R.style.dialogStyle);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(true);            //????????????????????????
        Window window = mDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);      //????????????
        window.setWindowAnimations(R.style.dialogStyle);    //????????????
        View inflate = View.inflate(this, R.layout.dialog_picture_camera, null);
        inflate.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();      //???????????????
                }
            }
        });
        inflate.findViewById(R.id.dialog_Picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();      //???????????????
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
                }
            }
        });
        inflate.findViewById(R.id.dialog_Camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();      //???????????????
                    openCamera(PublishActivity.this);
                }
            }
        });
        window.setContentView(inflate);
        //????????????
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    //??????????????????
    public void openCamera(Activity activity) {
        //??????????????????
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // ????????????
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File tempFile;

        // ???????????????????????????????????????????????????
        if (hasSdcard()) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                    "yyyy_MM_dd_HH_mm_ss");
            String filename = timeStampFormat.format(new Date());
            tempFile = new File(Environment.getExternalStorageDirectory(), filename + ".jpg");
            if (currentapiVersion < 24) {
                // ??????????????????uri
                imageUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            } else {
                //???????????????????????????
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());

                //??????????????????????????????????????????
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //??????WRITE_EXTERNAL_STORAGE??????
                    //Util.showToast(this,"?????????????????????");
                    Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show();
                    return;
                }
                imageUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }
        }
        // ??????????????????????????????Activity???????????????PHOTO_REQUEST_CAREMA
        activity.startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    /*
     * ??????sdcard???????????????
     */
    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * ????????????????????????????????????
     *
     * @param fileList ????????????
     * @param title    ??????
     * @param content  ??????
     * @param choose   choose==1???????????????????????????
     */
    public static void post(ArrayList<File> fileList, String title, String content, int choose) {
        new Thread(() -> {
            Gson gson = new Gson();
            int length = fileList.size();

            // url??????
            String url = "http://47.107.52.7:88/member/photo/image/upload";

            Log.d("DialogActivity", "upload-run: ???????????????");
            Log.d("fileList.size()", String.valueOf(length));
            // ?????????
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

            //??????????????????
            Request request = new Request.Builder()
                    .url(url)
                    // ???????????????????????????
                    .headers(headers)
                    .post(body)
                    .build();
            System.out.println(request);
            try {
                OkHttpClient client = new OkHttpClient();
                //?????????????????????callback????????????
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Type jsonType = new TypeToken<ResponseBody<Picture>>() {
                        }.getType();
                        // ??????????????????json???
                        String body = Objects.requireNonNull(response.body()).string();
                        Log.d("info", body);
                        // ??????json???????????????????????????
                        ResponseBody<Picture> dataResponseBody = gson.fromJson(body, jsonType);
                        LoginData.picture = dataResponseBody.getData();
                        Log.d("info", dataResponseBody.toString());
                        Log.d("Picture:", LoginData.picture.getImageCode());
                        Log.d("Picture:", String.valueOf(LoginData.picture.getImageUrlList()));
                        if (choose == 1) {
                            postAdd(LoginData.picture.getImageCode(), LoginData.loginUser.getId(), title, content);
                        } else {
                            save(LoginData.picture.getImageCode(), LoginData.loginUser.getId(), title, content);
                        }
                    }
                });

            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public static void save(String imageCode, String pUserId, String title, String content) {
        new Thread(() -> {

            // url??????
            String url = "http://47.107.52.7:88/member/photo/share/save";

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
            bodyMap.put("imageCode", imageCode);
            bodyMap.put("pUserId", pUserId);
            bodyMap.put("title", title);
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
        }).start();
    }
}