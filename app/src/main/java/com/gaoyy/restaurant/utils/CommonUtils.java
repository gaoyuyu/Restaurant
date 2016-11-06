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

    public static boolean isUserLogin(Context context)
    {
        SharedPreferences account = context.getSharedPreferences("account", Activity.MODE_PRIVATE);
        boolean  hasLogin = account.getBoolean("hasLogin", false);
        return hasLogin;
    }

}
