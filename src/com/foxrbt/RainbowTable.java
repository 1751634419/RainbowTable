package com.foxrbt;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class RainbowTable {
    // source, target
    private Map<Data, Data> map;
    private Function hashFunction;
    private Function[] reductionFunction;

    public RainbowTable(Map<Data, Data> map, Function hashFunction, Function[] reductionFunction) {
        this.map = map;
        this.hashFunction = hashFunction;
        this.reductionFunction = reductionFunction;
    }

    public Function getHashFunction() {
        return hashFunction;
    }

    public Function[] getReductionFunction() {
        return reductionFunction;
    }

    public Map<Data, Data> getMap() {
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RainbowTable that = (RainbowTable) o;
        return Objects.equals(map, that.map) &&
                Objects.equals(hashFunction, that.hashFunction) &&
                Arrays.equals(reductionFunction, that.reductionFunction);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(map, hashFunction);
        result = 31 * result + Arrays.hashCode(reductionFunction);
        return result;
    }
}