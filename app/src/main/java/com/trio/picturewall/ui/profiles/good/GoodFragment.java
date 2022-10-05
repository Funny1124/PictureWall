package com.trio.picturewall.ui.profiles.good;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
import com.trio.picturewall.activity.DetailActivity;
import com.trio.picturewall.activity.LoginActivity;
import com.trio.picturewall.activity.RegisterActivity;
import com.trio.picturewall.adapter.RecyclerViewAdapter;
import com.trio.picturewall.entity.MyPosts;
import com.trio.picturewall.entity.Records;
import com.trio.picturewall.information.LoginData;
import com.trio.picturewall.responseBody.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GoodFragment extends Fragment {

    private GoodViewModel mViewModel;
    private View view;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    private List<MyPosts> myPostsList;

    public static GoodFragment newInstance() {
        return new GoodFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        myPostsList = new ArrayList<>();
        view = inflater.inflate(R.layout.fragment_good, container, false);
        getMyPosts("1", "8", LoginData.loginUser.getId());
        initRecyclerView2();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(GoodViewModel.class);
        // TODO: Use the ViewModel
    }

    private void initRecyclerView2() {

        recyclerView = view.findViewById(R.id.good_recyclerView);
        adapter = new RecyclerViewAdapter(getActivity(), myPostsList);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        //RecyclerView中没有item的监听事件，需要自己在适配器中写一个监听事件的接口。参数根据自定义
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, MyPosts data) {
                DetailActivity.shareId = data.getId();
                startActivity(new Intent(getActivity(), DetailActivity.class));
            }
        });
    }

    public void getMyPosts(String current, String size, String userId) {
        // url路径
        String url = "http://47.107.52.7:88/member/photo/like?" +
                "current=" + current +
                "&size=" + size +
                "&userId=" + userId;
        // 请求头
        Headers headers = new Headers.Builder()
                .add("appId", Api.appId)
                .add("appSecret", Api.appSecret)
                .add("Accept", "application/json, text/plain, */*")
                .build();

        //请求组合创建
        Request request = new Request.Builder()
                .url(url)
                // 将请求头加至请求中
                .headers(headers)
                .get()
                .build();
        try {
            OkHttpClient client = new OkHttpClient();
            //发起请求，传入callback进行回调
            client.newCall(request).enqueue(callback);
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    public final Callback callback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            e.printStackTrace();
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            // 获取响应体的json串
            String body = Objects.requireNonNull(response.body()).string();
            Log.d("动态：", body);
            if (isAdded()) {
                requireActivity().runOnUiThread(new Runnable() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {
                        Type jsonType = new TypeToken<ResponseBody<Records>>() {
                        }.getType();
                        // 解析json串到自己封装的状态
                        ResponseBody<Records> dataResponseBody = new Gson().fromJson(body, jsonType);
                        if (dataResponseBody.getData() != null) {//判断当前用户是否有发布帖子
                            Log.d("动态：", dataResponseBody.getData().getRecords().toString());
                            myPostsList.addAll(dataResponseBody.getData().getRecords());
                        } else {
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(requireActivity(), "你没有点赞任何作品！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }
    };
}