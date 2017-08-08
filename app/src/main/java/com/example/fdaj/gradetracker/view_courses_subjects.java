package com.example.fdaj.gradetracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class view_courses_subjects extends AppCompatActivity {
    private TextView header;
    private ListView subjectList;
    private ListView subjectGradeList;
    private Button deleteCourseButton;
    private Button addNewSubButton;

    private DBHelper db;

    //connect to the database
    //search through the database for the subjects based
    //on the course that was clicked on
    //then display those courses

    //get the name of the course that was clicked on
    public String courseName;

    public boolean isRightListEnabled = true;
    public boolean isLeftListEnabled = true;

    public void init() {


        subjectList = (ListView)findViewById(R.id.subjects_view);
        String results = db.getSubjects(courseName);
        String[] subjects = results.split("\n");
        if(subjects[0] != "") {
            //set up the adapter
            ListAdapter subjectAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, subjects);
            //link the adapter
            subjectList.setAdapter(subjectAdapter);
        }
        subjectList.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        //set up the grade view
        subjectGradeList = (ListView)findViewById(R.id.subjects_grade_view);
        if(subjects[0] != "") {

            ArrayList<Double> resulted = db.getSubjectGrades(courseName);
            ArrayList<String> subjectGrades = new ArrayList<String>();
            for (Double points : resulted) {
                Double temp = Math.round(points * 100.00 * 100.00) / 100.00;
                subjectGrades.add(Double.toString(temp) + "%");
            }
            //set up the adapter
            ListAdapter gradeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, subjectGrades);
            //link the adapter
            subjectGradeList.setAdapter(gradeAdapter);
        }
        subjectGradeList.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        //set up the listviews so that they scroll at the same time
        subjectList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //onScroll will be called and there will be an infinite loop
                //stop the loop with an if statement
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    isRightListEnabled = false;
                }
                else if (scrollState == SCROLL_STATE_IDLE) {
                    isRightListEnabled = true;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                View c = view.getChildAt(0);
                if (c != null && isLeftListEnabled) {
                    subjectGradeList.setSelectionFromTop(firstVisibleItem, c.getTop());
                }
            }
        });

        subjectGradeList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    isLeftListEnabled = false;
                }
                else if (scrollState == SCROLL_STATE_IDLE) {
                    isLeftListEnabled = true;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                View c = view.getChildAt(0);
                if(c != null && isRightListEnabled) {
                    subjectList.setSelectionFromTop(firstVisibleItem, c.getTop());
                }
            }
        });

        //add the onclick listener for the assignments
        subjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedSubject = subjectList.getItemAtPosition(position).toString();

                Intent toSubAssignments = new Intent(view_courses_subjects.this, view_subjects_assignments.class);
                toSubAssignments.putExtra("subjectName", clickedSubject);
                toSubAssignments.putExtra("courseName", courseName);

                startActivity(toSubAssignments);
            }
        });


        //set the onlick listener for the add subject button
        addNewSubButton = (Button)findViewById(R.id.add_new_subject_button);
        addNewSubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = db.isWeighted(courseName);
                if(check == false) {
                    Intent toAddU = new Intent(view_courses_subjects.this, add_subjects_activity.class);
                    toAddU.putExtra("courseName", courseName);
                    startActivity(toAddU);
                }
                else if (check == true) {
                    Intent toAddW = new Intent(view_courses_subjects.this, add_weighted_subjects_activity.class);
                    toAddW.putExtra("courseName", courseName);
                    startActivity(toAddW);
                }
            }
        });

        //set the onclick listener for the delete course button
        deleteCourseButton = (Button)findViewById(R.id.delete_course_button);
        deleteCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when the button is clicked and alert dialog will pop up
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(view_courses_subjects.this);
                aBuilder.setMessage("Are you sure you want to delete this course?")
                        .setCancelable(false)
                        //if yes is chosen, then the course will be deleted and
                        //it will return to the home screen
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(view_courses_subjects.this, db.deleteCourse(courseName).toString(), Toast.LENGTH_LONG).show();
                                Intent toHome = new Intent(view_courses_subjects.this, home_activity.class);
                                startActivity(toHome);
                            }
                        })
                        //if no is chosen, then nothing happens
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = aBuilder.create();
                alert.setTitle("delete this course?");
                alert.show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_courses_subjects);

        //set the header for the course name
        courseName = getIntent().getStringExtra("courseName");
        header = (TextView)findViewById(R.id.course_title);
        header.setText(courseName);


        db = new DBHelper(this);


        init();
    }
}
