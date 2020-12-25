package test;

import com.foxrbt.*;
import com.foxrbt.impl.LReductionFunction;
import com.foxrbt.impl.MD5Function;
import com.foxrbt.impl.MagicReductionFunction;
import com.foxrbt.impl.SReductionFunction;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class Main {
    private static char[] charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-=_+".toCharArray();
    private static int minLength = 4;
    private static int maxLength = 20;
    private static MultiTableProcessor mtp;

    static {
        mtp = new MultiTableProcessor(new RainbowTable[] { generateTable() });
    }

    public static String byteToHex(byte[] bytes){
        String strHex = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < bytes.length; n++) {
            strHex = Integer.toHexString(bytes[n] & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex); // 每个字节由两个字符表示，位数不够，高位补0
        }
        return sb.toString().trim();
    }

    public static byte[] hexToByte(String hex){
        int m = 0, n = 0;
        int byteLen = hex.length() / 2; // 每两个字符描述一个字节
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
            ret[i] = Byte.valueOf((byte)intVal);
        }
        return ret;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String[] parameters = scanner.nextLine().split(" ");

            if (parameters.length == 0) {
                continue;
            }
            try {
                switch (parameters[0].toLowerCase()) {
//                    case "random":
//                        if (parameters.length != 2) {
//                            System.out.println("Invalid command.");
//                            break;
//                        }
//                        int num = Integer.parseInt(parameters[1]);
//                        spawnRandomly(num, processor);
//                        break;
                    case "search":
                        if (parameters.length != 2) {
                            System.out.println("Invalid command.");
                            break;
                        }
                        byte[] srcCode = hexToByte(parameters[1]);
                        Data dst = mtp.findSource(new Data(srcCode));
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
                        mtp.precalc(new Data(parameters[1].getBytes()));
                        break;
                    case "md5":
                        if (parameters.length != 2) {
                            System.out.println("Invalid command.");
                            break;
                        }
                        byte[] data = md5f.process(new Data(parameters[1].getBytes())).getData();
//                        BigInteger bigInt = new BigInteger(data);
//                        System.out.println("MD5: " + bigInt.toString(16));
                        System.out.println(byteToHex(data));
                        break;
//                    case "con":
//                        spawnContinuously();
//                        break;
                    case "save":
                        save("table");
                        break;
                    case "open":
                        mtp = open();
                        break;
                    case "import":
                        if (parameters.length != 2) {
                            System.out.println("Invalid command.");
                            break;
                        }

                        importFile(parameters[1]);
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

    private static void importFile(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        int count = 0;
        while ((line = reader.readLine()) != null) {
            if (!isValid(line)) {
                System.out.println("Invalid line: " + line);
                continue;
            }

            count++;
            int rv = mtp.precalc(new Data(line.getBytes()));
            if (rv == 1) {
                System.out.println("Size(" + mtp.getProcessorList().size() + ")/Count(" + count + ") Successful processed raw text: " + line);
            } else if (rv == -1) {
                System.out.println("Collided text: " + line + "... generating a new table");
                mtp.getProcessorList().add(new RainbowTableProcessor(generateTable()));
                mtp.precalc(new Data(line.getBytes()));
            } else { // rv == 0
                System.out.println("Imported text: " + line);
            }

            if (count % 4000 == 0) {
                save("table");
            }
            if (count % 8000 == 0) {
                save("table.bak");
            }
        }
        System.out.println("Done :)");
    }

    private static boolean isValid(String str) {
        char[] arr = str.toCharArray();
        if (arr.length < minLength || arr.length > maxLength) {
            return false;
        }

        for (int i = 0; i < arr.length; i++) {
            boolean f = false;
            for (int j = 0; j < charset.length; j++) {
                if (charset[j] == arr[i]) {
                    f = true;
                    break;
                }
            }
            if (!f) {
                return false;
            }
        }

        return true;
    }

//    private static void spawnRandomly(int num, RainbowTableProcessor processor) {
//        int repeatedCount = 0;
//        for (int i = 0; i < num; i++) {
//            int n = (maxLength == minLength ? 0 : random.nextInt(maxLength - minLength)) + minLength;
//            char[] arr = new char[n];
//            for (int j = 0; j < n; j++) {
//                arr[j] = charset[random.nextInt(charset.length)];
//            }
//
//            Data data = new Data(new String(arr).getBytes());
//            if (processor.getTable().getMap().containsKey(data) || !calc(data, processor)) {
//                i--;
//                repeatedCount++;
//
//                if (repeatedCount == 200) {
//                    System.out.println("Interrupted");
//                    return;
//                }
//                continue;
//            }
//            repeatedCount = 0;
//            System.out.println("Processed raw text: " + new String(arr));
//        }
//    }
//
//    private static void spawnContinuously(RainbowTableProcessor processor) throws IOException {
//        int count = 0;
//        while (true) {
//            int n = (maxLength == minLength ? 0 : random.nextInt(maxLength - minLength)) + minLength;
//            char[] arr = new char[n];
//            for (int j = 0; j < n; j++) {
//                arr[j] = charset[random.nextInt(charset.length)];
//            }
//
//            Data data = new Data(new String(arr).getBytes());
//            if (processor.getTable().getMap().containsKey(data)) {
//                continue;
//            } else if (!mtp.precalc(data, processor)) {
//                System.out.println("Collided");
//                continue;
//            }
//            count++;
//            System.out.println("Processed raw text: " + new String(arr));
//
//            if (count % 4000 == 0) {
//                save(processor.getTable(), "table");
//                System.out.println("Saved");
//            }
//            if (count % 8000 == 0) {
//                save(processor.getTable(), "table.bak");
//            }
//        }
//    }
//
//    private static void foreachPrecalc(char[] arr, int index, RainbowTableProcessor processor) {
//        if (index == arr.length) {
//            calc(new Data(new String(arr).getBytes()), processor);
//            return;
//        }
//
//        for (int i = 0; i < charset.length; i++) {
//            if (index == 0) {
//                System.out.println(charset[i]);
//            }
//
//            arr[index] = charset[i];
//            foreachPrecalc(arr, index + 1, processor);
//        }
//    }

    public static void save(String fileName) throws IOException {
        List<RainbowTableProcessor> processors = mtp.getProcessorList();
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
        out.writeInt(processors.size());
        for (int i = 0; i < processors.size(); i++) {
            write(out, processors.get(i).getTable());
        }
        out.close();
    }

    private static void write(ObjectOutputStream out, RainbowTable table) throws IOException {
        out.writeInt(table.getReductionFunction().length);
        Map<Data, Data> map = table.getMap();
        Set<Data> keySet = map.keySet();
        out.writeInt(keySet.size());

        for (Data key: keySet) {
            writeData(key, out);
            writeData(map.get(key), out);
        }
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

    private static RainbowTable readTable(ObjectInputStream in) throws IOException {
        Function[] rf = generateRF(in.readInt());

        int size = in.readInt();
        Map<Data, Data> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            map.put(readData(in), readData(in));
        }

        return new RainbowTable(map, md5f, rf);
    }

    public static MultiTableProcessor open() throws IOException, ClassNotFoundException {
        List<RainbowTableProcessor> processors = new Vector<>();
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("table"));
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            processors.add(new RainbowTableProcessor(readTable(in)));
        }
        in.close();

        return new MultiTableProcessor(processors);
    }

    private static RainbowTable generateTable() {
        return new RainbowTable(new HashMap<>(), new MD5Function(), generateRF(4096));
    }

    private static Function[] generateRF(int number) {
        Function[] rf = new Function[number];

        for (int i = 0; i < rf.length; i++) {
            if (i % 5 != 0) {
                rf[i] = new LReductionFunction(i, minLength, maxLength, charset, i == rf.length - 1);
            } else {
                rf[i] = new MagicReductionFunction(maxLength, i);
            }
        }

        return rf;
    }
}