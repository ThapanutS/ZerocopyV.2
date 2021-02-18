package server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class ServerThread extends Thread {

    public final static String sourcePath = "C:\\Users\\LENOVO\\Documents\\NetBeansProjects\\ZerocopyFile\\src\\Consadole sapporo Vs Urawa Red Diamonds Full Match.mkv";
    SocketChannel socketChannel_Client;
    int clientNo;

    ServerThread(SocketChannel inSocket, int no) {
        socketChannel_Client = inSocket;
        clientNo = no;
    }

    public void run() {
        try {
            DataInputStream inputStream = new DataInputStream(socketChannel_Client.socket().getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socketChannel_Client.socket().getOutputStream());
            outputStream.writeUTF("[[Server Message]] >> Connected");
            outputStream.flush();

            String clientMessage = "", serverMessage = "";
            while (true) {
                clientMessage = inputStream.readUTF();
                if (clientMessage.equals("N")) {
                    System.out.println("Client[" + clientNo + "] -> exit!! ");
                    break;
                } else {
                    System.out.println("[[Client Message]] << Client " + clientNo + " : Requirement is " + clientMessage);
                    serverMessage = "[[Server Message]] >> Client[" + clientNo + "] Processing";
                    outputStream.writeUTF(serverMessage);
                    outputStream.flush();
                    
                    //read input file to server
                    RandomAccessFile fileInput = new RandomAccessFile(sourcePath, "rw");
                    ByteBuffer request = ByteBuffer.allocate(16);
                    request.putLong(fileInput.length());
                    System.out.println("[[Message]] File Lenght : " + fileInput.length());
                    request.flip();
                    socketChannel_Client.write(request);
                    FileChannel fileChannel = fileInput.getChannel();
                    long AllBytesTransferFrom = 0;
                    while (AllBytesTransferFrom < fileInput.length()) {
                        long bytesTransferred = fileChannel.transferTo(AllBytesTransferFrom, fileInput.length() - AllBytesTransferFrom, socketChannel_Client);
                        AllBytesTransferFrom += bytesTransferred;
                    }
                    
                    //write output file to server
                    ByteBuffer response = ByteBuffer.allocate(16);
                    socketChannel_Client.read(response);
                    response.flip();
                    //long totalByteReceived = response.getLong();
                    socketChannel_Client.finishConnect();
                    outputStream.writeUTF("[[Server Message]] >> Zerocopy Completed!!!");
                    outputStream.flush();
                    System.out.println("Client[" + clientNo + "] -> Process Finished!!!");
                }
            }
            inputStream.close();
            outputStream.close();
            socketChannel_Client.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
