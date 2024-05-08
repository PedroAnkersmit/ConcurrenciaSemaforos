package _1Impresoras;

public class Principal {
	public static void main(String[] args) {
		final int N = 20; //numero de clientes
		final int M = 5; //numero de impresoras

		Gestor g = new Gestor(M);
		Cliente [] clientes = new Cliente[N];

		for (int i=0; i<clientes.length;i++){
			clientes[i] = new Cliente(i,g);
		}

		for (int i=0; i<clientes.length;i++){
			clientes[i].start();
		}

		for (int i=0; i<clientes.length;i++){
			try {
				clientes[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
