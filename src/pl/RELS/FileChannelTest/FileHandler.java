package pl.RELS.FileChannelTest;

import pl.RELS.Offer.Offer;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.nio.channels.Channels.newInputStream;
import static java.nio.channels.Channels.newOutputStream;


/**
 * Class responsible for handling the saving and loading to files.
 * It has methods that work like CLI (saveCLI and loadCLI) as well as technical methods (saving and loading)
 *
 * Class also will take into account the system file separator as well as line separator
 */
public class FileHandler {

    /**
     * Field that holds string which is Line Separator in the specific OS
     */
    private static final String LSEP = System.getProperty("line.separator");

    /**
     * Field that holds string which is File Separator in the specific OS
     */
    private static final String FSEP = System.getProperty("file.separator");

    /**
     * Field that holds a scanner object - so that we dont repeat ourselves in creation of scanners
     */
    private static final Scanner SCANNER = new Scanner(System.in);

    /**
     * Command Line Interface for saving the file. It prompts the user for the filepath (FOLDER!) where it will be and then
     * what will be the name of the file. What is important is that we make them separate. Also the program will delete invalid
     * symbols entered by the user.
     *
     * @param arr - array of offers to be saved to the file specified by user
     */
    public static void saveCLI(ArrayList<Offer> arr){
        System.out.println("Now please specify the path where you want to save the offers (Base folder will be 'res\\' folder in project directory)");
        String baseUserDir = "res\\";
        String path = SCANNER.next();

        while (true){
            if(path.endsWith("\\")){
                path = path.substring(0, path.length() - 1);
            }
            path.replaceAll("[\\\\/:*?\"<>|]", "_");
            System.out.println("Specify file with the extension. Only .txt, .dat (binary) and .ser (serialized) are available:");
            String token = SCANNER.next().toLowerCase();
            if (token.endsWith(".txt")){
                System.out.println("You choose txt extension.");
                savetxt(arr,  baseUserDir + path, token, false);
                break;
            }
            else if(token.endsWith(".dat")){
                System.out.println("You choose binary extension.");
                savebin(arr, baseUserDir + path, token, false);
                break;
            }
            else if (token.endsWith(".ser")){
                System.out.println("You choose to serialize the data.");
                saveser(arr, baseUserDir + path, token, false);
                break;
            }
            else{
                System.out.println("The filename with extension you specified [" + token + "] is incorrect. Please try again.");
            }
        }
    }

    /**
     * Command Line Interface for loading the file. It prompts the user for the filepath (FOLDER!) where it will be and then
     * what will be the name of the file. What is important is that we make them separate. Also the program will delete invalid
     * symbols entered by the user.
     *
     * @return array of offers that were loaded from the file specified by user. it will prompt if there was some failure
     */
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
                ret = loadtxt("res\\" + path, token, false);
                break;
            }
            else if(token.endsWith(".dat")){
                System.out.println("You choose binary extension.");
                ret = loadbin("res\\" + path, token, false);
                break;
            }
            else if (token.endsWith(".ser")){
                System.out.println("You choose to serialize the data.");
                ret = loadser("res\\" + path, token, false);
                break;
            }
            else{
                System.out.println("The filename with extension you specified [" + token + "] is incorrect. Please try again.");
            }
        }
        return ret;
    }

    /**
     * Helper method that will create directories that were specified by the user (if they dont exist)
     * @param path - a pth of FOLDERS to be created (if they dont exist)
     */
    private static void createDirs(String path){
        String dir = Paths.get("").toAbsolutePath().toString() + FSEP + path;
        File directories = new File(dir);
        if(!directories.exists()){
            directories.mkdirs();
        }
    }

    /**
     * Method responsible for saving the list of offers in text file
     * @param offers - list of offers to be saved
     * @param path - path of folders specified by user
     * @param filename - filename specified by user (ending with .txt)
     * @param wait - flag for debugging purposes - waits 10s inside lock
     */
    public static void savetxt(ArrayList<Offer> offers, String path, String filename, boolean wait){
        createDirs(path);
        File save = new File(path + FSEP + filename);
        System.out.println("[SAVE] Trying to save the file " + filename);
        try(RandomAccessFile reader = new RandomAccessFile(save.getAbsolutePath(), "rw");
            FileChannel fc = reader.getChannel()) {
            FileLock lock = fc.tryLock();
            if (lock != null) {
                System.out.println("[LOCK] Acquired lock.");
                try(OutputStream out = newOutputStream(fc)){
                    PrintWriter writer = new PrintWriter(out);
                    for(Offer o : offers){
                        writer.println(o.toString());
                    }
                    writer.close();
                    if(wait){
                        System.out.println("[WAIT] Waiting for testing purposes.");
                        Thread.sleep(10000);
                    }
                    System.out.println("[LOCK] Released lock.");
                    System.out.println("[SUCCESS] You have successfully saved the file " + filename);
                }

            }
            else{
                System.out.println("[FAIL] The file " + filename + " is already locked!");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method responsible for saving the list of offers in binary file
     * @param offers - list of offers to be saved
     * @param path - path of folders specified by user
     * @param filename - filename specified by user (ending with .dat)
     * @param wait - flag for debugging purposes - waits 10s inside lock
     */
    public static void savebin(ArrayList<Offer> offers, String path, String filename, boolean wait){
        createDirs(path);
        File save = new File(path + FSEP + filename);
        System.out.println("[SAVE] Trying to save the file " + filename);
        try(RandomAccessFile reader = new RandomAccessFile(save.getAbsolutePath(), "rw");
            FileChannel fc = reader.getChannel()) {

            FileLock lock = fc.tryLock();
            if (lock != null) {
                System.out.println("[LOCK] Acquired lock.");
                try(OutputStream out = newOutputStream(fc)){
                    for (Offer o : offers) {
                        byte[] bytes = (o.toString() + LSEP).getBytes();
                        out.write(bytes);
                    }
                    if(wait){
                        System.out.println("[WAIT] Waiting for testing purposes.");
                        Thread.sleep(10000);
                    }
                    System.out.println("[LOCK] Released lock.");
                    System.out.println("[SUCCESS] You have successfully saved the file " + filename);
                }
            }
            else{
                System.out.println("[FAIL] The file " + filename + " is already locked!");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method responsible for saving the list of offers in serialized file
     * @param offers - list of offers to be saved
     * @param path - path of folders specified by user
     * @param filename - filename specified by user (ending with .ser)
     * @param wait - flag for debugging purposes - waits 10s inside lock
     */
    public static void saveser(ArrayList<Offer> offers, String path, String filename, boolean wait){
        createDirs(path);
        File save = new File(path + FSEP + filename);
        System.out.println("[SAVE] Trying to save the file " + filename);
        try(RandomAccessFile reader = new RandomAccessFile(save.getAbsolutePath(), "rw");
            FileChannel fc = reader.getChannel();
            OutputStream out = newOutputStream(fc)) {

            FileLock lock = fc.tryLock();

            if (lock != null) {
                System.out.println("[LOCK] Acquired lock.");
                try(ObjectOutputStream  oos = new ObjectOutputStream (out)){
                    oos.writeObject(offers);
                    if(wait){
                        System.out.println("[WAIT] Waiting for testing purposes.");
                        Thread.sleep(10000);
                    }
                    System.out.println("[LOCK] Released lock.");
                    System.out.println("[SUCCESS] You have successfully saved the file " + filename);
                }
            }
            else{
                System.out.println("[FAIL] The file " + filename + " is already locked!");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    /**
     * Method responsible for loading the array list of offers from text file
     * @param path - path of folders specified by user
     * @param filename filename specified by user (ending with .txt)
     * @param wait - flag for debugging purposes - waits 10s inside lock
     * @return - list of offers that saved was in the file
     */
    public static ArrayList<Offer> loadtxt(String path, String filename, boolean wait){
        createDirs(path);
        ArrayList<Offer> ret = new ArrayList<>();
        String filepath = path + FSEP + filename;
        File load = new File(filepath);
        System.out.println("[LOAD] Trying to load the file " + filename);
        if (load.exists()){
            try(RandomAccessFile reader = new RandomAccessFile(load.getAbsolutePath(), "rw");
                FileChannel fc = reader.getChannel();
                 BufferedReader br = new BufferedReader(Channels.newReader(fc, "UTF-8"))) {
                FileLock lock = fc.tryLock();
                if (lock != null) {
                    System.out.println("[LOCK] Acquired lock.");
                    String line;
                    while ((line = br.readLine()) != null) {
                        Offer o = parseline(line);
                        if(o != null){
                            ret.add(o);
                        }
                    }
                    if(wait){
                        System.out.println("[WAIT] Waiting for testing purposes.");
                        Thread.sleep(10000);
                    }
                    System.out.println("[LOCK] Released lock.");
                    System.out.println("[SUCCESS] You have successfully saved the file " + filename);
                } else {
                    System.out.println("[FAIL] The file " + filename + " is already locked!");
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("The file specified - " + filepath + " - does not exist");
        }
        return ret;
    }

    /**
     * Method responsible for loading the array list of offers from binary file
     * @param path - path of folders specified by user
     * @param filename filename specified by user (ending with .dat)
     * @param wait - flag for debugging purposes - waits 10s inside lock
     * @return - list of offers that saved was in the file
     */
    public static ArrayList<Offer> loadbin(String path, String filename, boolean wait){
        ArrayList<Offer> ret = new ArrayList<>();
        String filepath = path + FSEP + filename;
        File load = new File(filepath);
        System.out.println("[LOAD] Trying to load the file " + filename);
        if (load.exists()){
            try(RandomAccessFile reader = new RandomAccessFile(load.getAbsolutePath(), "rw");
                FileChannel fc = reader.getChannel();
                 InputStream in = newInputStream(fc)) {
                FileLock lock = fc.tryLock();
                if (lock != null) {
                    System.out.println("[LOCK] Acquired lock.");
                    int ch;
                    String row = "";
                    while ((ch = in.read()) != -1) {
                        if ((char) ch != '\n') {
                            row += (char) ch;
                        } else {
                            Offer o = parseline(row);
                            if(o != null){
                                ret.add(o);
                            }
                            row = "";
                        }
                    }
                    if(wait){
                        System.out.println("[WAIT] Waiting for testing purposes.");
                        Thread.sleep(10000);
                    }
                    System.out.println("[LOCK] Released lock.");
                    System.out.println("[SUCCESS] You have successfully saved the file " + filename);
                } else {
                    System.out.println("[FAIL] The file " + filename + " is already locked!");
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("The file specified - " + filepath + " - does not exist");
        }

        return ret;
    }

    /**
     * Method responsible for loading the array list of offers from serialized file
     * @param path - path of folders specified by user
     * @param filename filename specified by user (ending with .ser)
     * @param wait - flag for debugging purposes - waits 10s inside lock
     * @return - list of offers that saved was in the file
     */
    public static ArrayList<Offer> loadser(String path, String filename, boolean wait){
        ArrayList<Offer> ret = new ArrayList<>();
        String filepath = path + "\\" + filename;
        File load = new File(filepath);
        System.out.println("[LOAD] Trying to load the file " + filename);
        if (load.exists()){
            try(RandomAccessFile reader = new RandomAccessFile(load.getAbsolutePath(), "rw");
                FileChannel fc = reader.getChannel();
                InputStream is = newInputStream(fc)){
                FileLock lock = fc.tryLock();
                if(lock != null){
                    System.out.println("[LOCK] Acquired lock.");
                    try(ObjectInputStream in = new ObjectInputStream(is)){
                        ret = (ArrayList<Offer>) in.readObject();
                        if(wait){
                            System.out.println("[WAIT]Waiting for testing purposes.");
                            Thread.sleep(10000);
                        }
                        System.out.println("[LOCK] Released lock.");
                        System.out.println("[SUCCESS] You have successfully saved the file " + filename);
                    }
                }
                else{
                    System.out.println("[FAIL] The file " + filename + " is already locked!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch ( ClassNotFoundException e){
                System.out.println("[FAIL] The file is corrupted. Please repair it and try again.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("[FAIL] The file specified - " + filepath + " - does not exist");
        }
        return ret;
    }


    /**
     * Helper function for parsing the string to Offer object
     * @param line - Correctly formatted String to be parsed to Offer object
     * @return - Offer object created from parsed String 'line'
     */
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
}