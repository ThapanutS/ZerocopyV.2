package cilent;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {

    public final static String sourcePath = "C:\\Users\\LENOVO\\Documents\\NetBeansProjects\\ZerocopyFile\\src\\Consadole sapporo Vs Urawa Red Diamonds Full Match.mkv";
    public static String fileExtention = sourcePath.substring(sourcePath.lastIndexOf("."), sourcePath.length());
    public final static String destiantionPath = sourcePath.substring(0, 110) + "[Zerocopy]" + fileExtention;

    public static void main(String[] args) throws Exception {
        try {
            System.out.println("_________________________________________________________________");
            System.out.println("Starting client...");
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("127.0.0.1", 3961));

            DataInputStream inputStream = new DataInputStream(socketChannel.socket().getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socketChannel.socket().getOutputStream());
            System.out.println(inputStream.readUTF()); // Message from server,if client joined to server;

            System.out.println("----------------------------- File -----------------------------");
            System.out.println(" 1 : Consadole sapporo Vs Urawa Red Diamonds Full Match.mkv");
            System.out.println("----------------------------------------------------------------");

            Scanner input = new Scanner(System.in);
            String clientMessage = "", serverMessage = "";

            while (true) {
                System.out.println("Do you want to make a zero copy [Y/N]?");
                clientMessage = input.nextLine();
                if (clientMessage.equals("N")) {
                    outputStream.writeUTF(clientMessage);
                    outputStream.flush();
                    System.out.println("##Finished...");
                    break;
                } else {
                    outputStream.writeUTF(clientMessage);
                    outputStream.flush();
                    long start = System.currentTimeMillis(); // Time
                    serverMessage = inputStream.readUTF();
                    System.out.println(serverMessage);
                    
                    //read input file from server;
                    ByteBuffer request = ByteBuffer.allocate(16);
                    socketChannel.read(request);
                    request.flip();
                    long length = request.getLong();
                    RandomAccessFile fileOutput = new RandomAccessFile(destiantionPath, "rw");
                    FileChannel fileChannel = fileOutput.getChannel();
                    long AllBytesTransferFrom = 0;
                    while (AllBytesTransferFrom < length) {
                        long transferFromByteCount = fileChannel.transferFrom(socketChannel, AllBytesTransferFrom, length - AllBytesTransferFrom);
                        if (transferFromByteCount <= 0) {
                            break;
                        }
                        AllBytesTransferFrom += transferFromByteCount;
                    }

                    //write output file from server
                    ByteBuffer response = ByteBuffer.allocate(16);
                    response.putLong(AllBytesTransferFrom);
                    response.flip();
                    socketChannel.write(response);
                    
                    long stop = System.currentTimeMillis();
                    long time = stop - start; // Time
                    System.out.println("Time " + time + " millisecond");
                    serverMessage = inputStream.readUTF();
                    System.out.println(serverMessage);
                }
            }
            System.out.println("_________________________________________________________________");
            outputStream.close();
            socketChannel.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
