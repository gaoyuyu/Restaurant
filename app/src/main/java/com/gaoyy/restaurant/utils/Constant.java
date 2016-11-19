package com.gaoyy.restaurant.utils;

/**
 * Created by gaoyy on 2016/11/4 0004.
 */
public class Constant
{
    public static int ERROR = -1;

    public static int SUCCESS = 0;

    public static final String TAG = "Demo";

    public static final int ADMIN = 999;
    public static final int DRIVER = 899;

    //0-等待，1-派送中，2-完成
    public static final String[] status = {"等待", "派送中", "完成"};

    public static final int MODE_REFRESH = 101;
    public static final int MODE_LOAD_MORE = 102;

    private static final String ROOT = "http://192.168.16.63/easycan/index.php/Api/";

    //登录
    public static final String LOGIN_URL = ROOT + "User/login";

    //提交订单
    public static final String SUBMIT_ORDER_URL = ROOT + "Order/submitOrder";

    //订单列表
    public static final String ORDER_LIST_URL = ROOT + "Order/orderList";

    //弃用
    public static final String MAP_LD_URL = ROOT + "Map/get2LocationAndDirection";

    //接单
    public static final String RECEIVE_ORDER_URL = ROOT + "Order/receiveOrder";

    //根据经纬度获取地址
    public static final String MAP_GETLATANDLNG_BY_ADDRESS_V2_URL = ROOT + "Map/getLatAndLngByAddressV2";

    //规划路线
    public static final String MAP_GETDIRECTIONPOLYLINE_V2_URL = ROOT + "Map/getDirectionPolylineV2";

    //根据经纬度反编码获取地理位置
    public static final String MAP_REVERSEGEOCODINGFORLATLNG_V2_URL = ROOT + "Map/reverseGeocodingForLatLngV2";

    //确认收货
    public static final String ORDER_CONFIRM_RECEIVE_URL=ROOT+"Order/finishOrder";


}
