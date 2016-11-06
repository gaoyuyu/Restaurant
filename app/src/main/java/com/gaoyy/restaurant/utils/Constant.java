package com.gaoyy.restaurant.utils;

/**
 * Created by gaoyy on 2016/11/4 0004.
 */
public class Constant
{
    public static int ERROR = -1;

    public static int SUCCESS = 0;

    public static final String TAG = "Demo";

    //0-等待，1-接受，2-派送中，3-完成
    public static final String[] status = {"等待", "接受", "派送中", "完成"};

    public static final int MODE_REFRESH = 101;
    public static final int MODE_LOAD_MORE = 102;

    private static final String ROOT = "http://192.168.16.93/easycan/index.php/Api/";

    public static final String LOGIN_URL = ROOT + "User/login";

    public static final String SUBMIT_ORDER_URL = ROOT + "Order/submitOrder";

    public static final String ORDER_LIST_URL = ROOT + "Order/orderList";
}
