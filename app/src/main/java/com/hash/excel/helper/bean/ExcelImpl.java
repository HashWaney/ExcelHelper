package com.hash.excel.helper.bean;

import com.smile.excelhelper.ExcelBaseEntity;

/**
 * Created by HashWaney on 2019/10/16.
 */

public class ExcelImpl extends ExcelBaseEntity {

    private String extend;
    private String info;
    private String key;
    private String value;

    public ExcelImpl(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String setInfo() {
        return info;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public String getExtend() {
        return extend;
    }


    @Override
    public String toString() {
        return "ExcelImpl{" +
                "key='" + key + '\'' +
                ", extend='" + extend + '\'' +
                ", info='" + info + '\'' +
                ", value='" + value + '\'' +
                '}';
    }


    @Override
    protected boolean isToString() {
        return true;
    }
}
