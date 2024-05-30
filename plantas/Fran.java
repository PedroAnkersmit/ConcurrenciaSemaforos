package plantas;

import java.util.*;
public class Fran extends Thread{
	private Random r = new Random();
	private Huerto b;

	public Fran(Huerto b){
		this.b = b;
	}
	public void run(){
		for (int i=0 ; i<25; i++){
			try {
				Thread.sleep(r.nextInt(10));
				b.esperaEcharTierra();
				Thread.sleep(r.nextInt(200)); //Fran está cubriendo el hoyo
				b.finEcharTierra(i);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
