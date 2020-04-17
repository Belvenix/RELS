package pl.RELS.FileChannelTest;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileChannelTest {

    public static void main(String args[]) throws IOException, InterruptedException {
        String filePath = "testing\\folder\\test.bin";
        File file = new File(filePath);
        Path path = Paths.get(filePath);

        FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE);
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
}
