package com.gaoyy.restaurant.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gaoyy on 2016/11/4 0004.
 */
public class GsonUtils
{
    /**
     * 根据返回字符串获取JSON对象
     *
     * @param body
     * @return
     */
    public static JSONObject getMainJsonObj(String body)
    {
        JSONObject jsonObject = null;
        try
        {
            jsonObject = new JSONObject(body);
        }
        catch (JSONException e)
        {
            Log.i(Constant.TAG, "catch Exception when getMainJsonObj：" + e.toString());
        }

        return jsonObject;
    }

    /**
     * 获取返回的结果码（0-请求成功，-1-内部错误）
     *
     * @param body
     * @return
     */
    public static int getResponseCode(String body)
    {
        JSONObject jsonObject = getMainJsonObj(body);
        int repCode = -2;
        try
        {
            repCode = jsonObject.getInt("code");
        }
        catch (JSONException e)
        {
            Log.i(Constant.TAG, "catch Exception when getRepCode：" + e.toString());
        }
        return repCode;
    }

    /**
     * 获取JSON字符串中的指定key字段，适用于结果码为-1的情况
     * @param body
     * @param key
     * @return
     */
    public static String getResponseInfo(String body,String key)
    {
        JSONObject jsonObject = getMainJsonObj(body);
        String info = "";
        try
        {
            info = jsonObject.getString(key);
        }
        catch (JSONException e)
        {
            Log.i(Constant.TAG, "catch Exception when getRepInfo：" + e.toString());
        }
        return info;
    }

    /**
     * 获取JSON字符串中的data字段
     *
     * @param body
     * @return
     */
    public static JSONObject getDataJsonObj(String body)
    {
        JSONObject jsonObject = getMainJsonObj(body);
        JSONObject dataJsonObj = null;
        try
        {
            dataJsonObj = (JSONObject) jsonObject.get("data");
        }
        catch (JSONException e)
        {
            Log.i(Constant.TAG, "catch Exception when getDataJsonObj：" + e.toString());
        }
        return dataJsonObj;

    }


}
