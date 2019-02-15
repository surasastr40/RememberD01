package com.example.simple.myrememberdfirebase;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.time.Month;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    private Button btn_submit;

    FirebaseFirestore db;
    Map<String, Object> user;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    Calendar calendar = Calendar.getInstance();
    String typeTime = "";
    String name_db = "db_remmemberD";
    APIService mService;

    private static final String TAG = "MainTest";

    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Common.currentToken = FirebaseInstanceId.getInstance().getToken();
        mService = Common.getFCMClient();

        Log.d("MyToken",Common.currentToken);

        db = FirebaseFirestore.getInstance();
        user = new HashMap<>();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        data_date = view.findViewById(R.id.data_date);
        data_time = view.findViewById(R.id.data_time);
        data_place = view.findViewById(R.id.data_place);
        data_activity = view.findViewById(R.id.data_activity);

        btn_submit = view.findViewById(R.id.btn_submit);

        show_data_from_firebase();

        data_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_DatePicker();
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

                String cdate = data_date.getText().toString();
                String ctime = data_time.getText().toString();
                String cplace = data_place.getText().toString();
                String cactivity = data_activity.getText().toString();

                String setTitle = " กิจกรรมของคุณ : วันที่ " + cdate + " เวลา " + ctime;
                String setBody = cactivity;

                send_data_to_firebase(cdate,ctime,cactivity,cplace);

                SetNotification notification = new SetNotification(setBody,setTitle);

                final Sender sender = new Sender(notification,Common.currentToken);
                mService.sendNotification(sender)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                Toast.makeText(getContext(),"Success",Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {

                            }
                        });


            }
        });

        return view;
    }

    private void set_DatePicker(){

        int Day = calendar.get(Calendar.DAY_OF_MONTH);
        int Month = calendar.get(Calendar.MONTH);
        int Year = calendar.get(Calendar.YEAR);
        datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getContext()), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                data_date.setText( day + "/" + (month+1) + "/" + (year+543) );
            }
        },Day,Month,Year);

        datePickerDialog.show();

    }//set_DatePicker

    private void set_TimePicker(){

        int Hour = calendar.get(Calendar.HOUR_OF_DAY);
        int Minute = calendar.get(Calendar.MINUTE);
        timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                if(hour >= 12){
                    typeTime = "PM";
                }else {
                    typeTime = "AM";
                }
                data_time.setText(hour + ":" + minute );
            }
        },Hour,Minute,false);
        timePickerDialog.show();

    }//set_TimePicker

    private void show_data_from_firebase(){

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        StringBuilder builder = new StringBuilder("ข้อความที่บันทึกไว้:\n\n");

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                builder.append("ลำดับ").append(document.getId() ).append(": ");
                                builder.append("\t").append(document.get("rmmb_activity") ).append("\n\n");

                            }
//                            text_view.setText( builder );
                        } else {
                            Log.i(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }//show_data_from_firebase -> show_data

    private void send_data_to_firebase(String cdate,String ctime,String cactivity,String cplace){

        String strDate = (String) DateFormat.format("yyyy-MM-dd hh:mm",new Date());

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
                        Log.d("MYTest", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MYTest", "Error adding document", e);
                    }
                });

    }//send_data_to_firebase -> insert_data

    private void update_to_firebase(String rmmb_id,String cdate,String ctime,String cactivity,String cplace,Integer csettime){
        String strDate = (String) DateFormat.format("yyyy-MM-dd hh:mm",new Date());

        DocumentReference noteRef2 = db.collection(name_db).document(rmmb_id);
        noteRef2.update("rmmb_date",cdate,"rmmb_time",ctime,
                "rmmb_place",cplace,"rmmb_activity",cactivity,
                "rmmb_settime",csettime,"rmmb_create_time",strDate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

    }//update_to_firebase -> update_data

    private void delete_id_firebase(String rmmb_id){

        DocumentReference noteRef = db.collection(name_db).document(rmmb_id);
        noteRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


            }
        });//delete

    }//delete_id_firebase -> delete_data

}
