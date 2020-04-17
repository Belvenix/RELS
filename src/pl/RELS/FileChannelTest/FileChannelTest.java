package pl.RELS.FileChannelTest;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;

import static java.nio.channels.Channels.newInputStream;
import static java.nio.channels.Channels.newOutputStream;

public class FileChannelTest {

    private static final String LSEP = System.getProperty("line.separator");
    private static final String FSEP = System.getProperty("file.separator");

    public static void main(String args[]) throws IOException, InterruptedException {

        String filePath = "testing" + FSEP + "folder" + FSEP + "test2.bin";
        testWrite(filePath);
        testRead(filePath);
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


    private static void testWrite(String filePath) {
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

    private static void testRead(String filePath) {
        try (RandomAccessFile reader = new RandomAccessFile(filePath, "rw");
             FileChannel fc = reader.getChannel();
             InputStream in = newInputStream(fc)) {
            FileLock lock = fc.tryLock();
            if(lock != null){
                int c;
                String s ="";
                while((c = in.read()) != -1){
                    s += (char) c;
                }
                System.out.println("The text is: " + s);
            }else{
                System.out.println("The file is already locked!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}