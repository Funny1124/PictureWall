package com.trio.picturewall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
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
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.simple_post, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position%2==0) {
            holder.imageView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1000));
        } else {
            holder.imageView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800));
        }
        MyPosts data = myPostsList.get(position);
        goodUrls = data.getImageUrlList();
        //设置图片
        if (goodUrls.length != 0) {
            Glide.with(holder.imageView.getContext())
                    .load(goodUrls[0])
                    .into(holder.imageView);
        }
        holder.title.setText(data.getTitle());
        holder.content.setText(data.getContent());
    }



    @Override
    public int getItemCount() {
        return this.myPostsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private RadiuImageView imageView;
        private TextView title;
        private TextView content;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.imageView =itemView.findViewById(R.id.sim_post_imageView);
            this.title =itemView.findViewById(R.id.sim_post_title);
            this.content =itemView.findViewById(R.id.sim_post_content);

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
