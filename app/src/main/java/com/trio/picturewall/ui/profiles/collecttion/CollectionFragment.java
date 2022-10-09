package com.trio.picturewall.ui.profiles.collecttion;

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
    private int current = 1;
    private int size = 1;
    private String userId = LoginData.loginUser.getId();

    public CollectionFragment() {
    }

    public static CollectionFragment newInstance() {
        return new CollectionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_collection, container, false);
        recyclerView = view.findViewById(R.id.collected_recyclerView);
        initData();

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            //用来标记是否正在向最后一个滑动
            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //设置什么布局管理器,就获取什么的布局管理器
                int[] positions = null;
                StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                // 当停止滑动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的ItemPosition ,角标值
                    int[] into = manager.findLastVisibleItemPositions(positions);
                    //所有条目,数量值
                    int totalItemCount = manager.getItemCount();
                    int lastPositon = Math.max(into[0], into[1]);
                    // 判断是否滚动到底部，并且是向右滚动
                    if ((totalItemCount - lastPositon) <= 8) {
                        //加载更多功能的代码
                        refreshData();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
                //dx>0:向右滑动,dx<0:向左滑动
                //dy>0:向下滑动,dy<0:向上滑动
                if (dy > 0) {
                    isSlidingToLast = true;
                } else {
                    isSlidingToLast = false;
                }
            }
        });

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
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, MyPosts data) {
                DetailActivity.post = data;
                startActivity(new Intent(getActivity(), DetailActivity.class));
            }
        });
        getcollet();
    }

    public void getcollet() {
        // url路径
        String url = "http://47.107.52.7:88/member/photo/collect?" +
                "current=" + current +
                "&size=8" + size +
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
                            Toast.makeText(requireActivity(), "你没有收藏任何分享！", Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }
    };

    public void refreshData() {
        current++;
        getcollet();
    }
}