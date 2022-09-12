package com.trio.picturewall.ui.home;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.trio.picturewall.R;
import com.trio.picturewall.ui.home.find.FindFragment;
import com.trio.picturewall.ui.home.following.FollowingFragment;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private FindFragment findFragment;
    private FollowingFragment followingFragment;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        findFragment = FindFragment.newInstance();
        followingFragment = FollowingFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.linearLayout,findFragment).commit();
        TabLayout tabLayout = root.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (Objects.equals(tab.getText(), "发现")){
                    getChildFragmentManager().beginTransaction().replace(R.id.linearLayout,findFragment).commit();
                }
                if (tab.getText().equals("关注")){
                    getChildFragmentManager().beginTransaction().replace(R.id.linearLayout,followingFragment).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }

}