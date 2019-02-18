package com.example.simple.myrememberdfirebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;
    Map<String, Object> user;
    private static final String connstr_query = "http://192.168.1.34/Android_Query/get_query.php";
    private static final String connstr_query_count = "http://192.168.1.34/Android_Query/get_query_count.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ข้อมูลของการแจ้งเตือนในแต่ละปี แต่ละเดือน
        StringRequest stringRequest = new StringRequest(Request.Method.POST, connstr_query,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i =0;i <array.length();i++){
                                JSONObject play = array.getJSONObject(i);

                                String data_date = play.getString("day")+"/"+play.getString("month")+"/"+play.getString("year");
                                String data_time = play.getString("time");

                                Log.d("minimal", data_date + " " + data_time);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("minimal", String.valueOf(error));
                        // Handle error
                    }
                }){

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> postMap = new HashMap<>();
                            postMap.put("year", "2562");
                            postMap.put("month", "02");
                            //..... Add as many key value pairs in the map as necessary for your request
                            return postMap;
                        }
                };

         Volley.newRequestQueue(this).add(stringRequest);

         //จำนวนของการแจ้งเตือน ตามปี และเดือน
        StringRequest stringRequestCount = new StringRequest(Request.Method.GET, connstr_query_count,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i =0;i <array.length();i++){
                                JSONObject play2 = array.getJSONObject(i);

                                String data_date = play2.getString("month")+"/"+play2.getString("year");
                                String count_notification = play2.getString("count_notification");

                                Log.d("asdasdasd",data_date + " " + count_notification);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("asdasdasd", String.valueOf(error));
                        // Handle error
                    }
                });

        Volley.newRequestQueue(this).add(stringRequestCount);


        db = FirebaseFirestore.getInstance();
        user = new HashMap<>();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("MyNotification","MyNotification",NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


        FloatingActionButton button = findViewById(R.id.btn_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.right_to_left,R.anim.left_to_right);
                transaction.replace(R.id.main, new AddFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        ArrayList<String> data = new ArrayList<>();
        data.add("January");
        data.add("February");
        data.add("March");
        data.add("April");
        data.add("May");
        data.add("June");
        data.add("July");
        data.add("August");
        data.add("September");
        data.add("October");
        data.add("November");
        data.add("December");

        Adapter1 adapter = new Adapter1(this, data, new Adapter1.OnItemClickListener() {
            @Override
            public void onItemClick(String data,int position) {
                Log.d("position", String.valueOf(position));
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                BlankFragment fragment = new BlankFragment();
                Bundle bundle = new Bundle();
                bundle.putString("month", data);
                fragment.setArguments(bundle);
                transaction.setCustomAnimations(R.anim.down_to_up,R.anim.up_to_down);
                transaction.replace(R.id.main, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        RecyclerView recyclerView = findViewById(R.id.rcv1);
        recyclerView.setAdapter(adapter);

        show_data_from_firebase();
    }//onCreate

    private void show_data_from_firebase() {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        StringBuilder builder = new StringBuilder("ข้อความที่บันทึกไว้:\n\n");

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                builder.append("ลำดับ").append(document.getId()).append(": ");
                                builder.append("\t").append(document.get("rmmb_activity")).append("\n\n");
                                Log.i("firebase", document.getId(), task.getException());
                            }
//                            text_view.setText( builder );
                        } else {
                            Log.i("firebase", "Error getting documents.", task.getException());
                        }
                    }
                });

    }//show_data_from_firebase -> show_data
}
