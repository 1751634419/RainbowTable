package com.foxrbt;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.*;

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

//    public Data getSource(Data src) {
//        for (int i = 0; i < processorList.size(); i++) {
//            Data solution = processorList.get(i).findSource(src);
//
//            if (solution != null) {
//                return solution;
//            }
//        }
//
//        return null;
//    }

    public Future<Data>[] executeSourceFinding(Data src) {
        ExecutorService service = Executors.newFixedThreadPool(processorList.size());
        Future<Data>[] futures = new Future[processorList.size()];
        for (int i = 0; i < futures.length; i++) {
            futures[i] = service.submit(new SourceFinder(processorList.get(i), src));
        }
        return futures;
    }

    public Data findSource(Data src) throws ExecutionException, InterruptedException {
        Future<Data>[] futures = executeSourceFinding(src);
        do {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                break;
            }
            boolean done = true;
            for (int i = 0; i < futures.length; i++) {
                if (!futures[i].isDone()) {
                    done = false;
                } else if (futures[i].get() != null) {
                    done = true;
                    break;
                }
            }
            if (done) {
                break;
            }
        } while (true);

        for (int i = 0; i < futures.length; i++) {
            if (futures[i].isDone()) {
                Data data = futures[i].get();
                if (data != null) {
                    return data;
                }
            }
        }
        return null;
    }

    public List<RainbowTableProcessor> getProcessorList() {
        return processorList;
    }
}

class SourceFinder implements Callable<Data> {
    private RainbowTableProcessor processor;
    private Data source;

    public SourceFinder(RainbowTableProcessor processor, Data source) {
        this.processor = processor;
        this.source = source;
    }

    @Override
    public Data call() throws Exception {
        return processor.findSource(source);
    }
}