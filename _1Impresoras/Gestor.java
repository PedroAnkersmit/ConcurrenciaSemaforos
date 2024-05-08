package _1Impresoras;

import java.util.concurrent.Semaphore;

public class Gestor {
	private int numImpresoras;
	//Para asegurar la integridad de la variable compartida numImpresoras necesitamos un mutex
	private Semaphore mutex = new Semaphore(1); //Los mutex siempre se declaran a 1
	//CS un cliente no puede usar una impresora que no existe
	private Semaphore hayLibres = new Semaphore(1); //Se pone a 1 pq se asume que hay 1 impresora libre al principio
	//hayLibres = 0 <==> numImpresoras == 0
	public Gestor(int N) throws IllegalArgumentException{
		if (N<=0) throw new IllegalArgumentException();
		
		this.numImpresoras = N;
		System.out.println("Hay " + N + " impresoras libres");
	}
	
	public void cogeImpresora(int id) throws InterruptedException{
		hayLibres.acquire(); //Al entrar ponemos el semáforo en rojo para que no entren más clientes
		mutex.acquire(); //Acquirimos el semáforo, es decir, ahora está a rojo
		numImpresoras--; //Se decrementa porque alguien ha cogido una impresora
		if(numImpresoras > 0){
			hayLibres.release(); //Si aún quedan impresoras libres, ponemos el semáforo en verde para que entre otro cliente
		}
		System.out.println("Hay " + numImpresoras + " impresoras libres");
		mutex.release(); //Liberamos el semáforo, es decir, lo ponemos en verde
	}
	
	public void sueltaImpresora(int id) throws InterruptedException{
		mutex.acquire(); //Acquirimos el semáforo, es decir, ahora está a rojo
		numImpresoras++; //Se aumenta porque alguien ha soltado una impresora
		if(numImpresoras == 1){ //En este caso, el semáforo estaba en rojo porque no había impresoras libres,
			//así que le toca a este método poner el semáforo en verde, ya que ahora sí hay impresora libre
			//En cualquier otro caso es el otro método el que pone el semáforo en verde
			hayLibres.release(); //El cliente libera la impresora, ponemos el semáforo en verde
		}
		System.out.println("Hay " + numImpresoras + " impresoras libres");
		mutex.release(); //Liberamos el semáforo, es decir, lo ponemos en verde
	}
}
