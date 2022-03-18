package com.bingo.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.lang3.StringUtils;

public class SubTaskChangeActivity extends AppCompatActivity {
    EditText editSubTaskName;

    Button subtaskCancelButton, subtaskConfirmButton, subtaskDelButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subtask_change);

        // find view
        subtaskCancelButton = (Button) findViewById(R.id.subtaskCancelButton);
        subtaskConfirmButton = (Button) findViewById(R.id.subtaskConfirmButton);
        subtaskDelButton = (Button) findViewById(R.id.subtaskDelButton);

        editSubTaskName =(EditText)findViewById(R.id.editTaskGroupName);

        // 处理传入信息
        Intent oldIntent = getIntent();
        int type = oldIntent.getIntExtra("type", TaskFragment.CREATE_ITEM);
        if (type == TaskFragment.CHANGE_ITEM){
            editSubTaskName.setText(oldIntent.getStringExtra("name"));
        } else {
            subtaskDelButton.setVisibility(View.INVISIBLE);
        }

        // btn cancel task
        subtaskCancelButton.setOnClickListener((View view)->{
            finish();
        });

        // btn confirm task
        subtaskConfirmButton.setOnClickListener((View view)->{
            // 错误处理
            String name = editSubTaskName.getText().toString();
            if (StringUtils.isEmpty(name)){
                Toast.makeText(this, "请输入子任务内容", Toast.LENGTH_SHORT).show();
                return;
            }
            // 传数据
            Intent newIntent = new Intent();
            newIntent.putExtra("type", type);
            newIntent.putExtra("name", name);
            if (type ==  TaskFragment.CHANGE_ITEM){
                newIntent.putExtra("id", oldIntent.getIntExtra("id", -1));
            }
            newIntent.putExtra("tid", oldIntent.getIntExtra("tid", -1));
            setResult(RESULT_OK, newIntent);
            finish();
        });

        // btn delete task
        subtaskDelButton.setOnClickListener((View view)->{
            Intent newIntent = new Intent();
            newIntent.putExtra("type", TaskFragment.DELETE_ITEM);
            newIntent.putExtra("id", oldIntent.getIntExtra("id", -1));
            newIntent.putExtra("tid", oldIntent.getIntExtra("tid", -1));
            setResult(RESULT_OK, newIntent);
            finish();
        });
    }
}