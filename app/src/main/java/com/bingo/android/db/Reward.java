package com.bingo.android.db;

import org.litepal.crud.DataSupport;

import java.util.UUID;

public class Reward extends DataSupport {
    private int id;

    private UUID cid;

    private String name;

    private int take_points;

    private boolean state; // todo

    // todo 常规奖励，一次奖励

    public int getId() {
        return id;
    }

    public UUID getCid() {
        return cid;
    }

    public void setCid(UUID cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTake_points() {
        return take_points;
    }

    public void setTake_points(int take_points) {
        this.take_points = take_points;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
