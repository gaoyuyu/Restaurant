package com.gaoyy.restaurant.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.gaoyy.restaurant.service.PollingService;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaoyy on 2016/11/5 0005.
 */

public class CommonUtils
{
    public static boolean isEmpty(String str)
    {
        if (str == null || str.length() == 0 || str.equalsIgnoreCase("null") || str.isEmpty() || str.equals(""))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 判断用户是否登陆
     *
     * @param context
     * @return
     */
    public static boolean isUserLogin(Context context)
    {
        SharedPreferences account = context.getSharedPreferences("account", Activity.MODE_PRIVATE);
        boolean hasLogin = account.getBoolean("hasLogin", false);
        return hasLogin;
    }

    public static void userLogout(Context context)
    {
        SharedPreferences account = context.getSharedPreferences("account", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = account.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * 获取登陆用户名
     *
     * @param context
     * @return
     */
    public static String getUserName(Context context)
    {
        SharedPreferences account = context.getSharedPreferences("account", Activity.MODE_PRIVATE);
        String username = account.getString("username", "");
        return username;
    }

    /**
     * 获取登陆用户ID
     *
     * @param context
     * @return
     */
    public static String getUserId(Context context)
    {
        SharedPreferences account = context.getSharedPreferences("account", Activity.MODE_PRIVATE);
        String userId = account.getString("id", "");
        return userId;
    }

    /**
     * 获取用户登录角色
     *
     * @param context
     * @return
     */
    public static String getUserRole(Context context)
    {
        SharedPreferences account = context.getSharedPreferences("account", Activity.MODE_PRIVATE);
        String roleName = account.getString("roleName", "");
        return roleName;
    }

    /**
     * 判断是否是管理员
     *
     * @param context
     * @return
     */
    public static boolean isAdmin(Context context)
    {
        String username = getUserName(context);
        return username.equals("admin");
    }


    /**
     * 解密Polyline
     *
     * @param encoded
     * @return
     */
    public static List<LatLng> decodePoly(String encoded)
    {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len)
        {
            int b, shift = 0, result = 0;
            do
            {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }
            while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do
            {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }
            while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    /**
     * 非饭店端用户登录时启动轮询Services
     * @param context
     * @param seconds
     */
    public static void startPollingService(Context context,int seconds)
    {
        if (CommonUtils.isUserLogin(context))
        {
            if (!CommonUtils.isAdmin(context))
            {
                PollingUtils.startPollingService(context, seconds, PollingService.class, PollingService.ACTION);
            }
        }

    }
}
