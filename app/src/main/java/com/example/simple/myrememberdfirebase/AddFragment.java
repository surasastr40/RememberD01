package com.example.simple.myrememberdfirebase;


import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.simple.myrememberdfirebase.Model.MyResponse;
import com.example.simple.myrememberdfirebase.Model.SetNotification;
import com.example.simple.myrememberdfirebase.Model.Sender;
import com.example.simple.myrememberdfirebase.Remote.APIService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFragment extends Fragment {
    private EditText data_date;
    private EditText data_time;
    private EditText data_place;
    private EditText data_activity;

    private TextInputLayout date;

    FirebaseFirestore db;
    Map<String, Object> user;
    TimePickerDialog timePickerDialog;
    Calendar calendar = Calendar.getInstance();
    String name_db = "db_remmemberD";
    APIService mService;

    private static final String TAG = "MainTest";

    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Common.currentToken = FirebaseInstanceId.getInstance().getToken();
        mService = Common.getFCMClient();

        Log.d("MyToken Me", Common.currentToken);

        db = FirebaseFirestore.getInstance();
        user = new HashMap<>();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        data_date = view.findViewById(R.id.data_date);
        data_time = view.findViewById(R.id.data_time);
        data_place = view.findViewById(R.id.data_place);
        data_activity = view.findViewById(R.id.data_activity);

        date = view.findViewById(R.id.date);

        Button btn_submit = view.findViewById(R.id.btn_submit);



        data_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker datePicker = new DatePicker();
                if (getFragmentManager() != null) {
                    datePicker.show(getFragmentManager(), "DatePicker");
                }
            }
        });

        data_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_TimePicker();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (ValidateDate() && ValidateTime() && ValidatePlace() && ValidateActivity()) {
                    String cdate = data_date.getText().toString();
                    String ctime = data_time.getText().toString();
                    String cplace = data_place.getText().toString();
                    String cactivity = data_activity.getText().toString();

//                    String setTitle = " กิจกรรมของคุณ : วันที่ " + cdate + " เวลา " + ctime;

                    send_data_to_firebase(cdate, ctime, cactivity, cplace);

                    String[] data_cdate = cdate.split("/");
                    String[] data_ctime = ctime.split(":");
                    String set_month = "";
                    String time_hr = "";
                    String time_minu = "";

                    String day = data_cdate[0];
                    int month = Integer.parseInt(data_cdate[1]);
                    if(month <= 9){
                         set_month = "0" + month;
                    }else{
                        set_month += month ;
                    }
                    String year = data_cdate[2];

                    int hr_time = Integer.parseInt(data_ctime[0]);
                    int minu_time = Integer.parseInt(data_ctime[1]);

                    if(hr_time <= 9){
                        time_hr = "0" + hr_time;
                    }else {
                        time_hr += hr_time;
                    }

                    if(minu_time <= 9){
                        time_minu = "0" + minu_time;
                    }else {
                        time_minu += minu_time;
                    }

                    String set_time = time_hr + ":" + time_minu;

                    MySQLConnect bg = new MySQLConnect(getActivity());
                    bg.execute(cactivity,cplace,Common.currentToken,year,set_month,day,set_time);


//                    SetNotification notification = new SetNotification(cactivity, setTitle);

//                    final Sender sender = new Sender(notification, Common.currentToken);
//                    mService.sendNotification(sender)
//                            .enqueue(new Callback<MyResponse>() {
//                                @Override
//                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
//
//                                }
//
//                                @Override
//                                public void onFailure(Call<MyResponse> call, Throwable t) {
//
//                                }
//                            });
//                }
            }
        });

        return view;
    }

    private void set_TimePicker() {
        int Hour = calendar.get(Calendar.HOUR_OF_DAY);
        int Minute = calendar.get(Calendar.MINUTE);
        timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                data_time.setText(hour + ":" + minute);
            }
        }, Hour, Minute, DateFormat.is24HourFormat(getActivity()));
        timePickerDialog.show();
    }//set_TimePicker



    private void send_data_to_firebase(String cdate, String ctime, String cactivity, String cplace) {

        String strDate = (String) DateFormat.format("yyyy-MM-dd hh:mm", new Date());

        user.put("rmmb_date", cdate);
        user.put("rmmb_time", ctime);
        user.put("rmmb_place", cplace);
        user.put("rmmb_activity", cactivity);
        user.put("rmmb_create_time", strDate);

        db.collection(name_db)
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(), "บันทึกสำเร็จ :)", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(getActivity(), MainActivity.class);
//                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MYTest", "Error adding document", e);
                    }
                });

    }//send_data_to_firebase -> insert_data


}
