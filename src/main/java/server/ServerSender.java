package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Class whose purpose is to send messages from server to all clients.
 */
public class ServerSender extends Thread {
    
    private InetAddress group;
    private MulticastSocket socket;
    private String networkInterface;
    
    /**
     * Constructor builds the object with the multi-casting group
     *
     * @param group the multi-casting group which will be used
     * @throws IOException caused by the sockets.
     */
    public ServerSender(InetAddress group) throws IOException {
        this.group = group;
        this.socket = new MulticastSocket();
        this.networkInterface = Utility.getInterface();
        
    }
    
    /**
     * Sends packets from the server to the clients.
     * @param message The message we want to send to the clients.
     * @throws IOException caused by the packets.
     */
    public void send(String message) throws IOException {
        byte[] buf = new byte[256];
        
        buf = message.getBytes();
        DatagramPacket sending = new DatagramPacket(buf, 0, buf.length, group, Utility.CLIENT_PORT);
        
        
        Enumeration<NetworkInterface> faces = NetworkInterface.getNetworkInterfaces();
        while (faces.hasMoreElements()) {
            NetworkInterface iface = faces.nextElement();
            if (iface.isLoopback() || !iface.isUp())
                continue;
            
            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (addr.toString().equals(this.networkInterface)) {
                    socket.setInterface(addr);
                    socket.send(sending);
                    return;
                }
                
            }
        }
    }
    
    /**
     * Empty run method for now due to the fact that threading functionality is not currently needed.
     */
    @Override
    public void run() {
        super.run();
    }
}
