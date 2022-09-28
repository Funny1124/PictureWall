package com.trio.picturewall.entity;

import java.util.List;

public class CommentRecords {
    private List<Comment> records;

    public List<Comment> getRecords() {
        return records;
    }

    public void setRecords(List<Comment> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return "Records{" +
                "records=" + records +
                '}';
    }
}
