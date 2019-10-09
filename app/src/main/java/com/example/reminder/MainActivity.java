package com.example.reminder;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.reminder.ReminderDataBaseContract.TaskEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.CalendarView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "com.example.reminder.TAG";
    private TaskRecycleAdapter mTaskRecycleAdapter;
    private TextView mNoTasksFound;
    private ReminderDbOpenHelper mMDbOpenHelper;
    private RecyclerView mRecyclerTasks;
    private LinearLayoutManager mTasksLayoutManager;
    private Cursor mTaskCursor;
    private TextView mNoItemsTxt;
     private CalendarView mCalendarView;
    private SearchView mSearchTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mNoTasksFound = findViewById(R.id.no_task_found_msg);
        mSearchTask = (SearchView) findViewById(R.id.search_task);
        mSearchTask.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        mNoItemsTxt = (TextView) findViewById(R.id.no_items_textView);

        mMDbOpenHelper = new ReminderDbOpenHelper(this);

        mCalendarView = (CalendarView) findViewById(R.id.mainActivitycalendarView);
        long date = mCalendarView.getDate();
        Log.d(TAG, "THIS IS THE DATE FROM CAL.VEIW: " + Long.toString(date));

        initializeDisplayContent();

        FloatingActionButton fab = findViewById(R.id.save_task_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SetTaskActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        mMDbOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DataManager.loadFromDatabase(mMDbOpenHelper);
        loadTasks();
        displayTasks();
    }

    private void initializeDisplayContent() {
            DataManager.loadFromDatabase(mMDbOpenHelper);
            loadTasks();
            mRecyclerTasks = (RecyclerView) findViewById(R.id.list_tasks);
            mTasksLayoutManager = new LinearLayoutManager(this);
            mRecyclerTasks.setLayoutManager(mTasksLayoutManager);
            mTaskRecycleAdapter = new TaskRecycleAdapter(this, mTaskCursor);
            mRecyclerTasks.setAdapter(mTaskRecycleAdapter);
            displayTasks();
    }

    private void displayTasks() {
        mRecyclerTasks.setLayoutManager(mTasksLayoutManager);
        mRecyclerTasks.setAdapter(mTaskRecycleAdapter);
    }

    private void loadTasks() {
        SQLiteDatabase db = mMDbOpenHelper.getReadableDatabase();
        final String[] taskColumns = {
                TaskEntry.COLUMN_DATE,
                TaskEntry.COLUMN_TIME,
                TaskEntry.COLUMN_TASK_TITLE,
                TaskEntry.COLUMN_DESCRIPTION,
                TaskEntry._ID
        };
        final String orderColumns = TaskEntry.COLUMN_DATE;
        mTaskCursor = db.query(TaskEntry.TABLE_NAME, taskColumns,
                null, null, null, null, orderColumns);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
