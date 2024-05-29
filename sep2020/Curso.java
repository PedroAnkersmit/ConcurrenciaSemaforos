package sep2020;
import java.util.concurrent.locks.*;
public class Curso {
	//Numero maximo de alumnos cursando simultaneamente la parte de iniciacion
	private final int MAX_ALUMNOS_INI = 10;

	//Numero de alumnos por grupo en la parte avanzada
	private final int ALUMNOS_AV = 3;

	Lock l = new ReentrantLock(true);
	Condition esperaIniciacion = l.newCondition();
	Condition esperaAvanzado = l.newCondition();
	Condition esperaTrabajoAvanzado = l.newCondition();
	Condition esperaTerminarAvanzado = l.newCondition();

	int alumnosIniciacion = 0;
	int alumnosEsperandoAvanzado = 0;
	int alumnosEsperandoTerminar = 0;
	boolean pAvanzadaOcupada = false;
	boolean esperaIniciar = true;
	boolean esperaTerminar = true;
	//El alumno tendra que esperar si ya hay 10 alumnos cursando la parte de iniciacion
	public void esperaPlazaIniciacion(int id) throws InterruptedException{
		l.lock();
		try{
			//Espera si ya hay 10 alumnos cursando esta parte
			while(alumnosIniciacion >= MAX_ALUMNOS_INI) esperaIniciacion.await();
			alumnosIniciacion++;
			//Mensaje a mostrar cuando el alumno pueda conectarse y cursar la parte de iniciacion
			System.out.println("PARTE INICIACION: Alumno " + id + " cursa parte iniciacion");
		} finally {
			l.unlock();
		}

	}

	//El alumno informa que ya ha terminado de cursar la parte de iniciacion
	public void finIniciacion(int id) throws InterruptedException {
		l.lock();
		try {
			//Mensaje a mostrar para indicar que el alumno ha terminado la parte de principiantes
			System.out.println("PARTE INICIACION: Alumno " + id + " termina parte iniciacion");
			alumnosIniciacion--;
			//Libera la conexion para que otro alumno pueda usarla
			if(alumnosIniciacion == MAX_ALUMNOS_INI-1) esperaIniciacion.signalAll();
		} finally {
			l.unlock();
		}
	}
	
	/* El alumno tendra que esperar:
	 *   - si ya hay un grupo realizando la parte avanzada
	 *   - si todavia no estan los tres miembros del grupo conectados
	 */
	public void esperaPlazaAvanzado(int id) throws InterruptedException{
		//Espera a que no haya otro grupo realizando esta parte
		l.lock();
		try {
			while(pAvanzadaOcupada) esperaAvanzado.await();
			alumnosEsperandoAvanzado++;
			//Espera a que haya tres alumnos conectados
			if(alumnosEsperandoAvanzado == ALUMNOS_AV) {
				pAvanzadaOcupada = true;
				esperaIniciar = false;
				esperaTerminar = true;
				alumnosEsperandoAvanzado = 0;
				esperaTrabajoAvanzado.signalAll();
			}
			System.out.println("PARTE AVANZADA: Alumno " + id + " espera a que haya " + ALUMNOS_AV + " alumnos");
			while (esperaIniciar) esperaTrabajoAvanzado.await();        //Mensaje a mostrar si el alumno tiene que esperar al resto de miembros en el grupo
			//Mensaje a mostrar cuando el alumno pueda empezar a cursar la parte avanzada

			System.out.println("PARTE AVANZADA: Hay " + ALUMNOS_AV + " alumnos. Alumno " + id + " empieza el proyecto");

		} finally {
			l.unlock();
		}
	}
	
	/* El alumno:
	 *   - informa que ya ha terminado de cursar la parte avanzada 
	 *   - espera hasta que los tres miembros del grupo hayan terminado su parte 
	 */ 
	public void finAvanzado(int id) throws InterruptedException{
		//Espera a que los 3 alumnos terminen su parte avanzada
		l.lock();
		try{
			alumnosEsperandoTerminar++;
			//Mensaje a mostrar si el alumno tiene que esperar a que los otros miembros del grupo terminen
			System.out.println("PARTE AVANZADA: Alumno " +  id + " termina su parte del proyecto. Espera al resto");
			if(alumnosEsperandoTerminar == ALUMNOS_AV){
				esperaTerminar = false;
				alumnosEsperandoTerminar = 0;
				esperaIniciar = true;
				esperaTerminarAvanzado.signalAll();

				pAvanzadaOcupada = false;
				esperaAvanzado.signalAll();
			}

			while(esperaTerminar) {
				esperaTerminarAvanzado.await();
			}


			//Mensaje a mostrar cuando los tres alumnos del grupo han terminado su parte
			System.out.println("PARTE AVANZADA: LOS " + ALUMNOS_AV + " ALUMNOS HAN TERMINADO EL CURSO");
		} finally {
			l.unlock();
		}
		}

}
