package com.trio.picturewall.ui.profiles.myposts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
import com.trio.picturewall.activity.DetailActivity;
import com.trio.picturewall.adapter.MyPostsAdapter;
import com.trio.picturewall.adapter.RecyclerViewAdapter;
import com.trio.picturewall.databinding.MypostsBinding;
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

public class MyPostsFragment extends Fragment {

    private MyPostsViewModel mViewModel;
    private MypostsBinding binding;
    public List<MyPosts> myPostsList;
    public RecyclerView recyclerView;//定义RecyclerView
    private RecyclerViewAdapter myPostsAdapter;
    private View view;


    public static MyPostsFragment newInstance() {
        return new MyPostsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_posts, container, false);
        myPostsList = new ArrayList<>();
        getMyPosts("1", "8", LoginData.loginUser.getId());
        initRecyclerView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MyPostsViewModel.class);
        // TODO: Use the ViewModel
    }

    private void initRecyclerView() {
        //获取RecyclerView
        recyclerView = view.findViewById(R.id.lv_news_list);
        //创建adapter
        myPostsAdapter = new RecyclerViewAdapter(getActivity(), myPostsList);
        //设置layoutManager,可以设置显示效果，是线性布局、grid布局，还是瀑布流布局
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        //给RecyclerView设置adapter
        recyclerView.setAdapter(myPostsAdapter);
        //参数是：上下文、列表方向（横向还是纵向）、是否倒叙
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        //RecyclerView中没有item的监听事件，需要自己在适配器中写一个监听事件的接口。参数根据自定义
        myPostsAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, MyPosts data) {
                DetailActivity.shareId = data.getId();
                startActivity(new Intent(getActivity(), DetailActivity.class));
            }
        });
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

            /*
            if (isAdded())
            解决Fragment MyPostsFragment{bc1af82} (d470bac1-b3ff-48ba-9586-aaa5eb1498a6)
             not attached to an activity.
             */
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
                                    Toast.makeText(requireActivity(), "你没有发布任何作品！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        myPostsAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    };
}