package com.lafilgroup.merchandisinginventory.config;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Ronald Remond Salas on 3/7/2018.
 */

public class AppContoller
{
    private static AppContoller mInstance;
    private RequestQueue requestQueue;
    private static Context mCtx;


    private AppContoller(Context context)
    {
        mCtx=context;
        requestQueue=getRequestQueue();
    }

    public RequestQueue getRequestQueue()
    {
        if(requestQueue==null)
        {
            requestQueue= Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized AppContoller getmInstance(Context context)
    {
        if (mInstance==null)
        {
            mInstance=new AppContoller(context);
        }
        return mInstance;
    }

    public<T> void addRequestQue(Request<T> request)
    {
        requestQueue.add(request);
    }
}
