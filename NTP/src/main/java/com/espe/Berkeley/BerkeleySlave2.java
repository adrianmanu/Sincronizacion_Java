package com.espe.Berkeley;

import java.io.*;
import java.net.*;
import java.util.Date;

public class BerkeleySlave2 {
	public static void main(String[] args) {
		String masterAddress = "localhost"; // Dirección del nodo maestro
		int port = 12345; // Puerto del nodo maestro

		try {
			Socket socket = new Socket(masterAddress, port);
			System.out.println("Conectado al Nodo Maestro");

			// Enviar la hora local al maestro
			long localTime = System.currentTimeMillis();
			DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
			dataOut.writeLong(localTime);
			dataOut.flush();
			System.out.println("Hora local enviada al Maestro");

			// Mantener el socket abierto y recibir la diferencia de tiempo
			DataInputStream dataIn = new DataInputStream(socket.getInputStream());
			while (true) {
				try {
					// Leer la diferencia de tiempo del maestro
					long timeDifference = dataIn.readLong();
					System.out.println("Diferencia de tiempo recibida: " + timeDifference);

					// Ajustar el reloj local con la diferencia recibida
					long adjustedTime = localTime + timeDifference;
					Date adjustedDate = new Date(adjustedTime);
					System.out.println("Hora ajustada del Nodo Esclavo: " + adjustedDate);

					// Salir del bucle si no se espera más comunicación
					break;
				} catch (EOFException e) {
					// Manejar el cierre del flujo por parte del maestro
					System.err.println("El maestro cerró la conexión.");
					break;
				}
			}

			// Cerrar recursos manualmente
			dataOut.close();
			dataIn.close();
			socket.close();
			System.out.println("Conexión cerrada con el Maestro.");

		} catch (IOException e) {
			System.err.println("Error en el Nodo Esclavo: " + e.getMessage());
		}
	}
}
