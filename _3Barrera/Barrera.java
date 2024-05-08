package _3Barrera;

import java.util.concurrent.Semaphore;

public class Barrera {
	//CS el worker no puede empezar la iteración i hasta que todos hayan terminado la i-1
	private int n; //numero de trabajadores
	private int hanTerminado = 0;
	private Semaphore mutex = new Semaphore(1); //Semáforo para la exclusión mutua
	private Semaphore espera = new Semaphore(0); //Semáforo que será para espera, siempre será cero
	private Semaphore sigIteracion = new Semaphore(1); //Iniciado a rojo

	public Barrera(int n){
		this.n = n;
	}

	public void finIteracion(int id, int i) throws InterruptedException{
		sigIteracion.acquire(); //Bloqueamos la siguiente iteración
		mutex.acquire();
		hanTerminado++;
		//System.out.println("El worker "+id+ " ha terminado la iter "+i);
		if (hanTerminado < n){
			mutex.release(); //libero el mutex para que no se bloquee
			sigIteracion.release();
			espera.acquire(); //Bloqueo el trabajador
			mutex.acquire(); //Lo vuelvo a adquirir
		}
		//Esta parte sólo se ejecuta cuando hanTerminado llega a ser n (Es decir, han terminado todos)
		hanTerminado--;
		if(hanTerminado != 0) espera.release();//Voy liberándolos de la espera, pero todavía no van a poder hacer
		//la siguiente iteracion, así que siguen pasando por aquí
		else{ //Cuando hanTerminado = 0, quiere decir que todos los trabajadores han salido de la espera,
			// así que libero la siguiente iteración
			sigIteracion.release();
		}
		mutex.release();
	}
}
