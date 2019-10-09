package com.example.reminder;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.reminder.ReminderDataBaseContract.TaskEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SetTaskActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    public static final String TASK_ID = "com.example.reminder.TASK_ID";
    public static final int ID_NOT_SET = -1;
    private Button mSelectDate;
    private Button mSelectTime;
    private DatePickerDialog mDatePickerDialog;
    private String mDate;
    private String mTime;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    DatePicker mDatePicker;
    android.widget.TimePicker mTimePicker;
    private Calendar mCalendar;
    private ReminderDbOpenHelper mDbOpenHelper;
    private int mTaskId;
    private boolean mIsNewTask;
    private Cursor mTaskCursor;
    private int mDatePos;
    private int mTimePos;
    private int mTitlePos;
    private int mDescriptionPos;
    private EditText mTaskTitle;
    private EditText mTaskDescription;
    private int mCurrentDay;
    private int mCurrentMonth;
    private int mCurrentYear;
    private int mCanelAlarm = 1;
    private Date mFormattedDate;
    private SimpleDateFormat mSimpleDateFormatter;
    private Date mFormattedTime;
    private DateFormat mTimeFormat;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_task);
        mSelectDate = (Button) findViewById(R.id.set_task_date);
        mSelectTime = (Button) findViewById(R.id.set_task_time);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        mDbOpenHelper = new ReminderDbOpenHelper(this);
        FloatingActionButton SaveTaskfab = findViewById(R.id.save_task_fab);
        FloatingActionButton RemoveTaskfab = findViewById(R.id.remove_task_fab);
        mSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCalendar = Calendar.getInstance();
                mYear = mCalendar.get(Calendar.YEAR);
                mMonth = mCalendar.get(Calendar.MONTH);
                mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
                mDatePickerDialog = new DatePickerDialog(SetTaskActivity.this, R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                mCurrentDay = mCalendar.get(Calendar.DAY_OF_MONTH);
                                mCurrentMonth = mCalendar.get(Calendar.MONTH);
                                mCurrentYear = mCalendar.get(Calendar.YEAR);
                                if ((year < mCurrentYear) || (day < mCurrentDay && month <= mCurrentMonth) ||
                                        (month < mCurrentMonth && year <= mCurrentYear)){
                                    Toast.makeText(SetTaskActivity.this, "Invalid date, try again", Toast.LENGTH_LONG).show();
                                }else{
                                    mDatePicker = datePicker;
                                    month++;
                                    mDate = day + "/" + month + "/" + year;
                                    mSimpleDateFormatter = new SimpleDateFormat("dd/MM/yyyy");
                                    try {
                                        mFormattedDate = mSimpleDateFormatter.parse(mDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    mSelectDate.setText(mDate);
                                }
                            }
                        }, mYear, mMonth, mDay);
                mDatePickerDialog.show();
            }
        });
        mSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment timePicker = new TimePicker();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        readDisplayStateValues();

        mTaskTitle = findViewById(R.id.task_title);
        mTaskDescription = findViewById(R.id.task_description);
        if(!mIsNewTask)
            loadTaskFromDb();

        SaveTaskfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDate == null || mTime == null){
                    Toast.makeText(SetTaskActivity.this, "Complete fields", Toast.LENGTH_LONG).show();
                }else{
                    saveTask();
                    setupAlarmManager();
                    //onBackPressed();
                    startActivity(new Intent(SetTaskActivity.this, MainActivity.class));
                }
            }
        });
        RemoveTaskfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlarm();
                deleteTask();
                onBackPressed();
                //startActivity(new Intent(SetTaskActivity.this, MainActivity.class));
            }
        });
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.EXTRA_TASK_TITLE, mTaskTitle.getText().toString());
        intent.putExtra(AlarmReceiver.EXTRA_TASK_DESCRIPTION, mTaskDescription.getText().toString());
        intent.putExtra(TASK_ID, mTaskId);
        intent.setData(Uri.parse("alarm://" + mTaskId));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, mTaskId,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    private void setupAlarmManager() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.EXTRA_TASK_TITLE, mTaskTitle.getText().toString());
        intent.putExtra(AlarmReceiver.EXTRA_TASK_DESCRIPTION, mTaskDescription.getText().toString());
        intent.putExtra(TASK_ID, mTaskId);
        intent.setData(Uri.parse("alarm://" + mTaskId));

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), mTaskId,
                intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null){
            alarmManager.cancel(pendingIntent);
        }
        pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), mTaskId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.set(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth(),
                mTimePicker.getHour(), mTimePicker.getMinute(), 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
    }

    private void deleteTask() {
        cancelAlarm();
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                final String selection = TaskEntry._ID + " = ?";
                final String[] selectionArgs = {Integer.toString(mTaskId)};
                SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
                db.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
                //db.update(TaskEntry.TABLE_NAME, null, null, null);
                return null;
            }
        };
        task.execute();
    }

    @Override
    public void onTimeSet(android.widget.TimePicker timePicker, int hour, int minute) {
        mTimePicker = timePicker;
        mHour = hour;
        mMinute = minute;
            mTime = hour + ":" + minute;
        mTimeFormat = new SimpleDateFormat("hh:mm");
        try {
            mFormattedTime = mTimeFormat.parse(mTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mSelectTime.setText(mTime);
    }

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //saveTask();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        //super.onBackPressed();
    }

    private void saveTask() {
        if (mDate != null){
            String date = mDate;
            String time = mTime;
            //Date date = mFormattedDate;
            //Date time = mFormattedTime;
            String title = mTaskTitle.getText().toString();
            String description = mTaskDescription.getText().toString();
            saveTaskToDatabase(date, time, title, description);
        }
    }

    private void saveTaskToDatabase(String date, String time, String title, String description) {
        String selection = TaskEntry._ID + " = ?";
        String[] selectionArgs = {Integer.toString(mTaskId)};
        ContentValues values = new ContentValues();

        //values.put(TaskEntry.COLUMN_DATE, mSimpleDateFormatter.format(date));
        //values.put(TaskEntry.COLUMN_TIME, mTimeFormat.format(time));
        values.put(TaskEntry.COLUMN_DATE, date);
        values.put(TaskEntry.COLUMN_TIME, time);
        values.put(TaskEntry.COLUMN_TASK_TITLE, title);
        values.put(TaskEntry.COLUMN_DESCRIPTION, description);
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        db.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    private void loadTaskFromDb() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        String selection = TaskEntry._ID + " = ?";
        String[] selectionArgs = {Integer.toString(mTaskId)};
        String[] taskColumns = new String[]{
                TaskEntry.COLUMN_DATE,
                TaskEntry.COLUMN_TIME,
                TaskEntry.COLUMN_TASK_TITLE,
                TaskEntry.COLUMN_DESCRIPTION
        };
        mTaskCursor = db.query(TaskEntry.TABLE_NAME, taskColumns, selection,
                selectionArgs, null, null, null);
        mDatePos = mTaskCursor.getColumnIndex(TaskEntry.COLUMN_DATE);
        mTimePos = mTaskCursor.getColumnIndex(TaskEntry.COLUMN_TIME);
        mTitlePos = mTaskCursor.getColumnIndex(TaskEntry.COLUMN_TASK_TITLE);
        mDescriptionPos = mTaskCursor.getColumnIndex(TaskEntry.COLUMN_DESCRIPTION);
        mTaskCursor.moveToNext();
        displayTask();
    }

    private void displayTask() {
        String taskDate = mTaskCursor.getString(mDatePos);
        String taskTime = mTaskCursor.getString(mTimePos);
        String taskTitle = mTaskCursor.getString(mTitlePos);
        String taskDescription = mTaskCursor.getString(mDescriptionPos);
        mSelectDate.setText(taskDate);
        mSelectTime.setText(taskTime);
        mTaskTitle.setText(taskTitle);
        mTaskDescription.setText(taskDescription);
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        mTaskId = intent.getIntExtra(TASK_ID, ID_NOT_SET);
        mIsNewTask = mTaskId == ID_NOT_SET;
        mCanelAlarm = intent.getIntExtra(AlarmReceiver.EXTRA_CANCEL_TASK_ALARM, 1);
        if (mIsNewTask){
            createNewTask();
        }
    }

    private void createNewTask() {
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_DATE, "");
        values.put(TaskEntry.COLUMN_TIME, "");
        values.put(TaskEntry.COLUMN_TASK_TITLE, "");
        values.put(TaskEntry.COLUMN_DESCRIPTION, "");
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        mTaskId = (int) db.insert(TaskEntry.TABLE_NAME, null, values);
    }
}
