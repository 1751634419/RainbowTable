package com.foxrbt.impl;

import com.foxrbt.Data;
import com.foxrbt.Function;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha1Function implements Function {
    @Override
    public Data process(Data source) {
        try {
            return new Data(MessageDigest.getInstance("sha1")
                    .digest(source.getData()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "SHA1Hash";
    }
}