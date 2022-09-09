package com.trio.picturewall.ui.share;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trio.picturewall.R;
import com.trio.picturewall.activity.PublishActivity;
import com.trio.picturewall.databinding.FragmentShareBinding;
import com.trio.picturewall.widget.CircleButton;

public class ShareFragment extends Fragment {

    private ShareViewModel mViewModel;
    private FragmentShareBinding binding;

    public static ShareFragment newInstance() {
        return new ShareFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ShareViewModel shareViewModel =
                new ViewModelProvider(this).get(ShareViewModel.class);

        binding = FragmentShareBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final CircleButton circleButton = binding.upload;
        circleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PublishActivity.class));
            }
        });

        return root;    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ShareViewModel.class);
        // TODO: Use the ViewModel
    }

}