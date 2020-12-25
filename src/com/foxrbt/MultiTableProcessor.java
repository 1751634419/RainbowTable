package com.foxrbt;

import java.util.List;
import java.util.Vector;

public class MultiTableProcessor {
    private List<RainbowTableProcessor> processorList;

    public MultiTableProcessor(List<RainbowTableProcessor> processorList) {
        this.processorList = processorList;
    }

    public MultiTableProcessor(RainbowTable[] tables) {
        processorList = new Vector<>();
        for (int i = 0; i < tables.length; i++) {
            processorList.add(new RainbowTableProcessor(tables[i]));
        }
    }

    public int precalc(Data src) {
        for (int i = 0; i < processorList.size(); i++) {
            Data dst = processorList.get(i).calculate(src);
            int rv = processorList.get(i).save(src, dst);
            if (rv == 1) {
                return 1;
            } else if (rv == 0) { // existed source data
                return 0;
            }
        }

        return -1;
    }

    public Data findSource(Data src) {
        for (int i = 0; i < processorList.size(); i++) {
            Data solution = processorList.get(i).findSource(src);

            if (solution != null) {
                return solution;
            }
        }

        return null;
    }

    public List<RainbowTableProcessor> getProcessorList() {
        return processorList;
    }
}