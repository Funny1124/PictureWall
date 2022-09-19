package com.trio.picturewall.entity;

import java.util.Arrays;
import java.util.List;

public class Records {
    private List<MyPosts> records;

    public List<MyPosts> getRecords() {
        return records;
    }

    public void setRecords(List<MyPosts> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return "Records{" +
                "records=" + records +
                '}';
    }
}
