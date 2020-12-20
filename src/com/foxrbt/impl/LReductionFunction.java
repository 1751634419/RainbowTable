package com.foxrbt.impl;

import com.foxrbt.Data;
import com.foxrbt.Function;

public class LReductionFunction implements Function {
    private int index;
    private int minLength;
    private int maxLength;
    private char[] charset;

    public LReductionFunction(int index, int minLength, int maxLength, char[] charset) {
            this.index = index;
            this.minLength = minLength;
            this.maxLength = maxLength;
            this.charset = charset;
    }

    @Override
    public Data process(Data source) {
        byte[] hash = source.getData();
        char[] result = new char[index % (maxLength - minLength + 1) + minLength];
        for (int i = 0; i < result.length; i++) {
            // unique results for each reduction function
            hash[i] ^= index;

            result[i] = charset[Math.abs(hash[i]) % charset.length];

            // cancel xor
            hash[i] ^= index;
        }

        return new Data(new String(result).getBytes());
    }
}