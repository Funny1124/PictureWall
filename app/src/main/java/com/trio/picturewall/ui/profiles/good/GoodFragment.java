package com.trio.picturewall.ui.profiles.good;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trio.picturewall.Http.Api;
import com.trio.picturewall.R;
import com.trio.picturewall.adapter.RecyclerViewAdapter;
import com.trio.picturewall.entity.MyPosts;
import com.trio.picturewall.information.LoginData;
import com.trio.picturewall.ui.profiles.myposts.MyPostsFragment;

import java.util.ArrayList;
import java.util.List;

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
        myPostsList = MyPostsFragment.myPostsList;
        view = inflater.inflate(R.layout.fragment_good, container, false);

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

        recyclerView = view.findViewById(R.id.good_list);
        adapter = new RecyclerViewAdapter(getActivity(), myPostsList);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

}