package _3Barrera;

public class Principal {

	public static void main(String[] argv){
		final int N = 10;
		
		Barrera barrera = new Barrera(N);
		
		Worker [] trabajadores = new Worker[N];
		for (int i = 0; i< trabajadores.length; i++){
			trabajadores[i] = new Worker(i,barrera);
		}
		
		for (int i = 0; i< trabajadores.length; i++){
			trabajadores[i].start();
		}
		
		for (int i = 0; i< trabajadores.length; i++){
			try {
				trabajadores[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
