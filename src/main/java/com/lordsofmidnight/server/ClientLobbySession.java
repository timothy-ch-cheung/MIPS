package com.lordsofmidnight.server;

import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.main.Client;
import com.lordsofmidnight.utils.Input;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.Queue;
import javafx.application.Platform;

/**
 * Handles joining and starting the game from the client's perspective through use of tcp sessions.
 */
public class ClientLobbySession {

  private Queue<String> clientIn;
  private Queue<Input> keypressQueue;
  private InetAddress serverIP;
  private ClientGameplayHandler handler;
  private Client client;
  private String clientName;
  private String[] playerNames = new String[5];
  private volatile boolean gameStarted = false;

  private Socket soc;
  private Socket ss = new Socket();
  private ServerSocket serverSocket;
  private PrintWriter out;
  private BufferedReader in;
  /**
   * Thread which waits for the com.lordsofmidnight.server to start the game and send over the
   * player names.
   */
  Thread gameStarter =
      new Thread(
          () -> {
            try {
              System.out.println("About to set up client game start channels");
              serverSocket = new ServerSocket(NetworkUtility.CLIENT_DGRAM_PORT);
              ss = serverSocket.accept();
              ss.setReuseAddress(true);
              BufferedReader gameIn =
                  new BufferedReader(new InputStreamReader(ss.getInputStream()));
              System.out.println("Waiting for game start message");
              // get other player names
              String r = gameIn.readLine();
              System.out.println("Start game msg -> " + r);
              if (r.equals(NetworkUtility.GAME_START)) {
                for (int i = 0; i < 5; i++) {
                  playerNames[i] = gameIn.readLine();
                  System.out.println("NAME: " + playerNames[i]);
                }
                gameStarted = true;
                handler = new ClientGameplayHandler(serverIP, keypressQueue, clientIn);
                client.setPlayerNames(playerNames);
                if (!client.isHost) {
                  Platform.runLater(() -> client.startMultiplayerGame());
                  shutdownTCP();
                }
              }

              gameIn.close();
              ss.close();
            } catch (IOException e) {
              if (ss != null && !ss.isClosed()) {
                try {
                  ss.close();
                } catch (IOException err) {
                  err.printStackTrace(System.err);
                }
              }

              if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                  serverSocket.close();
                } catch (IOException err) {
                  err.printStackTrace(System.err);
                }
              }
              System.out.println("Sockets have been closed in lobby session: " + e.getMessage());
            }
          });
  /**
   * A thread which will look for the {@link ServerLobby}'s UDP broadcast for players to join, and
   * then uses the IP address of the server to then start a TCP session where it joins the game
   * lobby.
   */
  Thread joiner =
      new Thread() {
        @Override
        public void run() {
          super.run();
          while (!Thread.currentThread().isInterrupted()) {
            try {
              System.out.println("Getting the server address");
              MulticastSocket socket = new MulticastSocket(NetworkUtility.CLIENT_M_PORT);
              socket.setSoTimeout(NetworkUtility.LOBBY_TIMEOUT);
              InetAddress group = NetworkUtility.GROUP;
              Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
              while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) {
                  continue;
                }

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                  InetAddress addr = addresses.nextElement();
                  socket.setInterface(addr);
                  socket.joinGroup(group);
                }
              }

              byte[] buf = new byte[256];
              DatagramPacket packet = new DatagramPacket(buf, buf.length);
              socket.receive(packet);
              serverIP = packet.getAddress();
              socket.close();

              soc = new Socket(serverIP, NetworkUtility.SERVER_DGRAM_PORT);
              soc.setSoTimeout(NetworkUtility.LOBBY_TIMEOUT);
              out = new PrintWriter(soc.getOutputStream());
              in = new BufferedReader(new InputStreamReader(soc.getInputStream()));

              String str = NetworkUtility.PREFIX + "CONNECT" + NetworkUtility.SUFFIX;
              out.println(str);
              out.println(clientName);
              out.flush();

              String r = in.readLine();
              int id = Integer.parseInt(r);
              client.setId(id);

              r = in.readLine();
              System.out.println("Map set to :" + r);
              client.setMap(Map.deserialiseMap(r));

              r = in.readLine();
              int MIPID = Integer.parseInt(r);
              client.setMIP(MIPID);
              r = in.readLine();
              if (r.equals("SUCCESS")) {
                System.out.println("Server connection success");
              }
              gameStarter.start();
              Thread.currentThread().interrupt();
            } catch (SocketTimeoutException e) {
              client.noGameFound();
              Thread.currentThread().interrupt();
            } catch (IOException e1) {
              e1.printStackTrace();
            }
          }
        }
      };

  /**
   * @param clientIn The input queue for the client
   * @param keypressQueue The keypress queue from the client
   * @param client The client
   * @param clientName The name of the client
   */
  public ClientLobbySession(
      Queue<String> clientIn, Queue<Input> keypressQueue, Client client, String clientName)
      throws IOException {

    this.clientIn = clientIn;
    this.keypressQueue = keypressQueue;
    this.client = client;
    this.clientName = clientName;
    joiner.start();
  }

  /**
   * Handles what message is sent to the com.lordsofmidnight.server when a client wants to
   * disconnect then shuts down the TCP connections and closes joining and game starting threads.
   */
  public void leaveLobby() {
    if (client.isHost) {
      out.write(NetworkUtility.DISCONNECT_HOST);
    } else {
      out.write(NetworkUtility.DISCONNECT_NON_HOST);
    }
    shutdownTCP();
    joiner.interrupt();
    gameStarter.interrupt();
    if (handler != null) {
      handler.close();
    }
  }

  /**
   * Handles shutting down TCP connections in the client lobby
   */
  private void shutdownTCP() {
    try {
      out.close();
      if (ss != null && !ss.isClosed()) {
        try {
          ss.close();
        } catch (IOException err) {
          err.printStackTrace(System.err);
        }
      }

      if (serverSocket != null && !serverSocket.isClosed()) {
        try {
          serverSocket.close();
        } catch (IOException err) {
          err.printStackTrace(System.err);
        }
      }
      in.close();
      soc.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
