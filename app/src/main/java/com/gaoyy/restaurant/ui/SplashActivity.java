package com.gaoyy.restaurant.ui;

import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaoyy.restaurant.R;
import com.gaoyy.restaurant.base.BaseActivity;
import com.gaoyy.restaurant.utils.CommonUtils;
import com.gaoyy.restaurant.utils.TransitionHelper;

public class SplashActivity extends BaseActivity implements Animation.AnimationListener
{
    private RelativeLayout activitySplash;
    private TextView splashWelcome;

    @Override
    protected void initContentView()
    {
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void assignViews()
    {
        super.assignViews();
        activitySplash = (RelativeLayout) findViewById(R.id.activity_splash);
        splashWelcome = (TextView) findViewById(R.id.splash_welcome);
    }

    @Override
    protected void configViews()
    {
        super.configViews();
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(2500);
        splashWelcome.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(this);
    }

    @Override
    public void onAnimationStart(Animation animation)
    {

    }

    @Override
    public void onAnimationEnd(Animation animation)
    {
        if (CommonUtils.isUserLogin(this))
        {
            redirectThenKill(MainActivity.class);
        }
        else
        {
            Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants
                    (SplashActivity.this, false, new Pair<>(splashWelcome, "splash"));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashActivity.this, pairs);
            redirectWithShareViewsThenKill(LoginActivity.class, options);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation)
    {

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        super.setStatusBarColor(-1);
        super.setNavigationBarColor(-1);
    }
}
