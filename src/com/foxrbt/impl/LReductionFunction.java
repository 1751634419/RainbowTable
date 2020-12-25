package com.foxrbt.impl;

import com.foxrbt.Data;
import com.foxrbt.Function;

public class LReductionFunction implements Function {
    private int index;
    private int minLength;
    private int maxLength;
    private char[] charset;
    private boolean alwaysLongest;

    public LReductionFunction(int index, int minLength, int maxLength, char[] charset, boolean isEndPoint) {
            this.index = index;
            this.minLength = minLength;
            this.maxLength = maxLength;
            this.charset = charset;
            if (isEndPoint || index % 3 == 0) {
                alwaysLongest = true;
            }
    }

    @Override
    public Data process(Data source) {
        byte[] hash = source.getData();
        char[] result = new char[alwaysLongest ? maxLength : index % (maxLength - minLength + 1) + minLength];
        for (int i = 0; i < result.length; i++) {
            int in = i % hash.length;
            // unique results for each reduction function
            hash[in] ^= index;

            result[i] = charset[Math.abs(hash[in]) % charset.length];

            // cancel xor
            hash[in] ^= index;
        }

        return new Data(new String(result).getBytes());
    }
}