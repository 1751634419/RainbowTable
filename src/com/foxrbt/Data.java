package com.foxrbt;

import java.io.Serializable;
import java.util.Arrays;

public class Data {
    private byte[] data;
    private int hashCode;

    public Data(byte[] data) {
        this.data = data;
        hashCode = Arrays.hashCode(data);
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data rawData = (Data) o;
        return Arrays.equals(data, rawData.data);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return "Data{" +
                "data=" + new String(data) +
                ", hashCode=" + hashCode +
                '}';
    }
}