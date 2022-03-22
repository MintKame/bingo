package com.bingo.android.db;

import org.litepal.crud.DataSupport;

import java.util.Date;
import java.util.UUID;

public class Task extends DataSupport {
    public static final int TASK_UNFINISH = 0; // 未完成
    public static final int TASK_IN = 1; // 按时完成
    public static final int TASK_OUT = 2; // 超时完成

    // task的类型，对应arrays.xml中的taskType
    public static final int IN_CLASS = 0;
    public static final int OUT_CLASS = 1;
    public static final int SPORT = 2;
    public static final int HOUSEWORK = 3;
    public static final int HOBBY = 4;

    private int id;

    private String name;

    private int state = TASK_UNFINISH; // todo

    private Date start_time;

    private Date end_time;

    private long spend_time = 0; // seconds

    private long finishCnt = 0;  // 已完成子任务数

    private long totalCnt = 0;  // 所有子任务数

    private int type;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public long getSpend_time() {
        return spend_time;
    }

    public void setSpend_time(long spend_time) {
        this.spend_time = spend_time;
    }

    public long getFinishCnt() {
        return finishCnt;
    }

    public void setFinishCnt(long finishCnt) {
        this.finishCnt = finishCnt;
    }

    public long getTotalCnt() {
        return totalCnt;
    }

    public void setTotalCnt(long totalCnt) {
        this.totalCnt = totalCnt;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}