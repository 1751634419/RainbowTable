package test;

import com.foxrbt.Data;
import com.foxrbt.Function;
import com.foxrbt.RainbowTable;
import com.foxrbt.RainbowTableProcessor;
import com.foxrbt.impl.LReductionFunction;
import com.foxrbt.impl.MD5Function;
import com.foxrbt.impl.MagicReductionFunction;
import com.foxrbt.impl.SReductionFunction;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class Main {
    private static char[] charset = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static int minLength = 8;
    private static int maxLength = 16;

    public static void main(String[] args) {
        RainbowTable table = generateTable();
        RainbowTableProcessor processor = new RainbowTableProcessor(table);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String[] parameters = scanner.nextLine().split(" ");

            if (parameters.length == 0) {
                continue;
            }
            try {
                switch (parameters[0].toLowerCase()) {
                    case "random":
                        if (parameters.length != 2) {
                            System.out.println("Invalid command.");
                            break;
                        }
                        int num = Integer.parseInt(parameters[1]);
                        spawnRandomly(num, processor);
                        break;
                    case "search":
                        if (parameters.length != 2) {
                            System.out.println("Invalid command.");
                            break;
                        }
                        BigInteger hashCode = new BigInteger(parameters[1], 16);
                        byte[] srcCode = hashCode.toByteArray();
                        Data dst = processor.findSource(new Data(srcCode));
                        if (dst != null) {
                            System.out.println("Result: " + dst);
                        } else {
                            System.out.println("A failed attempt.");
                        }
                        break;
                    case "calc":
                        if (parameters.length != 2) {
                            System.out.println("Invalid command.");
                            break;
                        }
                        calc(new Data(parameters[1].getBytes()), processor);
                        break;
                    case "md5":
                        if (parameters.length != 2) {
                            System.out.println("Invalid command.");
                            break;
                        }
                        BigInteger bigInt = new BigInteger(md5f.process(new Data(parameters[1].getBytes())).getData());
                        System.out.println("MD5: " + bigInt.toString(16));
                        break;
                    case "con":
                        spawnContinuously(processor);
                        break;
                    case "save":
                        save(table, "table");
                        break;
                    case "open":
                        table = open();
                        processor = new RainbowTableProcessor(table);
                        break;
                    default:
                        System.out.println("Unrecognized command.");
                        break;
                }
            } catch (NumberFormatException exception) {
                System.out.println("Invalid number.");
            } catch (IOException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }
        }
    }

    private static MD5Function md5f = new MD5Function();
    private static Random random = new Random();

    private static void spawnRandomly(int num, RainbowTableProcessor processor) {
        int repeatedCount = 0;
        for (int i = 0; i < num; i++) {
            int n = (maxLength == minLength ? 0 : random.nextInt(maxLength - minLength)) + minLength;
            char[] arr = new char[n];
            for (int j = 0; j < n; j++) {
                arr[j] = charset[random.nextInt(charset.length)];
            }

            Data data = new Data(new String(arr).getBytes());
            if (processor.getTable().getMap().containsKey(data) || !calc(data, processor)) {
                i--;
                repeatedCount++;

                if (repeatedCount == 200) {
                    System.out.println("Interrupted");
                    return;
                }
                continue;
            }
            repeatedCount = 0;
            System.out.println("Processed raw text: " + new String(arr));
        }
    }

    private static void spawnContinuously(RainbowTableProcessor processor) throws IOException {
        int count = 0;
        while (true) {
            int n = (maxLength == minLength ? 0 : random.nextInt(maxLength - minLength)) + minLength;
            char[] arr = new char[n];
            for (int j = 0; j < n; j++) {
                arr[j] = charset[random.nextInt(charset.length)];
            }

            Data data = new Data(new String(arr).getBytes());
            if (processor.getTable().getMap().containsKey(data)) {
                continue;
            } else if (!calc(data, processor)) {
                System.out.println("Collided");
                continue;
            }
            count++;
            System.out.println("Processed raw text: " + new String(arr));

            if (count % 4000 == 0) {
                save(processor.getTable(), "table");
                System.out.println("Saved");
            }
            if (count % 8000 == 0) {
                save(processor.getTable(), "table.bak");
            }
        }
    }

    private static void foreachPrecalc(char[] arr, int index, RainbowTableProcessor processor) {
        if (index == arr.length) {
            calc(new Data(new String(arr).getBytes()), processor);
            return;
        }

        for (int i = 0; i < charset.length; i++) {
            if (index == 0) {
                System.out.println(charset[i]);
            }

            arr[index] = charset[i];
            foreachPrecalc(arr, index + 1, processor);
        }
    }

    private static boolean calc(Data src, RainbowTableProcessor processor) {
        Data dst = processor.calculate(src);
        if (processor.getTable().getMap().containsValue(dst)) {
            return false;
        }
        processor.save(src, dst);
        return true;
    }

    public static void save(RainbowTable table, String fileName) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
        out.writeInt(table.getReductionFunction().length);
        Map<Data, Data> map = table.getMap();
        Set<Data> keySet = map.keySet();
        out.writeInt(keySet.size());
        for (Data key: keySet) {
            writeData(key, out);
            writeData(map.get(key), out);
        }
        out.close();
    }

    private static void writeData(Data data, ObjectOutputStream out) throws IOException {
        byte[] arr = data.getData();
        out.writeInt(arr.length);
        out.write(arr);
    }

    private static Data readData(ObjectInputStream in) throws IOException {
        byte[] arr = new byte[in.readInt()];
        in.readFully(arr);
        return new Data(arr);
    }

    public static RainbowTable open() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("table"));
        Function[] rf = new Function[in.readInt()];

        for (int i = 0; i < rf.length; i++) {
            if (i % 2 == 0) {
                rf[i] = new LReductionFunction(i, minLength, maxLength, charset);
            } else {
                rf[i] = new SReductionFunction(i, minLength, maxLength, charset);
            }
        }

        int size = in.readInt();
        Map<Data, Data> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            map.put(readData(in), readData(in));
        }
        in.close();
        return new RainbowTable(map, new MD5Function(), rf);
    }

    private static RainbowTable generateTable() {
        Function[] rf = new Function[4096];

        for (int i = 0; i < rf.length; i++) {
            if (i % 2 == 0) {
                rf[i] = new LReductionFunction(i, minLength, maxLength, charset);
            } else {
                rf[i] = new SReductionFunction(i, minLength, maxLength, charset);
            }
        }

        return new RainbowTable(new HashMap<>(), new MD5Function(), rf);
    }
}