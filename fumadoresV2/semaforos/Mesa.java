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
	//Habrá un semáforo para cada fumador, que le permita o no pasar dependiendo de los ingredientes que tenga
	private Semaphore esperaPapelTabaco = new Semaphore(0);
	private Semaphore esperaPapelCerillas = new Semaphore(0);
	private Semaphore esperaTabacoCerillas = new Semaphore(0);
	//Tenemos que controlar que el agente no siga poniendo ingredientes todo el tiempo
	private Semaphore esperaAgente = new Semaphore(1);

	//El agente llama a este metodo para poner en la mesa dos ingredientes de los tres
	public void nuevosIngredientes(int ingr1, int ingr2) throws InterruptedException{
		esperaAgente.acquire(); //Semáforo de tipo 1, deja pasar y se pone a rojo
		mutex.acquire();
		ingr[ingr1]=true;
		ingr[ingr2]=true;
		System.out.println("En la mesa hay " + java.util.Arrays.toString(ingredientes()));
		//Controlamos qué fumador puede fumar
		if(ingr[0] && ingr[1]){ //Si tenemos tabaco y papel
			esperaPapelTabaco.release();
		}else if(ingr[1] && ingr[2]){ //Si tenemos papel y cerillas
			esperaPapelCerillas.release();
		} else { //Si tenemos tabaco y cerillas
			esperaTabacoCerillas.release();
		}
		mutex.release();
	}
	//El fumador id quiere los dos ingredientes que le faltan para poder fumar
	public void quieroFumar(int id) throws InterruptedException{
	//El fumador se para en seco hasta que haya ingredientes en la mesa.
		switch (id){ //Dependiendo de qué tipo de fumador sea, le paramos en un semáforo o en otro
			case 0: esperaPapelCerillas.acquire(); break;
			case 1: esperaTabacoCerillas.acquire(); break;
			case 2: esperaPapelTabaco.acquire(); break;
		}

	}
	
	//El fumador id indica que ha terminado de fumar
	public void finFumar(int id) throws InterruptedException{
		mutex.acquire();
		for(int i = 0; i <3; i++){ //Nos aseguramos de que ya no quedan ingredientes
			ingr[i] = false;
		}
		esperaAgente.release(); //Liberamos al agente
		mutex.release();

	}
}
