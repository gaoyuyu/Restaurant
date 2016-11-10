package com.gaoyy.restaurant.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.gaoyy.restaurant.R;

import java.lang.reflect.Field;

public class CustomDialogFragment extends DialogFragment implements DialogInterface.OnClickListener
{
    private DialogType type = DialogType.LOADING;
    private String loadingText,title, message, negativeText, positiveText;
    private OnAlertDialogClickListener onAlertDialogClickListener;


    public void setOnAlertDialogClickListener(OnAlertDialogClickListener onAlertDialogClickListener)
    {
        this.onAlertDialogClickListener = onAlertDialogClickListener;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public void setNegativeText(String negativeText)
    {
        this.negativeText = negativeText;
    }

    public void setPositiveText(String positiveText)
    {
        this.positiveText = positiveText;
    }


    public void setType(DialogType type)
    {
        this.type = type;
    }

    public void setLoadingText(String loadingText)
    {
        this.loadingText = loadingText;
    }

    public enum DialogType
    {
        LOADING, LOADING_WITH_TEXT, ALERT
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        if (DialogType.LOADING == type)
        {
            return createLoadingDialog();
        }
        else if (DialogType.ALERT == type)
        {
            return createAlertDialog();
        }
        else if (DialogType.LOADING_WITH_TEXT == type)
        {
            return createLoadingDialogWithText();
        }
        return null;
    }

    @Override
    public void show(FragmentManager manager, String tag)
    {
        // 如果使用api本身自带的 show() 方法，调用的是 commit() 方法添加 DialogFragment，这样的话会导致
        // 当 Activity 调用 onSaveInstanceState() 方法后加载弹出框时报 IllegalStateException 异常，
        // 所以需要通过反射重写 show() 方法，使用 commitAllowingStateLoss() 方法添加 DialogFragment.
        try
        {
            Field mDismissed = getClass().getSuperclass().getDeclaredField("mDismissed");
            mDismissed.setAccessible(true);
            mDismissed.set(this, false);
            Field mShownByMe = getClass().getSuperclass().getDeclaredField("mShownByMe");
            mShownByMe.setAccessible(true);
            mShownByMe.set(this, false);
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 创建loading框
     *
     * @return
     */
    private Dialog createLoadingDialog()
    {
        Dialog dialog = getCustomDialog();
        dialog.setContentView(R.layout.dialog_loading);
        return dialog;
    }

    /**
     * 创建带文字loading框
     * @return
     */
    private Dialog createLoadingDialogWithText()
    {
        Dialog dialog = getCustomDialog();
        dialog.setContentView(R.layout.dialog_loading);
        ((TextView)dialog.findViewById(R.id.loading_text)).setVisibility(View.VISIBLE);
        ((TextView)dialog.findViewById(R.id.loading_text)).setText(loadingText);
        return dialog;
    }

    private Dialog createAlertDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton(negativeText, this)
                .setPositiveButton(positiveText, this);
        return builder.create();
    }


    private Dialog getCustomDialog()
    {
        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        return dialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        if(onAlertDialogClickListener == null) throw new NullPointerException("onAlertDialogClickListener is null");
        onAlertDialogClickListener.onButtonClick(dialog,which);
    }

    public interface OnAlertDialogClickListener
    {
        void onButtonClick(DialogInterface dialog, int which);
    }
}
