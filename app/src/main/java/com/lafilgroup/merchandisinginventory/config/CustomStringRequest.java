package com.lafilgroup.merchandisinginventory.config;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ronald Remond Salas on 3/7/2018.
 */

public class CustomStringRequest
{
    public static StringRequest stringRequest;
    public static RequestQueue requestQueue;
    Context context;
    public CustomStringRequest(Context context)
    {
        this.context = context;
    }

    public  void qryValue(final String qry, final String url, final dataCallback callback)
    {
        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                callback.onSuccess(response.toString());
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                callback.onError(error);
            }
        })
        {
            protected Map<String,String> getParams()
            {
                Map<String, String> params =new HashMap<>();
                params.put("qry",qry );
                return params;
            }
        };
        AppContoller.getmInstance(context).addRequestQue(stringRequest);
//        requestQueue= Volley.newRequestQueue(context);
//        requestQueue.add(stringRequest);
    }

    public interface dataCallback
    {
        void onSuccess(String response);
        void onError(VolleyError error);
    }
}
