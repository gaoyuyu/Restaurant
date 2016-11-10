package com.gaoyy.restaurant.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkhttpUtils
{
    private static OkHttpClient okHttpClient = null;
    private static OkhttpUtils okhttpUtils = null;
    private final static Handler delivery = new Handler(Looper.getMainLooper());

    private static final class SuccessRunnable implements Runnable
    {
        private String body;
        private ResultCallback callback;

        public SuccessRunnable(String body, ResultCallback callback)
        {
            this.body = body;
            this.callback = callback;
        }

        @Override
        public void run()
        {
            if (callback != null)
            {
                callback.onSuccess(body);
            }
        }
    }


    private static final class FailedRunnable implements Runnable
    {
        private Request request;
        private ResultCallback callback;
        private IOException e;

        public FailedRunnable(Request request, ResultCallback callback, IOException e)
        {
            this.request = request;
            this.callback = callback;
            this.e = e;
        }

        @Override
        public void run()
        {
            if (callback != null)
            {
                callback.onError(request, e);
            }
        }
    }

    private OkhttpUtils(Context context)
    {
        //设置缓存目录和大小
        int cacheSize = 10 << 20; // 10 MiB
        Cache cache = new Cache(context.getCacheDir(), cacheSize);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(1500000, TimeUnit.MILLISECONDS)
                .writeTimeout(2000000, TimeUnit.MILLISECONDS)
                .readTimeout(2000000, TimeUnit.MILLISECONDS);

        okHttpClient = getOkHttpClientSingletonInstance();
    }

    /**
     * 获取OkhttpUtils单例
     *
     * @param context
     * @return
     */
    public static OkhttpUtils getInstance(Context context)
    {
        if (okhttpUtils == null)
        {
            synchronized (OkhttpUtils.class)
            {
                okhttpUtils = new OkhttpUtils(context);
            }

        }
        return okhttpUtils;
    }

    /**
     * 获取OkHttpClient单例
     *
     * @return
     */
    public static OkHttpClient getOkHttpClientSingletonInstance()
    {
        if (okHttpClient == null)
        {
            synchronized (OkHttpClient.class)
            {
                okHttpClient = new OkHttpClient.Builder().build();
            }

        }
        return okHttpClient;
    }

    /**
     * 构建get的Request对象
     *
     * @param url url地址
     * @param tag 标记
     * @return
     */
    private Request buildGetRequest(String url, Object tag)
    {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if (tag != null)
        {
            builder.tag(tag);
        }
        return builder.build();
    }

    /**
     * 构建post的Request对象
     *
     * @param url
     * @param requestBody
     * @param tag
     * @return
     */
    private Request buildPostRequest(String url, RequestBody requestBody, Object tag)
    {
        Request.Builder builder = new Request.Builder();
        builder.url(url).post(requestBody);
        if (tag != null)
        {
            builder.tag(tag);
        }
        return builder.build();
    }

    /**
     * 构建RequestBody对象，适用于提交key-value
     *
     * @param params
     * @return
     */
    private RequestBody buildPostRequestBody(Map<String, String> params)
    {
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (params != null && !params.isEmpty())
        {
            for (Map.Entry<String, String> entry : params.entrySet())
            {
                formBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        return formBuilder.build();
    }


    /**
     * 同步Get请求（返回Response对象）
     *
     * @param url
     * @param tag
     * @return
     * @throws IOException
     */
    private Response _getSync(String url, Object tag) throws IOException
    {
        Request request = buildGetRequest(url, tag);
        Response response = okHttpClient.newCall(request).execute();
        return response;
    }

    /**
     * 同步Get请求（返回String）
     *
     * @param url
     * @param tag
     * @return
     * @throws IOException
     */
    private String _getSyncString(String url, Object tag) throws IOException
    {
        Response response = _getSync(url, tag);
        return response.body().string();
    }

    /**
     * 异步Get请求
     *
     * @param url
     * @param tag
     * @throws IOException
     */
    private void _getAsync(String url, ResultCallback callback, Object tag)
    {
        Request request = buildGetRequest(url, tag);
        enqueue(request, callback);
    }

    /**
     * 异步post（key-value）请求
     *
     * @param url
     * @param params
     * @param callback
     * @param tag
     */
    private void _postAsync(String url, Map<String, String> params, ResultCallback callback, Object tag)
    {
        RequestBody requestBody = buildPostRequestBody(params);
        Request request = buildPostRequest(url, requestBody, tag);
        enqueue(request, callback);
    }


    /**
     * 自定义方法enqueue，异步请求
     *
     * @param request
     * @param callback 自定义callback，内部使用handler传递消息
     */
    private void enqueue(final Request request, final ResultCallback callback)
    {
        okHttpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                sendFailedCallback(request, callback, e);
            }

            @Override
            public void onResponse(Call call, Response response)
            {
                if (!response.isSuccessful())
                {
                    sendFailedCallback(response.request(), callback, new IOException("Unexpected code " + response));
                    return;
                }
                String body = null;
                try
                {
                    body = response.body().string();
                    sendSuccessCallback(body, callback);
                }
                catch (IOException e)
                {
                    sendFailedCallback(response.request(), callback, e);
                }

            }
        });
    }

    /**
     * 失败回调
     *
     * @param request
     * @param callback
     * @param e
     */
    private void sendFailedCallback(Request request, ResultCallback callback, IOException e)
    {
        delivery.post(new FailedRunnable(request, callback, e));
    }

    /**
     * 成功回调
     *
     * @param body
     * @param callback
     */
    private void sendSuccessCallback(String body, ResultCallback callback)
    {
        delivery.post(new SuccessRunnable(body, callback));
    }

    /**
     * 自定义callback
     */
    public static abstract class ResultCallback
    {
        public abstract void onError(Request request, Exception e);

        public abstract void onSuccess(String body);
    }


    ///////////////////////////////////////////////////////////////////////////
    // 对外公布的方法
    ///////////////////////////////////////////////////////////////////////////

    public static String getSyncString(Context context, String url, Object tag) throws IOException
    {
        return getInstance(context)._getSyncString(url, tag);
    }

    public static void getAsync(Context context, String url, Object tag, ResultCallback callback)
    {
        getInstance(context)._getAsync(url, callback, tag);
    }

    public static void postAsync(Context context, String url, Object tag, Map<String, String> params, ResultCallback callback)
    {
        getInstance(context)._postAsync(url, params, callback, tag);
    }


    /**
     * 取消所有请求
     */
    public static void cancelAllCall()
    {
        okHttpClient.dispatcher().cancelAll();
    }

    /**
     * 根据tag标识取消请求
     *
     * @param tag
     */
    public static void cancelTag(Object tag)
    {
        for (Call call : okHttpClient.dispatcher().queuedCalls())
        {
            if (tag.equals(call.request().tag()))
            {
                call.cancel();
            }
        }
        for (Call call : okHttpClient.dispatcher().runningCalls())
        {
            if (tag.equals(call.request().tag()))
            {
                call.cancel();
            }
        }
    }

}
