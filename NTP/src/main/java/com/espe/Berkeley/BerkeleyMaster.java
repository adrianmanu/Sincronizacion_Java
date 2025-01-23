package com.espe.Berkeley;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class BerkeleyMaster {
	public static void main(String[] args) {
		int port = 12345; // Puerto de comunicación
		int numSlaves = 2; // Número de nodos esclavos esperados (ajustado a 1)
		List<Long> slaveTimes = new ArrayList<>();
		List<Socket> slaveSockets = new ArrayList<>();

		ExecutorService executor = Executors.newFixedThreadPool(numSlaves);

		try (ServerSocket serverSocket = new ServerSocket(port)) {
			System.out.println("Nodo Maestro esperando conexiones...");

			// Aceptar conexiones de los nodos esclavos
			for (int i = 0; i < numSlaves; i++) {
				Socket slaveSocket = serverSocket.accept();
				slaveSockets.add(slaveSocket);
				System.out.println("Conexión aceptada con el Nodo Esclavo " + (i + 1));

				// Leer el tiempo del esclavo en un hilo separado
				executor.submit(() -> {
					try {
						DataInputStream dataIn = new DataInputStream(slaveSocket.getInputStream());
						long slaveTime = dataIn.readLong(); // Leer el tiempo enviado por el esclavo
						synchronized (slaveTimes) {
							slaveTimes.add(slaveTime);
						}
					} catch (IOException e) {
						System.err.println("Error leyendo datos del esclavo: " + e.getMessage());
					}
				});
			}

			// Esperar a que todos los esclavos envíen su tiempo
			while (slaveTimes.size() < numSlaves) {
				Thread.sleep(1000); // Espera breve para evitar bloqueos
			}

			// Calcular el tiempo promedio
			long totalTime = slaveTimes.stream().mapToLong(Long::longValue).sum();
			long averageTime = totalTime / numSlaves;
			System.out.println("Tiempo promedio calculado por el Maestro: " + new Date(averageTime));

			// Enviar la diferencia de tiempo a los nodos esclavos
			for (int i = 0; i < slaveSockets.size(); i++) {
				Socket slaveSocket = slaveSockets.get(i);
				long slaveTime = slaveTimes.get(i);

				executor.submit(() -> {
					try {
						DataOutputStream dataOut = new DataOutputStream(slaveSocket.getOutputStream());
						long timeDifference = averageTime - slaveTime;
						dataOut.writeLong(timeDifference); // Enviar la diferencia al esclavo
						dataOut.flush();
						System.out.println("Diferencia de tiempo enviada al Nodo Esclavo");
					} catch (IOException e) {
						System.err.println("Error enviando datos al esclavo: " + e.getMessage());
					}
				});
			}

			// Cerrar las conexiones con los nodos esclavos
			executor.shutdown();
			executor.awaitTermination(5, TimeUnit.SECONDS);
			for (Socket slaveSocket : slaveSockets) {
				slaveSocket.close();
				System.out.println("Conexión cerrada con un Nodo Esclavo.");
			}

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}

