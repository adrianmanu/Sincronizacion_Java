package com.espe.NTP;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.util.Date;

@SpringBootApplication
public class NtpApplication {
	public static void main(String[] args) {
		// Dirección del servidor NTP al que se conectará para obtener la hora actual
		String timeServer = "time.google.com"; // Servidor NTP público reconocido

		// Cliente NTP que se encargará de enviar la solicitud y recibir la respuesta
		NTPUDPClient timeClient = new NTPUDPClient();

		try {
			// Obtener la dirección IP del servidor NTP utilizando su nombre de dominio
			InetAddress timeServerAddress = InetAddress.getByName(timeServer);

			// Enviar la solicitud al servidor NTP y obtener la respuesta
			TimeInfo timeInfo = timeClient.getTime(timeServerAddress);

			// Extraer el tiempo transmitido por el servidor NTP en formato de milisegundos
			long currentTimeMillis = timeInfo.getMessage().getTransmitTimeStamp().getTime();

			// Convertir el tiempo obtenido a un objeto Date para facilitar su visualización
			Date time = new Date(currentTimeMillis);

			// Mostrar la hora obtenida del servidor NTP
			System.out.println("Hora obtenida del servidor NTP: " + time);

		} catch (Exception e) {
			// Capturar y mostrar cualquier excepción ocurrida durante la conexión o procesamiento
			e.printStackTrace();
		} finally {
			// Asegurarse de cerrar el cliente NTP para liberar los recursos
			timeClient.close();
		}
	}
}
