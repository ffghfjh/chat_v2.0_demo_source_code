package com.yiliaodemo.chat.activity;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.yiliaodemo.chat.R;
import com.yiliaodemo.chat.base.BaseActivity;
import com.yiliaodemo.chat.constant.Constant;

import butterknife.BindView;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：查看图片页面
 * 作者：
 * 创建时间：2018/7/24
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class PhotoActivity extends BaseActivity {

    @BindView(R.id.content_pv)
    PhotoView mContentPv;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_photo_layout);
    }

    @Override
    protected boolean supportFullScreen() {
        return true;
    }

    @Override
    protected void onContentAdded() {
        needHeader(false);
        String imageUrl = getIntent().getStringExtra(Constant.IMAGE_URL);
        final PhotoViewAttacher attacher = new PhotoViewAttacher(mContentPv);

        Glide.with(this).load(imageUrl).crossFade().into(new GlideDrawableImageViewTarget(mContentPv) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                super.onResourceReady(resource, animation);
                attacher.update();
            }
        });

        attacher.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                finish();
            }
        });
    }


}
