package com.gaoyy.restaurant.ui;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaoyy.restaurant.R;
import com.gaoyy.restaurant.base.BaseActivity;
import com.gaoyy.restaurant.utils.CommonUtils;
import com.gaoyy.restaurant.utils.Constant;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DrawerLayout mainDrawerLayout;
    private NavigationView mainNavView;
    private Toolbar mainToolbar;
    private RelativeLayout mainContent;
    private View headerView;

    private ImageView headerAvatar;
    private TextView headerText;


    @Override
    protected void initContentView()
    {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initToolbar()
    {
        super.initToolbar(mainToolbar, R.string.app_name, false, null);
    }

    @Override
    protected void assignViews()
    {
        super.assignViews();
        mainDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mainNavView = (NavigationView) findViewById(R.id.main_nav_view);
        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mainContent = (RelativeLayout) findViewById(R.id.main_content);

    }

    @Override
    protected void configViews()
    {
        super.configViews();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mainDrawerLayout, mainToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mainDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        headerView = mainNavView.getHeaderView(0);
        headerAvatar = (ImageView) headerView.findViewById(R.id.header_avatar);
        headerText = (TextView) headerView.findViewById(R.id.header_text);
        headerText.setText("欢迎您，" + "[" + CommonUtils.getUserRole(this) + "]" + CommonUtils.getUserName(this));

    }

    @Override
    protected void setListener()
    {
        super.setListener();
        mainNavView.setNavigationItemSelectedListener(this);


    }

    @Override
    protected void initFragment(Bundle savedInstanceState, int contentLayoutId, int type)
    {
        if(CommonUtils.getUserName(this).equals("admin"))
        {
            type = Constant.ADMIN;
        }
        else
        {
            type = Constant.DRIVER;
        }
        super.initFragment(savedInstanceState, R.id.main_layout, type);
    }

    @Override
    public void onBackPressed()
    {
        if (mainDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            mainDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        super.setStatusBarColor(-1);
        super.setNavigationBarColor(-1);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id)
        {
            case R.id.nav_setting:
                showToast("Settings");
                break;
            case R.id.nav_exit:
                CommonUtils.userLogout(MainActivity.this);
                redirectThenKill(LoginActivity.class);
                break;
        }


        mainDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
