package com.trio.picturewall.ui.profiles;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
import com.trio.picturewall.activity.AlterActivity;
import com.trio.picturewall.activity.LoginActivity;
import com.trio.picturewall.adapter.MyPostsAdapter;
import com.trio.picturewall.databinding.FragmentProfilesBinding;
import com.trio.picturewall.entity.MyPosts;
import com.trio.picturewall.information.LoginData;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProfilesFragment extends Fragment {

    public static List<MyPosts> myPostsList;
    public RecyclerView recyclerView;//定义RecyclerView
    private MyPostsAdapter myPostsAdapter;

    private ProfilesViewModel profilesViewModel;
    private FragmentProfilesBinding binding;

    private View view;

    public static ProfilesFragment newInstance() {
        return new ProfilesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        profilesViewModel = new ViewModelProvider(this).get(ProfilesViewModel.class);
        binding = FragmentProfilesBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        //加载个人信息
        setUserData();
        //获取信息
        Api.getMyPosts("1","5",LoginData.loginUser.getId());
        //初始化动态数据
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        initRecyclerView();

        binding.mineUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListDialog();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        profilesViewModel = new ViewModelProvider(this).get(ProfilesViewModel.class);
        // TODO: Use the ViewModel
    }

    /**
     * 普通列表dialog
     */
    private void showListDialog() {
        final String[] listItems = new String[]{"修改信息", "退出登录"};

        AlertDialog.Builder listDialog = new AlertDialog.Builder(getContext());
        listDialog.setTitle(getString(R.string.dialog_list_text));
        listDialog.setIcon(R.mipmap.ic_launcher_round);

    /*
        设置item 不能用setMessage()
        用setItems
        items : listItems[] -> 列表项数组
        listener -> 回调接口
    */
        listDialog.setItems(listItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://修改信息
                        Log.v("Avatar Dialog:", listItems[which]);
                        startActivity(new Intent(getActivity(), AlterActivity.class));
                        break;
                    case 1://退出登录
                        Log.v("Avatar Dialog:", listItems[which]);
                        LoginData.loginUser = null;
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        break;
                }
            }
        });

        //设置按钮
        listDialog.setPositiveButton(getString(R.string.dialog_btn_confirm_text)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        listDialog.create().show();
    }

    /**
     * 加载个人信息
     */
    private void setUserData(){
        //加载头像
        Glide.with(this).load(profilesViewModel.getMineUserIconPath()).into(binding.mineUserIcon);
        //加载用户名
        binding.mineUserName.setText(profilesViewModel.getMineUserName());
        //加载用户名
        binding.mineUserIntro.setText(profilesViewModel.getMineUserIntroduce());

    }

    private void initRecyclerView() {
        //获取RecyclerView
        recyclerView = view.findViewById(R.id.lv_news_list);
        //创建adapter
        myPostsAdapter = new MyPostsAdapter(getActivity(), myPostsList);
        //给RecyclerView设置adapter
        recyclerView.setAdapter(myPostsAdapter);
        //设置layoutManager,可以设置显示效果，是线性布局、grid布局，还是瀑布流布局
        //参数是：上下文、列表方向（横向还是纵向）、是否倒叙
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));
        //设置item的分割线
        //mCollectRecyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL));

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                // super.getItemOffsets(outRect, view, parent, state);
                outRect.set(32, 32, 32, 32);
            }
        });

        //RecyclerView中没有item的监听事件，需要自己在适配器中写一个监听事件的接口。参数根据自定义
        myPostsAdapter.setOnItemClickListener(new MyPostsAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, MyPosts data) {
                //此处进行监听事件的业务处理
                Toast.makeText(requireActivity(),"点击了item-home",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 请求数据，控制台可见
     */
    private void initRequest() {

//        String url ="http://"+ OkHttpClientUtils.IP+":2048/requestPosts";
//        OkHttpClientUtils.getRequest(url, new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e){
//                Log.i("initRequest","onFailure:"+e.getMessage());
//            }
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException{
//                String result = Objects.requireNonNull(response.body()).string();
//                if(response.isSuccessful())
//                {
//
//                    ResultBean resultBean = new Gson().fromJson(result,ResultBean.class);
//
//                    postsBeanList = resultBean.getData();
//                    //回调的方法执行在子线程
//                    Log.d("result:",result);
//                }
//            }
//        });
    }
}