package com.trio.picturewall.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.trio.picturewall.R;
import com.trio.picturewall.entity.MyPosts;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder>{
    private Context context;
    private List<MyPosts> postlist;
    private String[] myPosts;
    private OnItemClickListener onItemClickListener;    //创建构造函数
    public PostAdapter(Context context, List<MyPosts> postlist) {
        //将传递过来的数据，赋值给本地变量
        this.context = context;//上下文
        this.postlist = postlist;//实体类数据ArrayList
    }
    @NonNull
    @Override
    public PostAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建自定义布局
        View itemView = View.inflate(context, R.layout.myposts, null);
        return new PostAdapter.viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostAdapter.viewHolder holder, int position) {
        //根据点击位置绑定数据
        MyPosts data = postlist.get(position);
        myPosts=data.getImageUrlList();
        //设置图片
        Glide.with(holder.imageView.getContext())
                .load(myPosts[0])
                .into(holder.imageView);
       holder.userName.setText(data.getTitle());//获取实体类中的name字段并设置
//       holder.description.setText(data.getId());//获取实体类中的price字段并设置
    }

    @Override
    public int getItemCount() {
        return postlist.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView userName;
//        private TextView description;
//        private ImageView good;
//        private ImageView download;
//        private ImageView share;


        public viewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.iv_image);
            userName = itemView.findViewById(R.id.picture_username);

            //点击事件放在adapter中使用，也可以写个接口在activity中调用
            //方法一：在adapter中设置点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyPosts data = postlist.get(getLayoutPosition());
                    //可以选择直接在本位置直接写业务处理
                    Toast.makeText(context, "点击了item", Toast.LENGTH_SHORT).show();
                    //此处回传点击监听事件
                    if (onItemClickListener != null) {
                        onItemClickListener.OnItemClick(v, data);
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(PostAdapter.OnItemClickListener onItemClickListener) {
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
