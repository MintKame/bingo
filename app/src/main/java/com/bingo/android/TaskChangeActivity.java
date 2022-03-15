package com.bingo.android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

public class TaskChangeActivity extends AppCompatActivity {
    EditText editTaskName, editFromDate, editToDate;
    Button taskCancelButton, taskConfirmButton, taskDelButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_change);

        // find view
        taskCancelButton = (Button) findViewById(R.id.taskCancelButton);
        taskConfirmButton = (Button) findViewById(R.id.taskConfirmButton);
        taskDelButton = (Button) findViewById(R.id.taskDelButton);

        editTaskName =(EditText)findViewById(R.id.editTaskGroupName);
        editFromDate =(EditText)findViewById(R.id.editFromDate);
        editToDate =(EditText)findViewById(R.id.editToDate);

        // 处理传入信息
        Intent oldIntent = getIntent();
        int type = oldIntent.getIntExtra("type", TaskFragment.CREATE_ITEM);
        if (type == TaskFragment.CHANGE_ITEM){
            editTaskName.setText(oldIntent.getStringExtra("name"));
            editFromDate.setText(oldIntent.getStringExtra("start_time"));
            editToDate.setText(oldIntent.getStringExtra("end_time"));
        } else {
            taskDelButton.setVisibility(View.INVISIBLE);
        }

        // btn cancel task
        taskCancelButton.setOnClickListener((View view)->{
            finish();
        });

        // btn confirm task
        taskConfirmButton.setOnClickListener((View view)->{
            Intent newIntent = new Intent();
            newIntent.putExtra("type", type);
            newIntent.putExtra("name", editTaskName.getText().toString());
            newIntent.putExtra("start_time",editFromDate.getText().toString());
            newIntent.putExtra("end_time",editToDate.getText().toString());
            if (type ==  TaskFragment.CHANGE_ITEM){
                newIntent.putExtra("id", oldIntent.getIntExtra("id", -1));
            }
            setResult(RESULT_OK, newIntent); //todo 处理错误输入，task，group，subt
            finish();
        });

        // btn delete task
        taskDelButton.setOnClickListener((View view)->{
            Intent newIntent = new Intent();
            newIntent.putExtra("type", TaskFragment.DELETE_ITEM);
            newIntent.putExtra("id", oldIntent.getIntExtra("id", -1));
            setResult(RESULT_OK, newIntent);
            finish();
        });

        // start time edit task
        editFromDate.setOnTouchListener((View v, MotionEvent event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                pickDate(editFromDate);
                return true;
            }
            return false;
        });

        // end time edit task
        editToDate.setOnTouchListener((View v, MotionEvent event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                pickDate(editToDate);
                return true;
            }
            return false;
        });
    }

    protected void pickDate(EditText editText) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(TaskChangeActivity.this,

                (DatePicker view, int year, int monthOfYear, int dayOfMonth)->{
                    editText.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth); },

                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }
}