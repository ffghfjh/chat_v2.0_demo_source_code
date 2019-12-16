package com.yiliaodemo.chat.helper;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.yiliaodemo.chat.R;

import java.util.ArrayList;
import java.util.List;

import me.samlss.timomenu.TimoItemViewParameter;

/**
 * @author SamLeung
 * @e-mail samlssplus@gmail.com
 * @github https://github.com/samlss
 * @description helper of menu.
 */
public class MenuHelper {
    private MenuHelper(){

    }

    public static void setupToolBarBackAction(final AppCompatActivity appCompatActivity, Toolbar toolbar){
        if (appCompatActivity == null
                || toolbar == null){
            return;
        }

        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setHomeButtonEnabled(true);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCompatActivity.finish();
            }
        });
    }

    public static int ROW_TEXT[][] = {
        {R.string.a_cache, R.string.a_cache, R.string.a_cache, R.string.a_cache, R.string.a_cache, R.string.a_cache},
        {R.string.a_cache, R.string.a_cache, R.string.a_cache, R.string.a_cache, R.string.report, 0},
    };

    public static List<TimoItemViewParameter> getTopList(int itemWidth){
        List<TimoItemViewParameter> listTop = new ArrayList<>();
        TimoItemViewParameter facebook = getTimoItemViewParameter(itemWidth, R.drawable.vip_pay_way_select,
                R.drawable.active_image_lock, R.string.pref_key_using_opensl_es, R.color.red_80fe2947,
                R.color.black_text);

        TimoItemViewParameter alipay = getTimoItemViewParameter(itemWidth, R.drawable.vip_pay_way_select,
                R.drawable.active_image_lock, R.string.pref_key_using_opensl_es, R.color.red_80fe2947,
                R.color.black_text);




        listTop.add(facebook);
        listTop.add(alipay);
        return listTop;
    }



    public static TimoItemViewParameter getTimoItemViewParameter(int itemWidth,
                                                                 int normalImageRes,
                                                                 int highlightImageRes,
                                                                 int normalTextRes,
                                                                 int normalTextColorRes,
                                                                 int highlightTextColorRes){
        return new TimoItemViewParameter.Builder()
                .setWidth(itemWidth)
                .setImagePadding(new Rect(10, 10, 10, 10))
                .setTextPadding(new Rect(5, 0, 5, 0))
                .setNormalImageRes(normalImageRes)
                .setHighlightedImageRes(highlightImageRes)
                .setNormalTextRes(normalTextRes)
                .setNormalTextColorRes(normalTextColorRes)
                .setHighlightedTextColorRes(highlightTextColorRes)
                .build();

    }


    public static List<String> getAllOpenAnimationName(){
        List<String> list = new ArrayList<>();
        list.add("Flip");
        list.add("Scale");
        list.add("Bomb");
        list.add("Stand Up");
        list.add("Bounce");
        list.add("Bounce In Down");
        list.add("Bounce In Up");
        list.add("Rotate");
        return list;
    }


}
