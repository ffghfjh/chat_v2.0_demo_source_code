package cn.tillusory.tiui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.shizhefei.fragment.LazyFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.tiui.R;
import cn.tillusory.tiui.adapter.TiFilterAdapter;
import cn.tillusory.tiui.model.TiFilter;

/**
 * Created by Anko on 2018/12/1.
 * Copyright (c) 2018 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiFilterFragment extends LazyFragment {

    private List<TiFilter> filterList = new ArrayList<>();
    private TiSDKManager tiSDKManager;

    public TiFilterFragment setTiSDKManager(TiSDKManager tiSDKManager) {
        this.tiSDKManager = tiSDKManager;
        return this;
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        setContentView(R.layout.fragment_ti_recyclerview);

        filterList.clear();
        filterList.addAll(Arrays.asList(TiFilter.values()));

        RecyclerView tiFilterRV = (RecyclerView) findViewById(R.id.tiRecyclerView);
        TiFilterAdapter filterAdapter = new TiFilterAdapter(filterList, tiSDKManager);
        tiFilterRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tiFilterRV.setAdapter(filterAdapter);
    }

}
