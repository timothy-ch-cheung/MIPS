package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class PacketReceiver extends Thread {
    private InetAddress group;
    private MulticastSocket socket;
    private int port;
    private PacketSender sender;
    
    /**
     * Constructor only needs the multicasting group to communicate
     *
     * @param group The multi-casting group that we will connect to
     * @throws IOException caused by the use of sockets
     */
    public PacketReceiver(InetAddress group, int port, PacketSender sender) throws IOException {
        this.group = group;
        this.socket = new MulticastSocket(port);
        this.port = port;
        this.sender = sender;
        
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            if (iface.isLoopback() || !iface.isUp())
                continue;
            
            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                socket.setInterface(addr);
                socket.joinGroup(group);
            }
        }
        
    }
    
    /**
     * Continuously listens to the port in the group which the server is sending messages to
     */
    @Override
    public void run() {
        super.run();
        while (true) {
            try {
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                
                String received = new String(packet.getData());
                System.out.println("Received -> " + received);
                sender.send("Hello, your message was: " + received);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        
    }
}
