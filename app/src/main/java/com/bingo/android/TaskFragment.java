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
    public static final int LEVEL_TASK = 1;

    public static final int LEVEL_SUBTASK = 2;

    public static final int CREATE_ITEM = 3;

    public static final int CHANGE_ITEM = 4;

    public static final int DELETE_ITEM = 5;

    static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");    // 转换的时间格式

    // view
    private ListView listView;

    private ArrayAdapter<String> adapter;

    private ProgressDialog progressDialog;

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
                    // todo 由于上次完成子任务后，直接退出，子任务列表未更新，get获取的仍未未完成状态
                    selectedSubTask = DataSupport.find(SubTask.class, selectedSubTask.getId());
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
                    updateSubTask(subTask);
                }else if (currentLevel == LEVEL_TASK){
                    Task task = taskList.get(position);
                    updateTask(task);
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
        finishButton.setOnClickListener((View v)->{
            if (currentLevel != LEVEL_SUBTASK){
                Toast.makeText(getContext(), "错误：未选择任务", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedTask = DataSupport.find(Task.class, selectedTask.getId());
            if (selectedTask.getState() != Task.TASK_UNFINISH){
                Toast.makeText(getContext(), "错误：已完成", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedTask.getFinishCnt() < selectedTask.getTotalCnt()){
                Toast.makeText(getContext(), "未完成所有子任务", Toast.LENGTH_SHORT).show();
                return;
            }
            // 计算奖励
            long points = selectedTask.getTotalCnt(); // 子任务数
            boolean inTime =  new Date().before(selectedTask.getEnd_time()); // 按时
            if (!inTime){
                points *= 0.5;
                selectedTask.setState(Task.TASK_OUT);
            }else
                selectedTask.setState(Task.TASK_IN);
            selectedTask.save();
            Toast.makeText(getContext(), "获得奖励：" + points, Toast.LENGTH_SHORT).show();
            MainActivity.child.setPoint((int) (MainActivity.child.getPoint() + points));
            MainActivity.child.save();
        });

        showTasks(); // 开始查询

        // 显示桌面宠物
        createPet();

        return view;
    }

    // 菜单栏: 添加项目 返回
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_item:
                Intent intent;
                switch (currentLevel){
                    case LEVEL_SUBTASK:
                        intent = new Intent(getActivity(), SubTaskChangeActivity.class);
                        intent.putExtra("type", CREATE_ITEM);
                        intent.putExtra("tid", selectedTask.getId());
                        startActivityForResult(intent, LEVEL_SUBTASK);
                        break;
                    case LEVEL_TASK:
                        intent = new Intent(getActivity(), TaskChangeActivity.class);
                        intent.putExtra("type", CREATE_ITEM);
                        startActivityForResult(intent, LEVEL_TASK);
                        break;
                }
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
        int id = data.getIntExtra("id", -1);
        switch (requestCode) {
            case LEVEL_SUBTASK:
                // 找到subTask
                // todo
                SubTask subTask = null;
                if (type == CREATE_ITEM)
                    subTask = new SubTask();
                else if (id != -1 && (type == CHANGE_ITEM || type == DELETE_ITEM ) ) {
                    for (int i = 0; i < subTaskList.size(); i++) {
                        subTask = subTaskList.get(i);
                        if (subTask != null && subTask.getId() == id) {
                            break;
                        }
                    }
                    if (subTask == null || subTask.getId() != id)
                        return;
                }
                else
                    return;
                // 处理subTask：增删改; task的子任务数
                Task belongTask = DataSupport.find(Task.class, data.getIntExtra("tid", -1));
                long totalCnt = -1;
                if (belongTask != null)
                    totalCnt = belongTask.getTotalCnt();

                if (type == DELETE_ITEM){
                    subTask.delete();
                    totalCnt--;
                }else{
                    if (type == CREATE_ITEM)
                        totalCnt++;
                    subTask.setName(data.getStringExtra("name"));
                    subTask.setTid(selectedTask.getId());
                    subTask.setFinish(false);
                    subTask.save();
                }
                belongTask.setTotalCnt(totalCnt);
                belongTask.save();
                showSubTasks();
                break;
            case LEVEL_TASK:
                // 找到Task
                Task task = null;
                if (type == CREATE_ITEM)
                    task = new Task();
                else if (id != -1 && (type == CHANGE_ITEM || type == DELETE_ITEM ) ) {
                    for (int i = 0; i < taskList.size(); i++) {
                        task = taskList.get(i);
                        if (task != null && task.getId() == id) {
                            break;
                        }
                    }
                    if (task == null || task.getId() != id)
                        return;
                }
                else
                    return;
                // 处理Task：增删改
                if (type == DELETE_ITEM){
                    task.delete();
                    //todo 同时删除subTask
                }else {
                    task.setName(data.getStringExtra("name"));
                    try { // 设置起止时间
                        task.setStart_time(format.parse(data.getStringExtra("start_time")));
                        task.setEnd_time(format.parse(data.getStringExtra("end_time")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    task.save();
                }
                showTasks();
                break;
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
            dataList.add(subTask.getName());
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
    private void updateSubTask(SubTask subTask){
        Intent intent = new Intent(getActivity(), SubTaskChangeActivity.class);
        intent.putExtra("type", CHANGE_ITEM);
        intent.putExtra("id", subTask.getId());
        intent.putExtra("tid", selectedTask.getId());

        intent.putExtra("name", subTask.getName());
        startActivityForResult(intent, LEVEL_SUBTASK);
    }

    // 长按  任务，跳转到修改页面
    private void updateTask(Task task){
        Intent intent = new Intent(getActivity(), TaskChangeActivity.class);
        intent.putExtra("type", CHANGE_ITEM);
        intent.putExtra("id", task.getId());
        intent.putExtra("name", task.getName());

        intent.putExtra("start_time", format.format(task.getStart_time()));
        intent.putExtra("end_time", format.format(task.getEnd_time()));
        startActivityForResult(intent, LEVEL_TASK);
    }

    // todo 服务器 * 3 method
    /**
     *  从服务器上查询所有数据。
     */
    private void update(){
        String address = "http://localhost:8080/";

//            queryFromServer(address, "taskGroup");

//            int taskGroupCode = selectedTaskGroup.getTaskGroupCode();
//            String address = "http://guolin.tech/api/china/" + taskGroupCode;
//            queryFromServer(address, "task");

        // 去服务器查询数据
//            int taskGroupCode = selectedTaskGroup.getTaskGroupCode();
//            int taskCode = selectedTask.getTaskCode();
//            String address = "http://guolin.tech/api/china/" + taskGroupCode + "/" + taskCode;
//            queryFromServer(address, "subTask");
    }

    /**
     *  从服务器上查询某类数据，存到本地数据库。
     */
    private void queryFromServer(String address, final String type) {
        // 发送请求
//        HttpUtil.sendOkHttpRequest(address, new Callback() {
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String responseText = response.body().string();
//                boolean result = false;
        // 根据查询的数据类型，处理响应，存到数据库
//                if ("taskGroup".equals(type)) {
//                    result = Utility.handleTaskGroupResponse(responseText);
//                } else if ("task".equals(type)) {
//                    result = Utility.handleTaskResponse(responseText, selectedTaskGroup.getId());
//                } else if ("subTask".equals(type)) {
//                    result = Utility.handleSubTaskResponse(responseText, selectedTask.getId());
//                }
//                if (result) {
//                    // 通过runOnUiThread()方法回到主线程处理逻辑
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            closeProgressDialog();
        // 重新在本地查询并显示之前去服务器查的数据
//                            if ("taskGroup".equals(type)) {
//                                queryTaskGroups();
//                            } else if ("task".equals(type)) {
//                                queryTasks();
//                            } else if ("subTask".equals(type)) {
//                                showSubTasks();
//                            }
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                // 通过runOnUiThread()方法回到主线程处理逻辑
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        closeProgressDialog();
//                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /** 显示桌面宠物 */ // todo pet
    private void createPet(){
    /*
        var layoutParam = new WindowManager.LayoutParams().apply {
            //设置大小 自适应
            width = WRAP_CONTENT;
            height = WRAP_CONTENT;
            flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL || WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        };
// 新建悬浮窗控件
        View floatRootView = LayoutInflater.from(this).inflate(R.layout.activity_float_item, null);
//设置拖动事件
        floatRootView.setOnTouchListener(View.OnTouchListener ()->{
            return false;
        });
// 将悬浮窗控件添加到WindowManager
        windowManager.addView(floatRootView, layoutParam);
    */

}
}