package plantas;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Huerto { //Recurso
	
	private int N; //distancia máxima entre David y Fran

	Lock l = new ReentrantLock();
	Condition esperaDavid = l.newCondition();
	Condition esperaJuan = l.newCondition();
	Condition esperaFran = l.newCondition();
	Condition esperaPalaDavid = l.newCondition();
	Condition esperaPalaFran = l.newCondition();

	boolean palaDavid = true;
	boolean lejosDavid = false;
	int hoyosCavados = 0;
	int hoyosSemilla = 0;
	int hoyosRellenos = 0;
	int distanciaDavid = 0;


	public Huerto(int tam){
		
		N = tam;
	}
	
	/**
	 * David espera en este método para poder empezar a hacer 
	 * un hoyo. Tiene que esperar si está alejado N hoyos sin rellenar de Fran y,
	 * opcionalmente, si la pala compartida está siendo utilizada.
	 */
	public  void esperaHacerHoyo() throws InterruptedException{
		l.lock();
		try {
			while (!palaDavid){
				System.out.println("DAVID: No tengo la pala, me espero");
				esperaPalaDavid.await();
			}
			distanciaDavid = hoyosCavados - hoyosRellenos;
			while (distanciaDavid >= N) {
				System.out.println("DAVID: Estoy demasiado lejos, me espero");
				lejosDavid = true;
				palaDavid = false;
				System.out.println("DAVID: Le doy la pala a Fran");
				esperaPalaFran.signal();
				esperaDavid.await();
			}
		} finally {
			l.unlock();
		}
	}
	
	/**
	 * David ha hecho el hoyo número num. Actualiza el recurso
	 * para informar a Juan y a Fran.
	 * @param num
	 */
	public  void finHacerHoyo(int num) throws InterruptedException{
		l.lock();
		try {
			hoyosCavados++;
			System.out.println("DAVID: He acabado el hoyo " + hoyosCavados);
			if(hoyosSemilla < hoyosCavados) {
				System.out.println("DAVID: Aviso a Juan");
				esperaJuan.signal();

			}

		} finally {
			l.unlock();
		}
	}
	
	/**
	 * Juan espera en este método para poder echar semillas a 
	 * un hoyo. Debe esperar si no hay un hoyo sin semillas.
	 */
	public  void esperaPonerSemilla() throws InterruptedException{
		l.lock();
		try {
			while(hoyosCavados <= hoyosSemilla) {
				System.out.println("JUAN: No hay hoyos vacios disponibles, me espero");
				esperaJuan.await();
			}

		} finally {
			l.unlock();
		}
	}
	
	/**
	 * Juan ha puesto semillas en el  hoyo número num. 
 	 * Actualiza el recurso para informar Fran.
	 * @param num
	 */
	public  void finPonerSemilla(int num) throws InterruptedException{
		l.lock();
		try {
			hoyosSemilla++;
			System.out.println("JUAN: He puesto una semilla " + hoyosSemilla);
			if(hoyosRellenos <= hoyosSemilla) {
				System.out.println("JUAN: Aviso a Fran");
				esperaFran.signal();
			}

		} finally {
			l.unlock();
		}
	}
	
	/**
	 * Fran espera en este método para poder rellenar 
	 * un hoyo. Espera si no hay un hoyo con semilla no relleno
	 *  y, opcionalmente, si la pala no está libre.
	 */
	public  void esperaEcharTierra() throws InterruptedException{
		l.lock();
		try {
			while (palaDavid){
				System.out.println("FRAN: No tengo la pala, me espero");
				esperaPalaFran.await();
			}
			if (hoyosSemilla <= hoyosRellenos) {
				System.out.println("FRAN: No hay hoyos con semilla, me espero");
				esperaFran.await();
			}

		} finally {
			l.unlock();
		}
	}
	
	/**
	 * Fran ha rellenado el  hoyo número num. 
 	 * Actualiza el recurso para informar a Juan y a David.
	 * @param num
	 */
	public  void finEcharTierra(int num) throws InterruptedException{
		l.lock();
		try {
			hoyosRellenos++;
			System.out.println("FRAN: Rellleno un hoyo " + hoyosRellenos);

			if(lejosDavid) {
				distanciaDavid = hoyosCavados - hoyosRellenos;
				if(distanciaDavid < N) {
					System.out.println("FRAN: David ya no esta lejos, le aviso");
					lejosDavid = false;
					esperaDavid.signal();
				}
			}
			System.out.println("FRAN: Le doy la pala a David");
			palaDavid = true;
			esperaPalaDavid.signal();
		} finally {
			l.unlock();
		}
	}
	


}
