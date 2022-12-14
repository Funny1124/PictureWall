package com.trio.picturewall.ui.profiles;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.trio.picturewall.ui.profiles.like.LikeFragment;
import com.trio.picturewall.ui.profiles.myposts.MyPostsFragment;

import java.util.Objects;

public class ProfilesFragment extends Fragment {

    private MyPostsFragment myPostsFragment;
    private CollectionFragment collectionFragment;
    private LikeFragment likeFragment;
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
        likeFragment = LikeFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.profiles_linearLayout, myPostsFragment).commit();
        TabLayout tabLayout = view.findViewById(R.id.profiles_tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (Objects.equals(tab.getText(), "??????")) {
                    getChildFragmentManager().beginTransaction().replace(R.id.profiles_linearLayout, myPostsFragment).commit();
                }
                if (tab.getText().equals("??????")) {
                    getChildFragmentManager().beginTransaction().replace(R.id.profiles_linearLayout, collectionFragment).commit();
                }
                if (tab.getText().equals("??????")) {
                    getChildFragmentManager().beginTransaction().replace(R.id.profiles_linearLayout, likeFragment).commit();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //??????????????????
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
     * ????????????dialog
     */
    private void showListDialog() {
        final String[] listItems = new String[]{"????????????", "????????????"};

        AlertDialog.Builder listDialog = new AlertDialog.Builder(getContext());
        listDialog.setTitle(getString(R.string.dialog_list_text));
        listDialog.setIcon(R.mipmap.ic_launcher_round);

    /*
        ??????item ?????????setMessage()
        ???setItems
        items : listItems[] -> ???????????????
        listener -> ????????????
    */
        listDialog.setItems(listItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://????????????
                        Log.v("Avatar Dialog:", listItems[which]);
                        startActivity(new Intent(getActivity(), AlterActivity.class));
                        break;
                    case 1://????????????
                        Log.v("Avatar Dialog:", listItems[which]);
                        LoginData.loginUser = null;
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        break;
                }
            }
        });

        //????????????
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
     * ??????????????????
     */
    private void setUserData() {
//        String avatar = LoginData.loginUser.getAvatar();
        String avatar = profilesViewModel.getMineUserIconPath();
        String userIntro = profilesViewModel.getMineUserIntroduce();
        if (avatar != null){
            //????????????
            Glide.with(this).load(avatar).into(binding.mineUserIcon);
        }
        //???????????????
        binding.mineUserName.setText(profilesViewModel.getMineUserName());
        if (userIntro != null){
            //???????????????
            binding.mineUserIntro.setText(userIntro);
        }else {
            binding.mineUserIntro.setText("???????????????????????????????????????");
        }
    }


}