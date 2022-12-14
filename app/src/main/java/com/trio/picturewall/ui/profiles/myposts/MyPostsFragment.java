package com.trio.picturewall.ui.profiles.myposts;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
import com.trio.picturewall.activity.DetailActivity;
import com.trio.picturewall.adapter.MyPostAdapter;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyPostsFragment extends Fragment {

    private MyPostsViewModel mViewModel;
    public List<MyPosts> myPostsList;
    public RecyclerView recyclerView;//定义RecyclerView
    private MyPostAdapter myPostsAdapter;
    private View view;
    private int current = 1;
    private int size = 8;
    private String userId = LoginData.loginUser.getId();
    private int shareId;
    public static MyPostsFragment newInstance() {
        return new MyPostsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_posts, container, false);
        myPostsList = new ArrayList<>();
        getMyPosts();
        initRecyclerView();

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
        mViewModel = new ViewModelProvider(this).get(MyPostsViewModel.class);
        // TODO: Use the ViewModel
    }

    private void initRecyclerView() {
        //获取RecyclerView
        recyclerView = view.findViewById(R.id.lv_news_list);
        //创建adapter
        myPostsAdapter = new MyPostAdapter(getActivity(), myPostsList);
        //设置layoutManager,可以设置显示效果，是线性布局、grid布局，还是瀑布流布局
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        //给RecyclerView设置adapter
        recyclerView.setAdapter(myPostsAdapter);
        //参数是：上下文、列表方向（横向还是纵向）、是否倒叙
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        //RecyclerView中没有item的监听事件，需要自己在适配器中写一个监听事件的接口。参数根据自定义
        myPostsAdapter.setOnItemClickListener(new MyPostAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, MyPosts data) {
                DetailActivity.post = data;
                startActivity(new Intent(getActivity(), DetailActivity.class));
            }
        });

        myPostsAdapter.setOnItemLongClickListener(new MyPostAdapter.OnItemLongClickListener() {
            @Override
            public void OnItemLongClick(View view, MyPosts data) {
                shareId = data.getId();
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.delete, popupMenu.getMenu());

                //弹出式菜单的菜单项点击事件
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        myPostsList.remove(data);
                        //此处回传点击监听事件
                        myPostsAdapter.notifyItemRemoved(myPostsAdapter.pos);
                        deleteMyPost();
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }


    public void getMyPosts() {
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
                            Toast.makeText(requireActivity(), "你没有发布任何作品！", Toast.LENGTH_SHORT).show();
                        }
                        myPostsAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    };

    public void refreshData() {
        current++;
        getMyPosts();
    }

    public void deleteMyPost() {
        // url路径
        String url = "http://47.107.52.7:88/member/photo/share/delete?shareId="
                + shareId + "&userId=" + LoginData.loginUser.getId();

        // 请求头
        Headers headers = new Headers.Builder()
                .add("Accept", "application/json, text/plain, */*")
                .add("appId", Api.appId)
                .add("appSecret", Api.appSecret)
                .build();

        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

        //请求组合创建
        Request request = new Request.Builder()
                .url(url)
                // 将请求头加至请求中
                .headers(headers)
                .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                .build();
        try {
            OkHttpClient client = new OkHttpClient();
            //发起请求，传入callback进行回调
            client.newCall(request).enqueue(ResponseBody.callback);
        } catch (NetworkOnMainThreadException ex) {
            ex.printStackTrace();
        }


    }
}