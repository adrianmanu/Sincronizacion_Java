package com.espe.Cristian;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import java.net.InetAddress;
import java.util.Date;

public class CristianAlgorithmClient {
	public static void main(String[] args) {
		// Dirección del servidor NTP al que se conectará para obtener la hora
		String serverAddress = "time.google.com"; // Servidor NTP público

		// Cliente NTP que se encargará de comunicarse con el servidor
		NTPUDPClient timeClient = new NTPUDPClient();

		try {
			// Obtener la dirección IP del servidor NTP a partir de su nombre de dominio
			InetAddress timeServerAddress = InetAddress.getByName(serverAddress);

			// Solicitar la hora al servidor NTP y obtener la respuesta
			TimeInfo timeInfo = timeClient.getTime(timeServerAddress);

			// Registrar el tiempo actual en el cliente antes de enviar la solicitud al servidor (T1)
			long T1 = System.currentTimeMillis();

			// Obtener el tiempo del servidor cuando procesó la solicitud (T2 y T3 son iguales para el servidor NTP)
			long T2 = timeInfo.getMessage().getTransmitTimeStamp().getTime();
			long T3 = T2; // Tiempo del servidor transmitido en la respuesta (lo mismo que T2)

			// Registrar el tiempo actual en el cliente después de recibir la respuesta del servidor (T4)
			long T4 = System.currentTimeMillis();

			// Calcular el tiempo de ida y vuelta (Round-Trip Time, RTT)
			long RTT = T4 - T1;

			// Calcular la latencia (delay) como la mitad del RTT
			long delay = (T3 - T2) / 2;

			// Calcular la hora estimada del servidor ajustada con el retraso
			long estimatedServerTime = T3 + delay;

			// Calcular el tiempo sincronizado en el cliente ajustando también el RTT
			long synchronizedTime = estimatedServerTime + (RTT / 2);

			// Convertir el tiempo sincronizado a un objeto Date para su representación
			Date date = new Date(synchronizedTime);

			// Mostrar la hora sincronizada estimada del servidor
			System.out.println("Hora estimada del servidor: " + date);

		} catch (Exception e) {
			// Imprimir cualquier excepción ocurrida durante la conexión o sincronización
			e.printStackTrace();
		}
	}
}
