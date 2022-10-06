package com.trio.picturewall.ui.profiles;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.trio.picturewall.R;
import com.trio.picturewall.activity.AlterActivity;
import com.trio.picturewall.activity.LoginActivity;
import com.trio.picturewall.databinding.FragmentProfilesBinding;
import com.trio.picturewall.information.LoginData;
import com.trio.picturewall.ui.profiles.collecttion.CollectionFragment;
import com.trio.picturewall.ui.profiles.good.GoodFragment;
import com.trio.picturewall.ui.profiles.myposts.MyPostsFragment;

import java.util.Objects;

public class ProfilesFragment extends Fragment {

    private MyPostsFragment myPostsFragment;
    private CollectionFragment collectionFragment;
    private GoodFragment goodFragment;
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

        myPostsFragment = MyPostsFragment.newInstance();
        collectionFragment = CollectionFragment.newInstance();
        goodFragment = GoodFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.profiles_linearLayout, myPostsFragment).commit();
        TabLayout tabLayout = view.findViewById(R.id.profiles_tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (Objects.equals(tab.getText(), "动态")) {
                    getChildFragmentManager().beginTransaction().replace(R.id.profiles_linearLayout, myPostsFragment).commit();
                }
                if (tab.getText().equals("收藏")) {
                    getChildFragmentManager().beginTransaction().replace(R.id.profiles_linearLayout, collectionFragment).commit();
                }
                if (tab.getText().equals("点赞")) {
                    getChildFragmentManager().beginTransaction().replace(R.id.profiles_linearLayout, goodFragment).commit();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //加载个人信息
        setUserData();

        binding.profilesSetting.setOnClickListener(new View.OnClickListener() {
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
    private void setUserData() {
        String url = LoginData.loginUser.getAvatar();
        //加载头像
        Glide.with(this).load(url).into(binding.mineUserIcon);
        //加载用户名
        binding.mineUserName.setText(profilesViewModel.getMineUserName());
        //加载用户名
        binding.mineUserIntro.setText(profilesViewModel.getMineUserIntroduce());
    }

}