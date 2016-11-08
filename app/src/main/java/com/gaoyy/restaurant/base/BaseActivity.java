package com.gaoyy.restaurant.base;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.gaoyy.restaurant.R;
import com.gaoyy.restaurant.fragment.AdminFragment;
import com.gaoyy.restaurant.fragment.DriverFragment;
import com.gaoyy.restaurant.utils.CommonUtils;
import com.gaoyy.restaurant.utils.Constant;
import com.gaoyy.restaurant.utils.ToolbarHelper;


public abstract class BaseActivity extends AppCompatActivity
{


    //记录当前使用的Fragment
    private Fragment currentFragment;

    public void setCurrentFragment(Fragment currentFragment)
    {
        this.currentFragment = currentFragment;
    }

    public Fragment getCurrentFragment()
    {
        return currentFragment;
    }

    //颜色资源
    public int[] colors = {R.color.colorPrimary, R.color.colorPrimaryDark};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setExitTransition(new ChangeBounds());
        }
        //加载布局
        initContentView();
        //初始化view
        assignViews();
        //初始化toolbar
        initToolbar();
        //配置views
        configViews();
        //设置监听器
        setListener();
        //加载数据
        loadData();
        initFragment(savedInstanceState,-1,-1);
    }


    protected void initFragment(Bundle savedInstanceState,int contentLayoutId,int type)
    {
        if(contentLayoutId == -1 || type == -1) return;

        if (savedInstanceState == null)
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = null;
            switch (type)
            {
                case Constant.ADMIN:
                    fragment = AdminFragment.newInstance();
                    break;
                default:
                    fragment = DriverFragment.newInstance();
                    break;

            }
            ft.replace(contentLayoutId, fragment).commit();
        }
    }


    /**
     * abstract method to init layout xml
     */
    protected abstract void initContentView();

    /**
     * init view
     */
    protected void assignViews()
    {
    }

    /**
     * init toolbar,include some settings
     */
    protected void initToolbar()
    {
    }

    /**
     * should override in ChildClass if in need
     */
    public void initToolbar(Toolbar toolbar, int titleId, boolean enabled, int[] colors)
    {
        if (null == colors)
        {
            colors = this.colors;
        }
        toolbar.setTitle(titleId);
        toolbar.setBackgroundColor(getResources().getColor(colors[0]));
        setSupportActionBar(toolbar);
        //设置返回键可用
        getSupportActionBar().setHomeButtonEnabled(enabled);
        getSupportActionBar().setDisplayHomeAsUpEnabled(enabled);
    }

    /**
     * config views
     */
    protected void configViews()
    {
    }


    /**
     * set listener for views
     */
    protected void setListener()
    {
    }

    /**
     * loadData
     */
    protected void loadData()
    {
    }


    /**
     * startActivity
     *
     * @param clazz
     */
    public void redirect(Class<?> clazz)
    {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    /**
     * startActivity with bundle
     *
     * @param clazz
     * @param bundle
     */
    public void redirect(Class<?> clazz, Bundle bundle)
    {
        Intent intent = new Intent(this, clazz);
        if (null != bundle)
        {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * startActivity with transition and share views
     *
     * @param clazz
     * @param options
     */
    public void redirectWithShareViews(Class<?> clazz, ActivityOptionsCompat options)
    {
        if (options == null) return;
        Intent intent = new Intent(this, clazz);
        startActivity(intent, options.toBundle());
    }
    /**
     * startActivity with transition and share views,then finish
     *
     * @param clazz
     * @param options
     */
    public void redirectWithShareViewsThenKill(Class<?> clazz, ActivityOptionsCompat options)
    {
        if (options == null) return;
        Intent intent = new Intent(this, clazz);
        startActivity(intent, options.toBundle());
        finishAtyAfterTransition();
    }

    /**
     * startActivity then finish
     *
     * @param clazz
     */
    public void redirectThenKill(Class<?> clazz)
    {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        finishAtyAfterTransition();
    }

    /**
     * startActivity with bundle then finish
     *
     * @param clazz
     * @param bundle
     */
    public void redirectThenKill(Class<?> clazz, Bundle bundle)
    {
        Intent intent = new Intent(this, clazz);
        if (null != bundle)
        {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        finishAtyAfterTransition();
    }

    /**
     * startActivityForResult
     *
     * @param clazz
     * @param requestCode
     */
    public void redirectForResult(Class<?> clazz, int requestCode)
    {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }

    /**
     * startActivityForResult with bundle
     *
     * @param clazz
     * @param requestCode
     * @param bundle
     */
    public void redirectForResult(Class<?> clazz, int requestCode, Bundle bundle)
    {
        Intent intent = new Intent(this, clazz);
        if (null != bundle)
        {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * Ativity退出效果
     */
    public void finishAtyAfterTransition()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            finishAfterTransition();
        }
        else
        {
            finish();
        }
    }

    /**
     * showToast
     *
     * @param msg
     */
    protected void showToast(String msg)
    {
        if (null != msg && !CommonUtils.isEmpty(msg))
        {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * showSnackbar
     *
     * @param view
     * @param msg
     */
    protected void showSnackbar(View view, String msg)
    {
        if (null != msg && !CommonUtils.isEmpty(msg))
        {
            Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
        }
    }


    /**
     * setStatusBarColor
     *
     * @param color
     */
    protected void setStatusBarColor(int color)
    {
        color = getDefaultColor(color);
        ToolbarHelper.setStatusBarColor(this, color);
    }

    /**
     * setNavigationBarColor
     *
     * @param color
     */
    protected void setNavigationBarColor(int color)
    {
        color = getDefaultColor(color);
        ToolbarHelper.setNavigationBarColor(this, color);
    }

    protected int getDefaultColor(int color)
    {
        if (-1 == color)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                color = this.colors[0];
            }
            else
            {
                color = this.colors[1];
            }
        }
        return color;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemId = item.getItemId();
        switch (itemId)
        {
            case android.R.id.home:
                finishAtyAfterTransition();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
