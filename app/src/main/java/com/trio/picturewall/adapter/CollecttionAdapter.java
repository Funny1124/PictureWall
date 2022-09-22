package com.trio.picturewall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.trio.picturewall.R;
import com.trio.picturewall.entity.MyPosts;

import java.util.List;

public class CollecttionAdapter extends ArrayAdapter<MyPosts> {

    private final List<MyPosts> mNewsData;
    private Context mContext;
    private int resourceId;
    private String[] goodUrls;


    public CollecttionAdapter(Context context,
                              int resourceId, List<MyPosts> data) {
        super(context, resourceId, data);
        this.mContext = context;
        this.mNewsData = data;
        this.resourceId = resourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyPosts news = getItem(position);
        goodUrls = news.getImageUrlList();
        View view ;
        final ViewHolder vh;
        if (convertView == null) {
            view = LayoutInflater.from(getContext())
                    .inflate(resourceId, parent, false);
            vh = new ViewHolder();
            vh.ivImage = view.findViewById(R.id.iv_image);
            view.setTag(vh);
        } else {
            view = convertView;
            vh = (ViewHolder) view.getTag();
        }
        Glide.with(mContext).load(goodUrls[0])
                .into(vh.ivImage);
        return view;
    }

    class ViewHolder {
        ImageView ivImage;

    }
}
