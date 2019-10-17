package com.hash.excel.helper.bean;

import com.smile.excelhelper.ExcelBaseEntity;

/**
 * Created by HashWaney on 2019/10/17.
 */

public class Test extends ExcelBaseEntity {
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    protected boolean isToString() {
        return false;
    }
}
