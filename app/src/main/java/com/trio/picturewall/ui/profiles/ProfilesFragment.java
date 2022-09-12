package com.trio.picturewall.ui.profiles;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trio.picturewall.R;
import com.trio.picturewall.databinding.FragmentProfilesBinding;
import com.trio.picturewall.ui.share.ShareViewModel;

public class ProfilesFragment extends Fragment {

    private ProfilesViewModel profilesViewModel;
    private FragmentProfilesBinding binding;
    public static ProfilesFragment newInstance() {
        return new ProfilesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        profilesViewModel=new ViewModelProvider(this).get(ProfilesViewModel.class);
        binding = FragmentProfilesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        profilesViewModel = new ViewModelProvider(this).get(ProfilesViewModel.class);
        // TODO: Use the ViewModel
    }

}