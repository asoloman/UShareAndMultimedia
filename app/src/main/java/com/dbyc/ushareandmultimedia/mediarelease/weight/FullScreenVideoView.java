package com.dbyc.ushareandmultimedia.mediarelease.weight;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**全屏视频播放视频*/
public class FullScreenVideoView extends VideoView {


    public FullScreenVideoView(Context context) {
        super(context);
// TODO Auto-generated constructor stub
    }
    public FullScreenVideoView(Context context, AttributeSet attrs)
    {
        super(context,attrs);
    }
    public FullScreenVideoView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context,attrs,defStyle);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width , height);
    }


}