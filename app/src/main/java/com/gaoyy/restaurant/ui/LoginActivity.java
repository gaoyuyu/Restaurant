package com.gaoyy.restaurant.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.gaoyy.restaurant.R;
import com.gaoyy.restaurant.base.BaseActivity;
import com.gaoyy.restaurant.fragment.CustomDialogFragment;
import com.gaoyy.restaurant.utils.Constant;
import com.gaoyy.restaurant.utils.DialogUtils;
import com.gaoyy.restaurant.utils.GsonUtils;
import com.gaoyy.restaurant.utils.OkhttpUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;

public class LoginActivity extends BaseActivity implements View.OnClickListener
{

    private TextInputLayout loginUsernameTextinputlayout;
    private TextInputEditText loginUsername;
    private TextInputLayout loginPasswordTextinputlayout;
    private TextInputEditText loginPassword;
    private Button loginBtn;

    @Override
    protected void initContentView()
    {
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void assignViews()
    {
        super.assignViews();
        loginUsernameTextinputlayout = (TextInputLayout) findViewById(R.id.login_username_textinputlayout);
        loginUsername = (TextInputEditText) findViewById(R.id.login_username);
        loginPasswordTextinputlayout = (TextInputLayout) findViewById(R.id.login_password_textinputlayout);
        loginPassword = (TextInputEditText) findViewById(R.id.login_password);
        loginBtn = (Button) findViewById(R.id.login_btn);
    }

    @Override
    protected void setListener()
    {
        super.setListener();
        loginUsername.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if (editable.toString() != null || !editable.toString().equals(""))
                {
                    loginUsernameTextinputlayout.setError(null);
                    loginUsernameTextinputlayout.setErrorEnabled(false);
                }
            }
        });

        loginPassword.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if (editable.toString().length() > 6)
                {
                    loginPasswordTextinputlayout.setErrorEnabled(true);
                    loginPasswordTextinputlayout.setError("password's length must be 6");
                }
                else
                {
                    //必须要添加否则出现空白不隐藏的问题
                    loginPasswordTextinputlayout.setError(null);
                    loginPasswordTextinputlayout.setErrorEnabled(false);
                }
            }
        });
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.login_btn:
                validate();
                if (loginUsernameTextinputlayout.isErrorEnabled() || loginPasswordTextinputlayout.isErrorEnabled())
                    return;
                Map<String, String> params = new HashMap<>();
                params.put("username", loginUsername.getText().toString());
                params.put("password", loginPassword.getText().toString());
                final CustomDialogFragment loading = DialogUtils.showLoadingDialog(LoginActivity.this);
                OkhttpUtils.postAsync(LoginActivity.this, Constant.LOGIN_URL, "login", params, new OkhttpUtils.ResultCallback()
                {
                    @Override
                    public void onError(Request request, Exception e)
                    {

                    }

                    @Override
                    public void onSuccess(String body)
                    {
                        loading.dismiss();
                        Log.i(Constant.TAG, body);
                        int code = GsonUtils.getResponseCode(body);
                        if (code == Constant.ERROR)
                        {
                            showSnackbar(loginBtn, GsonUtils.getResponseInfo(body, "data"));
                            return;
                        }
                        else
                        {

                            JSONObject data = GsonUtils.getDataJsonObj(body);
                            Gson gson = new Gson();
                            try
                            {
                                String id = data.getString("id");
                                String username = data.getString("username");
                                JSONArray role = (JSONArray) data.get("Role");
                                Log.e(Constant.TAG, "id==>" + id.toString());
                                Log.e(Constant.TAG, "username==>" + username.toString());
                                Log.e(Constant.TAG, "role=id=>" + role.getJSONObject(0).getString("id"));
                                Log.e(Constant.TAG, "role=name=>" + role.getJSONObject(0).getString("name"));
                                saveUser(id, username, role);
                                redirectThenKill(MainActivity.class);

                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }

                    }
                });
                break;
        }
    }

    private void saveUser(String id, String username, JSONArray role) throws JSONException
    {
        SharedPreferences account = (LoginActivity.this).getSharedPreferences("account",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = account.edit();
        editor.putBoolean("hasLogin", true);
        editor.putString("id", id);
        editor.putString("username", username);
        editor.putString("roleId", role.getJSONObject(0).getString("id"));
        editor.putString("roleName", role.getJSONObject(0).getString("name"));
        editor.apply();
    }

    private void validate()
    {
        if (loginUsername.getText().toString().equals(""))
        {
            loginUsernameTextinputlayout.setErrorEnabled(true);
            loginUsernameTextinputlayout.setError("username mustn't be empty");
        }
        else
        {
            loginUsernameTextinputlayout.setError(null);
            loginUsernameTextinputlayout.setErrorEnabled(false);
        }
        if (loginPassword.getText().toString().equals(""))
        {
            loginPasswordTextinputlayout.setErrorEnabled(true);
            loginPasswordTextinputlayout.setError("password mustn't be empty");
        }
        else
        {
            loginPasswordTextinputlayout.setError(null);
            loginPasswordTextinputlayout.setErrorEnabled(false);
        }

        if (loginPassword.getText().toString().length() < 6 || loginPassword.getText().toString().length() > 6)
        {
            loginPasswordTextinputlayout.setErrorEnabled(true);
            loginPasswordTextinputlayout.setError("password's length must be 6");
        }
        else
        {
            //必须要添加否则出现空白不隐藏的问题
            loginPasswordTextinputlayout.setError(null);
            loginPasswordTextinputlayout.setErrorEnabled(false);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        OkhttpUtils.cancelTag("login");
    }
}
