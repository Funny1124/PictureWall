package com.trio.picturewall.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
