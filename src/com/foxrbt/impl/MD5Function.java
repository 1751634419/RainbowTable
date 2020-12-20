package com.foxrbt.impl;

import com.foxrbt.Data;
import com.foxrbt.Function;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Function implements Function {
    @Override
    public Data process(Data source) {
        try {
            return new Data(MessageDigest.getInstance("md5")
                    .digest(source.getData()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}