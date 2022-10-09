package com.trio.picturewall.ui.home.find;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class FindFragment extends Fragment {

    private FindViewModel findViewModel;
    private FindFragment binding;
    public List<MyPosts> myPostsList;
    public RecyclerView recyclerView;//定义RecyclerView
    private RecyclerViewAdapter myPostsAdapter;
    private View view;
    private SwipeRefreshLayout swipe;
    private int current = 1;

    public static FindFragment newInstance() {
        return new FindFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_find, container, false);
        swipe = view.findViewById(R.id.swipe_find);
        myPostsList = new ArrayList<>();
        find(current);
        initRecyclerView();

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                initRecyclerView();

                //在获取数据完成后设置刷新状态为false
                //isRefreshing() 是否是处于刷新状态
                if (swipe.isRefreshing()) {
                    swipe.setRefreshing(false);
                }
            }
        });

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
                    if ((totalItemCount - lastPositon) <= 9) {
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
        findViewModel = new ViewModelProvider(this).get(FindViewModel.class);
        // TODO: Use the ViewModel
    }

    private void initRecyclerView() {
        //获取RecyclerView
        recyclerView = view.findViewById(R.id.find_list);
        //创建adapter
        myPostsAdapter = new RecyclerViewAdapter(getActivity(), myPostsList);
        //给RecyclerView设置adapter
        recyclerView.setAdapter(myPostsAdapter);

        //设置layoutManager,可以设置显示效果，是线性布局、grid布局，还是瀑布流布局
        //参数是：上下文、列表方向（横向还是纵向）、是否倒叙
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        //RecyclerView中没有item的监听事件，需要自己在适配器中写一个监听事件的接口。参数根据自定义
        myPostsAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, MyPosts data) {
                //此处进行监听事件的业务处理
                DetailActivity.post = data;
                startActivity(new Intent(getActivity(), DetailActivity.class));
            }
        });
    }

    public void find(int current) {
        // url路径
        String url = "http://47.107.52.7:88/member/photo/share?" +
                "current=" + current +
                "&size=8" +
                "&userId=" + LoginData.loginUser.getId();

        // 请求头
        Headers headers = new Headers.Builder()
                .add("Accept", "application/json, text/plain, */*")
                .add("appId", Api.appId)
                .add("appSecret", Api.appSecret)
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
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    // 获取响应体的json串
                    String body = Objects.requireNonNull(response.body()).string();
                    Log.d("发现：", body);
                    if (isAdded()) {
                        requireActivity().runOnUiThread(new Runnable() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void run() {
                                Type jsonType = new TypeToken<ResponseBody<Records>>() {
                                }.getType();
                                // 解析json串到自己封装的状态
                                ResponseBody<Records> dataResponseBody = new Gson().fromJson(body, jsonType);
                                if (dataResponseBody.getData() != null) {
                                    Log.d("发现：", dataResponseBody.getData().getRecords().toString());
                                    myPostsList.addAll(dataResponseBody.getData().getRecords());
                                } else {
                                    Toast.makeText(requireActivity(), "暂时没有人分享作品！", Toast.LENGTH_SHORT).show();
                                }
                                myPostsAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            });
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }
    }

    public void refreshData() {
        current++;
        find(current);
    }
}