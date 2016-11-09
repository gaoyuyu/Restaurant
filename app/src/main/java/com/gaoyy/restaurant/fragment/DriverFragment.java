package com.gaoyy.restaurant.fragment;


import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.RelativeLayout;

import com.gaoyy.restaurant.R;
import com.gaoyy.restaurant.base.BaseFragment;
import com.gaoyy.restaurant.ui.CheckActivity;
import com.gaoyy.restaurant.utils.CommonUtils;

public class DriverFragment extends BaseFragment implements View.OnClickListener
{

    private RelativeLayout mainContent;
    private AppCompatButton driverAllBtn;
    private AppCompatButton driverMyBtn;


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
        mainContent = (RelativeLayout) view.findViewById(R.id.main_content);
        driverAllBtn = (AppCompatButton) view.findViewById(R.id.driver_all_btn);
        driverMyBtn = (AppCompatButton) view.findViewById(R.id.driver_my_btn);
    }

    @Override
    protected void setListener()
    {
        super.setListener();
        driverAllBtn.setOnClickListener(this);
        driverMyBtn.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_driver;
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.driver_all_btn:
                mActivity.redirect(CheckActivity.class);
                break;
            case R.id.driver_my_btn:
                Bundle bundle = new Bundle();
                bundle.putString("uid", CommonUtils.getUserId(mActivity));
                mActivity.redirect(CheckActivity.class,bundle);
                break;
        }
    }
}
