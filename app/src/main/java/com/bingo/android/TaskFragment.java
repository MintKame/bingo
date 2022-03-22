package com.bingo.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.bingo.android.db.SubTask;
import com.bingo.android.db.Task;

import org.litepal.crud.DataSupport;

import java.text.DateFormat;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TaskFragment extends Fragment {
    static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");    // 转换的时间格式

    public static final int LEVEL_TASK = 1;

    public static final int LEVEL_SUBTASK = 2;

    public static final int CREATE_ITEM = 3;

    public static final int CHANGE_ITEM = 4;

    public static final int DELETE_ITEM = 5;

    // view
    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    // 任务，子任务列表
    private List<Task> taskList;

    private List<SubTask> subTaskList;

    private Button finishButton;

    // 当前选中的 任务（点击列表后记录，长按不记录
    Task selectedTask;

    SubTask selectedSubTask;

    // 当前选中的级别
    int currentLevel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = View.inflate(getActivity(),R.layout.fragment_task,null);

        finishButton = view.findViewById(R.id.finishButton);

        listView = (ListView) view.findViewById(R.id.list_task);
        adapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, dataList
        );
        listView.setAdapter(adapter);

        // 点击list某项目，跳转页面
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 判断当前数据级别， 决定调用不同方法
                if (currentLevel == LEVEL_TASK) {
                    selectedTask = taskList.get(position);
                    showSubTasks();
                } else if (currentLevel == LEVEL_SUBTASK) {
                    selectedSubTask = subTaskList.get(position);
                    showTimer();
                }
            }
        });

        // 长按list某项目，修改或删除
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 判断当前数据级别，决定调用不同方法
                if (currentLevel == LEVEL_SUBTASK){
                    SubTask subTask = subTaskList.get(position);
                    showUpdateSubTask(subTask);
                }else if (currentLevel == LEVEL_TASK){
                    Task task = taskList.get(position);
                    showUpdateTask(task);
                }
                return false;
            }
        });

        /*  完成任务
        * 奖励点数 = 子任务数 * 效率 * 质量
        * 效率：
        *       按时完成 = 1
        *       超时完成 = 0.5
        * 质量：
        *       家长决定 // todo
        * */
//        todo taskType 存信息 用于分析
        finishButton.setOnClickListener((View v)->{
            selectedTask = DataSupport.find(Task.class, selectedTask.getId());
            if (selectedTask.getFinishCnt() < selectedTask.getTotalCnt()){
                Toast.makeText(getContext(), "未完成所有子任务", Toast.LENGTH_SHORT).show();
                return;
            }
            // 计算奖励
            long points = selectedTask.getTotalCnt(); // 子任务数
//            boolean inTime =  !new Date().after(selectedTask.getEnd_time()); // 按时
//            if (!inTime) points *= 0.5;
            // 更新账号：点数，任务总数，该类型的任务数
            MainActivity.child.setPoint(MainActivity.child.getPoint() + points);
            MainActivity.child.setFinish_total(MainActivity.child.getFinish_total() + 1);
            switch (selectedTask.getType()){
                case Task.IN_CLASS:
                    MainActivity.child.setFinish_in( MainActivity.child.getFinish_in()+1);
                    break;
                case Task.OUT_CLASS:
                    MainActivity.child.setFinish_out( MainActivity.child.getFinish_out()+1);
                    break;
                case Task.SPORT:
                    MainActivity.child.setFinish_sport( MainActivity.child.getFinish_sport()+1);
                    break;
                case Task.HOUSEWORK:
                    MainActivity.child.setFinish_housework( MainActivity.child.getFinish_housework()+1);
                    break;
                case Task.HOBBY:
                    MainActivity.child.setFinish_hobby( MainActivity.child.getFinish_hobby()+1);
                    break;
            }
            MainActivity.child.save();
            // 提醒
            Toast.makeText(getContext(), "获得奖励：" + points, Toast.LENGTH_LONG).show();
            // 删除任务
            deleteTask(selectedTask.getId());
            showTasks();
        });

        showTasks(); // 开始查询

        return view;
    }

    // 置于最前时调用，用于刷新ListView（如完成子任务后，需要刷新子任务列表）
    @Override
    public void onStart() {
        super.onStart();
        if (currentLevel == LEVEL_SUBTASK)
            showSubTasks();
        else if (currentLevel == LEVEL_TASK)
            showTasks();
    }

    // 菜单栏: 添加项目 返回
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_item:
                showCreateItem();
                break;
            case R.id.ret:
                switch (currentLevel){
                    case LEVEL_SUBTASK:
                        currentLevel = LEVEL_TASK;
                        showTasks();
                        break;
                    case LEVEL_TASK:
                        getActivity().finish();
                        break;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        int type = data.getIntExtra("type", -1);
        if (requestCode == LEVEL_SUBTASK){
            if (type == CREATE_ITEM) createSubTask(data);
            else if (type == DELETE_ITEM) deleteSubTask(data.getIntExtra("id", -1));
            else if (type == CHANGE_ITEM) changeSubTask(data);

            showSubTasks();
        } else if (requestCode == LEVEL_TASK){
            if (type == CREATE_ITEM) createTask(data);
            else if (type == DELETE_ITEM) deleteTask(data.getIntExtra("id", -1));
            else if (type == CHANGE_ITEM) changeTask(data);

            showTasks();
        }
    }

    // 查询选中TaskGroup的Task
    private void showTasks() {
        // 查找
        getActivity().setTitle("所有任务");
        finishButton.setVisibility(View.INVISIBLE);

        taskList = DataSupport.findAll(Task.class);
        // 更新list
        dataList.clear();
        for (Task task : taskList) {
            dataList.add(task.getName());
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(0);
        currentLevel = LEVEL_TASK;
    }

    // 查询选中Task的SubTask
    private void showSubTasks() {
        // 查找
        getActivity().setTitle("任务：" + selectedTask.getName());
        finishButton.setVisibility(View.VISIBLE);

        subTaskList = DataSupport.where("tid = ?", ""+selectedTask.getId()).find(SubTask.class);
        // 更新list
        dataList.clear();
        for (SubTask subTask : subTaskList) {
            dataList.add((subTask.isFinish()?"✔ ":"") + subTask.getName());
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(0);
        currentLevel = LEVEL_SUBTASK;
    }

    // 点击子任务后，跳转到计时器页面
    private void showTimer(){
        if (selectedSubTask.isFinish()){ // 提醒已经完成
            Toast.makeText(getActivity(), "子任务已经完成！",Toast.LENGTH_SHORT).show();
        } else {
            // start timer
            Intent intent = new Intent(getActivity(), TimerActivity.class);
            intent.putExtra("sid", selectedSubTask.getId());
            intent.putExtra("tid", selectedTask.getId());
            startActivity(intent);
        }
    }

    // 长按子任务，跳转到修改页面
    private void showUpdateSubTask(SubTask subTask){
        Intent intent = new Intent(getActivity(), SubTaskChangeActivity.class);
        intent.putExtra("type", CHANGE_ITEM);
        intent.putExtra("id", subTask.getId());
        intent.putExtra("tid", selectedTask.getId());

        intent.putExtra("name", subTask.getName());
        startActivityForResult(intent, LEVEL_SUBTASK);
    }

    // 长按  任务，跳转到修改页面
    private void showUpdateTask(Task task){
        Intent intent = new Intent(getActivity(), TaskChangeActivity.class);
        intent.putExtra("type", CHANGE_ITEM);
        intent.putExtra("id", task.getId());
        intent.putExtra("name", task.getName());
        intent.putExtra("task_type", task.getType());

        intent.putExtra("start_time", format.format(task.getStart_time()));
        intent.putExtra("end_time", format.format(task.getEnd_time()));
        startActivityForResult(intent, LEVEL_TASK);
    }

    // 通过+，跳转到创建页面 创建子任务或任务
    private void showCreateItem(){
        Intent intent;
        if (currentLevel == LEVEL_SUBTASK){
            intent = new Intent(getActivity(), SubTaskChangeActivity.class);
            intent.putExtra("type", CREATE_ITEM);
            intent.putExtra("tid", selectedTask.getId());
            startActivityForResult(intent, LEVEL_SUBTASK);
        } else if (currentLevel == LEVEL_TASK){
            intent = new Intent(getActivity(), TaskChangeActivity.class);
            intent.putExtra("type", CREATE_ITEM);
            startActivityForResult(intent, LEVEL_TASK);
        }
    }
    /*
        任务的增删改
     */
    private void setTask(Intent data, Task task){
        task.setName(data.getStringExtra("name"));
        task.setStart_time((Date) data.getSerializableExtra("start_time"));
        task.setEnd_time( (Date) data.getSerializableExtra("end_time"));
        task.setType(data.getIntExtra("task_type", 0));
    }

    private void createTask(Intent data){
        // 创建task 并设置信息
        Task task = new Task();
        setTask(data, task);
        task.save();
    }

    private void deleteTask(int id){
        // 找到task 并删除
        Task task = DataSupport.find(Task.class, id);
        if (task == null) return;

        task.delete();

        // 同时删除subTask
        List<SubTask> subTasks = DataSupport.where("tid = ?", Integer.toString(id))
                .find(SubTask.class);
        for (SubTask subTask : subTasks) {
            subTask.delete();
        }
    }

    private void changeTask(Intent data){
        // 找到 Task 并设置信息
        int id = data.getIntExtra("id", -1);
        Task task = DataSupport.find(Task.class, id);
        if (task == null) return;

        setTask(data, task);

        task.save();
    }

        /*
            子任务的增删改
         */
        private void setSubTask(Intent data, SubTask subTask){
            subTask.setName(data.getStringExtra("name"));
            subTask.setTid(selectedTask.getId());
            subTask.setFinish(false);
        }


        private void createSubTask(Intent data){
        // 创建subTask 并设置信息
        SubTask subTask = new SubTask();

        setSubTask(data, subTask);

        subTask.save();

        // 修改 task 的子任务个数
        Task belongTask = DataSupport.find(
                Task.class, data.getIntExtra("tid", -1));
        belongTask.setTotalCnt(belongTask.getTotalCnt()+1);
        belongTask.save();
    }

    private void deleteSubTask(int id){
        // 找到subTask 并删除
        SubTask subTask = DataSupport.find(SubTask.class, id);
        if (subTask == null) return;

        subTask.delete();

        // 修改 task 的子任务个数
        Task belongTask = DataSupport.find(Task.class, subTask.getTid());
        belongTask.setTotalCnt(belongTask.getTotalCnt()-1);
        belongTask.save();
    }

    private void changeSubTask(Intent data){
        // 找到subTask 并设置信息
        int id = data.getIntExtra("id", -1);
        SubTask subTask = DataSupport.find(SubTask.class, id);
        if (subTask == null) return;

        setSubTask(data, subTask);

        subTask.save();
    }
}