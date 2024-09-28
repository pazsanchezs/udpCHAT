package py.una.client.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ChatUDPClient {
    public static void main(String[] args) {
        String direccionServidor = "127.0.0.1"; // Dirección del servidor
        int puertoServidor = 9876; // Puerto del servidor

        try {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName(direccionServidor);
            
            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingrese su nombre de usuario: ");
            String username = scanner.nextLine();

            // Crear un hilo para recibir mensajes del servidor
            new Thread(() -> {
                try {
                    byte[] receiveData = new byte[1024];
                    while (true) {
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        clientSocket.receive(receivePacket);
                        String message = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

                        // Comprobar si el mensaje proviene del cliente
                        if (!message.startsWith(username + ":")) {
                            System.out.println(message);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            // Enviar mensajes al servidor
            while (true) {
                System.out.print("Escriba su mensaje: ");
                String messageToSend = scanner.nextLine();
                String fullMessage = username + ": " + messageToSend;
                byte[] sendData = fullMessage.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, puertoServidor);
                clientSocket.send(sendPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}