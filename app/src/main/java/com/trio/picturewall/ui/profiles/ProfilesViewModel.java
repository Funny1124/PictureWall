package com.trio.picturewall.ui.profiles;

import androidx.lifecycle.ViewModel;

import com.trio.picturewall.information.LoginData;

public class ProfilesViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private String mineUserIconPath;
    private String mineUserName;
    private String mineUserIntroduce;

    {
//        mineUserIconPath = LoginData.loginUser.getAvatar();
        mineUserIconPath = LoginData.loginUser.getAvatar();
        mineUserName = LoginData.loginUser.getUsername();
        mineUserIntroduce = LoginData.loginUser.getIntroduce();
    }

    public String getMineUserIconPath() {
        return mineUserIconPath;
    }

    public void setMineUserIconPath(String mineUserIconPath) {
        this.mineUserIconPath = mineUserIconPath;
    }

    public String getMineUserName() {
        return mineUserName;
    }

    public void setMineUserName(String mineUserName) {
        this.mineUserName = mineUserName;
    }

    public String getMineUserIntroduce() {
        return mineUserIntroduce;
    }

    public void setMineUserIntroduce(String mineUserIntroduce) {
        this.mineUserIntroduce = mineUserIntroduce;
    }

}