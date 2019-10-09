package com.example.reminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;

public class PopUpActivity extends AppCompatActivity {

    public static final String SEARCH_FILTER = "com.example.reminder.SEARCH_FILTER";
    private DisplayMetrics mDisplayMetrics;
    private ReminderDbOpenHelper mDbOpenHelper;
    private Cursor mCursor;
    private RecyclerView mPopUpRecycleView;
    private LinearLayoutManager mLinearLayoutManager;
    private TaskRecycleAdapter mRecycleAdapter;
    private String mFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);
        mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);

        int width = mDisplayMetrics.widthPixels;
        int height = mDisplayMetrics.heightPixels;

        getWindow().setLayout((int) (width * .8), (int)(height * .7));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);

        mDbOpenHelper = new ReminderDbOpenHelper(this);
        initializeContent();
    }

    @Override
    protected void onPause() {
        mDbOpenHelper.close();
        super.onPause();
    }

    private void initializeContent() {
        Intent intent = getIntent();
        mFilter = intent.getStringExtra(SEARCH_FILTER);
        DataManager.loadFromDbWithFilter(mDbOpenHelper, mFilter);
        loadTasks();
        mPopUpRecycleView = (RecyclerView) findViewById(R.id.list_tasks_popup);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mPopUpRecycleView.setLayoutManager(mLinearLayoutManager);
        mRecycleAdapter = new TaskRecycleAdapter(this, mCursor);
        mPopUpRecycleView.setAdapter(mRecycleAdapter);
        showTasks();
    }

    private void showTasks() {
        mPopUpRecycleView.setLayoutManager(mLinearLayoutManager);
        mPopUpRecycleView.setAdapter(mRecycleAdapter);
    }

    private void loadTasks() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        final String[] taskColumns = {
                ReminderDataBaseContract.TaskEntry.COLUMN_DATE,
                ReminderDataBaseContract.TaskEntry.COLUMN_TIME,
                ReminderDataBaseContract.TaskEntry.COLUMN_TASK_TITLE,
                ReminderDataBaseContract.TaskEntry.COLUMN_DESCRIPTION,
                ReminderDataBaseContract.TaskEntry._ID
        };
        String selection = ReminderDataBaseContract.TaskEntry.COLUMN_DATE + " LIKE ?";
        String[] selectionArgs = new String[]{mFilter + "%"};
        final String orderColumns = ReminderDataBaseContract.TaskEntry.COLUMN_TASK_TITLE;
        mCursor = db.query(ReminderDataBaseContract.TaskEntry.TABLE_NAME, taskColumns,
                selection, selectionArgs, null, null, orderColumns);
    }
}
