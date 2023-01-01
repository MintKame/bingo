package com.bingo.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.TimeUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bingo.android.db.SubTask;
import com.bingo.android.db.Task;
import com.bingo.android.view.TimeView;

import org.litepal.crud.DataSupport;

public class TimerActivity extends AppCompatActivity {

    Button startButton, exitButton, finishButton;

    TextView tmpTime, taskTime, contentText;

    TimeView timeView;

    long tmpDuration = 0, taskDuration; // 本次计时，本任务总计时

    boolean isStop = true; // 记录当前计时器状态（暂停、计时）

    Task task;

    SubTask subTask;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        startButton = findViewById(R.id.startButton);
        exitButton = findViewById(R.id.exitButton);
        finishButton = findViewById(R.id.finishButton);

        timeView = findViewById(R.id.timer);
        tmpTime = findViewById(R.id.tmpTime);
        taskTime = findViewById(R.id.taskTime);

        contentText = findViewById(R.id.contentText);
        // 处理传入信息
        Intent oldIntent = getIntent();
        int tid = oldIntent.getIntExtra("tid", -1);
        int sid = oldIntent.getIntExtra("sid", -1);
        if (tid == -1 || sid == -1) finish();

        task = DataSupport.where("id = ?", ""+ tid).find(Task.class).get(0);

        subTask = DataSupport.where("id = ?", ""+ sid).find(SubTask.class).get(0);

        taskDuration = task.getSpend_time();
        taskTime.setText(convert(taskDuration));
        contentText.setText(subTask.getName());

        /* 3 buttons */
        // btn exit
        exitButton.setOnClickListener((View view)->{
            task.setSpend_time(taskDuration);
            task.save();
            finish();
        });

        // btn finish
        finishButton.setOnClickListener((View view)->{
            subTask.setFinish(true);
            subTask.save();

            task.setSpend_time(taskDuration);
            task.setFinishCnt(task.getFinishCnt() + 1);
            task.save();
            finish();
        });

        // btn start
        startButton.setOnClickListener((View view)->{
            isStop = !isStop;
            if (isStop){
                startButton.setText("开始");
                exitButton.setVisibility(View.VISIBLE);
                finishButton.setVisibility(View.VISIBLE);
            }else {
                startButton.setText("暂停");
                exitButton.setVisibility(View.INVISIBLE);
                finishButton.setVisibility(View.INVISIBLE);
            }
        });

        countTimer(); // 开始每秒处理一次
    }

    private void countTimer(){
        handler.postDelayed(TimerRunnable, 1000);
    }

    private Runnable TimerRunnable = () -> {
        if(!isStop){
            taskDuration ++;
            tmpDuration ++;
            taskTime.setText(convert(taskDuration));
            tmpTime.setText(convert(tmpDuration));
            timeView.rotate();
        }
        countTimer();
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(TimerRunnable);
    }

    String convert(long duration) {
        Long hours = duration / (60 * 60);
        duration -= hours * (60 * 60);
        Long minutes = duration / 60;
        duration -= minutes * 60;
        Long seconds = duration;

        return hours + " : " + minutes + " : " + seconds;
    }
}