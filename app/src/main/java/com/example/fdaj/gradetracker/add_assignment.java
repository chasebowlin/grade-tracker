package com.example.fdaj.gradetracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class add_assignment extends AppCompatActivity {
    private EditText assignmentName;
    private EditText dueDate;
    private EditText pointsEarned;
    private EditText pointsPosible;
    private Button addAssignmentButton;
    private String subject;
    private DBHelper db;

    public void init() {
        //create a db helper
        db = new DBHelper(this);

        //set the onclick listener for the button
        addAssignmentButton = (Button)findViewById(R.id.add_assignment_button);
        addAssignmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //grab the information entered in by the user
                assignmentName = (EditText)findViewById(R.id.assignment_name);
                dueDate = (EditText)findViewById(R.id.assignment_date);
                pointsEarned = (EditText)findViewById(R.id.assignment_earned);
                pointsPosible = (EditText)findViewById(R.id.assignment_possible);

                //make sure that none of them are empty
                if(assignmentName.getText().length() == 0 || dueDate.getText().length() == 0 || pointsEarned.getText().length() == 0 || pointsPosible.getText().length() == 0) {
                    Toast.makeText(add_assignment.this, "one or more fields is empty. please enter in assignment information", Toast.LENGTH_LONG).show();
                }
                else {

                    //split up the day, month, and year to put into the database
                    String uDate = dueDate.getText().toString();

                    String[] date = uDate.split("/");

                    int day = Integer.parseInt(date[0]);
                    int month = Integer.parseInt(date[1]);
                    int year = Integer.parseInt(date[2]);

                    //convert the points earned and possible to doubles
                    double earned = Double.parseDouble(pointsEarned.getText().toString());
                    double possible = Double.parseDouble(pointsPosible.getText().toString());

                    String test = db.addAssignment(assignmentName.getText().toString(), day, month, year, earned, possible, subject);

                    Intent toHome = new Intent(add_assignment.this, home_activity.class);
                    startActivity(toHome);
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment);

        //grab the subject that was passed on
        subject = getIntent().getStringExtra("subjectName");

        init();
    }
}
