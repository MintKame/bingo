package com.bingo.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bingo.android.db.Reward;
import com.bingo.android.db.SubTask;
import com.bingo.android.db.Task;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class RewardFragment extends Fragment {

    public static final int LEVEL_REWARD = 0;

    public static final int CREATE_REWARD = 3;

    public static final int CHANGE_REWARD = 4;

    public static final int DELETE_REWARD = 5;

    // view
    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    // 奖励列表
    private List<Reward> rewardList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = View.inflate(getActivity(),R.layout.fragment_reward,null);

        listView = (ListView) view.findViewById(R.id.list_reward);
        adapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, dataList
        );
        listView.setAdapter(adapter);

        // 按兑换奖励
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long point = MainActivity.child.getPoint();
                long usePoint = rewardList.get(position).getTake_points();
                if (point < usePoint){
                    Toast.makeText(getContext(), "点数不足", Toast.LENGTH_SHORT).show();
                    return;
                }
                point -= usePoint;
                MainActivity.child.setPoint(point);
                MainActivity.child.save();
                getActivity().setTitle("可用点数     " + point);
                Toast.makeText(getContext(), "成功兑换", Toast.LENGTH_SHORT).show();
            }
        });

        // 长按修改或删除奖励
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                Reward reward = rewardList.get(position);
                intent = new Intent(getActivity(), RewardChangeActivity.class);
                intent.putExtra("type", CHANGE_REWARD); // todo
                intent.putExtra("id", reward.getId());

                intent.putExtra("name", reward.getName());
                intent.putExtra("points", reward.getTake_points());
                startActivityForResult(intent, LEVEL_REWARD);

                return false;
            }
        });

        getActivity().setTitle("可用点数     " + MainActivity.child.getPoint());

        showRewards(); // 开始查询

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.add_item:
                intent = new Intent(getActivity(), RewardChangeActivity.class);
                intent.putExtra("type", CREATE_REWARD);
                startActivityForResult(intent, LEVEL_REWARD);
                break;
            case R.id.ret:
                getActivity(). finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 处理从RewardChangeActivity传来的信息
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        int type = data.getIntExtra("type", -1);
        int id = data.getIntExtra("id", -1);
        if (requestCode == LEVEL_REWARD){
            if (type == CREATE_REWARD) createReward(data);
            else if (type == DELETE_REWARD) deleteReward(data.getIntExtra("id", -1));
            else if (type == CHANGE_REWARD) changeReward(data);
            showRewards();
        }
    }

    private void showRewards() {
        // 查找
        rewardList = DataSupport.findAll(Reward.class);
        // 更新list
        dataList.clear();
        for (Reward reward : rewardList) {
            dataList.add(reward.getName());
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(0);
    }

    /*
        奖励的增删改
     */
    private void setReward(Intent data, Reward reward){
        reward.setName(data.getStringExtra("name"));
        reward.setTake_points(data.getIntExtra("point", 0));
        reward.setState(false);
    }

    private void createReward(Intent data){
        // 创建 并设置信息
        Reward reward = new Reward();

        setReward(data, reward);
        reward.save();
    }

    private void deleteReward(int id){
        // 找到 并删除
        Reward reward = DataSupport.find(Reward.class, id);
        if (reward == null) return;

        reward.delete();
    }


    private void changeReward(Intent data){
        // 找到 并设置信息
        int id = data.getIntExtra("id", -1);
        Reward reward = DataSupport.find(Reward.class, id);
        if (reward == null) return;

        setReward(data, reward);
        reward.save();
    }
}
