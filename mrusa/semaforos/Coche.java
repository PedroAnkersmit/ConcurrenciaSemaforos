package mrusa.semaforos;

import java.util.concurrent.Semaphore;

public class Coche{
	
	private int asientos; 	//Capacidad del coche
	
	public Coche(int tam){
		asientos = tam;
	}
	
	public Coche(){
		asientos = 5;
	}
	private int numPasajeros = 0;

	private Semaphore mutex = new Semaphore(1); //Mutex
	private Semaphore esperaSubir = new Semaphore(1); //Semáforo que avisa cuando está lleno el coche
	private Semaphore esperaBajar = new Semaphore(0); //Semáforo que controla la espera de los pasajeros
	private Semaphore estaLleno = new Semaphore(0); //Semáforo que controla la espera del control
	public void darVuelta(int id) throws InterruptedException{
		//CS1- Pasajero espera a que haya hueco en el coche
		esperaSubir.acquire();
		//PASAJERO SUBE
		mutex.acquire();
		numPasajeros++;
		System.out.println("Se sube el pasajero "+id);
		if(numPasajeros < asientos) esperaSubir.release();
		else{estaLleno.release();}
		mutex.release();
		//Ya se ha subido

		//CS2- Pasajero espera a que pare la atracción y pueda bajar

		esperaBajar.acquire(); //Se quedan los pasajeros bloqueados
		//Sigue por aquí si ha sido liberado
		mutex.acquire();
		numPasajeros--;
		System.out.println("Baja el pasajero "+id );


		if(numPasajeros != 0) esperaBajar.release(); //Despertado en cascada, voy liberando las otras esperaBajar que estén en rojo
		else {
			System.out.println("Coche vacio");
			esperaSubir.release();
		}
		mutex.release();

	}
	//CS-3 Esperar a que el coche esté lleno
	public void esperaLleno() throws InterruptedException{
		//Se espera a que el coche este lleno
		estaLleno.acquire(); //Bloqueo hasta que alguien me avise de que está lleno
		System.out.println("El coche esta lleno, la atraccion se pone en funcionamiento");

	}

	public void finVuelta() throws InterruptedException {
		//Avisa a los pasajeros que deben bajar
		esperaBajar.release();
		//estaLleno.acquire();
		System.out.println("Termina la vuelta, los pasajeros se pueden bajar");


	}
}