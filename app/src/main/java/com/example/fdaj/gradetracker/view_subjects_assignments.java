package com.example.fdaj.gradetracker;

import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.Arrays;
import java.util.List;

public class view_subjects_assignments extends AppCompatActivity {
    private TextView header;
    private Button deleteButton;
    private Button addAssignmentButton;
    private ListView assignmentsView;
    private ListView gradeView;
    private String subject;
    private String course;
    private DBHelper db;

    public boolean isLeftListEnabled = true;
    public boolean isRightListEnabled = true;

    public void init() {
        //connect to the db
        db = new DBHelper(this);




        //set up the list view for the assignment names
        assignmentsView = (ListView) findViewById(R.id.assignment_names);
        //get the data from the database and then parse it into an array
        String result = db.getAssignments(subject, course);
        String[] assignments = result.split("\n");
        if(assignments[0] != "") {
            //set up an adaptor
            ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, assignments);
            //link the adapter with the list view
            assignmentsView.setAdapter(adapter);
        }
        assignmentsView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);



        //set up the list view for the assignment grades
        gradeView = (ListView) findViewById(R.id.assignment_grades);
        //get the data from the database
        if(assignments[0] != "") {
            ArrayList<Double> resulted = db.getAssignmentGrades(subject, course);
            ArrayList<String> assignmentGrades = new ArrayList<String>();
            for (Double points : resulted) {
                Double temp = Math.round(points * 100.00 * 100.00) / 100.00;
                assignmentGrades.add(Double.toString(temp) + "%");
            }
            //set up an adaptor
            ListAdapter adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, assignmentGrades);
            //link the adapter with the list view
            gradeView.setAdapter(adapter1);
        }
        gradeView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        //set up the list views so that they scroll together
        assignmentsView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                    gradeView.setSelectionFromTop(firstVisibleItem, c.getTop());
                }
            }
        });

        gradeView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                    assignmentsView.setSelectionFromTop(firstVisibleItem, c.getTop());
                }
            }
        });

        //add the onclick listener for the assignments
        assignmentsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedAssignment = assignmentsView.getItemAtPosition(position).toString();

                Intent toAssignmentDetails = new Intent(view_subjects_assignments.this, view_assignment_details.class);
                toAssignmentDetails.putExtra("assignmentName", clickedAssignment);
                toAssignmentDetails.putExtra("subjectName", subject);
                toAssignmentDetails.putExtra("courseName", course);

                startActivity(toAssignmentDetails);
            }
        });



        //set up the add assignments button
        addAssignmentButton = (Button)findViewById(R.id.add_sub_assignment_button);
        addAssignmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toAddAssignment = new Intent(view_subjects_assignments.this, add_assignment.class);
                toAddAssignment.putExtra("subjectName", subject);
                startActivity(toAddAssignment);
            }
        });

        //set up the delete subject button
        deleteButton = (Button)findViewById(R.id.delete_sub_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when the button is clicked an alert dialog will pop up
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(view_subjects_assignments.this);
                aBuilder.setMessage("Are you sure you want to delete this subject?")
                        .setCancelable(false)
                        //if yes is chosen, then the course will be deleted
                        //will return to the view course's subjects activity
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(view_subjects_assignments.this, db.deleteSubject(subject), Toast.LENGTH_LONG).show();

                                Intent toCoursesSubjects = new Intent(view_subjects_assignments.this, home_activity.class);
                                startActivity(toCoursesSubjects);
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
                alert.setTitle("delete this subject?");
                alert.show();
            }
        });






    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_subjects_assignments);

        //grab the subject from the other activity
        subject = getIntent().getStringExtra("subjectName");
        course = getIntent().getStringExtra("courseName");
        //set the textview header
        header = (TextView)findViewById(R.id.subjects_header);
        header.setText(subject);


        init();
    }
}
