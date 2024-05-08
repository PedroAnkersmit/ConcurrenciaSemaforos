package fumadoresV2.semaforos;

import java.util.concurrent.Semaphore;

public class Mesa {
	private boolean[] ingr; //true-tiene el ingrediente/false-no lo tiene
	
	public Mesa(){
		ingr = new boolean[3];
	}
	
	//Método privado que devuelve los ingredientes que hay en la mesa
	private String[] ingredientes(){
		String[] nombres = new String[3];
		if (ingr[0]){
			nombres[0] = "tabaco";
		}
		if (ingr[1]){
			nombres[1] = "papel";
		}
		if (ingr[2]){
			nombres[2] = "cerillas";
		}	
		return nombres;
	}

	private Semaphore mutex = new Semaphore(1);
	private Semaphore hayIngredientes = new Semaphore(0);
	private Semaphore puedoFumar = new Semaphore(0);
	private Semaphore esperaAgente = new Semaphore(0);
	private Semaphore puedoPoner = new Semaphore(1);

	//El agente llama a este metodo para poner en la mesa dos ingredientes de los tres
	public void nuevosIngredientes(int ingr1, int ingr2) throws InterruptedException{
		//mutex para controlar los ingredientes

		mutex.acquire();
		for (int i = 0; i < ingr.length; i++) {
			ingr[i] = false;
		}
		ingr[ingr1] = true;
		ingr[ingr2] = true;
		/*int[] a = {0,1,2};
		a[ingr1] = 0;
		a[ingr2] = 0;
		int x = 0;
		for(int i = 0; i < a.length; i++){
			if(a[i] != 0){
				x = i;
			}
		}*/
		String[] n = ingredientes();
		System.out.println("El agente pone en la mesa " + n[ingr1] + " y " + n[ingr2]);
		mutex.release();
		hayIngredientes.release();
		hayIngredientes.release();
		hayIngredientes.release();
		esperaAgente.acquire();
		mutex.acquire();
		for (int i = 0; i < ingr.length; i++) {
			ingr[i] = false;
		}
		mutex.release();
		for(int i = 0; i < 3; i++){
			puedoFumar.release();
		}

	}

	//El fumador id quiere los dos ingredientes que le faltan para poder fumar
	public void quieroFumar(int id) throws InterruptedException{

		//El fumador se para en seco hasta que haya ingredientes en la mesa.
		hayIngredientes.acquire();

		mutex.acquire();
		boolean aux = ingr[id];
		mutex.release();

		if(aux){
			System.out.println("El fumador " + id + "no puede fumar y se bloquea");
			puedoFumar.acquire();
		}

	}
	
	//El fumador id indica que ha terminado de fumar
	public void finFumar(int id) throws InterruptedException{
		mutex.acquire();
		boolean aux = ingr[id];
		mutex.release();
		if(!aux){
			puedoPoner.release();
			esperaAgente.release();
		}

	}
}
