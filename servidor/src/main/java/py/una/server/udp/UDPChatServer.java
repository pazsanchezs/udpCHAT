package py.una.server.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

public class UDPChatServer {
    public static void main(String[] args) {
        int puertoServidor = 9876;
        Set<InetAddress> clientAddresses = new HashSet<>();
        Set<Integer> clientPorts = new HashSet<>();

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

                // Guardar la dirección y puerto del cliente si no están en la lista
                clientAddresses.add(clientAddress);
                clientPorts.add(clientPort);

                // Reenviar el mensaje a todos los clientes
                for (InetAddress address : clientAddresses) {
                    for (Integer port : clientPorts) {
                        // Asegurarse de que no se reenvíe al mismo cliente que envió el mensaje
                        if (!(address.equals(clientAddress) && port.equals(clientPort))) {
                            DatagramPacket sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(), address, port);
                            serverSocket.send(sendPacket);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}