package com.bingo.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

public class RewardChangeActivity extends AppCompatActivity {
    Button rewardConfirmButton, rewardCancelButton, rewardDelButton;
    EditText editRewardName, editRewardPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_change);

        // find view
        rewardCancelButton = findViewById(R.id.rewardCancelButton);
        rewardConfirmButton = findViewById(R.id.rewardConfirmButton);
        rewardDelButton = (Button) findViewById(R.id.rewardDelButton);

        editRewardName = findViewById(R.id.editRewardName);
        editRewardPoint = findViewById(R.id.editRewardPoint);

        // 处理传入信息
        Intent oldIntent = getIntent();
        int type = oldIntent.getIntExtra("type", RewardFragment.CREATE_REWARD);
        if (type == RewardFragment.CHANGE_REWARD){
            editRewardName.setText(oldIntent.getStringExtra("name"));
            editRewardPoint.setText(String.valueOf(oldIntent.getIntExtra("points", 0)));
        }else {
            rewardDelButton.setVisibility(View.INVISIBLE);
        }

        // btn cancel task
        rewardCancelButton.setOnClickListener((View view)->{
            finish();
        });

        // btn confirm task
        rewardConfirmButton.setOnClickListener((View view)->{
            // 错误处理
            String name = editRewardName.getText().toString();
            if (StringUtils.isEmpty(name)){
                Toast.makeText(this, "请输入奖励内容", Toast.LENGTH_SHORT).show();
                return;
            }
            int point = 0;
            try {
                point = Integer.parseInt(editRewardPoint.getText().toString());
            } catch (NumberFormatException ex){
                Toast.makeText(this, "请输入奖励点数", Toast.LENGTH_SHORT).show();
                return;
            }
            if (point <= 0){
                Toast.makeText(this, "点数需要大于0", Toast.LENGTH_SHORT).show();
                return;
            }

            // 传数据
            Intent newIntent = new Intent();
            newIntent.putExtra("type", type);
            newIntent.putExtra("name", name);
            newIntent.putExtra("point", point);

            if (type ==  RewardFragment.CHANGE_REWARD){
                newIntent.putExtra("id", oldIntent.getIntExtra("id", -1));
            }
            setResult(RESULT_OK, newIntent);
            finish();
        });

        // btn delete task
        rewardDelButton.setOnClickListener((View view)->{
            Intent newIntent = new Intent();
            newIntent.putExtra("type", RewardFragment.DELETE_REWARD);
            newIntent.putExtra("id", oldIntent.getIntExtra("id", -1));
            setResult(RESULT_OK, newIntent);
            finish();
        });
    }
}