package plantas;

import java.util.Random;

public class David extends Thread{
	private Random r = new Random();
	private Huerto b;
	public David(Huerto b){
		this.b = b;
	}
	public void run(){
		for (int i=0 ; i<25; i++){
			try {
				Thread.sleep(r.nextInt(10));
				b.esperaHacerHoyo();
				Thread.sleep(r.nextInt(200)); //David está haciendo el hoyo
				b.finHacerHoyo(i);	
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
