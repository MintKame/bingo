package com.bingo.android.db;

import org.litepal.crud.DataSupport;

import java.util.UUID;

public class Child extends DataSupport {
    private UUID cid = UUID.randomUUID();// todo

    // 记录
    private int point = 0;// todo

    private int finish_cnt = 0;// todo

    // 注册时填写
    private String cphone;// todo

    private String name;// todo

    private boolean gender;// todo

    private int age;// todo

    private int npc;// todo

    public UUID getCid() {
        return cid;
    }

    public String getCphone() {
        return cphone;
    }

    public void setCphone(String cphone) {
        this.cphone = cphone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getNpc() {
        return npc;
    }

    public void setNpc(int npc) {
        this.npc = npc;
    }

    public int getFinish_cnt() {
        return finish_cnt;
    }

    public void setFinish_cnt(int finish_cnt) {
        this.finish_cnt = finish_cnt;
    }
}
