package server;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


public class Display_server {
    public static void main(String[] args) throws Exception {
        try {
            int counter = 0;
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(3961));
            System.out.println("Server Status : ON ");
            System.out.println(">>>>>>>>>> STATUS <<<<<<<<<<");
            
            while (true) {
                counter++;
                SocketChannel socketChannel = serverSocketChannel.accept();  //server accept the client connection request
                System.out.println(" >> " + "Client No:" + counter + " started!");
                ServerThread sct = new ServerThread(socketChannel, counter); //send  the request to a separate thread
                sct.start();
            }
            
        } catch (Exception e) {
            System.out.println("Muti");
            System.out.println(e);
        }
    }
}