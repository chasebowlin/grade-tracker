package com.example.fdaj.gradetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class home_activity extends AppCompatActivity {
    //create a connection with the database
    DBHelper db;

    //add course button
    public Button addClassButton;

    public ListView courseView;
    public ListView courseGradeView;


    public boolean isRightListEnabled = true;
    public boolean isLeftListEnabled = true;



    //initializes the data
    public void init() {
        addClassButton = (Button)findViewById(R.id.add_course_button);
        addClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toAddCourseActivity = new Intent(home_activity.this, add_course_activity.class);

                startActivity(toAddCourseActivity);
            }
        });


        //set up the list view for the courses
        courseView = (ListView)findViewById(R.id.course_list);
        //get the data and put it into the array
        String temp = db.getAllCourses();
        String[] courses = temp.split("\n");

        if(courses[0] != "") {
            //set up the adaptor
            ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, courses);
            //link the adapter with the listview
            courseView.setAdapter(adapter);
        }
        courseView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        //set up the list view for the grades
        courseGradeView = (ListView)findViewById(R.id.course_grade_list);
        //get the data and put it into an array
        ArrayList<Double> results = db.getCourseGrades();
        if(results.get(0) != 359.00) {
            ArrayList<String> courseGrades = new ArrayList<String>();
            for (Double points : results) {
                Double test = Math.round(points * 100.00 * 100.00) / 100.00;
                courseGrades.add(Double.toString(test) + "%");
            }
            //set up the adaptor
            ListAdapter adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, courseGrades);
            //link the adapter with the list view
            courseGradeView.setAdapter(adapter1);
        }
        courseGradeView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        //set up the list views so that they scroll together
        courseView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                    courseGradeView.setSelectionFromTop(firstVisibleItem, c.getTop());
                }
            }
        });
        courseGradeView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                    courseView.setSelectionFromTop(firstVisibleItem, c.getTop());
                }
            }
        });


        courseView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedCourse = courseView.getItemAtPosition(position).toString();

                Intent toCoursesSubjects = new Intent(home_activity.this, view_courses_subjects.class);
                toCoursesSubjects.putExtra("courseName", clickedCourse);

                startActivity(toCoursesSubjects);
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activity);


        //connect to the db
        db = new DBHelper(this);


        //get all of the courses
        String results = db.getAllCourses();

        //split up the string into the seperate courses
        String[] courses = results.split("\n");



        //db.deleteDatabase();
        init();
    }
}
