package com.example.simple.myrememberdfirebase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MySQLConnect extends AsyncTask <String, Void,String> {

    AlertDialog dialog;
    Context context;
    public Boolean login = false;

    public MySQLConnect(Context context)
    {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("Login Status");
    }
    @Override
    protected void onPostExecute(String s) {
        dialog.setMessage(s);
        dialog.show();
        if(s.contains("login successful"))
        {
//            Intent intent_name = new Intent();
//            intent_name.setClass(context.getApplicationContext(),Main2Activity.class);
//            context.startActivity(intent_name);
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        String result = "";
        String activity = strings[0];
        String place = strings[1];
        String token = strings[2];
        String year = strings[3];
        String month = strings[4];
        String day = strings[5];
        String time = strings[6];

        String connstr = "http://192.168.1.100/Android_Query/query_insert.php";

        try {
            URL url = new URL(connstr);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoInput(true);
            http.setDoOutput(true);


            OutputStream ops = http.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
            String data = URLEncoder.encode("activity","UTF-8")+"="+URLEncoder.encode(activity,"UTF-8")
                    +"&&"+URLEncoder.encode("place","UTF-8")+"="+URLEncoder.encode(place,"UTF-8")
                    +"&&"+URLEncoder.encode("token","UTF-8")+"="+URLEncoder.encode(token,"UTF-8")
                    +"&&"+URLEncoder.encode("year","UTF-8")+"="+URLEncoder.encode(year,"UTF-8")
                    +"&&"+URLEncoder.encode("month","UTF-8")+"="+URLEncoder.encode(month,"UTF-8")
                    +"&&"+URLEncoder.encode("day","UTF-8")+"="+URLEncoder.encode(day,"UTF-8")
                    +"&&"+URLEncoder.encode("time","UTF-8")+"="+URLEncoder.encode(time,"UTF-8");

            writer.write(data);
            writer.flush();
            writer.close();
            ops.close();

            InputStream ips = http.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(ips,"ISO-8859-1"));
            String line ="";
            while ((line = reader.readLine()) != null)
            {
                result += line;
            }
            reader.close();
            ips.close();
            http.disconnect();
            Log.d("Myresult", result);
            return result;

        } catch (MalformedURLException e) {
            result = e.getMessage();
        } catch (IOException e) {
            result = e.getMessage();
        }


        return result;
    }

}//MySQLConnect
