package com.gaoyy.restaurant.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment
{

    protected BaseActivity mActivity;

    protected abstract void initView(View view, Bundle savedInstanceState);

    //获取布局文件ID
    protected abstract int getLayoutId();

    //获取宿主Activity
    protected BaseActivity getHoldingActivity()
    {
        return mActivity;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.mActivity = (BaseActivity) context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(getLayoutId(), container, false);
        initView(view, savedInstanceState);
        configViews();
        setListener();
        loadData();
        return view;
    }

    /**
     * config views
     */
    protected void configViews()
    {
    }


    /**
     * set listener for views
     */
    protected void setListener()
    {
    }

    /**
     * loadData
     */
    protected void loadData()
    {
    }
}
