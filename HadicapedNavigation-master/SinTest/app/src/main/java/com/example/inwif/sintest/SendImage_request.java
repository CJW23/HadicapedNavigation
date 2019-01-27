package com.example.inwif.sintest;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SendImage_request extends StringRequest {
    final static private String URL = "http://192.168.0.2/sendimage.php";
    private Map<String, String> parameters;

    public SendImage_request(String image, String name, Response.Listener<String> listener) {
        super(Method.POST, URL
                , listener, null);
        parameters = new HashMap<>();
        parameters.put("image", image);
        Log.d("sdsds","wwd");
        parameters.put("image_name", name);
    }
    @Override
    public Map<String,String> getParams()
    {
        Log.d("bbbbbbbbbbbbbbbbbbbbbb","bbbbbbbbbbbbbbbbbbbbb");
        return parameters;
    }
}
