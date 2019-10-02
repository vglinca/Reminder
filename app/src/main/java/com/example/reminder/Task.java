package com.example.reminder;

public class Task {
   private String mDate;
   private String mTime;
   private String mTitle;
   private String mDescription;
   public Task(String date, String time, String title, String description){
       mDate = date;
       mTime = time;
       mTitle = title;
       mDescription = description;
   }

   public String getTitle(){return mTitle;}
   public String getDate(){return mDate;}
   public String getTime(){return mTime;}
   public String getDescription(){return mDescription;}
}
