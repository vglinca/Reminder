package com.example.reminder;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.reminder.ReminderDataBaseContract.TaskEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TaskRecycleAdapter mTaskRecycleAdapter;
    private TextView mNoTasksFound;
    private ReminderDbOpenHelper mMDbOpenHelper;
    private RecyclerView mRecyclerTasks;
    private LinearLayoutManager mTasksLayoutManager;
    private Cursor mTaskCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mNoTasksFound = findViewById(R.id.no_task_found_msg);

        mMDbOpenHelper = new ReminderDbOpenHelper(this);

        /*if (mTaskRecycleAdapter.getItemCount() == 0){
            mNoTasksFound.setText("No tasks found :/");
        }*/
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

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        //int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_remove_task) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
