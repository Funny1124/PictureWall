package com.trio.picturewall.ui.profiles.myposts;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
import com.trio.picturewall.adapter.MyPostsAdapter;
import com.trio.picturewall.adapter.RecyclerViewAdapter;
import com.trio.picturewall.databinding.MypostsBinding;
import com.trio.picturewall.entity.MyPosts;
import com.trio.picturewall.information.LoginData;

import java.util.ArrayList;
import java.util.List;

public class MyPostsFragment extends Fragment {

    private MyPostsViewModel mViewModel;
    private MypostsBinding binding;
    public static List<MyPosts> myPostsList;
    public RecyclerView recyclerView;//定义RecyclerView
    private MyPostsAdapter myPostsAdapter;
    private View view;


    public static MyPostsFragment newInstance() {
        return new MyPostsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_posts, container, false);
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
        myPostsAdapter = new MyPostsAdapter(getActivity(), myPostsList);
        //给RecyclerView设置adapter
        recyclerView.setAdapter(myPostsAdapter);
        //设置layoutManager,可以设置显示效果，是线性布局、grid布局，还是瀑布流布局
        //参数是：上下文、列表方向（横向还是纵向）、是否倒叙
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
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
                Toast.makeText(requireActivity(), "点击了item-home", Toast.LENGTH_SHORT).show();
            }
        });
    }


}