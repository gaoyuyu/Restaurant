package com.gaoyy.restaurant.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gaoyy.restaurant.R;
import com.gaoyy.restaurant.base.BaseActivity;
import com.gaoyy.restaurant.fragment.CustomDialogFragment;
import com.gaoyy.restaurant.utils.CommonUtils;
import com.gaoyy.restaurant.utils.Constant;
import com.gaoyy.restaurant.utils.DialogUtils;
import com.gaoyy.restaurant.utils.GsonUtils;
import com.gaoyy.restaurant.utils.OkhttpUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

import static com.gaoyy.restaurant.R.id.map;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
{

    private GoogleMap mMap;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Toolbar mapsToolbar;
    private TextView mapsLocationFailedText;

    private LinearLayout mapLayout;

    private TextView mapsDestination;

    private Map<String, String> markers = null;
    private Map<String, Polyline> polylines = null;

    private static final String DRIVER2RESTAURANT = "Driver2Restaurant";
    private static final String RESTAURANT2CUSTOMER = "Restaurant2Customer";

    private LinearLayout mapsTextLayout;

    private int orderStatus;

    private Marker myMarker = null;

    @Override
    protected void initContentView()
    {
        setContentView(R.layout.activity_maps);
    }


    @Override
    protected void assignViews()
    {
        super.assignViews();
        mapsToolbar = (Toolbar) findViewById(R.id.maps_toolbar);
        mapLayout = (LinearLayout) findViewById(R.id.map_layout);
        mapsDestination = (TextView) findViewById(R.id.maps_destination);
        mapsTextLayout = (LinearLayout) findViewById(R.id.maps_text_layout);
        mapsLocationFailedText = (TextView) findViewById(R.id.maps_location_failed_text);
    }

    @Override
    protected void initToolbar()
    {
        super.initToolbar(mapsToolbar, R.string.real_time_location, true, null);
    }

    @Override
    protected void configViews()
    {
        super.configViews();

        if (CommonUtils.isAdmin(MapsActivity.this))
        {
            mapsLocationFailedText.setVisibility(View.GONE);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        markers = new HashMap<>();
        polylines = new HashMap<>();

        orderStatus = Integer.valueOf(getIntent().getExtras().getString("order_status"));
    }

    @Override
    protected void loadData()
    {
        super.loadData();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        super.setStatusBarColor(-1);
        super.setNavigationBarColor(-1);
        //饭店端登陆不需要定位服务
        if (!CommonUtils.isAdmin(MapsActivity.this))
        {
            //司机端的订单状态为完成状态也不需要定位服务，司机位置由服务器获取
            if (orderStatus != Constant.FINISH)
            {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mGoogleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

        //取消请求
//        OkhttpUtils.cancelTag("get_driver_location_by_orderId");
//        OkhttpUtils.cancelTag("get_latlng_address_v2");
//        OkhttpUtils.cancelTag("get_polyline_v2");
//        OkhttpUtils.cancelTag("reverse_v2");
//        OkhttpUtils.cancelTag("upload_location");
//        OkhttpUtils.cancelTag("receive_order");
//        OkhttpUtils.cancelTag("order_confirm_receive");
        OkhttpUtils.cancelAllCall();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        configMapUiSettings();
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {

            @Override
            public boolean onMarkerClick(Marker marker)
            {
                mapsDestination.setText(markers.get(marker.getTitle()));
                mapsTextLayout.setVisibility(View.VISIBLE);
                return false;
            }
        });
        //加载饭店和客户Maker
        loadRestaurantAndCustomerMarker();

        /**
         *  1、饭店端登陆不需要定位服务，从服务器上获取某一订单下司机的位置
         *  2、司机端的订单状态为完成状态也不需要定位服务，司机位置由服务器获取
         */
        if (CommonUtils.isAdmin(MapsActivity.this) || orderStatus == Constant.FINISH)
        {
            setDriverLocationFromServer();
        }


    }

    /**
     * 从服务器上获取司机位置和到饭店的导航
     */
    private void setDriverLocationFromServer()
    {
        Log.e(Constant.TAG, "==========setDriverLocationFromServer===========");
        Map<String, String> params = new HashMap<>();
        params.put("oid", getIntent().getExtras().getString("oid"));
        OkhttpUtils.postAsync(MapsActivity.this, Constant.MAP_GET_DRIVER_LOCATION_BY_ORDERID, "get_driver_location_by_orderId", params, new OkhttpUtils.ResultCallback()
        {
            @Override
            public void onError(Request request, Exception e)
            {

            }

            @Override
            public void onSuccess(String body)
            {
                if (GsonUtils.getResponseCode(body) == Constant.ERROR)
                {
                    showSnackbar(mapsToolbar, GsonUtils.getResponseInfo(body, "data"));
                }
                else
                {
                    JSONObject data = GsonUtils.getDataJsonObj(body);

                    try
                    {
                        String lng = data.getString("longitude");
                        String lat = data.getString("latitude");
                        showMyMarkerAndPolyLine(Double.parseDouble(lat), Double.parseDouble(lng));
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 加载饭店和客户Maker
     */
    public void loadRestaurantAndCustomerMarker()
    {
        Map<String, String> params = new HashMap<>();
        params.put("restaurant", "广州世界大观");
        params.put("customer", "广州奥林匹克网球中心");
        OkhttpUtils.postAsync(MapsActivity.this, Constant.MAP_GETLATANDLNG_BY_ADDRESS_V2_URL, "get_latlng_address_v2", params, new OkhttpUtils.ResultCallback()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                Log.e(Constant.TAG, "v2 get latlng===>" + e.toString());
            }

            @Override
            public void onSuccess(String body)
            {
                if (GsonUtils.getResponseCode(body) == Constant.ERROR)
                {
                    showSnackbar(mapsToolbar, "网络错误");
                    return;
                }
                else
                {
                    JSONObject data = GsonUtils.getDataJsonObj(body);

                    try
                    {
                        JSONObject restaurant = (JSONObject) data.get("restaurant_latlng");
                        String restaurantAddress = restaurant.getString("formatted_address");
                        JSONObject restaurantCode = (JSONObject) restaurant.get("location");
                        String restaurantLat = restaurantCode.getString("lat");
                        String restaurantLng = restaurantCode.getString("lng");
                        JSONObject customer = (JSONObject) data.get("customer_latlng");
                        String customerAddress = customer.getString("formatted_address");
                        JSONObject customerCode = (JSONObject) customer.get("location");
                        String customerLat = customerCode.getString("lat");
                        String customerLng = customerCode.getString("lng");

                        LatLng res = new LatLng(Double.parseDouble(restaurantLat),
                                Double.parseDouble(restaurantLng));
                        LatLng cus = new LatLng(Double.parseDouble(customerLat),
                                Double.parseDouble(customerLng));

                        MarkerOptions resOptions = new MarkerOptions()
                                .position(res)
                                .title("饭店")
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_restaurant_location));
                        MarkerOptions cusOptions = new MarkerOptions()
                                .position(cus)
                                .title("客人")
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_customer_location));


                        mMap.addMarker(resOptions);
                        mMap.addMarker(cusOptions);

                        markers.put("饭店", restaurantAddress);
                        markers.put("客人", customerAddress);

                        mMap.animateCamera(CameraUpdateFactory.newLatLng(res));

                        loadOrigin2DestinationPolyline(restaurantAddress, customerAddress, getResources().getColor(R.color.colorAccent), 6, RESTAURANT2CUSTOMER);


                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    /**
     * 规划origin-destination路线
     *
     * @param origin      起点
     * @param destination 终点
     * @param lineColor   线条颜色
     * @param lineWidth   线条宽度
     * @param polyLineTag 路线名
     */
    public void loadOrigin2DestinationPolyline(String origin, String destination, final int lineColor, final int lineWidth, final String polyLineTag)
    {
        Map<String, String> params = new HashMap<>();
        params.put("origin", origin);
        params.put("destination", destination);
        OkhttpUtils.postAsync(MapsActivity.this, Constant.MAP_GETDIRECTIONPOLYLINE_V2_URL, "get_polyline_v2", params, new OkhttpUtils.ResultCallback()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                Log.e(Constant.TAG, "get_polyline_v2===>" + e.toString());
            }

            @Override
            public void onSuccess(String body)
            {
                Log.e(Constant.TAG, "get_polyline_v2===>" + body);
                if (GsonUtils.getResponseCode(body) == Constant.ERROR)
                {
                    showSnackbar(mapsToolbar, "网络错误");
                    return;
                }
                else
                {
                    String restaurant2customer = GsonUtils.getResponseInfo(body, "data");
                    List<LatLng> line = CommonUtils.decodePoly(restaurant2customer);
                    PolylineOptions lineOptions = new PolylineOptions();
                    lineOptions.addAll(line);
                    lineOptions.width(lineWidth);
                    lineOptions.geodesic(true);
                    lineOptions.color(lineColor);
                    Log.e(Constant.TAG, "polylines--------->" + polylines.toString());
                    if (polyLineTag.equals(DRIVER2RESTAURANT))
                    {
                        Polyline d2r = polylines.get(DRIVER2RESTAURANT);
                        if (d2r != null)
                        {
                            d2r.remove();
                            polylines.remove(DRIVER2RESTAURANT);
                        }
                    }
                    Polyline polyline = mMap.addPolyline(lineOptions);
                    polylines.put(polyLineTag, polyline);
                }
            }
        });
    }


    /**
     * 配置地图
     */
    private void configMapUiSettings()
    {
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
        mUiSettings.setMapToolbarEnabled(true);
    }

    private void handleNewLocation(Location location)
    {
        Log.e(Constant.TAG, location.toString());
        final double currentLatitude = location.getLatitude();
        final double currentLongitude = location.getLongitude();
        Log.e(Constant.TAG, "handleNewLocation===location===>" + location.toString());
        showMyMarkerAndPolyLine(currentLatitude, currentLongitude);


    }

    /**
     * 显示司机位置和到饭店的导航
     *
     * @param currentLatitude
     * @param currentLongitude
     */
    private void showMyMarkerAndPolyLine(double currentLatitude, double currentLongitude)
    {
        if (myMarker != null) myMarker.remove();
        LatLng my = new LatLng(currentLatitude, currentLongitude);
        MarkerOptions localOptions = new MarkerOptions()
                .position(my)
                .title("I am Here")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_driver_location));

        myMarker = mMap.addMarker(localOptions);


        Map<String, String> params = new HashMap<>();
        params.put("lat", String.valueOf(currentLatitude));
        params.put("lng", String.valueOf(currentLongitude));
        OkhttpUtils.postAsync(MapsActivity.this, Constant.MAP_REVERSEGEOCODINGFORLATLNG_V2_URL, "reverse_v2", params, new OkhttpUtils.ResultCallback()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                Log.e(Constant.TAG, "reverse_v2=====>" + e.toString());
            }

            @Override
            public void onSuccess(String body)
            {
                if (GsonUtils.getResponseCode(body) == Constant.ERROR)
                {
                    showSnackbar(mapsToolbar, "网络错误");
                    return;
                }
                else
                {
                    String localAddress = GsonUtils.getResponseInfo(body, "data");
                    markers.put("I am Here", localAddress);
                    loadOrigin2DestinationPolyline(localAddress,
                            "中国广东省广州市天河区广州世界大观 邮政编码: 510735", getResources().getColor(R.color.colorPrimaryDark150), 10, DRIVER2RESTAURANT);
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (orderStatus == Constant.DELIVERYING)
        {
            Log.e(Constant.TAG, "onConnected upLoadLocation");
            upLoadLocation(location);
        }
        Log.e(Constant.TAG, (location == null) + "" + "====>location");
        if (location == null)
        {
            //实时更新位置
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else
        {
            //实时更新位置
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution())
        {
            try
            {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            }
            catch (IntentSender.SendIntentException e)
            {
                // Log the error
                e.printStackTrace();
            }
        }
        else
        {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(Constant.TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.e(Constant.TAG, "onLocationChanged time===" + df.format(new Date()));
        if (orderStatus == Constant.DELIVERYING)
        {
            Log.e(Constant.TAG, "onLocationChanged upLoadLocation");
            upLoadLocation(location);
        }
        handleNewLocation(location);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.e(Constant.TAG, "=====order status==>" + orderStatus);
        if (CommonUtils.isAdmin(MapsActivity.this))
        {
            getMenuInflater().inflate(R.menu.maps_menu_restaurant, menu);
            configOrderStatus(R.id.maps_order_status);

        }
        else
        {
            getMenuInflater().inflate(R.menu.maps_menu_driver, menu);
            //设置司机端下的订单状态显示
            if (orderStatus != Constant.WAITING)
            {
                configOrderStatus(R.id.maps_receiveorder);
            }
            configReceiveConfirmEnabled();

        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 设置"确认送达"是否可用
     */
    private void configReceiveConfirmEnabled()
    {
        if (orderStatus == Constant.DELIVERYING)
        {
            mapsToolbar.getMenu().findItem(R.id.maps_confirmreceive).setEnabled(true);
        }
        else
        {
            mapsToolbar.getMenu().findItem(R.id.maps_confirmreceive).setEnabled(false);
        }
    }

    /**
     * 设置订单状态显示
     */
    private void configOrderStatus(int itemId)
    {
        String[] status = Constant.status;
        mapsToolbar.getMenu().findItem(itemId).setTitle(status[orderStatus]).setEnabled(false);
    }

    /**
     * 上传订单ID和司机位置信息
     */
    private void upLoadLocation(Location location)
    {
        if (location == null)
        {
            Log.e(Constant.TAG, "location为空，定位失败，跳过上传");
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("oid", getIntent().getExtras().getString("oid"));
        params.put("lat", location.getLatitude() + "");
        params.put("lng", location.getLongitude() + "");
        OkhttpUtils.postAsync(MapsActivity.this, Constant.MAP_UPLOAD_LOCATION, "upload_location", params, new OkhttpUtils.ResultCallback()
        {
            @Override
            public void onError(Request request, Exception e)
            {

            }

            @Override
            public void onSuccess(String body)
            {
                Log.e(Constant.TAG, "上传位置信息：" + GsonUtils.getResponseInfo(body, "data"));
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.maps_receiveorder:
                if (CommonUtils.isAdmin(MapsActivity.this))
                {
                    showSnackbar(mapsToolbar, "超级管理员不允许接单");
                }
                else
                {
                    receiveOrder();
                }
                break;
            case R.id.maps_confirmreceive:
                CustomDialogFragment dialog = DialogUtils.showAlertDialog(MapsActivity.this,
                        getResources().getString(R.string.notice),
                        getResources().getString(R.string.confirm_text),
                        getResources().getString(R.string.cancel),
                        getResources().getString(R.string.confirm)
                );
                dialog.setOnAlertDialogClickListener(new CustomDialogFragment.OnAlertDialogClickListener()
                {
                    @Override
                    public void onButtonClick(DialogInterface dialog, int which)
                    {
                        switch (which)
                        {
                            case AlertDialog.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                            case AlertDialog.BUTTON_POSITIVE:
                                confirmReceive();
                                break;
                            default:
                                break;
                        }
                    }
                });
                break;
            case R.id.maps_location:
                manualLocated();
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * 手动设置定位信息
     */
    private void manualLocated()
    {
        final CustomDialogFragment manualDialog = DialogUtils.showManualLocationDialog(MapsActivity.this,
                getResources().getString(R.string.notice),
                getResources().getString(R.string.cancel),
                getResources().getString(R.string.confirm));
        manualDialog.setOnAlertDialogClickListener(new CustomDialogFragment.OnAlertDialogClickListener()
        {
            @Override
            public void onButtonClick(DialogInterface dialog, int which)
            {
                switch (which)
                {
                    case AlertDialog.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                    case AlertDialog.BUTTON_POSITIVE:
                        // TODO: 手动设置定位
                        TextInputEditText editText = (TextInputEditText) manualDialog.getDialog().findViewById(R.id.dialog_manual_edit);
                        String place = editText.getText().toString();
                        if (TextUtils.isEmpty(place))
                        {
                            showSnackbar(mapsToolbar, "请输入位置");
                            dialog.dismiss();
                            return;
                        }
                        Map<String, String> params = new HashMap<>();
                        params.put("address", place);
                        OkhttpUtils.postAsync(MapsActivity.this, Constant.MAP_GETLATANDLNG_BY_ADDRESS_MANUALLY, "manual", params, new OkhttpUtils.ResultCallback()
                        {
                            @Override
                            public void onError(Request request, Exception e)
                            {

                            }

                            @Override
                            public void onSuccess(String body)
                            {
                                Log.e(Constant.TAG, "manual=====>" + body);
                                if (GsonUtils.getResponseCode(body) == Constant.ERROR)
                                {
                                    showSnackbar(mapsToolbar, "网络错误");
                                    return;
                                }
                                else
                                {
                                    JSONObject data = GsonUtils.getDataJsonObj(body);

                                    try
                                    {
                                        String manualAddress = data.getString("formatted_address");
                                        JSONObject manualAddressCode = (JSONObject) data.get("location");
                                        String lat = manualAddressCode.getString("lat");
                                        String lng = manualAddressCode.getString("lng");
                                        Log.e(Constant.TAG, "manualAddress==>" + manualAddress);
                                        Log.e(Constant.TAG, "lat==>" + lat);
                                        Log.e(Constant.TAG, "lng==>" + lng);

//                                        Location location = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                                        Location location = new Location("");
                                        location.setLatitude(Double.parseDouble(lat));
                                        location.setLongitude(Double.parseDouble(lng));
                                        upLoadLocation(location);
                                        showMyMarkerAndPolyLine(Double.parseDouble(lat), Double.parseDouble(lng));


                                    }
                                    catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        });


                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 接单
     */
    private void receiveOrder()
    {
        Map<String, String> params = new HashMap<>();
        params.put("oid", getIntent().getExtras().getString("oid"));
        params.put("uid", getIntent().getExtras().getString("uid"));
        Log.e(Constant.TAG, "rece maps==>" + params);
        OkhttpUtils.postAsync(MapsActivity.this, Constant.RECEIVE_ORDER_URL, "receive_order", params, new OkhttpUtils.ResultCallback()
        {
            @Override
            public void onError(Request request, Exception e)
            {

            }

            @Override
            public void onSuccess(String body)
            {
                Log.e(Constant.TAG, ">>>>>>>>>>>>>" + body);
                if (GsonUtils.getResponseCode(body) == Constant.ERROR)
                {
                    showSnackbar(mapsToolbar, GsonUtils.getResponseInfo(body, "data"));
                    return;
                }
                else
                {
                    showSnackbar(mapsToolbar, GsonUtils.getResponseInfo(body, "data"));
                    orderStatus = Constant.DELIVERYING;
                    configOrderStatus(R.id.maps_receiveorder);
                    configReceiveConfirmEnabled();
                }
            }
        });
    }

    /**
     * 确认订单送达
     */
    private void confirmReceive()
    {
        Map<String, String> params = new HashMap<>();
        params.put("oid", getIntent().getExtras().getString("oid"));
        OkhttpUtils.postAsync(MapsActivity.this, Constant.ORDER_CONFIRM_RECEIVE_URL, "order_confirm_receive", params, new OkhttpUtils.ResultCallback()
        {
            @Override
            public void onError(Request request, Exception e)
            {

            }

            @Override
            public void onSuccess(String body)
            {
                if (GsonUtils.getResponseCode(body) == Constant.ERROR)
                {
                    showSnackbar(mapsToolbar, "网络错误");
                    return;
                }
                else
                {
                    showSnackbar(mapsToolbar, GsonUtils.getResponseInfo(body, "data"));
                    orderStatus = Constant.FINISH;
                    configOrderStatus(R.id.maps_receiveorder);
                    configReceiveConfirmEnabled();
                    //停止定位服务
                    if (mGoogleApiClient.isConnected())
                    {
                        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, MapsActivity.this);
                        mGoogleApiClient.disconnect();
                    }

                }
            }
        });

    }
}
