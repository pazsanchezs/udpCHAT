package py.una.client.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ChatUDPClient {
    public static void main(String[] args) {
        // Dirección IP y puerto del servidor al que se conectará el cliente
        String direccionServidor = "127.0.0.1"; // Dirección del servidor
        int puertoServidor = 9876; // Puerto del servidor

        try {
            // Crear un socket UDP para el cliente
            DatagramSocket clientSocket = new DatagramSocket();
            
            // Obtener la dirección IP del servidor
            InetAddress serverAddress = InetAddress.getByName(direccionServidor);
            
            // Usar un scanner para leer la entrada del usuario (nombre de usuario y mensajes)
            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingrese su nombre de usuario: ");
            String username = scanner.nextLine();  // Guardar el nombre de usuario ingresado

            // Crear un hilo separado para recibir mensajes del servidor de forma asíncrona
            new Thread(() -> {
                try {
                    // Buffer para almacenar los datos recibidos
                    byte[] receiveData = new byte[1024];
                    
                    // Bucle infinito para recibir mensajes del servidor
                    while (true) {
                        // Crear un paquete UDP para recibir los datos
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        
                        // Recibir el mensaje enviado por el servidor
                        clientSocket.receive(receivePacket);
                        
                        // Convertir los datos recibidos a un String
                        String message = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

                        // Comprobar si el mensaje no fue enviado por el propio usuario
                        if (!message.startsWith(username + ":")) {
                            // Si no es del propio usuario, imprimir el mensaje en consola
                            System.out.println(message);
                        }
                    }
                } catch (Exception e) {
                    // Capturar y mostrar cualquier excepción que ocurra en el hilo de recepción
                    e.printStackTrace();
                }
            }).start(); // Iniciar el hilo para recibir mensajes

            // Bucle infinito para enviar mensajes al servidor
            while (true) {
                // Pedir al usuario que ingrese un mensaje
                System.out.print("Escriba su mensaje: ");
                
                // Leer el mensaje que el usuario quiere enviar
                String messageToSend = scanner.nextLine();
                
                // Crear el mensaje completo con el formato: "usuario: mensaje"
                String fullMessage = username + ": " + messageToSend;
                
                // Convertir el mensaje a bytes para poder enviarlo
                byte[] sendData = fullMessage.getBytes();
                
                // Crear un paquete UDP con los datos del mensaje, la dirección del servidor y el puerto
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, puertoServidor);
                
                // Enviar el paquete al servidor
                clientSocket.send(sendPacket);
            }
        } catch (Exception e) {
            // Capturar y mostrar cualquier excepción que ocurra en el cliente
            e.printStackTrace();
        }
    }
}