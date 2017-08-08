package com.example.fdaj.gradetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class add_subjects_activity extends AppCompatActivity {
    private String courseName;
    private DBHelper db;
    private EditText subject1;
    private EditText subject2;
    private EditText subject3;
    private EditText subject4;
    private EditText subject5;
    private EditText subject6;
    private EditText subject7;
    private Button finishButton;


    public void init() {
        subject1 = (EditText)findViewById(R.id.new_subject1);
        subject2 = (EditText)findViewById(R.id.new_subject2);
        subject3 = (EditText)findViewById(R.id.new_subject3);
        subject4 = (EditText)findViewById(R.id.new_subject4);
        subject5 = (EditText)findViewById(R.id.new_subject5);
        subject6 = (EditText)findViewById(R.id.new_subject6);
        subject7 = (EditText)findViewById(R.id.new_subject7);
        finishButton = (Button)findViewById(R.id.finish_new_course_button);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(subject1.getText().length() == 0 &&
                        subject2.getText().length() == 0 &&
                        subject3.getText().length() == 0 &&
                        subject4.getText().length() == 0 &&
                        subject5.getText().length() == 0 &&
                        subject6.getText().length() == 0 &&
                        subject7.getText().length() == 0) {
                    Toast.makeText(add_subjects_activity.this, "Please enter in atleast one subject", Toast.LENGTH_LONG).show();
                }
                else {
                    //check to see if the editTexts are empty
                    //if not, then add in the new data to the database
                    if (subject1.getText().length() != 0) {
                        db.addSubject(subject1.getText().toString(), 1.00, courseName);
                    }
                    if (subject2.getText().length() != 0) {
                        db.addSubject(subject2.getText().toString(), 1.00, courseName);
                    }
                    if (subject3.getText().length() != 0) {
                        db.addSubject(subject3.getText().toString(), 1.00, courseName);
                    }
                    if (subject4.getText().length() != 0) {
                        db.addSubject(subject4.getText().toString(), 1.00, courseName);
                    }
                    if (subject5.getText().length() != 0) {
                        db.addSubject(subject5.getText().toString(), 1.00, courseName);
                    }
                    if (subject6.getText().length() != 0) {
                        db.addSubject(subject6.getText().toString(), 1.00, courseName);
                    }
                    if (subject7.getText().length() != 0) {
                        db.addSubject(subject7.getText().toString(), 1.00, courseName);
                    }

                    Intent toHomeActivity = new Intent(add_subjects_activity.this, home_activity.class);

                    startActivity(toHomeActivity);
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subjects_activity);

        courseName = getIntent().getStringExtra("courseName");
        db = new DBHelper(this);
        init();
    }
}
