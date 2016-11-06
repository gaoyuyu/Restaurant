package com.gaoyy.restaurant.utils;

import android.app.Activity;
import android.os.Build;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.lang.ref.WeakReference;

/**
 * Created by gaoyy on 2016/11/2 0002.
 */
public class ToolbarHelper
{
    /**
     * 设置StatusBar颜色
     * @param activity
     * @param color
     */
    public static void setStatusBarColor(Activity activity, int color)
    {
        WeakReference<SystemBarTintManager> ws = new WeakReference<>(new SystemBarTintManager(activity));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(color));
        } else
        {
            SystemBarTintManager systemBarTintManager = ws.get();
            systemBarTintManager.setStatusBarTintResource(color);
            systemBarTintManager.setStatusBarTintEnabled(true);
        }
    }

    /**
     * 设置NavigationBar颜色（SDK>=LOLLIPOP）
     * @param activity
     * @param color
     */
    public static void setNavigationBarColor(Activity activity, int color)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            activity.getWindow().setNavigationBarColor(activity.getResources().getColor(color));
        }
    }
}
