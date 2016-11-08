package com.gaoyy.restaurant.fragment;


import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.gaoyy.restaurant.R;
import com.gaoyy.restaurant.base.BaseFragment;
import com.gaoyy.restaurant.ui.CheckActivity;
import com.gaoyy.restaurant.ui.DeliveryActivity;

public class AdminFragment extends BaseFragment implements View.OnClickListener
{
    private AppCompatButton adminDeliveryBtn;
    private AppCompatButton adminCheckBtn;


    public AdminFragment()
    {
        // Required empty public constructor
    }

    public static AdminFragment newInstance()
    {
        return new AdminFragment();
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState)
    {
        adminDeliveryBtn = (AppCompatButton) view.findViewById(R.id.admin_delivery_btn);
        adminCheckBtn = (AppCompatButton) view.findViewById(R.id.admin_check_btn);

    }

    @Override
    protected void setListener()
    {
        super.setListener();
        adminDeliveryBtn.setOnClickListener(this);
        adminCheckBtn.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_admin;
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();
//        Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants
//                (MainActivity.this, false, new Pair<>(view, "toolbar"));
//        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, pairs);
        switch (id)
        {
            case R.id.admin_delivery_btn:
                mActivity.redirect(DeliveryActivity.class);
//                redirectWithShareViews(DeliveryActivity.class,options);
                break;
            case R.id.admin_check_btn:
                mActivity.redirect(CheckActivity.class);
                break;
        }
    }
}
