package com.bingo.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

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
            Intent newIntent = new Intent();
            newIntent.putExtra("type", type);
            newIntent.putExtra("name", editSubTaskName.getText().toString());
            if (type ==  TaskFragment.CHANGE_ITEM){
                newIntent.putExtra("id", oldIntent.getIntExtra("id", -1));
            }
            newIntent.putExtra("tid", oldIntent.getIntExtra("tid", -1));
            setResult(RESULT_OK, newIntent); //todo 处理错误输入，task，group，subt
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