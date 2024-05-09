package viajeTren;


import java.util.concurrent.Semaphore;

public class Tren {
	private int tamV = 5;
	private int pasV1 = 0;
	private int pasV2 = 0;

	private Semaphore mutex = new Semaphore(1); //Mutex para pasV1 y pasV2
	private Semaphore esperaPasajeroViajeV1 = new Semaphore(0); //Semáforo que para a los pas del V1 al subirse
	private Semaphore esperaPasajeroViajeV2 = new Semaphore(0); //Semáforo que para a los pas del V2 al subirse
	private Semaphore esperaTrenLLeno = new Semaphore(1); //Semáforo que para a los pas de subir cuando el tren está lleno
	private Semaphore esperaMaquinistaTrenLleno = new Semaphore(0); //Semáforo que para al maquinista hasta que esté el tren lleno
	public void viaje(int id) throws InterruptedException {
		//Debemos controlar si el tren está lleno
		esperaTrenLLeno.acquire(); //Deja pasar a 1 y se pone a rojo
		//Cuando entra un pasajero se aumenta el número de pasajeros
		mutex.acquire();
		if(pasV1 < tamV){ //mientras quepan en el v1
			pasV1++;
			System.out.println("Se sube el pasajero " + id + " al vagon 1" + " hay en el v1 " + pasV1 +" pasajeros");
			esperaTrenLLeno.release(); //Liberamos el paso a otro pasajero
		} else if (pasV2 < tamV-1){ //Mientras quepan en el v2
			pasV2++;
			System.out.println("Se sube el pasajero " + id + " al vagon 2" +  " hay en el v2 " + pasV2 + " pasajeros");
			esperaTrenLLeno.release(); //Liberamos el paso a otro pasajero
		} else if (pasV2 < tamV) { //No caben más, no liberamos el paso
			pasV2++;
			System.out.println("Se sube el pasajero " + id + " al vagon 2"+  " hay en el v2 " + pasV2 + " pasajeros");
		}
		mutex.release();
		//Paramos al pasajero, será liberado por finViaje
		if(pasV1 <= tamV && pasV2 == 0) { //Tendremos un semaforo diferente dependiendo de si está en el v1 o el v2
			esperaPasajeroViajeV1.acquire();
		} else if (pasV2 < tamV){
			esperaPasajeroViajeV2.acquire();
		} else if (pasV2 == tamV) {
			esperaMaquinistaTrenLleno.release(); //El último pasajero despierta al maquinista
			esperaPasajeroViajeV2.acquire(); //y luego también se para
		}
		//Esto no se ejecutará hasta que finViaje haga esperaPasajeroViajeV1.acquire
		mutex.acquire();
		if(pasV1 >1){ //Si quedan más de 1 pasajero en el v1
			pasV1--;
			System.out.println("Se baja el pasajero " + id + " del vagon 1");
			esperaPasajeroViajeV1.release(); //Despertado en cascada
		} else if (pasV1 == 1) { //Cuando queda 1 pasajero, este despierta a los del v2 antes de irse
			pasV1--;
			System.out.println("Se baja el pasajero " + id + " del vagon 1");
			esperaPasajeroViajeV2.release(); //Despertamos al primer pasajero del vagón 2
		} else if(pasV2!=1){ //Mientras quede más de un pasajero en v2
			pasV2--;
			System.out.println("Se baja el pasajero " + id + " del vagon 2");
			esperaPasajeroViajeV2.release(); //Despertado en cascada
		} else if (pasV2 == 1) { //El último pasajero avisa de qeu el tren está vacío antes de irse
			pasV2--;
			System.out.println("Se baja el pasajero " + id + " del vagon 2");
			if (pasV1 == 0 && pasV2 == 0) {//Esto es para asegurarme, pero no debería hacer falta
				System.out.println("Tren vacio, se puede volver a llenar v1 = " + pasV1 + " v2 = " + pasV2);
				esperaTrenLLeno.release(); //El tren está vacío
			}
		}
		mutex.release();
	}

	public void empiezaViaje() throws InterruptedException {
		esperaMaquinistaTrenLleno.acquire(); //Para que se frene hasta recibir el aviso de que el tren está lleno
		System.out.println("        Maquinista:  empieza el viaje");
	}
	public void finViaje() throws InterruptedException  {
		System.out.println("        Maquinista:  fin del viaje");
		esperaPasajeroViajeV1.release(); //El viaje termina y se libera al primer pasajero del V1
	}
}
