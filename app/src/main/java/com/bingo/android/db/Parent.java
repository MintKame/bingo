package com.bingo.android.db;

import org.litepal.crud.DataSupport;

import java.util.UUID;

public class Parent extends DataSupport {
    private UUID pid  = UUID.randomUUID();// todo

    private String pphone;// todo

    private String name;// todo

    public UUID getPid() {
        return pid;
    }

    public String getPphone() {
        return pphone;
    }

    public void setPphone(String pphone) {
        this.pphone = pphone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
