package com.trio.picturewall.ui.profiles.collecttion;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
import com.trio.picturewall.adapter.CollecttionAdapter;
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

public class CollectionFragment extends Fragment {

    private CollectionViewModel mViewModel;
    private RecyclerViewAdapter adapter;
    private List<MyPosts> myPostsList;
    private RecyclerView recyclerView;
    private View view;

    public CollectionFragment() {
    }

    public static CollectionFragment newInstance() {
        return new CollectionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_collection, container, false);
        recyclerView = view.findViewById(R.id.clllected_recyclerView);
        initData();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CollectionViewModel.class);
        // TODO: Use the ViewModel
    }

    private void initData() {
        myPostsList = new ArrayList<>();
        adapter = new RecyclerViewAdapter(getActivity(), myPostsList);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        getMyPosts("1", "6", LoginData.loginUser.getId());
    }

    private void initView() {


//        lvNewsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//            }
//        }
    }

    public void getMyPosts(String current, String size, String userId) {
        // url路径
        String url = "http://47.107.52.7:88/member/photo/share/myself?" +
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
                        Gson gson = new Gson();
                        Type jsonType = new TypeToken<ResponseBody<Records>>() {
                        }.getType();
                        // 解析json串到自己封装的状态
                        ResponseBody<Records> dataResponseBody = new Gson().fromJson(body, jsonType);
                        Log.d("动态：", dataResponseBody.getData().getRecords().toString());
                        myPostsList.addAll(dataResponseBody.getData().getRecords());
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }
    };
}