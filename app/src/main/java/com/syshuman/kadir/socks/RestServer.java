package com.syshuman.kadir.socks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kadir on 2016-12-08.
 */

public class RestServer {



    private String baseUrl  = "http://hcapi.free-estimation.com/";
    private Context context;

    public RestServer() {
    }

    public void login(final Context context, final String uname, final String upass) {

        String url = this.baseUrl + "api/v1/users/login";
        this.context = context;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String statusCode = json.getString("status_code");
                            String message = json.getString("message");
                            JSONObject data = json.getJSONObject("data");
                            if(statusCode.equals("200 OK")) {
                                Intent intent = new Intent(context, DeviceActivity.class);
                                saveUser(data);
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Please double check username and password", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("um_email", uname);
                params.put("um_upass", upass);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }

    public void saveUser(JSONObject data) {
        try {
            JSONObject userm = data.getJSONObject("userm");
            Integer um_no = userm.getInt("um_no");
            Integer cm_no = userm.getInt("cm_no");
            String um_email = userm.getString("um_email");
            String um_uname = userm.getString("um_uname");
            String um_token = userm.getString("um_token");
            SharedPreferences prefs = this.context.getSharedPreferences("com.gedenek.haircolor", context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("um_no", um_no);
            editor.putInt("cm_no", cm_no);
            editor.putString("um_email", um_email);
            editor.putString("um_uname", um_uname);
            editor.putString("um_token", um_token);
            editor.apply();

        } catch(JSONException e) {


            e.printStackTrace();
        }


    }

    public void register(final Context context, final String uname, final String upass, final String fname, final String lname, final String devId, final int tz) {


        String url   = baseUrl + "api/v1/users/register"; // ?um_email=" + uname + "&um_upass=" + upass;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String statusCode = json.getString("status_code");
                            String message = json.getString("message");
                            if(message.equals("Success")) {
                                Intent intent = new Intent(context, DeviceActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Please double check username and password", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("um_uname", uname);
                params.put("um_upass", upass);
                params.put("um_fname", fname);
                params.put("um_lname", lname);
                params.put("um_devid", devId);
                params.put("um_devtype", "android");
                params.put("um_timezone", String.valueOf(tz));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("um_token", "");
                //params.put("upass", upass);
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }

    public void getNames(final Context context,  final Spinner spColor) {
        String url   = baseUrl + "api/v1/users/getnames";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String statusCode = json.getString("status_code");
                            String message = json.getString("message");
                            JSONArray jsonArray = json.getJSONArray("data");

                            if(message.equals("Success")) {
                                Toast.makeText(context, "Color Obtained from server", Toast.LENGTH_LONG).show();

                                List<String> label = new ArrayList<String>();
                                if (jsonArray != null) {
                                    for (int i=0;i<jsonArray.length();i++){
                                        JSONObject inner = new JSONObject(jsonArray.get(i).toString() );
                                        String names = inner.getString("lc_code");
                                        label.add(names);
                                    }
                                }

                                ArrayAdapter<String> rtAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, label);
                                rtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spColor.setAdapter(rtAdapter);


                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Please double check service at gelcolor", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("um_token", "298347decdd47d3ea70361c3acef225a");
                //params.put("upass", upass);
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }




    public void getColor(final Context context, final TextView txtcolor, final String company, final String readType, final String Red, final String Gre, final String Blu, final String Cle, final String Tem, final String Lum ) {

        String url   = baseUrl + "api/v1/users/getcolor";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String statusCode = json.getString("status_code");
                            String message = json.getString("message");
                            JSONObject data = new JSONObject(json.getString("data"));
                            String result = data.getString("color");

                            if(message.equals("Success")) {
                                Toast.makeText(context, "Color Obtained from server", Toast.LENGTH_LONG).show();
                                txtcolor.setText(result.toString());
                            } else {

                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Please double check service at gelcolor", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("company", company);
                params.put("readType", readType);
                params.put("Red", Red);
                params.put("Gre", Gre);
                params.put("Blu", Blu);
                params.put("Cle", Cle);
                params.put("Tem", Tem);
                params.put("Lum", Lum);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("um_token", "298347decdd47d3ea70361c3acef225a");
                //params.put("upass", upass);
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }




    public void train(final Context context, String r, String g, String b, String c, String t, String l, String comp, String color) {
        final String t_r, t_g, t_b, t_c, t_t, t_l, t_comp, t_color;
        t_r = r;
        t_g = g;
        t_b = b;
        t_c = c;
        t_t = t;
        t_l = l;
        t_comp = comp;
        t_color = color;


        String url = "http://hcapi.free-estimation.com/api/v1/users/train";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String statusCode = json.getString("status_code");
                            String message = json.getString("message");
                            if(message.equals("Success")) {
                                Toast.makeText(context, "Training data saved at server...Please try other colors", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Please double check username and password", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("r", t_r);
                params.put("g", t_g);
                params.put("b", t_b);
                params.put("c", t_c);
                params.put("t", t_t);
                params.put("l", t_l);
                params.put("comp", t_comp);
                params.put("color", t_color);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("um_token", "6efcfbb2b37a61edd0fce33001aab6cc");
                //params.put("upass", upass);
                return params;
            }

        };

        RequestQueue _requestQueue = Volley.newRequestQueue(context);
        _requestQueue.add(stringRequest);
    }


}
