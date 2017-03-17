package com.dbyc.ushareandmultimedia.mediarelease.okhttp.builder;


import com.dbyc.ushareandmultimedia.mediarelease.okhttp.OkHttpUtils;
import com.dbyc.ushareandmultimedia.mediarelease.okhttp.request.RequestCall;
import com.dbyc.ushareandmultimedia.mediarelease.okhttp.request.OtherRequest;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
