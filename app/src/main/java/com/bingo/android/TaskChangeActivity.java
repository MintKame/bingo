package com.bingo.android;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;

public class TaskChangeActivity extends AppCompatActivity {
    static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");    // 转换的时间格式

    EditText editTaskName, editFromDate, editToDate;
    Button taskCancelButton, taskConfirmButton, taskDelButton;
    Spinner selectType;
    int taskType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_change);

        // find view
        taskCancelButton = findViewById(R.id.taskCancelButton);
        taskConfirmButton = findViewById(R.id.taskConfirmButton);
        taskDelButton =  findViewById(R.id.taskDelButton);

        editTaskName = findViewById(R.id.editTaskGroupName);
        editFromDate = findViewById(R.id.editFromDate);
        editToDate = findViewById(R.id.editToDate);
        selectType = findViewById(R.id.selectType);

        // 处理传入信息
        Intent oldIntent = getIntent();
        int type = oldIntent.getIntExtra("type", TaskFragment.CREATE_ITEM);
        if (type == TaskFragment.CHANGE_ITEM){
            editTaskName.setText(oldIntent.getStringExtra("name"));
            editFromDate.setText(oldIntent.getStringExtra("start_time"));
            editToDate.setText(oldIntent.getStringExtra("end_time"));
            taskType = oldIntent.getIntExtra("task_type", 0);
            selectType.setSelection(taskType);
        } else {
            taskDelButton.setVisibility(View.INVISIBLE);
        }

        // btn cancel task
        taskCancelButton.setOnClickListener((View view)->{
            finish();
        });

        // btn confirm task
        taskConfirmButton.setOnClickListener((View view)->{
            // 错误处理
            String name = editTaskName.getText().toString(); // 任务名
            if (StringUtils.isEmpty(name)){
                Toast.makeText(this, "请输入任务名", Toast.LENGTH_SHORT).show();
                return;
            }
            // 起止时间
            Date startTime = null, endTime = null;
            try {
                startTime = format.parse(editFromDate.getText().toString());
                endTime = format.parse(editToDate.getText().toString());
            } catch (ParseException e) {
                Toast.makeText(this, "请选择起止时间", Toast.LENGTH_SHORT).show();
                return;
            }
            if (startTime.after(endTime)){
                Toast.makeText(this, "结束时间应不小于开始时间", Toast.LENGTH_SHORT).show();
                return;
            }
            // 传数据
            Intent newIntent = new Intent();
            newIntent.putExtra("type", type);
            newIntent.putExtra("name", name);
            newIntent.putExtra("start_time", startTime);
            newIntent.putExtra("end_time", endTime);
            newIntent.putExtra("task_type", taskType);
            if (type ==  TaskFragment.CHANGE_ITEM){
                newIntent.putExtra("id", oldIntent.getIntExtra("id", -1));
            }
            setResult(RESULT_OK, newIntent);
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

        // taskType spinner
        selectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                taskType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
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