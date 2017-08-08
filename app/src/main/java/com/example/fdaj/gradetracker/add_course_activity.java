package com.example.fdaj.gradetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class add_course_activity extends AppCompatActivity {
    //get the db
    DBHelper db;

    //next button
    public Button toAddSubject;

    //check box
    public CheckBox cb;

    //get course name text
    public EditText courseName;


    public void init() {
        cb = (CheckBox)findViewById(R.id.is_weighted);
        toAddSubject = (Button)findViewById(R.id.to_add_subjects);
        toAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the data from the textfeild
                courseName = (EditText)findViewById(R.id.new_course_name);

                //check to make sure that the user entered in a course name
                if(courseName.getText().length() == 0) {
                    Toast.makeText(add_course_activity.this, "no course was entered", Toast.LENGTH_SHORT).show();
                }
                else {

                    int test = db.checkCourse(courseName.getText().toString());
                    if(test != 0) {
                        Toast.makeText(add_course_activity.this, "course already exist, please enter a unique course", Toast.LENGTH_LONG).show();
                    }
                    else {
                        //check whether or not the checkbox is clicked
                        if (cb.isChecked()) {
                            db.addCourse(courseName.getText().toString(), true);
                            Toast.makeText(add_course_activity.this, "weighted", Toast.LENGTH_SHORT).show();

                            Intent toAddSubjectActivity = new Intent(add_course_activity.this, add_weighted_subjects_activity.class);

                            //PAss IN THE COURSE NAME
                            toAddSubjectActivity.putExtra("courseName", courseName.getText().toString());
                            startActivity(toAddSubjectActivity);
                        } else {
                            db.addCourse(courseName.getText().toString(), false);
                            Toast.makeText(add_course_activity.this, "unweighted", Toast.LENGTH_SHORT).show();

                            Intent toAddSubjectActivity = new Intent(add_course_activity.this, add_subjects_activity.class);

                            //pass in the course name
                            toAddSubjectActivity.putExtra("courseName", courseName.getText().toString());
                            startActivity(toAddSubjectActivity);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course_activity);
        db = new DBHelper(this);
        init();
    }
}
