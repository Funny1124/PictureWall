package com.trio.picturewall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.trio.picturewall.R;
import com.trio.picturewall.entity.MyPosts;

import java.util.List;

public class CollectedAdapter extends RecyclerView.Adapter<CollectedAdapter.MyViewHolder> {

    private Context context;

    List<MyPosts> myPostsList;
    private String[] goodUrls;

    public CollectedAdapter(Context context, List<MyPosts> myPostsList) {
        this.context = context;
        this.myPostsList = myPostsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.goodposts, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if ((position % 6 == 0) || (position % 8 == 4)) {
            holder.imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 648));
        } else {
            holder.imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));
        }
        MyPosts data = myPostsList.get(position);
        goodUrls = data.getImageUrlList();
        //设置图片
        if (goodUrls.length != 0) {
            Glide.with(holder.imageView.getContext())
                    .load(goodUrls[0])
                    .into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return this.myPostsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView1);
        }
    }

}
