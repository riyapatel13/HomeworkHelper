package com.example.a10016322.homeworkhelper;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Calendar;

//http://www.androidhive.info/2012/08/android-session-management-using-shared-preferences/
//https://crunchify.com/how-to-write-json-object-to-file-in-java/
//https://crunchify.com/how-to-read-json-object-from-file-in-java/
//use api 16 bc device monitor wont show data
//Android Device Monitor->data->data->appname->files


//NOTES
//6:00 -> 6:0
//have multiple lines in dialog for address
//check to make sure the user has not said the same thing twice - if they have and the dates are different, let them know

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{

    TextView addInfo, dateset, timeset;
    EditText writeTask;
    Button addTask, addDate;
    String fileName = "Homework.json";
    String message,dateString, timeString;
    SharedPreferences preferences;
    OutputStreamWriter writer;
    JSONObject jsonAddress;
    JSONArray jsonTasks;
    int year, month, day, hour, min;
    int setYear, setMonth, setDay, setHour, setMin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addInfo = (TextView) (findViewById(R.id.textView2_id));
        dateset = (TextView) (findViewById(R.id.dateset_id));
        timeset = (TextView) (findViewById(R.id.timeset_id));
        writeTask = (EditText) (findViewById(R.id.writeList_id));
        addTask = (Button) (findViewById(R.id.addTask_id));
        addDate = (Button) (findViewById(R.id.datebutton_id));

        //dialog for opening app for the 1st time
        final String PREFS_NAME = "MyPref";
        preferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("first_time", true);
        editor.commit();

        jsonAddress = new JSONObject();
        jsonTasks = new JSONArray();

        if (preferences.getBoolean("first_time", true))
        {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
            final View dialogView = getLayoutInflater().inflate(R.layout.dialoglayout, null);
            TextView dialogText = (TextView) (dialogView.findViewById(R.id.dialogtextview_id));
            final EditText dialogEdit = (EditText) (dialogView.findViewById(R.id.dialogedittext_id));
            Button dialogButton = (Button) (dialogView.findViewById(R.id.dialogbutton_id));

            dialogBuilder.setView(dialogView);
            final AlertDialog dialog = dialogBuilder.create();
            dialog.show();

            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //json object for address
                        jsonAddress.put("Address", dialogEdit.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //message = "\"Address\": "  +  "\"" +address.getText().toString()+ "\"";

                    //writes object in file
                    /*try {
                        writer = new OutputStreamWriter(openFileOutput(fileName, Context.MODE_WORLD_WRITEABLE));
                        writer.write(jsonAddress.toString());
                        //writer.write(message);
                        writer.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                    preferences.edit().putBoolean("first_time", false).commit();
                    dialog.dismiss();
                }
            });
        }

        addDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                min = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, MainActivity.this, hour, min, false);
                timePickerDialog.show();

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, MainActivity.this, year, month, day);
                datePickerDialog.show();
            }
        });
        //http://www.journaldev.com/9976/android-date-time-picker-dialog


        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //clears file
                try {
                    PrintWriter printWriter = new PrintWriter(fileName);
                    printWriter.print("");
                    printWriter.flush();
                    printWriter.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                //taskNum++;
                //message = "\n\"Task "+taskNum+"\": "  +  "\"" +writeTask.getText().toString()+ "\"";

                try {
                    //creates new object for every task
                    JSONObject task = new JSONObject();
                    task.put("TaskName", writeTask.getText().toString());
                    task.put("Due month", setMonth);
                    task.put("Due day", setDay);
                    task.put("Due year", setYear);
                    task.put("Due hour", setHour);
                    task.put("Due min", setMin);
                    //adds task to array
                    jsonTasks.put(task);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //rewrites file
                try {
                    writer = new OutputStreamWriter(openFileOutput(fileName, Context.MODE_WORLD_WRITEABLE));
                    writer.write(jsonAddress.toString());
                    writer.write("\n" + jsonTasks.toString());
                    //writer.append(message);
                    writer.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(MainActivity.this, "You have added "+writeTask.getText().toString()+" due on "+dateString+" at "+timeString+" (Note: The hour is in army time.)", Toast.LENGTH_LONG).show();
            /*try
            {
                //reads files
                BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput(fileName)));
               // BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

                String information = reader.readLine();
                while (information != null)
                {
                    Log.d("FILE INFO", information);
                    information = reader.readLine();
                    if (information.contains("["))
                    {

                    }
                }


                //writes array
                writer = new OutputStreamWriter(openFileOutput(fileName, Context.MODE_APPEND));
                if (tempArray.length() >= 0)
                {
                    writer.
                }

                writer.append("\n"+jsonTasks.toString());
                //writer.append(message);
                writer.close();
                reader.close();
                //writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        setMonth = month+1;
        setDay = dayOfMonth;
        setYear = year;
        dateString = (setMonth)+"/"+setDay+"/"+setYear;
        dateset.setText("Due date: "+dateString);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        setHour = hourOfDay;
        setMin = minute;
        timeString = hourOfDay+":"+minute;
        timeset.setText("Time due: "+timeString+"\n (Note: The hour is in army time.)");
    }
}









