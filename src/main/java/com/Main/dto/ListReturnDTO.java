package com.Main.dto;

import java.util.List;

public class ListReturnDTO<T> {
    private List<T> list;

    public ListReturnDTO() {}

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {}
}
