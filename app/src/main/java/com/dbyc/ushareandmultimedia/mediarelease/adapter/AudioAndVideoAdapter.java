package com.dbyc.ushareandmultimedia.mediarelease.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.dbyc.ushareandmultimedia.R;
import com.dbyc.ushareandmultimedia.mediarelease.activity.GetvAudioAndVideoActivity;
import com.dbyc.ushareandmultimedia.mediarelease.model.AudioAndVideoBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016-07-06.
 */
public class AudioAndVideoAdapter extends BaseAdapter {

    public List<AudioAndVideoBean> mDatas;
    public Context mContex;

    public AudioAndVideoAdapter(List<AudioAndVideoBean> mDatas, Context mContex) {
        this.mDatas = mDatas;
        this.mContex = mContex;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView==null){
            convertView = LayoutInflater.from(mContex).inflate(R.layout.item_audio_video,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_video_img = (ImageView) convertView.findViewById(R.id.iv_video_img);
            viewHolder.iv_isclick = (ImageView) convertView.findViewById(R.id.iv_isclick);
            viewHolder.tv_audio_name = (TextView) convertView.findViewById(R.id.tv_audio_name);
            viewHolder.tv_time_long = (TextView) convertView.findViewById(R.id.tv_time_long);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(GetvAudioAndVideoActivity.TYPE==2){
            viewHolder.iv_video_img.setImageBitmap(mDatas.get(position).getVideoImg());
        }

        int isclick = mDatas.get(position).getIsClick();
        if(isclick==1){
            viewHolder.iv_isclick.setImageResource(R.mipmap.pictures_selected);
        }else {
            viewHolder.iv_isclick.setImageResource(R.mipmap.picture_unselected);
        }

        Date dates = new Date(mDatas.get(position).getDuration());
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String times= sdf.format(dates);
        viewHolder.tv_time_long.setText(times);
        viewHolder.tv_audio_name.setText(mDatas.get(position).getName());

        return convertView;
    }


    class ViewHolder{
        ImageView iv_video_img;
        TextView tv_audio_name;
        TextView tv_time_long;
        ImageView iv_isclick;

    }
}
