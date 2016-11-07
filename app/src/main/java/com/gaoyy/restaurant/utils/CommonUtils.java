package com.gaoyy.restaurant.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

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

}
