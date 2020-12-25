package com.foxrbt.impl;

import com.foxrbt.Data;
import com.foxrbt.Function;

import java.math.BigInteger;

public class MagicReductionFunction implements Function {
    private int length;
    private int index;

    public MagicReductionFunction(int length, int index) {
        this.length = length;
        this.index = index;
    }

    @Override
    public Data process(Data source) {
        String raw = new BigInteger(source.getData()).multiply(BigInteger.valueOf(index + 1)).toString(16);
        if (raw.length() < length) {
            StringBuilder sb = new StringBuilder(raw);
            while (sb.length() != length) {
                sb.append(index);
            }
            raw = sb.toString();
        }
        return new Data(raw.substring(0, length).getBytes());
    }
}