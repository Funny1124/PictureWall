package com.trio.picturewall.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.myViewHodler> {
    private final Context context;
    private final List<MyPosts> myPostsList;
    private String[] myPosts;

    //创建构造函数
    public MyPostsAdapter(Context context, List<MyPosts> myPostsList) {
        //将传递过来的数据，赋值给本地变量
        this.context = context;//上下文
        this.myPostsList = myPostsList;//实体类数据ArrayList
    }

    /**
     * 创建viewhodler，相当于listview中getview中的创建view和viewhodler
     *
     * @param parent   parent
     * @param viewType viewType
     * @return myViewHodler
     */
    @NonNull
    @Override
    public myViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建自定义布局
        View itemView = View.inflate(context, R.layout.myposts, null);
        return new myViewHodler(itemView);
    }

    /**
     * 绑定数据，数据与view绑定
     *
     * @param holder   holder
     * @param position position
     */
    @Override
    public void onBindViewHolder(myViewHodler holder, int position) {
        //根据点击位置绑定数据
        MyPosts data = myPostsList.get(position);
        myPosts = data.getImageUrlList();
        //设置图片
        if (myPosts.length != 0) {
            Glide.with(holder.imageView.getContext())
                    .load(myPosts[0])
                    .into(holder.imageView);
        }
        holder.userName.setText(data.getTitle());//获取实体类中的name字段并设置
        holder.content.setText(data.getContent());//获取实体类中的content字段并设置
    }


    /**
     * 得到总条数
     *
     * @return myPostsList.size()
     */
    @Override
    public int getItemCount() {
        return myPostsList.size();
    }

    //自定义viewhodler
    class myViewHodler extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView userName;
        private final TextView content;
//        private TextView description;
//        private ImageView good;
//        private ImageView download;
//        private ImageView share;


        public myViewHodler(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.iv_image);
            userName = itemView.findViewById(R.id.picture_username);
            content = itemView.findViewById(R.id.picture_content);

            //点击事件放在adapter中使用，也可以写个接口在activity中调用
            //方法一：在adapter中设置点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyPosts data = myPostsList.get(getLayoutPosition());
                    //可以选择直接在本位置直接写业务处理
                    Toast.makeText(context, "点击了item", Toast.LENGTH_SHORT).show();
                    //此处回传点击监听事件
                    if (onItemClickListener != null) {
                        onItemClickListener.OnItemClick(v, data);
                    }
                }
            });

//            download.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    MyPosts data= myPostsList.get(getLayoutPosition());
//                    myPosts=data.getImageUrlList();
//                    Toast.makeText(context,myPosts[0],Toast.LENGTH_SHORT).show();
//
//                    new Thread() {
//                        public void run() {
//                            Log.e("图片","开始下载");
//                            try {
//                                Bitmap myBitmap = Glide.with(context)
//                                        .asBitmap()
//                                        .skipMemoryCache(true)//跳过内存缓存
//                                        .diskCacheStrategy(DiskCacheStrategy.NONE)//不缓冲disk硬盘中
//                                        .load(myPosts[0])
//                                        .submit(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL).get();
//                                Bitmap bitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
//                                Log.e("图片", bitmap.toString());
//                                saveImageToGallery(bitmap);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }.start();
//                }
//            });

        }
    }

    /**
     * 设置item的监听事件的接口
     */
    public interface OnItemClickListener {
        /**
         * 接口中的点击每一项的实现方法，参数自己定义
         *
         * @param view 点击的item的视图
         * @param data 点击的item的数据
         */
        void OnItemClick(View view, MyPosts data);
    }

    //需要外部访问，所以需要设置set方法，方便调用
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void saveImageToGallery(Bitmap bmp) {
        if (bmp == null) {
            Log.e("TAG", "bitmap---为空");
            return;
        }
        String galleryPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "Camera" + File.separator;
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(galleryPath, fileName);
        try {
            if (!Objects.requireNonNull(file.getParentFile()).exists()) {
                if (!file.getParentFile().mkdirs()) {
                    Log.e("图片", "创建文件失败");
                }
            }
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                Log.e("TAG", "图片保存成功 保存在:" + file.getPath());
            } else {
                Log.e("TAG", "图片保存失败");
            }
        } catch (IOException e) {
            Log.e("TAG", "保存图片找不到文件夹");
            e.printStackTrace();
        }
    }
}