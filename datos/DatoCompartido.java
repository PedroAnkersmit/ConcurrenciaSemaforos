package datos;

import java.util.concurrent.Semaphore;

public class DatoCompartido {
	private int dato = 0; //Dato a procesar
	private int nProcesadores; //Numero de procesadores totales
	private int procPend = 0; //Numero de procesadores pendientes de procesar el dato

	private Semaphore mutexDato = new Semaphore(1);
	private Semaphore mutexProcPend = new Semaphore(1);
	private Semaphore esperaProcesado = new Semaphore(0);
	private Semaphore esperaDato = new Semaphore(0);
	private Semaphore esperaPendientes = new Semaphore(0);
	private Semaphore oleada = new Semaphore(1);
	/* Recibe como par�metro el n�mero de procesadores que tienen que manipular 
	 * cada dato generado. Debe ser un n�mero mayor que 0. 
	 */
	public DatoCompartido(int nProc) {
		nProcesadores = nProc;
	}
	
	/*  El Generador utiliza este metodo para almacenar un nuevo dato a procesar. 
	 *  Una vez almacenado el dato se debe avisar a los procesadores de que se ha 
	 *  almacenado un nuevo dato. 
	 *  
	 *  Por ultimo, el Generador tendra que esperar en este metodo a que todos los 
	 *  procesadores terminen de procesar el dato. Una vez que todos terminen, 
	 *  se devolvera el resultado del procesamiento, permitiendo al Generador 
	 *  la generacion de un nuevo dato.
	 * 
	 *  CS_Generador: Una vez generado un dato, el Generador espera a que todos los procesadores 
	 *  terminen antes de generar el siguiente dato
	 */
	public int generaDato(int d) throws InterruptedException {
		//COMPLETAR y colocar los mensajes en el lugar apropiado dentro del c�digo
		esperaPendientes.acquire(); //El generador se espera a que haya 10 procesadores esperándole
		mutexDato.acquire();
		dato = d; //Cogemos el dato
		mutexDato.release();
		System.out.println("Dato a procesar: " + dato);
		esperaDato.release(); //Liberamos al primer procesador
		System.out.println("Numero de procesadores pendientes: " + procPend);
		esperaProcesado.acquire(); //Nos bloqueamos hasta que haya terminado el procesado
		return dato;
	}

	/*  El Procesador con identificador id utiliza este metodo para leer el 
	 *  dato que debe procesar (el dato se devuelve como valor de retorno del metodo). 
	 *  Debera esperarse si no hay datos nuevos para procesar 
	 *  o si otro procesador esta manipulando el dato. 
	 * 
	 *  CS1_Procesador: Espera si no hay un nuevo dato que procesar. 
	 *                  Esto puede ocurrir porque el generador aun no haya almacenado 
	 *                  ningun dato o porque el Procesador ya haya procesado el dato 
	 *                  almacenado en ese momento en el recurso compartido.
	 *  CS2_Procesador: Espera a que el dato este disponible para poder procesarlo 
	 *                  (es decir, no hay otro Procesador procesando al dato)
	 */
	public int leeDato(int id) throws InterruptedException {
		oleada.acquire(); //Deja pasar a 1 y se bloquea
		mutexProcPend.acquire();
		procPend++; //Aumentamos el n de procPend
		mutexProcPend.release();
		if(procPend < nProcesadores){ //Si nos caben procesadores
		oleada.release(); //Les dejamos pasar
		} else { //Si ya no caben
			esperaPendientes.release(); //Avisamos al generador para que genere el dato y no dejamos pasar más procesadores
		}
		esperaDato.acquire(); //Nos bloqueamos hasta que el dato esté generado

		return dato;
	}
	/*  El Procesador con identificador id almacena en el recurso compartido el resultado 
	 *  de haber procesado el dato. Una vez hecho esto actuara de una de las dos formas siguientes: 
	 *  
	 *  (1) Si aun hay procesadores esperando a procesar el dato los avisara, 
	 *  (2) Si el era el ultimo procesador avisara al Generador de que han terminado. 
	 * 
	 */
	public void actualizaDato(int id, int datoActualizado) throws InterruptedException {
		//COMPLETAR y colocar los mensajes en el lugar apropiado dentro del c�digo
		mutexDato.acquire();
		dato = datoActualizado; //Actualizamos el dato
		mutexDato.release();
		System.out.println("	Procesador " + id + " ha procesado el dato. Nuevo dato: " + dato);
		mutexProcPend.acquire();
		procPend--; //Disminuimos el numero de procesadores pendientes
		mutexProcPend.release();
		System.out.println("Numero de procesadores pendientes: " + procPend);
		if(procPend > 0){ //Si nos quedan procesadores pendientes
		esperaDato.release(); //Los liberamos, despertado en cascada
		}
		if(procPend == 0){ //Si ya no quedan
			esperaProcesado.release(); //Avisamos al generador de que el procesado ha terminado
			oleada.release(); //Liberamos el paso al resto de procesadores
		}

	}
}
