package com.example.fdaj.gradetracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class view_assignment_details extends AppCompatActivity {

    private TextView assignment_header;
    private TextView points;
    private TextView dueDate;
    private Button deleteAssignment;
    private String assignmentName;
    private String courseName;
    private String subjectName;
    private DBHelper db;

    public void init() {
        points = (TextView)findViewById(R.id.points_view);
        dueDate = (TextView)findViewById(R.id.due_date_view);

        //get the data about the assignment from the database
        db = new DBHelper(this);
        String assignmentData = db.getAssignmentDetails(assignmentName, subjectName, courseName);
        String[] results = assignmentData.split("\n");

        StringBuilder date = new StringBuilder();
        date.append(results[0]);
        date.append("/");
        date.append(results[1]);
        date.append("/");
        date.append(results[2]);

        StringBuilder point = new StringBuilder();
        point.append(results[3]);
        point.append("/");
        point.append(results[4]);

        points.setText(point.toString());
        dueDate.setText(date.toString());


        deleteAssignment = (Button) findViewById(R.id.delete_assignment_button);
        deleteAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(view_assignment_details.this);
                aBuilder.setMessage("Are you sure you want to delete this assignment?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(view_assignment_details.this, db.deleteAssignment(assignmentName), Toast.LENGTH_LONG).show();

                                Intent toHome = new Intent(view_assignment_details.this, home_activity.class);

                                startActivity(toHome);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               dialog.cancel();
                            }
                        });
                AlertDialog alert = aBuilder.create();
                alert.setTitle("DELETE ASSIGNMENT");
                alert.show();
            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assignment_details);

        //get and set the assignment name
        assignmentName = getIntent().getStringExtra("assignmentName");
        courseName = getIntent().getStringExtra("courseName");
        subjectName = getIntent().getStringExtra("subjectName");
        assignment_header = (TextView)findViewById(R.id.assignment_header);
        assignment_header.setText(assignmentName);


        init();
    }
}
