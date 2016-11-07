package com.gaoyy.restaurant.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.gaoyy.restaurant.R;
import com.gaoyy.restaurant.base.BaseActivity;
import com.gaoyy.restaurant.fragment.CustomDialogFragment;
import com.gaoyy.restaurant.utils.Constant;
import com.gaoyy.restaurant.utils.DialogUtils;
import com.gaoyy.restaurant.utils.GsonUtils;
import com.gaoyy.restaurant.utils.OkhttpUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;

import static com.gaoyy.restaurant.R.string.delivery;

public class DeliveryActivity extends BaseActivity
{
    private Toolbar deliveryToolbar;
    private TextInputLayout deliveryPhoneTextinputlayout;
    private TextInputEditText deliveryPhone;
    private TextInputLayout deliveryAddressTextinputlayout;
    private TextInputEditText deliveryAddress;
    private TextInputLayout deliveryPriceTextinputlayout;
    private TextInputEditText deliveryPrice;
    private TextInputLayout deliveryRemarkTextinputlayout;
    private TextInputEditText deliveryRemark;

    private SharedPreferences account = null;

    @Override
    protected void initContentView()
    {
        setContentView(R.layout.activity_delivery);
    }

    @Override
    protected void assignViews()
    {
        super.assignViews();
        deliveryToolbar = (Toolbar) findViewById(R.id.delivery_toolbar);
        deliveryPhoneTextinputlayout = (TextInputLayout) findViewById(R.id.delivery_phone_textinputlayout);
        deliveryPhone = (TextInputEditText) findViewById(R.id.delivery_phone);
        deliveryAddressTextinputlayout = (TextInputLayout) findViewById(R.id.delivery_address_textinputlayout);
        deliveryAddress = (TextInputEditText) findViewById(R.id.delivery_address);
        deliveryPriceTextinputlayout = (TextInputLayout) findViewById(R.id.delivery_price_textinputlayout);
        deliveryPrice = (TextInputEditText) findViewById(R.id.delivery_price);
        deliveryRemarkTextinputlayout = (TextInputLayout) findViewById(R.id.delivery_remark_textinputlayout);
        deliveryRemark = (TextInputEditText) findViewById(R.id.delivery_remark);
        account = getSharedPreferences("account", Activity.MODE_PRIVATE);
    }

    @Override
    protected void initToolbar()
    {
        super.initToolbar(deliveryToolbar, delivery, true, null);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        super.setStatusBarColor(-1);
        super.setNavigationBarColor(-1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.delivery_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.delivery_submit:
                Map<String,String> params  = new HashMap<>();
                params.put("uid",account.getString("id",""));
                params.put("phone",deliveryPhone.getText().toString());
                params.put("address",deliveryAddress.getText().toString());
                params.put("price",deliveryPrice.getText().toString());
                params.put("remark",deliveryRemark.getText().toString());
                Log.e(Constant.TAG,"params==>"+params.toString());
                OkhttpUtils.postAsync(DeliveryActivity.this, Constant.SUBMIT_ORDER_URL, "submit_order", params, new OkhttpUtils.ResultCallback()
                {
                    @Override
                    public void onError(Request request, Exception e)
                    {

                    }

                    @Override
                    public void onSuccess(String body)
                    {
                        int code = GsonUtils.getResponseCode(body);
                        if (code == Constant.ERROR)
                        {
                            showSnackbar(deliveryToolbar, GsonUtils.getResponseInfo(body, "data"));
                            return;
                        }
                        else
                        {
                            CustomDialogFragment dialog = DialogUtils.showAlertDialog(DeliveryActivity.this,"提示",GsonUtils.getResponseInfo(body, "data"),
                                    "继续填写","去查看订单");
                            dialog.setOnAlertDialogClickListener(new CustomDialogFragment.OnAlertDialogClickListener()
                            {
                                @Override
                                public void onButtonClick(DialogInterface dialog, int which)
                                {
                                    switch(which) {
                                        case AlertDialog.BUTTON_NEGATIVE:
                                            dialog.dismiss();
                                            deliveryPhone.setText("");
                                            deliveryAddress.setText("");
                                            deliveryPrice.setText("");
                                            deliveryRemark.setText("");
                                            deliveryPhone.requestFocus();
                                            break;
                                        case AlertDialog.BUTTON_POSITIVE:
                                            redirectThenKill(CheckActivity.class);
                                            break;
                                        default:
                                            break;
                                    }

                                }
                            });
                        }
                    }
                });
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}