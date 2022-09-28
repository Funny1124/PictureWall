package com.trio.picturewall.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trio.picturewall.R;
import com.trio.picturewall.entity.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
        private final List<Comment> mDatas;
        public CommentAdapter(Context context, List<Comment> datats)
        {
            mInflater = LayoutInflater.from(context);
            mDatas = datats;
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            public ViewHolder(View arg0)
            {
                super(arg0);
            }
            ImageView user_icon;
            TextView user_name;
            TextView com_context;
            TextView com_time;
        }

        @Override
        public int getItemCount()
        {
            return mDatas.size();
        }

        /**
         * 创建ViewHolder
         */
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view = mInflater.inflate(R.layout.item_comment,
                    viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.user_icon = view.findViewById(R.id.com_user_icon);
            viewHolder.com_context = view.findViewById(R.id.com_context);
            viewHolder.com_time = view.findViewById(R.id.com_time);

            return viewHolder;
        }
        /**
         * 设置值
         */
        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int i)
        {
//            viewHolder.user_name.setText(mDatas.get(0).getUserName());
            viewHolder.com_context.setText(mDatas.get(i).getContent());
            viewHolder.com_time.setText(mDatas.get(i).getCreateTime());
        }
    }