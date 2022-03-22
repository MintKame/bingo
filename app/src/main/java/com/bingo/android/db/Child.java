package com.bingo.android.db;

import org.litepal.crud.DataSupport;

import java.util.UUID;

public class Child extends DataSupport {
    private UUID cid = UUID.randomUUID();

    // todo 注册时填写
    private String cphone;

    private String name;

    private boolean gender;

    private int age;

    private int npc;

    private long point = 0; // 记录 当前可用点数

    // todo 按类型，记录完成的任务数
    private long finish_total = 0;
    private long finish_in = 0;
    private long finish_out = 0;
    private long finish_sport = 0;
    private long finish_housework = 0;
    private long finish_hobby = 0;


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

    public long getPoint() {
        return point;
    }

    public void setPoint(long point) {
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

    public long getFinish_total() {
        return finish_total;
    }

    public void setFinish_total(long finish_cnt) {
        this.finish_total = finish_cnt;
    }

    public long getFinish_in() {
        return finish_in;
    }

    public void setFinish_in(long finish_in) {
        this.finish_in = finish_in;
    }

    public long getFinish_out() {
        return finish_out;
    }

    public void setFinish_out(long finish_out) {
        this.finish_out = finish_out;
    }

    public long getFinish_sport() {
        return finish_sport;
    }

    public void setFinish_sport(long finish_sport) {
        this.finish_sport = finish_sport;
    }

    public long getFinish_housework() {
        return finish_housework;
    }

    public void setFinish_housework(long finish_housework) {
        this.finish_housework = finish_housework;
    }

    public long getFinish_hobby() {
        return finish_hobby;
    }

    public void setFinish_hobby(long finish_hobby) {
        this.finish_hobby = finish_hobby;
    }
}
