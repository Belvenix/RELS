package pl.RELS.FileChannelTest;

import pl.RELS.Offer.Offer;
import pl.RELS.Offer.OfferGenerator;
import pl.RELS.User.Seller;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.nio.channels.Channels.newInputStream;
import static java.nio.channels.Channels.newOutputStream;

public class FileHandler {

    private static final String LSEP = System.getProperty("line.separator");
    private static final String FSEP = System.getProperty("file.separator");
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String args[]) throws IOException, InterruptedException {

        String filePath = "testing" + FSEP + "folder" + FSEP + "test2.bin";
        //testWritebin(filePath);
        //testReadbin(filePath);
        System.out.println(filePath);

    }



    public static void saveCLI(ArrayList<Offer> arr){
        System.out.println("Now please specify the path where you want to save the offers (Base folder will be 'res\\' folder in project directory)");
        String baseUserDir = "res\\";
        String path = SCANNER.next();
        while (true){
            System.out.println("Specify file with the extension. Only .txt, .dat (binary) and .ser (serialized) are available:");
            String token = SCANNER.next().toLowerCase();
            if (token.endsWith(".txt")){
                System.out.println("You choose txt extension.");
                savetxt(arr,  baseUserDir + path, token);
                break;
            }
            else if(token.endsWith(".dat")){
                System.out.println("You choose binary extension.");
                savebin(arr, baseUserDir + path, token);
                break;
            }
            else if (token.endsWith(".ser")){
                System.out.println("You choose to serialize the data.");
                saveser(arr, baseUserDir + path, token);
                break;
            }
            else{
                System.out.println("The filename with extension you specified [" + token + "] is incorrect. Please try again.");
            }
        }
    }

    public static ArrayList<Offer> loadCLI(){
        System.out.println("Now please specify the path from where you want to load the offers from (Base folder will be " +
                " 'res\\' folder in project directory): ");
        String path = SCANNER.next();
        ArrayList<Offer> ret;
        while (true){
            System.out.println("Specify file with the extension. Only .txt, .dat (binary) and .ser (serialized) are available:");
            String token = SCANNER.next().toLowerCase();
            if (token.endsWith(".txt")){
                System.out.println("You choose txt extension.");
                ret = loadtxt("res\\" + path, token);
                break;
            }
            else if(token.endsWith(".dat")){
                System.out.println("You choose binary extension.");
                ret = loadbin("res\\" + path, token);
                break;
            }
            else if (token.endsWith(".ser")){
                System.out.println("You choose to serialize the data.");
                ret = loadser("res\\" + path, token);
                break;
            }
            else{
                System.out.println("The filename with extension you specified [" + token + "] is incorrect. Please try again.");
            }
        }
        return ret;
    }

    private static void createDirs(String path){
        String dir = Paths.get("").toAbsolutePath().toString() + "\\" + path;
        File directories = new File(dir);
        if(!directories.exists()){
            directories.mkdirs();
        }
    }

    public static void savetxt(ArrayList<Offer> offers, String path, String filename){
        createDirs(path);
        File save = new File(path + FSEP + filename);
        if(save.exists()){
            save.delete();
        }
        try (FileChannel fc = FileChannel.open((save).toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             OutputStream out = newOutputStream(fc)) {
            FileLock lock = fc.tryLock();
            if (lock != null) {
                PrintWriter writer = new PrintWriter(out);
                for(Offer o : offers){
                    writer.println(o.toString());
                }
                writer.close();
                System.out.println("You have successfully saved the file " + filename);
            }
            else{
                System.out.println("The file is already locked!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void savebin(ArrayList<Offer> offers, String path, String filename){
        createDirs(path);
        File save = new File(path + FSEP + filename);
        if(save.exists()){
            save.delete();
        }
        try (FileChannel fc = FileChannel.open((save).toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             OutputStream out = newOutputStream(fc)) {
            FileLock lock = fc.tryLock();
            if (lock != null) {
                for (Offer o : offers) {
                    byte[] bytes = (o.toString() + LSEP).getBytes();
                    out.write(bytes);
                }
                System.out.println("You have successfully saved the file " + filename);
            }
            else{
                System.out.println("The file is already locked!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveser(ArrayList<Offer> offers, String path, String filename){
        createDirs(path);
        File save = new File(path + FSEP + filename);
        if(save.exists()){
            save.delete();
        }
        try(FileChannel fc = FileChannel.open((save).toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            OutputStream out = newOutputStream(fc); ObjectOutputStream  oos = new ObjectOutputStream (out)) {

            FileLock lock = fc.tryLock();
            if (lock != null) {
                oos.writeObject(offers);
                System.out.println("You have successfully saved the file " + filename);
            }
            else{
                System.out.println("The file is already locked!");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }

    public static ArrayList<Offer> loadtxt(String path, String filename){
        createDirs(path);
        ArrayList<Offer> ret = new ArrayList<>();
        String filepath = path + FSEP + filename;
        File file = new File(filepath);
        if (file.exists()) {
            try (RandomAccessFile reader = new RandomAccessFile(filepath, "rw");
                 FileChannel fc = reader.getChannel();
                 BufferedReader br = new BufferedReader(Channels.newReader(fc, "UTF-8"))) {
                FileLock lock = fc.tryLock();
                if (lock != null) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                    System.out.println("You have successfully loaded the file " + filename);
                } else {
                    System.out.println("The file is already locked!");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("The file specified - " + filepath + " - does not exist");
        }

        return ret;
    }

    public static ArrayList<Offer> loadbin(String path, String filename){
        ArrayList<Offer> ret = new ArrayList<>();
        String filepath = path + FSEP + filename;
        File file = new File(filepath);
        if (file.exists()) {
            try (RandomAccessFile reader = new RandomAccessFile(filepath, "rw");
                 FileChannel fc = reader.getChannel();
                 InputStream in = newInputStream(fc)) {
                FileLock lock = fc.tryLock();
                if (lock != null) {
                    int ch;
                    String row = "";
                    while ((ch = in.read()) != -1) {
                        if ((char) ch != '\n') {
                            row += (char) ch;
                        } else {
                            ret.add(parseline(row));
                            row = "";
                        }
                    }
                    System.out.println("You have successfully loaded the file " + filename);
                } else {
                    System.out.println("The file is already locked!");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("The file specified - " + filepath + " - does not exist");
        }

        return ret;
    }

    public static ArrayList<Offer> loadser(String path, String filename){
        ArrayList<Offer> ret = new ArrayList<>();
        String filepath = path + "\\" + filename;
        File file = new File(filepath);
        if (file.exists()){
            try(FileChannel fc = FileChannel.open((new File(path + FSEP + filename)).toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE);
                InputStream is = newInputStream(fc);
                ObjectInputStream in = new ObjectInputStream(is)){
                FileLock lock = fc.tryLock();
                if(lock != null){
                    ret = (ArrayList<Offer>) in.readObject();
                    System.out.println("You have successfully loaded the file " + filename);
                }
                else{
                    System.out.println("The file is already locked!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch ( ClassNotFoundException e){
                System.out.println("The file is corrupted. Please repair it and try again.");
            }
        }
        else {
            System.out.println("The file specified - " + filepath + " - does not exist");
        }
        return ret;
    }

    private static Offer parseline(String line){
        String [] st = line.split("\\|");
        Map<String, String> map = new HashMap<>();
        if(st.length == 12) {
            for (String data : st) {
                String[] keyValue = data.split("=");
                map.put(keyValue[0], keyValue[1]);
            }
            Timestamp ts1 = new Timestamp( Long.parseLong(map.get("issueDate")));
            Timestamp ts2 = new Timestamp(Long.parseLong(map.get("expirationDate")));
            String adr = map.get("address");
            double p = Double.parseDouble(map.get("price"));
            Offer.OfferType t = Offer.OfferType.valueOf(map.get("type"));
            long oId = Long.parseLong(map.get("offerId"));
            long uId = Long.parseLong(map.get("userId"));
            Offer.FloorType f = Offer.FloorType.valueOf(map.get("floor"));
            boolean furnish = Boolean.parseBoolean(map.get("isFurnished"));
            double s = Double.parseDouble(map.get("surface"));
            double r = Double.parseDouble(map.get("rooms"));
            String desc = map.get("description");
            return new Offer(ts1, ts2, adr, p, t, oId, uId, f, furnish, s, r, desc);
        }
        else{
            return null;
        }


    }

    private static void readByteBuffer(String filePath) throws IOException, InterruptedException {
        File file = new File(filePath);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file.getAbsolutePath(), "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        System.out.println("File channel opened for read. Acquiring lock...");
        FileLock lock = fileChannel.lock(0, Long.MAX_VALUE, false);
        System.out.println("Lock acquired.");

        ByteBuffer buffer = ByteBuffer.allocate(20);
        int noOfBytesRead = fileChannel.read(buffer);
        System.out.println("Buffer contents: ");

        while (noOfBytesRead != -1) {

            buffer.flip();
            System.out.print("    ");

            while (buffer.hasRemaining()) {

                System.out.print((char) buffer.get());
            }

            System.out.println(" ");

            buffer.clear();
            Thread.sleep(1000);
            noOfBytesRead = fileChannel.read(buffer);
        }

        fileChannel.close();
        System.out.print("Closing the channel and releasing lock.");
    }


    private static void testWritebin(String filePath) {
        try (FileChannel fc = FileChannel.open((new File(filePath)).toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE);
             OutputStream out = newOutputStream(fc)) {
            FileLock lock = fc.tryLock();
            if (lock != null) {
                out.write("tkudkutdtufjufvnfvrdhtdtrwsgtdutdjndnydjtfjuysatyqwu".getBytes());
                Thread.sleep(100);
            }
            else{
                System.out.println("The file is already locked!");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void testReadbin(String path, String filename) {
        ArrayList<Offer> ret = new ArrayList<>();
        String filepath = path + LSEP + filename;
        try (RandomAccessFile reader = new RandomAccessFile(filepath, "rw");
             FileChannel fc = reader.getChannel();
             InputStream in = newInputStream(fc)) {
            FileLock lock = fc.tryLock();
            if(lock != null){
                int ch;
                String row ="";
                while ((ch = in.read()) != -1){
                    if((char) ch != '\n'){
                        row += (char) ch;
                    }
                    else{
                        ret.add(parseline(row));
                        row = "";
                    }
                }
                System.out.println("You have successfully loaded the file " + filename);
            }else{
                System.out.println("The file is already locked!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testReadtxt(String path, String filename) {
        ArrayList<Offer> ret = new ArrayList<>();
        String filepath = path + LSEP + filename;
        try (RandomAccessFile reader = new RandomAccessFile(filepath, "rw");
             FileChannel fc = reader.getChannel();
             InputStream in = newInputStream(fc)) {
            FileLock lock = fc.tryLock();
            if(lock != null){
                try (var br = new BufferedReader(new InputStreamReader(
                        new FileInputStream(filepath), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                    System.out.println("You have successfully loaded the file " + filename);
                }
            }else{
                System.out.println("The file is already locked!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}