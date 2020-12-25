package com.foxrbt;

import java.util.*;

public class RainbowTableProcessor {
    private RainbowTable table;

    public RainbowTableProcessor(RainbowTable table) {
        this.table = table;
    }

    public RainbowTable getTable() {
        return table;
    }

    public Data calculate(Data source) {
        Function hashFunction = table.getHashFunction();
        Function[] reductionFunction = table.getReductionFunction();
        Data target = source;
        for (int i = 0; i < reductionFunction.length; i++) {
            target = hashFunction.process(target);
//            System.out.print(target + " ");
            target = reductionFunction[i].process(target);
//            System.out.println(target);
        }
        return target;
    }

    public int save(Data source, Data target) {
        if (table.getMap().containsKey(source)) {
            return 0;
        } else if (table.getMap().containsValue(target)) {
            return -1;
        }

        table.getMap().put(source, target);
        return 1;
    }

    public Data findSource(Data data) {
        Function hashFunction = table.getHashFunction();
        Function[] reductionFunction = table.getReductionFunction();
        Map<Data, Data> map = table.getMap();

        for (int k = reductionFunction.length - 1; k >= 0; k--) {
            Data v = reductionFunction[k].process(data);
            // loop until the end point is reached
            for (int i = k + 1; i < reductionFunction.length; i++) {
                v = hashFunction.process(v);
                v = reductionFunction[i].process(v);
            }

            if (map.containsValue(v)) {
                // retrace
                Set<Data> dataSet = getKeysByLoop(map, v);
                for (Data s : dataSet) {
                    for (int i = 0; i < k; i++) {
                        s = hashFunction.process(s);
                        s = reductionFunction[i].process(s);
                    }

                    if (table.getHashFunction().process(s).equals(data)) {
                        return s;
                    }
                }
            }
        }

        return null;
    }

    private <K, V> Set<K> getKeysByLoop(Map<K, V> map, V value) {
        Set<K> set = new HashSet<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (Objects.equals(entry.getValue(), value)) {
                set.add(entry.getKey());
            }
        }
        return set;
    }
}