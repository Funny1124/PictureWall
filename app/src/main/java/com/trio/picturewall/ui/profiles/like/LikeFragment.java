package com.trio.picturewall.ui.profiles.like;

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

public class LikeFragment extends Fragment {

    private LikeViewModel mViewModel;
    private View view;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    private List<MyPosts> myPostsList;
    private int current = 1;
    private int size = 8;
    private String userId = LoginData.loginUser.getId();

    public static LikeFragment newInstance() {
        return new LikeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        myPostsList = new ArrayList<>();
        view = inflater.inflate(R.layout.fragment_good, container, false);
        getLike();
        initRecyclerView();

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            //?????????????????????????????????????????????
            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //???????????????????????????,?????????????????????????????????
                int[] positions = null;
                StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                // ??????????????????
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //?????????????????????????????????ItemPosition ,?????????
                    assert manager != null;
                    int[] into = manager.findLastVisibleItemPositions(positions);
                    //????????????,?????????
                    int totalItemCount = manager.getItemCount();
                    int lastPositon = Math.max(into[0], into[1]);
                    // ???????????????????????????????????????????????????
                    if ((totalItemCount - lastPositon) <= 8) {
                        //???????????????????????????
                        refreshData();
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dx?????????????????????????????????dy??????????????????????????????
                //dx>0:????????????,dx<0:????????????
                //dy>0:????????????,dy<0:????????????
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
        mViewModel = new ViewModelProvider(this).get(LikeViewModel.class);
        // TODO: Use the ViewModel
    }

    private void initRecyclerView() {

        recyclerView = view.findViewById(R.id.good_recyclerView);
        adapter = new RecyclerViewAdapter(getActivity(), myPostsList);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        //RecyclerView?????????item???????????????????????????????????????????????????????????????????????????????????????????????????
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, MyPosts data) {
                DetailActivity.post = data;
                startActivity(new Intent(getActivity(), DetailActivity.class));
            }
        });
    }

    public void getLike() {
        // url??????
        String url = "http://47.107.52.7:88/member/photo/like?" +
                "current=" + current +
                "&size=" + size +
                "&userId=" + userId;
        // ?????????
        Headers headers = new Headers.Builder()
                .add("appId", Api.appId)
                .add("appSecret", Api.appSecret)
                .add("Accept", "application/json, text/plain, */*")
                .build();

        //??????????????????
        Request request = new Request.Builder()
                .url(url)
                // ???????????????????????????
                .headers(headers)
                .get()
                .build();
        try {
            OkHttpClient client = new OkHttpClient();
            //?????????????????????callback????????????
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

            // ??????????????????json???
            String body = Objects.requireNonNull(response.body()).string();
            Log.d("?????????", body);
            if (isAdded()) {
                requireActivity().runOnUiThread(new Runnable() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {
                        Type jsonType = new TypeToken<ResponseBody<Records>>() {
                        }.getType();
                        // ??????json???????????????????????????
                        ResponseBody<Records> dataResponseBody = new Gson().fromJson(body, jsonType);
                        if (dataResponseBody.getData() != null) {//???????????????????????????????????????
                            Log.d("?????????", dataResponseBody.getData().getRecords().toString());
                            myPostsList.addAll(dataResponseBody.getData().getRecords());
                        } else {
                            Toast.makeText(requireActivity(), "???????????????????????????", Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }
    };

    public void refreshData() {
        current++;
        getLike();
    }
}