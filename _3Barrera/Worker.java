package _3Barrera;

import java.util.Random;

public class Worker extends Thread{
	private final int N = 25;
	private int id;
	private Barrera barrera;
	private static Random rnd = new Random();
	
	public Worker(int id, Barrera b){
		this.id = id;
		this.barrera = b;
	}
	
	public void run(){
		for (int i = 0; i< N; i++){
			//Realizar la iteración i
			try {
				Thread.sleep(rnd.nextInt(100));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Worker " + id + " iteración " + i);
			try {
				barrera.finIteracion(id, i);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
