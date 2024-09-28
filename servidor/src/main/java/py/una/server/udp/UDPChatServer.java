package py.una.server.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

public class UDPChatServer {
    public static void main(String[] args) {
        // Definir el puerto en el que escuchará el servidor
        int puertoServidor = 9876;
        
        // Conjuntos para almacenar las direcciones y puertos de los clientes conectados
        Set<InetAddress> clientAddresses = new HashSet<>();  // Almacena direcciones IP de los clientes
        Set<Integer> clientPorts = new HashSet<>();  // Almacena los puertos de los clientes

        try {
            // Crear un socket UDP en el puerto especificado
            DatagramSocket serverSocket = new DatagramSocket(puertoServidor);
            System.out.println("Servidor de Chat UDP iniciado en el puerto " + puertoServidor);

            // Bucle infinito para mantener el servidor activo y en espera de mensajes
            while (true) {
                // Array de bytes para recibir los datos del cliente
                byte[] receiveData = new byte[1024];
                
                // Crear un paquete de datagramas para recibir el mensaje del cliente
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                
                // Mostrar en la consola que el servidor está esperando mensajes de clientes
                System.out.println("Esperando a algún cliente...");
                
                // El servidor recibe el paquete (bloquea hasta que llegue un mensaje)
                serverSocket.receive(receivePacket);

                // Convertir los datos recibidos (en bytes) a un String y eliminar espacios en blanco
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
                
                // Obtener la dirección IP y el puerto del cliente que envió el mensaje
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                // Mostrar en la consola el mensaje recibido junto con la dirección y el puerto del cliente
                System.out.println("Mensaje recibido de " + clientAddress + ":" + clientPort + " - " + message);

                // Agregar la dirección y puerto del cliente a los conjuntos si no están ya registrados
                clientAddresses.add(clientAddress);
                clientPorts.add(clientPort);

                // Reenviar el mensaje a todos los clientes conectados
                for (InetAddress address : clientAddresses) {
                    for (Integer port : clientPorts) {
                        // Asegurarse de no reenviar el mensaje al cliente que lo envió
                        if (!(address.equals(clientAddress) && port.equals(clientPort))) {
                            // Crear un paquete de datagramas para enviar el mensaje a los otros clientes
                            DatagramPacket sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(), address, port);
                            
                            // Enviar el paquete al cliente
                            serverSocket.send(sendPacket);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Capturar y mostrar cualquier excepción que ocurra durante la ejecución
            e.printStackTrace();
        }
    }
}