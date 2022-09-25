package com.trio.picturewall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.trio.picturewall.R;
import com.trio.picturewall.entity.MyPosts;
import com.trio.picturewall.widget.RadiuImageView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context context;

    List<MyPosts> myPostsList;
    private String[] goodUrls;
    private RecyclerViewAdapter.OnItemClickListener onItemClickListener;    //创建构造函数
    public RecyclerViewAdapter(Context context, List<MyPosts> myPostsList) {
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
            holder.imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 616));
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

        private RadiuImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.imageView =itemView.findViewById(R.id.imageView1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyPosts data = myPostsList.get(getLayoutPosition());
                    //此处回传点击监听事件
                    if (onItemClickListener != null) {
                        onItemClickListener.OnItemClick(v, data);
                    }
                }
            });
        }
    }
    public void setOnItemClickListener(RecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        /**
         * 接口中的点击每一项的实现方法，参数自己定义
         *
         * @param view 点击的item的视图
         * @param data 点击的item的数据
         */
        void OnItemClick(View view, MyPosts data);
    }
}
