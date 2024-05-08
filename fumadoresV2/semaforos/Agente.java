package fumadoresV2.semaforos;

import java.util.*;
public class Agente extends Thread{
	private Random r = new Random();
	private Mesa m;
	public Agente(Mesa m){
		this.m = m;
	}

	public void run(){
		while (true){
			try {
				int ingr1, ingr2;
				ingr1=r.nextInt(3);
				do{
					ingr2=r.nextInt(3);
				}while(ingr1==ingr2);
				m.nuevosIngredientes(ingr1,ingr2); 
				Thread.sleep(r.nextInt(200));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
