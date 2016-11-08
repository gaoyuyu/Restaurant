package com.gaoyy.restaurant.fragment;


import android.os.Bundle;
import android.view.View;

import com.gaoyy.restaurant.R;
import com.gaoyy.restaurant.base.BaseFragment;

public class DriverFragment extends BaseFragment
{


    public DriverFragment()
    {
        // Required empty public constructor
    }

    public static DriverFragment newInstance()
    {
        return new DriverFragment();
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState)
    {

    }

    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_driver;
    }

}
