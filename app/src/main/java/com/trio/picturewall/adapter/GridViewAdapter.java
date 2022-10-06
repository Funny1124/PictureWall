package com.trio.picturewall.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trio.picturewall.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private List<Bitmap> mlist;
    private Context mcontext;
    private LayoutInflater minflater;
    private int selected = -1;

    private View deleteView;
    private boolean isShowDelete;// 根据这个变量来判断是否显示删除图标，true是显示，false是不显示

    public GridViewAdapter(List<Bitmap> list, Context context) {
        super();
        this.mlist = list;
        this.mcontext = context;
        this.minflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() { return mlist.size()+1; }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setIsShowDelete(boolean isShowDelete) {
        this.isShowDelete = isShowDelete;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        VIewHolder vh;

        if (convertView == null) {
            vh = new VIewHolder();
            convertView = minflater.inflate(R.layout.gridview_item, null);
            vh.iv = (ImageView) convertView.findViewById(R.id.iv_image);
            convertView.setTag(vh);
        } else {
            vh = (VIewHolder) convertView.getTag();
        }
        if (position < mlist.size()){
            deleteView = convertView.findViewById(R.id.delete_markView);
            deleteView.setVisibility(isShowDelete ? View.VISIBLE : View.GONE);// 设置删除按钮是否显示
            vh.iv.setImageBitmap(mlist.get(position));
        }
        else{
            vh.iv.setBackgroundResource(R.mipmap.post_fill);
        }
        //Bitmap path = mlist.get(position);
        return convertView;
    }
}

class VIewHolder {
    ImageView iv;
}



