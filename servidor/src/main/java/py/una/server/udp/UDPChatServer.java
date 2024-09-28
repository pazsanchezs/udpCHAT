package py.una.server.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class UDPChatServer {
    public static void main(String[] args) {
        int puertoServidor = 9876;
        List<DatagramPacket> clients = new ArrayList<>();

        try {
            DatagramSocket serverSocket = new DatagramSocket(puertoServidor);
            System.out.println("Servidor de Chat UDP iniciado en el puerto " + puertoServidor);

            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                
                System.out.println("Esperando a algún cliente...");
                serverSocket.receive(receivePacket);

                String message = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                System.out.println("Mensaje recibido de " + clientAddress + ":" + clientPort + " - " + message);

                // Guardar el cliente si no está en la lista
                if (!clients.contains(receivePacket)) {
                    clients.add(receivePacket);
                }

                // Reenviar el mensaje a todos los clientes
                for (DatagramPacket client : clients) {
                    if (!client.getAddress().equals(clientAddress) || client.getPort() != clientPort) {
                        DatagramPacket sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(), client.getAddress(), client.getPort());
                        serverSocket.send(sendPacket);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
