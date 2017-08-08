package com.example.fdaj.gradetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Settings;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper{

    //name of the db file
    private static final String DB_NAME = "GradeLog.db";
    private Context context;


    //===========================================================================
    //constructor
    public DBHelper(Context c) {
        super(c, DB_NAME, null, 5);
        //onCreate will be called if the db file dne
        context = c;
        SQLiteDatabase db = getWritableDatabase();
    }
    //===========================================================================

    //===========================================================================
    //opens up a connection ith the database and adds in a new subject
    //takes in the database that it will be connected too
    //creates all of the tables in the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        //holds the queries to make the tables
        StringBuilder courses_table = new StringBuilder();
        StringBuilder subjects_table = new StringBuilder();
        StringBuilder assignments_table = new StringBuilder();



        //create the courses table

        courses_table.append("CREATE TABLE Courses ( ");
        courses_table.append("course_id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        courses_table.append("course_name TEXT, ");
        courses_table.append("weighted INTEGER ");
        courses_table.append(");");
        db.execSQL(courses_table.toString());


        subjects_table.append("CREATE TABLE Subjects ( ");
        subjects_table.append("subject_id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        subjects_table.append("subject_name TEXT, ");
        subjects_table.append("weight DECIMAL(3, 2), ");
        subjects_table.append("course_id INTEGER NOT NULL ");
        subjects_table.append(");");
        db.execSQL(subjects_table.toString());


        //create the assignments table
        assignments_table.append("CREATE TABLE Assignments ( ");
        assignments_table.append("assignment_id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        assignments_table.append("assignment_name TEXT, ");
        assignments_table.append("day INTEGER, ");
        assignments_table.append("month INTEGER, ");
        assignments_table.append("year INTEGER, ");
        assignments_table.append("points_earned DOUBLE, ");
        assignments_table.append("points_possible DOUBLE, ");
        assignments_table.append("subject_id INTEGER NOT NULL ");
        assignments_table.append(");");
        db.execSQL(assignments_table.toString());


    }
    //===========================================================================


    //===========================================================================
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //do nothing here there wont be an upgrade yet

        db.execSQL("DROP TABLE IF EXISTS Courses");
        db.execSQL("DROP TABLE IF EXISTS Subjects");
        db.execSQL("DROP TABLE IF EXISTS Assignments");
        onCreate(db);
    }
    //===========================================================================

    //===========================================================================
    public int checkCourse(String courseName) {
        int returnInt = 0;
        SQLiteDatabase db = getReadableDatabase();

        try {
            Cursor result = db.rawQuery("SELECT COUNT(*) FROM Courses WHERE Courses.course_name = '" + courseName + "'", null);
            if(result.moveToFirst()) {
                returnInt = result.getInt(0);
            }
        }
        catch(Exception e) {

        }
        return returnInt;
    }
    //===========================================================================

    //===========================================================================
    public boolean isWeighted(String course) {
        boolean temp = true;

        SQLiteDatabase db = getReadableDatabase();

        try {
            Cursor result = db.rawQuery("SELECT Courses.weighted AS weight " +
                                        "FROM Courses " +
                                        "WHERE Courses.course_name = '" + course + "'", null);

            if(result.moveToFirst()) {
                if(result.getInt(result.getColumnIndex("weight")) == 0) {
                    temp = false;
                }
                else if (result.getInt(result.getColumnIndex("weight")) == 1) {
                    temp = true;
                }
            }
            result.close();
            db.close();
        }
        catch(Exception e) {

        }
        return temp;
    }
    //===========================================================================


    //===========================================================================
    //opens up a connection with the database and adds in a new course
    //takes in the course name
    public String addCourse(String courseName, boolean weighted) {
        String returnS;
        ContentValues cv = new ContentValues();
        cv.put("course_name", courseName);

        if(weighted == true) {
            cv.put("weighted", 1);
        }
        else {
            cv.put("weighted", 0);
        }

        //get writable db
        SQLiteDatabase db = getWritableDatabase();
        db.insert("Courses", null, cv);
        db.close();

        returnS = "here";
        return  returnS;

    }
    //===========================================================================


    //===========================================================================
    //opens up a connection ith the database and adds in a new subject
    //takes in the subject name, the weight of the subject, and the course name
    //it is linked too
    public String addSubject(String subjectName, double weighted, String course_name) {
        int course_id = 0;
        StringBuilder add = new StringBuilder();

        //get a readable database
        SQLiteDatabase db = getReadableDatabase();

        try {
            //get the course id using cursor
            Cursor result = db.rawQuery("SELECT course_id AS _id FROM Courses WHERE course_name = '" + course_name + "'", null);

            if(result == null) {
                add.append("No such course exists");
            }
            else {
                result.moveToFirst();
                course_id = result.getInt(result.getColumnIndex("_id"));
            }

            result.close();


        }catch (Exception e) {
            add.append(e);
        }
        db.close();

        ContentValues cv = new ContentValues();
        cv.put("subject_name", subjectName);
        cv.put("weight", weighted);
        cv.put("course_id", course_id);

        db = getWritableDatabase();
        db.insert("Subjects", null, cv);


        db.close();
        return add.toString();
    }
    //===========================================================================

    //===========================================================================
    //opens up a connection ith the database and adds in a new subject
    //takes in the assignment name, the day & month & year it was due, the points
    //earned, the points possible, and the subject it is linked too
    public String addAssignment(String assignmentName, int day, int month, int year, double pointsEarned, double pointsPossible, String subjectName) {
        int subject_id = 0;
        String rString = "added";

        //get a readable database
        SQLiteDatabase db = getReadableDatabase();

        try {
            //get the subject id using the cursor
            Cursor result = db.rawQuery("SELECT subject_id " +
                    "FROM Subjects " +
                    "WHERE subject_name = '" + subjectName + "'", null);

            //go through the results
            while (result.moveToNext()) {
                subject_id = result.getInt(result.getColumnIndex("subject_id"));
                rString = Integer.toString(subject_id);
            }
            result.close();
        }catch (Exception e) {
            rString = e.toString();
        }
        db.close();

        ContentValues cv = new ContentValues();
        cv.put("assignment_name", assignmentName);
        cv.put("day", day);
        cv.put("month", month);
        cv.put("year", year);
        cv.put("points_earned", pointsEarned);
        cv.put("points_possible", pointsPossible);
        cv.put("subject_id", subject_id);

        db = getWritableDatabase();
        db.insert("Assignments", null, cv);
        db.close();

        return rString;
    }
    //===========================================================================






    //===========================================================================
    ////opens up a connection ith the database and adds in a new subject
    //goes through the Courses table and gets te course name of all the courses
    public String getAllCourses() {
        StringBuilder allCourses = new StringBuilder();

        //open up a readable db
        SQLiteDatabase db = getReadableDatabase();

        try {
            Cursor results = db.rawQuery("SELECT course_name FROM Courses", null);
            //go through the results
            while (results.moveToNext()) {
                allCourses.append(results.getString(results.getColumnIndex("course_name")));
                allCourses.append("\n");
            }
            results.close();
        }
        catch (Exception e) {
            allCourses.append(e);
        }


        db.close();

        //return all courses string
        return  allCourses.toString();
    }
    //===========================================================================


    //===========================================================================
    //opens up a connection ith the database and adds in a new subject
    //takes in the course the subjects are linked too
    //goes through and gets the name of all the subjects
    public String getSubjects(String course) {
        StringBuilder subjects = new StringBuilder();

        //open up a readable db
        SQLiteDatabase db = getReadableDatabase();

        try {
            //get the subjects where the course name is == the passed in
            //course name and the ids match up

            Cursor results = db.rawQuery("SELECT Subjects.subject_name AS sn " +
                                         "FROM Subjects, Courses " +
                                         "WHERE Courses.course_name = '" + course + "' " +
                                         "AND Courses.course_id = Subjects.course_id", null);

            //go through the results
            results.moveToFirst();
            //if the cursor is not empty
            if(results.getCount() != 0) {
                subjects.append(results.getString(results.getColumnIndex("sn")));
                subjects.append("\n");

                while (results.moveToNext()) {
                    subjects.append(results.getString(results.getColumnIndex("sn")));
                    subjects.append("\n");
                }
            }
            results.close();

        }catch (Exception e) {
            subjects.append(e);
        }

        db.close();

        return subjects.toString();
    }
    //===========================================================================

    //===========================================================================
    public String getAssignments(String subject, String course) {
        StringBuilder assignments = new StringBuilder();

        SQLiteDatabase db = getReadableDatabase();

        try {
            //get all the assignments where the subject name is == the
            //name passed in and the subject id's match up
            Cursor results = db.rawQuery("SELECT Assignments.assignment_name AS an " +
                                         "FROM Assignments, Subjects, Courses " +
                                         "WHERE Courses.course_name = '" + course + "' " +
                                         "AND Courses.course_id = Subjects.course_id " +
                                         "AND Subjects.subject_name = '" + subject + "' " +
                                         "AND Subjects.subject_id = Assignments.subject_id", null);
            //go through the results
            results.moveToFirst();

            //if the cursor is not empty
            if(results.getCount() != 0) {
                assignments.append(results.getString(results.getColumnIndex("an")));
                assignments.append("\n");

                while (results.moveToNext()) {
                    assignments.append(results.getString(results.getColumnIndex("an")));
                    assignments.append("\n");
                }
            }
            results.close();
        }
        catch(Exception e) {
            assignments.append(e.toString());
        }
        db.close();

        return assignments.toString();
    }
    //===========================================================================

    //===========================================================================
    public String getAssignmentDetails(String assignment, String subject, String course) {
        StringBuilder assignmentDetails = new StringBuilder();

        SQLiteDatabase db = getReadableDatabase();

        try {
            //get the assignment details based on the
            //name of the assignment clicked on
            Cursor results = db.rawQuery("SELECT Assignments.day AS day, Assignments.month AS month, Assignments.year AS year, Assignments.points_earned AS earned, Assignments.points_possible AS possible " +
                                         "FROM Assignments, Subjects, Courses " +
                                         "WHERE Courses.course_name = '" + course + "' " +
                                         "AND Courses.course_id = Subjects.course_id " +
                                         "AND Subjects.subject_name = '" + subject + "' " +
                                         "AND Subjects.subject_id = Assignments.subject_id " +
                                         "AND Assignments.assignment_name = '" + assignment + "'", null);

            //go through the results
            results.moveToFirst();

            //if the cursor is not empty
            if(results.getCount() != 0) {
                assignmentDetails.append(results.getString(results.getColumnIndex("day")));
                assignmentDetails.append("\n");
                assignmentDetails.append(results.getString(results.getColumnIndex("month")));
                assignmentDetails.append("\n");
                assignmentDetails.append(results.getString(results.getColumnIndex("year")));
                assignmentDetails.append("\n");
                assignmentDetails.append(results.getString(results.getColumnIndex("earned")));
                assignmentDetails.append("\n");
                assignmentDetails.append(results.getString(results.getColumnIndex("possible")));
            }
            results.close();
        }
        catch(Exception e) {
            assignmentDetails.append(e.toString());
        }
        db.close();

        return assignmentDetails.toString();
    }
    //===========================================================================





    //===========================================================================
    //opens up a connection ith the database and adds in a new subject
    //goes through all the different ASSIGNMENTS of SUBJECTS of each of the COURSES
    //returns the calculated grades for each course

    public  ArrayList<Double> getCourseGrades(){
        ArrayList<Double> coursesGrades = new ArrayList<Double>();
        ArrayList<String> courseNames = new ArrayList<String>();
        ArrayList<ArrayList<Double>> subjectsGrades = new ArrayList<ArrayList<Double>>();
        ArrayList<ArrayList<Double>> subjectsWeights = new ArrayList<ArrayList<Double>>();
        //connect to the database
        SQLiteDatabase db = getReadableDatabase();

        try {
            Cursor results = db.rawQuery("SELECT Courses.course_name AS name " +
                                         "FROM Courses ", null);

            //get the all of the course names
            results.moveToFirst();
            courseNames.add(results.getString(results.getColumnIndex("name")));
            while (results.moveToNext()) {
                courseNames.add(results.getString(results.getColumnIndex("name")));
            }
            results.close();


            //get all of the weights of all subjects for each course
            for(int i = 0; i < courseNames.size(); i++) {
                results = db.rawQuery("SELECT Subjects.weight AS weight " +
                        "FROM Subjects, Courses " +
                        "WHERE Courses.course_name = '" + courseNames.get(i) +"' " +
                        "AND Subjects.course_id = Courses.course_id", null);
                ArrayList<Double> temp = new ArrayList<Double>();

                results.moveToFirst();
                temp.add(results.getDouble(results.getColumnIndex("weight")));
                while(results.moveToNext()) {
                    temp.add(results.getDouble(results.getColumnIndex("weight")));
                }
                subjectsWeights.add(temp);
            }
            results.close();
            db.close();

            for(String course : courseNames) {
                subjectsGrades.add(getSubjectGrades(course));
            }

           for(int i = 0; i < subjectsGrades.size(); i++) {
               Double finalGradeU = 0.0;
               Double finalGradeW = 0.0;
               for(int j = 0; j < subjectsGrades.get(i).size(); j++) {
                   if(subjectsWeights.get(i).get(j) == 1.00) {
                       finalGradeU = finalGradeU + subjectsGrades.get(i).get(j);
                   }
                   else {
                       Double temp = subjectsGrades.get(i).get(j) * subjectsWeights.get(i).get(j);
                       finalGradeW = finalGradeW + temp;
                   }
               }
               if(finalGradeU != 0.0) {
                   coursesGrades.add(finalGradeU / subjectsGrades.get(i).size());
               }
               else {
                   coursesGrades.add(finalGradeW);
               }
           }

        }
        catch(Exception e) {
            coursesGrades.add(359.00);
        }

        return coursesGrades;
    }

    //===========================================================================

    //===========================================================================
    public ArrayList<Double> getSubjectGrades(String course) {
        ArrayList<Double> subjectGrades = new ArrayList<Double>();
        ArrayList<Double> assignment_grades;
        double subjectGrade = 0.0;
        //connect to the database
        SQLiteDatabase db = getReadableDatabase();


        try {
            Cursor results = db.rawQuery("SELECT Subjects.subject_name AS name " +
                                         "FROM Subjects, Courses " +
                                         "WHERE Courses.course_name = '" + course + "' " +
                                         "AND Subjects.course_id = Courses.course_id", null);

            //get the first result
            results.moveToFirst();
            assignment_grades = getAssignmentGrades(results.getString(results.getColumnIndex("name")), course);
            for(int i = 0; i < assignment_grades.size(); i++) {
                subjectGrade = subjectGrade + assignment_grades.get(i);
            }
            subjectGrade = subjectGrade / assignment_grades.size();
            subjectGrades.add(subjectGrade);

            //go through all of the subjects linked to the course
            while(results.moveToNext()) {
                subjectGrade = 0.0;
                //get the rest of the subject's grades
                assignment_grades = getAssignmentGrades(results.getString(results.getColumnIndex("name")), course);
                for(int i = 0; i < assignment_grades.size(); i++) {
                    subjectGrade = subjectGrade + assignment_grades.get(i);
                }
                subjectGrade = subjectGrade / assignment_grades.size();
                subjectGrades.add(subjectGrade);
            }
            results.close();
            db.close();
        }
        catch(Exception e) {
            //go through an get all the points possible and points earned for
            //each of the subjects based on the course
            subjectGrades.add(0.00);
        }
        return  subjectGrades;
    }
    //===========================================================================

    //===========================================================================
    public ArrayList<Double> getAssignmentGrades(String subject, String course) {
        SQLiteDatabase db = getReadableDatabase();

        //go through and get all the assignments' grades
        //and calculate their percentage
        ArrayList<Double> assignmentsGrades = new ArrayList<Double>();
        try {
            Cursor result =  db.rawQuery("SELECT Assignments.points_earned AS earned, Assignments.points_possible AS possible " +
                                         "FROM Assignments, Subjects, Courses " +
                                         "WHERE Courses.course_name = '" + course + "' " +
                                         "AND Courses.course_id = Subjects.course_id " +
                                         "AND Subjects.subject_name = '" + subject + "' " +
                                         "AND Subjects.subject_id = Assignments.subject_id", null);
            //go to the first of the results
            result.moveToFirst();

            //if the cursor is not empty
            if(result.getCount() != 0) {
                Double earned = result.getDouble(result.getColumnIndex("earned"));
                Double possible = result.getDouble(result.getColumnIndex("possible"));

                assignmentsGrades.add(earned / possible);

                while(result.moveToNext()) {
                    earned = result.getDouble(result.getColumnIndex("earned"));
                    possible = result.getDouble(result.getColumnIndex("possible"));

                    assignmentsGrades.add(earned / possible);
                }
            }
            result.close();
        }
        catch(Exception e) {
            assignmentsGrades.add(0.00);
        }
        db.close();

        return assignmentsGrades;
    }
    //===========================================================================











    //===========================================================================
    //goes through and deletes all the assignments, subjects, and the course that
    //is paired up with the name of the course that the user inputs
    public String deleteCourse(String course) {
        StringBuilder returnS = new StringBuilder();
        //connect to the database
        SQLiteDatabase db = getReadableDatabase();

        int course_id;
        ArrayList<Integer> subject_ids = new ArrayList<Integer>();

        try {
            Cursor result = db.rawQuery("SELECT Courses.course_id AS _id " +
                                        "FROM Courses " +
                                        "WHERE Courses.course_name = '" + course + "'", null);
            returnS.append("De");
            result.moveToFirst();
            course_id = result.getInt(result.getColumnIndex("_id"));


            result.close();

            //get all the subjects linked to the course
            result = db.rawQuery("SELECT Subjects.subject_id AS _id " +
                                 "FROM Subjects " +
                                 "WHERE Subjects.course_id = '" + course_id + "'", null);

            result.moveToFirst();
            while(result.moveToNext()) {
                subject_ids.add(result.getInt(result.getColumnIndex("_id")));
                returnS.append("le");
            }
            result.close();

            //go through and delete all assignments for all of the subjects
            //that are linked with the account
            for (int i = 0; i < subject_ids.size(); i++) {
                db.execSQL("DELETE FROM Assignments WHERE Assignments.subject_id = '" + subject_ids.get(i) + "'");
            }
            //go through and delete all of the subjects linked to the course
            db.execSQL("DELETE FROM Subjects WHERE Subjects.course_id = '" + course_id + "'");
            //delete the course
            db.execSQL("DELETE FROM Courses WHERE Courses.course_name = '" + course + "'");

            returnS.append("ted");
        }
        catch (Exception e) {
            returnS.append(e.toString());
        }
        db.close();

        return returnS.toString();
    }
    //===========================================================================

    //===========================================================================
    //goes through and deletes all the assignments linked with the subject that the
    //user entered as well as deleting the subject
    public String deleteSubject(String subject) {
        String returnS;
        //connect to the database
        SQLiteDatabase db = getReadableDatabase();

        int subject_id;

        try {
            Cursor result = db.rawQuery("SELECT Subjects.subject_id AS _id " +
                                        "FROM Subjects " +
                                        "WHERE Subjects.subject_name = '" + subject + "'", null);

            result.moveToFirst();
            subject_id = result.getInt(result.getColumnIndex("_id"));
            result.close();

            db = getWritableDatabase();


            db.execSQL("DELETE FROM Assignments WHERE Assignments.subject_id = '" + subject_id + "'");
            db.execSQL("DELETE FROM Subjects WHERE Subjects.subject_id = '" + subject_id + "'");
            returnS = "Deleted";

            result.close();
        }
        catch(Exception e) {
            returnS = e.toString();
        }
        db.close();

        return  returnS;
    }
    //===========================================================================

    //===========================================================================
    //goes through and deletes the assignment that the user entered
    public String deleteAssignment(String assignment) {

        String result;

        //connect to the database
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.execSQL("DELETE FROM Assignments WHERE assignment_name = '" + assignment + "'");
            result = "it worked";
        }
        catch(Exception e) {
            result = e.toString();
        }
        db.close();
        return  result;
    }
    //===========================================================================


    //===========================================================================
    //opens up a connection ith the database and adds in a new subject
    //goes through and deletes all the tables from the database
    public void deleteDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("Courses", null, null);
        db.delete("Subjects", null, null);
        db.delete("Assignments", null, null);
        db.close();
    }
    //===========================================================================
}
