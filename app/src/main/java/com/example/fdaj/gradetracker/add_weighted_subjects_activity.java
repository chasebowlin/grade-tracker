package com.example.fdaj.gradetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class add_weighted_subjects_activity extends AppCompatActivity {
    private String courseName;
    private DBHelper db;
    private EditText sub1;
    private EditText sub2;
    private EditText sub3;
    private EditText sub4;
    private EditText sub5;
    private EditText sub6;
    private EditText sub7;
    private EditText weight1;
    private EditText weight2;
    private EditText weight3;
    private EditText weight4;
    private EditText weight5;
    private EditText weight6;
    private EditText weight7;
    private Button addSubButton;


    public void init() {
        //grab the course name
        courseName = getIntent().getStringExtra("courseName");

        //link up all of the edit text fields
        sub1 = (EditText)findViewById(R.id.new_w_subject1);
        sub2 = (EditText)findViewById(R.id.new_w_subject2);
        sub3 = (EditText)findViewById(R.id.new_w_subject3);
        sub4 = (EditText)findViewById(R.id.new_w_subject4);
        sub5 = (EditText)findViewById(R.id.new_w_subject5);
        sub6 = (EditText)findViewById(R.id.new_w_subject6);
        sub7 = (EditText)findViewById(R.id.new_w_subject7);
        weight1 = (EditText)findViewById(R.id.new_weight1);
        weight2 = (EditText)findViewById(R.id.new_weight2);
        weight3 = (EditText)findViewById(R.id.new_weight3);
        weight4 = (EditText)findViewById(R.id.new_weight4);
        weight5 = (EditText)findViewById(R.id.new_weight5);
        weight6 = (EditText)findViewById(R.id.new_weight6);
        weight7 = (EditText)findViewById(R.id.new_weight7);
        addSubButton = (Button)findViewById(R.id.finish_new_w_course_button);

        //set the on click button
        addSubButton = (Button)findViewById(R.id.finish_new_w_course_button);
        addSubButton.setOnClickListener(new View.OnClickListener() {
            //when the button is pressed, grab the data from all of the edit text
            @Override
            public void onClick(View v) {
                //check to make sure that all the weights add up to 1.0
                Double total = 0.0;
                Double w1 = 0.0;
                Double w2 = 0.0;
                Double w3 = 0.0;
                Double w4 = 0.0;
                Double w5 = 0.0;
                Double w6 = 0.0;
                Double w7 = 0.0;

                if(weight1.getText().length() != 0) {
                    w1 = Math.round(Double.parseDouble(weight1.getText().toString()) * 100.00) / 100.00;
                    total = total + w1;
                }
                if(weight2.getText().length() != 0) {
                    w2 = Math.round(Double.parseDouble(weight2.getText().toString()) * 100.00) / 100.00;
                    total = total + w2;
                }
                if(weight3.getText().length() != 0) {
                    w3 = Math.round(Double.parseDouble(weight3.getText().toString()) * 100.00) / 100.00;
                    total = total + w3;
                }
                if(weight4.getText().length() != 0) {
                    w4 = Math.round(Double.parseDouble(weight4.getText().toString()) * 100.00) / 100.00;
                    total = total + w4;
                }
                if(weight5.getText().length() != 0) {
                    w5 = Math.round(Double.parseDouble(weight5.getText().toString()) * 100.00) / 100.00;
                    total = total + w5;
                }
                if(weight6.getText().length() != 0) {
                    w6 = Math.round(Double.parseDouble(weight6.getText().toString()) * 100.00) / 100.00;
                    total = total + w6;
                }
                if(weight7.getText().length() != 0) {
                    w7 = Math.round(Double.parseDouble(weight7.getText().toString()) * 100.00) / 100.00;
                    total = total + w7;
                }

                if(total != 1.0) {
                    Toast.makeText(add_weighted_subjects_activity.this, "please make sure that the weights add up to 1.0", Toast.LENGTH_LONG).show();
                }

                else {
                    if (sub1.getText().length() != 0 && weight1.getText().length() != 0) {
                        db.addSubject(sub1.getText().toString(), w1, courseName);
                    }
                    if (sub2.getText().length() != 0 && weight2.getText().length() != 0) {
                        db.addSubject(sub2.getText().toString(), w2, courseName);
                    }
                    if (sub3.getText().length() != 0 && weight3.getText().length() != 0) {
                        db.addSubject(sub3.getText().toString(), w3, courseName);
                    }
                    if (sub4.getText().length() != 0 && weight4.getText().length() != 0) {
                        db.addSubject(sub4.getText().toString(), w4, courseName);
                    }
                    if (sub5.getText().length() != 0 && weight5.getText().length() != 0) {
                        db.addSubject(sub5.getText().toString(), w5, courseName);
                    }
                    if (sub6.getText().length() != 0 && weight6.getText().length() != 0) {
                        db.addSubject(sub6.getText().toString(), w6, courseName);
                    }
                    if (sub7.getText().length() != 0 && weight7.getText().length() != 0) {
                        db.addSubject(sub7.getText().toString(), w7, courseName);
                    }

                    //send the user back to the home page
                    Intent toHomeActivity = new Intent(add_weighted_subjects_activity.this, home_activity.class);

                    startActivity(toHomeActivity);
                }
            }
        });



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weighted_subjects_activity);

        db = new DBHelper(this);
        init();
    }
}
