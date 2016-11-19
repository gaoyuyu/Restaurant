package com.gaoyy.restaurant.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
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

    private LinearLayout mapLayout;

    private TextView mapsDestination;

    private boolean isFirstLoadingMarker = true;
    private boolean isFirstLoadingPolyline = true;

    private Map<String,String> markers = null;

    private LinearLayout mapsTextLayout;


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
        mGoogleApiClient.connect();
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
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
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

    }

    /**
     * 加载饭店和客户Maker
     */
    public void loadRestaurantAndCustomerMarker()
    {
        final CustomDialogFragment dialog = DialogUtils.showLoadingDialog(MapsActivity.this, "加载位置...");
        if (!isFirstLoadingMarker)
        {
            dialog.dismiss();
        }
        Map<String, String> params = new HashMap<>();
        params.put("restaurant", "广州世界大观");
        params.put("customer", "广州奥林匹克网球中心");
        OkhttpUtils.postAsync(MapsActivity.this, Constant.MAP_GETLATANDLNG_BY_ADDRESS_V2_URL, "get_latlng_v2", params, new OkhttpUtils.ResultCallback()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                dialog.dismiss();
                Log.e(Constant.TAG, "v2 get latlng===>" + e.toString());
            }

            @Override
            public void onSuccess(String body)
            {
                dialog.dismiss();
                isFirstLoadingMarker = false;
                Log.e(Constant.TAG, "v2 get latlng===>" + body);

                if (GsonUtils.getResponseCode(body) == Constant.ERROR)
                {
                    showSnackbar(mapsToolbar, "网络错误");
                    return;
                }
                JSONObject data = GsonUtils.getDataJsonObj(body);

                try
                {
                    JSONObject restaurant = (JSONObject) data.get("restaurant_latlng");
                    String restaurantAddress = restaurant.getString("formatted_address");
                    JSONObject restaurantCode = (JSONObject) restaurant.get("location");
                    String restaurantLat = restaurantCode.getString("lat");
                    String restaurantLng = restaurantCode.getString("lng");
                    Log.e(Constant.TAG, "restaurantAddress==>" + restaurantAddress);
                    Log.e(Constant.TAG, "restaurantLat==>" + restaurantLat);
                    Log.e(Constant.TAG, "restaurantLng==>" + restaurantLng);
                    JSONObject customer = (JSONObject) data.get("customer_latlng");
                    String customerAddress = customer.getString("formatted_address");
                    JSONObject customerCode = (JSONObject) customer.get("location");
                    String customerLat = customerCode.getString("lat");
                    String customerLng = customerCode.getString("lng");
                    Log.e(Constant.TAG, "customerAddress==>" + customerAddress);
                    Log.e(Constant.TAG, "customerLat==>" + customerLat);
                    Log.e(Constant.TAG, "customerLng==>" + customerLng);


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

                    markers.put("饭店",restaurantAddress);
                    markers.put("客人",customerAddress);

                    mMap.animateCamera(CameraUpdateFactory.newLatLng(res));

                    loadOrigin2DestinationPolyline(restaurantAddress, customerAddress, getResources().getColor(R.color.colorAccent),6);


                }
                catch (JSONException e)
                {
                    e.printStackTrace();
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
     */
    public void loadOrigin2DestinationPolyline(String origin, String destination, final int lineColor, final int lineWidth)
    {
        final CustomDialogFragment dialog = DialogUtils.showLoadingDialog(MapsActivity.this, "规划驾车路线...");
        if (!isFirstLoadingPolyline)
        {
            dialog.dismiss();
        }
        Map<String, String> params = new HashMap<>();
        params.put("origin", origin);
        params.put("destination", destination);
        OkhttpUtils.postAsync(MapsActivity.this, Constant.MAP_GETDIRECTIONPOLYLINE_V2_URL, "get_polyline_v2", params, new OkhttpUtils.ResultCallback()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                dialog.dismiss();
                Log.e(Constant.TAG, "get_polyline_v2===>" + e.toString());
            }

            @Override
            public void onSuccess(String body)
            {
                isFirstLoadingPolyline = false;
                dialog.dismiss();
                Log.e(Constant.TAG, "get_polyline_v2===>" + body);
                if (GsonUtils.getResponseCode(body) == Constant.ERROR)
                {
                    showSnackbar(mapsToolbar, "网络错误");
                    return;
                }
                Log.e(Constant.TAG, "get_polyline_v2==line=>" + GsonUtils.getResponseInfo(body, "data"));
                String restaurant2customer = GsonUtils.getResponseInfo(body, "data");
                List<LatLng> line = CommonUtils.decodePoly(restaurant2customer);
                PolylineOptions lineOptions = new PolylineOptions();
                lineOptions.addAll(line);
                lineOptions.width(lineWidth);
                lineOptions.geodesic(true);
                lineOptions.color(lineColor);
                mMap.addPolyline(lineOptions);
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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
        LatLng my = new LatLng(currentLatitude, currentLongitude);
        MarkerOptions localOptions = new MarkerOptions()
                .position(my)
                .title("I am Here")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_driver_location));

        mMap.addMarker(localOptions);


//        final CustomDialogFragment dialog = DialogUtils.showLoadingDialog(MapsActivity.this,"定位当前位置...");
        Map<String, String> params = new HashMap<>();
        params.put("lat", String.valueOf(currentLatitude));
        params.put("lng", String.valueOf(currentLongitude));

        OkhttpUtils.postAsync(MapsActivity.this, Constant.MAP_REVERSEGEOCODINGFORLATLNG_V2_URL, "reverse_v2", params, new OkhttpUtils.ResultCallback()
        {
            @Override
            public void onError(Request request, Exception e)
            {
//                dialog.dismiss();
                Log.e(Constant.TAG, "reverse_v2=====>" + e.toString());
            }

            @Override
            public void onSuccess(String body)
            {
                Log.e(Constant.TAG, "reverse_v2=====>" + body);
                if (GsonUtils.getResponseCode(body) == Constant.ERROR)
                {
                    showSnackbar(mapsToolbar, "网络错误");
                    return;
                }
                String localAddress = GsonUtils.getResponseInfo(body, "data");
                markers.put("I am Here",localAddress);
                loadOrigin2DestinationPolyline(localAddress,
                        "中国广东省广州市天河区广州世界大观 邮政编码: 510735", getResources().getColor(R.color.colorPrimaryDark),10);


            }
        });


    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.e(Constant.TAG, (location == null) + "" + "=======location");
        if (location == null)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else
        {
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
        handleNewLocation(location);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.e(Constant.TAG, "=====order status==>" + Integer.valueOf(getIntent().getExtras().getString("order_status")));
        if(CommonUtils.isAdmin(MapsActivity.this))
        {
            getMenuInflater().inflate(R.menu.maps_menu_restaurant, menu);
            configOrderStatus(R.id.maps_confirmreceive);
        }
        else
        {
            getMenuInflater().inflate(R.menu.maps_menu_driver, menu);
            configOrderStatus(R.id.maps_receiveorder);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 设置订单状态显示
     */
    private void configOrderStatus(int itemId)
    {
        if (Integer.valueOf(getIntent().getExtras().getString("order_status")) != 0)
        {
            String[] status = Constant.status;
            mapsToolbar.getMenu().findItem(itemId).setTitle(status[Integer.valueOf(getIntent().getExtras().getString("order_status"))]).setEnabled(false);
        }
    }

    /**
     * 上传订单ID和司机位置信息
     */
    private void upLoadLocation()
    {

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
                CustomDialogFragment dialog=  DialogUtils.showAlertDialog(MapsActivity.this,
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
                        switch(which) {
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

        }


        return super.onOptionsItemSelected(item);
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
                    mapsToolbar.getMenu().getItem(0).setTitle("正在派送中").setEnabled(false);
                }
            }
        });
    }

    /**
     * 确认订单送达
     */
    private void confirmReceive()
    {
        Map<String ,String> params = new HashMap<>();
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

                showSnackbar(mapsToolbar,GsonUtils.getResponseInfo(body,"data"));
                mapsToolbar.getMenu().getItem(0).setTitle("完成").setEnabled(false);
            }
        });

    }
}
