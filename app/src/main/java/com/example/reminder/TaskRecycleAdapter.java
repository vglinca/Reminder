package com.example.reminder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminder.ReminderDataBaseContract.TaskEntry;

public class TaskRecycleAdapter extends RecyclerView.Adapter<TaskRecycleAdapter.ViewHolder> {

    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private Cursor mCursor;
    private int mDatePos;
    private int mTimePos;
    private int mTitlePos;
    private int mDescriptionPos;
    private int mIdPos;

    public TaskRecycleAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
        mLayoutInflater = LayoutInflater.from(context);
        populateColumnPositions();
    }

    public void changeCursor(Cursor cursor){
        if (cursor != null)
            cursor.close();
        mCursor = cursor;
        populateColumnPositions();
        notifyDataSetChanged();
    }

    private void populateColumnPositions() {
        if(mCursor == null)
            return;
        mDatePos = mCursor.getColumnIndex(TaskEntry.COLUMN_DATE);
        mTimePos = mCursor.getColumnIndex(TaskEntry.COLUMN_TIME);
        mTitlePos = mCursor.getColumnIndex(TaskEntry.COLUMN_TASK_TITLE);
        mDescriptionPos = mCursor.getColumnIndex(TaskEntry.COLUMN_DESCRIPTION);
        mIdPos = mCursor.getColumnIndex(TaskEntry._ID);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_task_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String date = mCursor.getString(mDatePos);
        String time = mCursor.getString(mTimePos);
        String title = mCursor.getString(mTitlePos);
        String description = mCursor.getString(mDescriptionPos);
        int id = mCursor.getInt(mIdPos);

        holder.mDate.setText(date);
        holder.mTime.setText(time);
        holder.mTitle.setText(title);
        holder.mDescription.setText(description);
        holder.tId = id;
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView mDate;
        public final TextView mTime;
        public final TextView mTitle;
        public final TextView mDescription;
        public int tId;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mDate = (TextView) itemView.findViewById(R.id.task_date);
            mTime = (TextView) itemView.findViewById(R.id.task_time);
            mTitle = (TextView) itemView.findViewById(R.id.task_title);
            mDescription = (TextView) itemView.findViewById(R.id.task_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, SetTaskActivity.class);
                    intent.putExtra(SetTaskActivity.TASK_ID, tId);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
