package com.bingo.android.util;

import android.text.TextUtils;

import com.bingo.android.db.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    /** analyze task json */
    public static boolean handleTaskResponse(String response){
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allTasks = new JSONArray(response);
                for (int i = 0; i < allTasks.length(); i++) {
                    JSONObject taskObject = allTasks.getJSONObject(i);
                    Task task = new Task();
                    task.setName(taskObject.getString("name"));
                    // todo
                    task.save();  // save to database
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
