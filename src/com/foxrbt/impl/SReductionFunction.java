package com.foxrbt.impl;

import com.foxrbt.Data;
import com.foxrbt.Function;

import java.math.BigInteger;
import java.util.Arrays;

public class SReductionFunction implements Function {
    private int index;
    private int minLength;
    private int maxLength;
    private char[] charset;

    public SReductionFunction(int index, int minLength, int maxLength, char[] charset) {
        this.index = index;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.charset = charset;
    }

    @Override
    public Data process(Data source) {
        BigInteger bigint = new BigInteger(source.getData()).xor(BigInteger.valueOf(index)).abs();
        String str = bigint.toString(charset.length);
        char[] arr = str.toCharArray();
        char[] dst = new char[(arr[arr.length - 1]) % (maxLength - minLength + 1) + minLength];
        for (int i = 0; i < dst.length; i++) {
            dst[i] = getChar(arr[index % 2 == 0 ? i : (dst.length - i - 1)]);
        }
        return new Data(new String(dst).getBytes());
//        BigInteger bigint = new BigInteger(source.getData()).add(BigInteger.valueOf(index));
//        char[] dst = new char[(int) bigint.mod(BigInteger.valueOf(maxLength - minLength + 1)).longValue() + minLength];
//        for (int i = 0; i < dst.length; i++) {
//            dst[i] = charset[(int) bigint.xor(BigInteger.TWO.pow(i)).mod(BigInteger.valueOf(charset.length)).longValue()];
//        }
//        return new Data(new String(dst).getBytes());
    }

    private char getChar(char c) {
        if (c >= 48 && c <= 57) {
            return charset[(int) c - 48];
        } else {
            return charset[(int) c - 97 + 10];
        }
    }
}