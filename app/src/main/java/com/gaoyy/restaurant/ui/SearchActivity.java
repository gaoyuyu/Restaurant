package com.gaoyy.restaurant.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.gaoyy.restaurant.R;
import com.gaoyy.restaurant.adapter.OrderListAdapter;
import com.gaoyy.restaurant.base.BaseActivity;
import com.gaoyy.restaurant.bean.Order;
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

public class SearchActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener
{
    private Toolbar searchToolbar;
    private Spinner searchSpinner;
    private EditText searchEdit;
    private ProgressWheel commonProgresswheel;
    private SwipeRefreshLayout commonSwipeRefreshLayout;
    private RecyclerView commonRv;

    private LinkedList<Order> orderList = new LinkedList<>();
    private OrderListAdapter orderListAdapter = null;
    private LinearLayoutManager linearLayoutManager = null;
    private int lastVisibleItemPosition;

    private int currentPage = 1;
    private int pageCount;
    //默认订单状态为-1，即显示全部状态下订单
    private int currentOrderStatus = -1;
    String[] selectCondition;


    public int getCurrentOrderStatus()
    {
        return currentOrderStatus;
    }

    public void setCurrentOrderStatus(int currentOrderStatus)
    {
        this.currentOrderStatus = currentOrderStatus;
    }


    @Override
    protected void initContentView()
    {
        setContentView(R.layout.activity_search);
    }

    @Override
    protected void assignViews()
    {
        super.assignViews();
        searchToolbar = (Toolbar) findViewById(R.id.search_toolbar);
        searchSpinner = (Spinner) findViewById(R.id.search_spinner);
        searchEdit = (EditText)findViewById(R.id.search_edit);
        commonProgresswheel = (ProgressWheel) findViewById(R.id.common_progresswheel);
        commonSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.common_swipeRefreshLayout);
        commonRv = (RecyclerView) findViewById(R.id.common_rv);
    }

    @Override
    protected void initToolbar()
    {
        super.initToolbar(searchToolbar, R.string.empty, true, null);
    }

    @Override
    protected void configViews()
    {
        super.configViews();
        selectCondition = getResources().getStringArray(R.array.languages);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, selectCondition);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchSpinner.setAdapter(adapter);
        configProgressView();

        orderListAdapter = new OrderListAdapter(this, orderList);
        commonRv.setAdapter(orderListAdapter);
        //设置布局
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        commonRv.setLayoutManager(linearLayoutManager);
        commonRv.setItemAnimator(new DefaultItemAnimator());

        commonRv.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition + 1 == orderListAdapter.getItemCount())
                {
                    if (currentPage + 1 > pageCount)
                    {
//                        showSnackbar(recyclerView, ":)到底啦");
                        Log.e(Constant.TAG, "当前页：" + currentPage + ":)到底啦");
                    }
                    else
                    {
                        currentPage = currentPage + 1;
                        load(currentPage, Constant.MODE_LOAD_MORE,searchSpinner.getSelectedItem().toString(),searchEdit.getText().toString());
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
        commonSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void configProgressView()
    {
        commonSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        commonSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        commonSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_search:
                load(1, Constant.MODE_REFRESH,searchSpinner.getSelectedItem().toString(),searchEdit.getText().toString());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        super.setStatusBarColor(-1);
        super.setNavigationBarColor(-1);
    }

    private String getMapingKey(String value)
    {
        String key = null;
        if (value.equals(selectCondition[0]))
        {
            key = "phone";
        }
        else if (value.equals(selectCondition[1]))
        {
            key = "address";
        }
        else if (value.equals(selectCondition[2]))
        {
            key = "remark";
        }
        return key;
    }

    public void load(int currentPage, final int mode,String selectCondition,String selectValue)
    {
        commonSwipeRefreshLayout.setRefreshing(true);
        Map<String, String> params = new HashMap<>();
        params.put("currentPage", String.valueOf(currentPage));
        params.put("orderStatus", String.valueOf(getCurrentOrderStatus()));
        params.put(getMapingKey(selectCondition),selectValue);
        if (getIntent().getExtras() != null)
        {
            params.put("uid", getIntent().getExtras().getString("uid"));
        }
        Log.e(Constant.TAG, "==order list=>" + params.toString());
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
                    showSnackbar(commonRv, GsonUtils.getResponseInfo(body, "data"));
                    return;
                }
                else
                {

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
                        commonSwipeRefreshLayout.setVisibility(View.VISIBLE);
                        commonProgresswheel.setVisibility(View.GONE);

                        commonSwipeRefreshLayout.setRefreshing(false);

                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onRefresh()
    {
        load(1, Constant.MODE_REFRESH,searchSpinner.getSelectedItem().toString(),searchEdit.getText().toString());
    }
}
