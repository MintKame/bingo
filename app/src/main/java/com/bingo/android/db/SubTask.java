package com.bingo.android.db;

import org.litepal.crud.DataSupport;

import java.util.UUID;

public class SubTask extends DataSupport {
    private int id;

    private int tid;

    private String name;

    private boolean isFinish; // todo


    public int getId() {
        return id;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }
}