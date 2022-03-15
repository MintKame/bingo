package com.bingo.android;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bingo.android.db.Child;

import org.litepal.crud.DataSupport;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    RadioGroup radioGroup;

    static Child child;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 清空数据库
//        getApplicationContext().deleteDatabase("bingo.db");

        radioGroup = findViewById(R.id.radio_group);

        // listener
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    RadioButton rb = (RadioButton) group.getChildAt(i);
                    if (rb.isChecked()) {
                        setIndexSelected(i);
                        break;
                    }
                }
            }
        });

        // 查询或创建child
        List<Child> list = DataSupport.findAll(Child.class);
        if (list.size() == 0){
            child = new Child();
            child.save();
        }else {
            child = list.get(0);
        }

        setIndexSelected(0); // 最初显示task界面
    }

    //通过index判断当前加载哪个界面
    public void setIndexSelected(int index) {
        switch (index) {
            case 0:
                changeFragment(new TaskFragment());
                break;
            case 1:
                changeFragment(new RewardFragment());
                break;
            case 2:
                changeFragment(new AnalyzeFragment());
                break;
            default:
                break;
        }
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();//开启事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment , fragment);
        transaction.commit();
    }
}
