package com.example.yensontam.mirrortest;

import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    private FloatingActionButton dobPicker;
    private TextView user;
    private EditText password;
    private TextView ageLabel;
    private TextView heightLabel;
    private TextView age;
    private TextView height;

    private DatePickerDialog.OnDateSetListener dobPickerOnDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dobPicker = (FloatingActionButton) findViewById(R.id.profileFABDOB);
        user = (TextView) findViewById(R.id.profileTextViewUserName);
        password = (EditText) findViewById(R.id.profileEditTextPassword);
        ageLabel = (TextView) findViewById(R.id.profileTextViewAgeLabel);
        age = (TextView) findViewById(R.id.profileTextViewAge);
        heightLabel = (TextView) findViewById(R.id.profileTextViewHeightLabel);

        dobPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // should initialized with dob stored locally (in sharedpreferences?)
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(ProfileActivity.this,
                        dobPickerOnDateSetListener,
                        year,
                        month,
                        day);
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.show();
            }
        });
        dobPickerOnDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                age.setText(month+"/"+day+"/"+year);
            }
        };



    }
}
