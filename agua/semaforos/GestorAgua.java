package agua.semaforos;

import java.util.concurrent.Semaphore;

public class GestorAgua {	
	//CS 2 hidrogenos y 1 oxigeno
	private int nHidrogenos = 0, nOxigenos = 0;
	private Semaphore mutexH = new Semaphore(1);
	private Semaphore mutexO = new Semaphore(1);
	private Semaphore esperaH = new Semaphore(1);
	private Semaphore esperaO = new Semaphore(1);
	private Semaphore esperaMoleculaH = new Semaphore(0);
	private Semaphore esperaMoleculaO = new Semaphore(0);

	public void hListo(int id) throws InterruptedException {
		//ENTRA UN ATOMO DE HIDROGENO
		esperaH.acquire();
		mutexH.acquire();
		nHidrogenos++;
		System.out.println("Atomo de H "+id+" esperando para fusionarse");
		mutexH.release();

		if(nHidrogenos <2) esperaH.release(); //Si hay suficientes H
		else esperaMoleculaO.release(); //Liberamos la espera a O

		esperaMoleculaH.acquire(); //Tenemos nuestra propia espera que debe ser liberada por O


		mutexH.acquire();
		nHidrogenos--;
		System.out.println("liberamos el atomo de H "+id);
		if(nHidrogenos > 0) esperaMoleculaH.release(); //Despertar en cascada, si nos queda algún hidrógeno bloqueado, lo liberamos
		else{
			System.out.println("No hay atomos de H esperando");
			esperaO.release();//Liberamos la entrada para que puedan llegar nuevos atomos de oxigeno
		}
		esperaH.release(); //Liberamos la entrada para que puedan llegar nuevos atomos de hidrogeno
		mutexH.release();
	}
	
	public void oListo(int id) throws InterruptedException {
		//ENTRA UN ATOMO DE OXIGENO
		esperaO.acquire();
		mutexO.acquire();
		nOxigenos++;
		System.out.println("Atomo de O "+ id+ " esperando para fusionarse");
		mutexO.release();

		//Tenemos suficientes oxigenos
		esperaMoleculaO.acquire(); //Esperamos a recibir el aviso de que hay suficientes Hidrogenos
		System.out.println("Molecula de Agua creada");
		esperaMoleculaH.release(); //Ahora que sabemos que hay suficientes Hidrogenos, liberamos el proceso para crear la molecula

		mutexO.acquire();
		nOxigenos--;
		System.out.println("Liberamos el atomo de O "+id); System.out.println("No hay atomos de O esperando");
		mutexO.release();
	}
}