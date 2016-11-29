package com.gaoyy.restaurant.utils;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.gaoyy.restaurant.fragment.CustomDialogFragment;

import java.lang.ref.WeakReference;

public class DialogUtils
{
    /**
     * 显示loading框
     * @param context
     * @return
     */
    public static CustomDialogFragment showLoadingDialog(Context context)
    {
        if (!(context instanceof FragmentActivity)) return null;
        CustomDialogFragment dialog = new WeakReference<CustomDialogFragment>(new CustomDialogFragment()).get();
        if (dialog == null) return null;
        dialog.setType(CustomDialogFragment.DialogType.LOADING);
        dialog.setCancelable(true);
        dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "LoadingDialog");
        return dialog;
    }
    public static CustomDialogFragment showLoadingDialog(Context context,String loadingText)
    {
        if (!(context instanceof FragmentActivity)) return null;
        CustomDialogFragment dialog = new WeakReference<CustomDialogFragment>(new CustomDialogFragment()).get();
        if (dialog == null) return null;
        dialog.setType(CustomDialogFragment.DialogType.LOADING_WITH_TEXT);
        dialog.setLoadingText(loadingText);
        dialog.setCancelable(true);
        dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "LoadingDialog");
        return dialog;
    }


    /**
     * 显示带选项按钮对话框
     * @param context
     * @return
     */
    public static CustomDialogFragment showAlertDialog(Context context,String title,String message,String negativeText,String positionText)
    {
        if (!(context instanceof FragmentActivity)) return null;
        CustomDialogFragment dialog = new WeakReference<CustomDialogFragment>(new CustomDialogFragment()).get();
        if (dialog == null) return null;
        dialog.setType(CustomDialogFragment.DialogType.ALERT);
        dialog.setCancelable(false);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setNegativeText(negativeText);
        dialog.setPositiveText(positionText);
        dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "AlertDialog");
        return dialog;
    }
    /**
     * 显示带选项按钮对话框
     * @param context
     * @return
     */
    public static CustomDialogFragment showManualLocationDialog(Context context,String title,String negativeText,String positionText)
    {
        if (!(context instanceof FragmentActivity)) return null;
        CustomDialogFragment dialog = new WeakReference<CustomDialogFragment>(new CustomDialogFragment()).get();
        if (dialog == null) return null;
        dialog.setType(CustomDialogFragment.DialogType.MANUAL_LOCATION);
        dialog.setCancelable(false);
        dialog.setTitle(title);
        dialog.setNegativeText(negativeText);
        dialog.setPositiveText(positionText);
        dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "ManualLocationDialog");
        return dialog;
    }
}
