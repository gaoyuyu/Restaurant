package com.gaoyy.restaurant.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.gaoyy.restaurant.R;
import com.gaoyy.restaurant.adapter.OrderListAdapter;
import com.gaoyy.restaurant.base.BaseActivity;
import com.gaoyy.restaurant.bean.Order;
import com.gaoyy.restaurant.utils.CommonUtils;
import com.gaoyy.restaurant.utils.Constant;
import com.gaoyy.restaurant.utils.GsonUtils;
import com.gaoyy.restaurant.utils.OkhttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import okhttp3.Request;

public class CheckActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, OrderListAdapter.OnItemClickListener
{
    private Toolbar checkToolbar;
    private SwipeRefreshLayout checkSwipeRefreshLayout;
    private RecyclerView checkRv;
    private ProgressWheel checkProgresswheel;
    private LinkedList<Order> orderList = new LinkedList<>();
    private OrderListAdapter orderListAdapter = null;
    private LinearLayoutManager linearLayoutManager = null;
    private int lastVisibleItemPosition;

    private int currentPage = 1;
    private int pageCount;




    @Override
    protected void initContentView()
    {
        setContentView(R.layout.activity_check);
    }

    @Override
    protected void assignViews()
    {
        super.assignViews();
        checkToolbar = (Toolbar) findViewById(R.id.check_toolbar);
        checkSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.check_swipeRefreshLayout);
        checkRv = (RecyclerView) findViewById(R.id.check_rv);
        checkProgresswheel = (ProgressWheel) findViewById(R.id.check_progresswheel);
    }

    @Override
    protected void configViews()
    {
        super.configViews();
        configProgressView();

        orderListAdapter = new OrderListAdapter(this, orderList);
        checkRv.setAdapter(orderListAdapter);
        //设置布局
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        checkRv.setLayoutManager(linearLayoutManager);
        checkRv.setItemAnimator(new DefaultItemAnimator());

        checkRv.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition + 1 == orderListAdapter.getItemCount())
                {
                    if (currentPage + 1 > pageCount)
                    {
                        showSnackbar(recyclerView, ":)到底啦");
                    }
                    else
                    {
                        currentPage = currentPage + 1;
                        load(currentPage, Constant.MODE_LOAD_MORE);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    @Override
    protected void setListener()
    {
        super.setListener();
        checkSwipeRefreshLayout.setOnRefreshListener(this);
        orderListAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void loadData()
    {
        super.loadData();
        load(1,Constant.MODE_REFRESH);
    }

    private void configProgressView()
    {
        checkSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        checkSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        checkSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
    }

    public void load(int currentPage, final int mode)
    {
        checkSwipeRefreshLayout.setRefreshing(true);
        Map<String, String> params = new HashMap<>();
        params.put("currentPage", String.valueOf(currentPage));
        if(getIntent().getExtras() !=null)
        {
            params.put("uid", getIntent().getExtras().getString("uid"));
        }
        Log.e(Constant.TAG,"==order list=>"+params.toString());
        OkhttpUtils.postAsync(this, Constant.ORDER_LIST_URL, "order_list", params, new OkhttpUtils.ResultCallback()
        {
            @Override
            public void onError(Request request, Exception e)
            {

            }

            @Override
            public void onSuccess(String body)
            {
                Log.e(Constant.TAG, body);
                int code = GsonUtils.getResponseCode(body);
                if (code == Constant.ERROR)
                {
                    showSnackbar(checkRv, GsonUtils.getResponseInfo(body, "data"));
                    return;
                }

                Gson gson = new Gson();
                JSONObject data = GsonUtils.getDataJsonObj(body);
                try
                {
                    pageCount = data.getInt("pageCount");
                    Log.e(Constant.TAG, "pageCount===>" + pageCount);
                    orderList = gson.fromJson(data.get("list").toString(),
                            new TypeToken<LinkedList<Order>>()
                            {
                            }.getType());
                    Log.e(Constant.TAG, "===>" + orderList.toString());

                    if (mode == Constant.MODE_REFRESH)
                    {
                        orderListAdapter.addItem(orderList);
                    }
                    if (mode == Constant.MODE_LOAD_MORE)
                    {
                        orderListAdapter.addMoreItem(orderList);
                    }
                    checkSwipeRefreshLayout.setVisibility(View.VISIBLE);
                    checkProgresswheel.setVisibility(View.GONE);

                    checkSwipeRefreshLayout.setRefreshing(false);

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void initToolbar()
    {
        super.initToolbar(checkToolbar, R.string.check_order_list, true, null);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        super.setStatusBarColor(-1);
        super.setNavigationBarColor(-1);
    }

    @Override
    public void onRefresh()
    {
        checkSwipeRefreshLayout.setRefreshing(true);
        load(1, Constant.MODE_REFRESH);
    }

    @Override
    public void onItemClick(View view, int position)
    {
        int id = view.getId();
        switch (id)
        {
            case R.id.item_check_layout:
                Log.e(Constant.TAG,"uid===>"+CommonUtils.getUserId(CheckActivity.this));
                Log.e(Constant.TAG,"oid===>"+orderList.get(position).getId());

                Bundle bundle = new Bundle();
                bundle.putString("uid", CommonUtils.getUserId(CheckActivity.this));
                bundle.putString("oid",orderList.get(position).getId());
                bundle.putString("order_status",orderList.get(position).getStatus());
                redirect(MapsActivity.class,bundle);
                break;
        }
    }
}
