package plantas;

public class Principal {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Huerto b = new Huerto(5);
		David david = new David(b);
		Juan juan = new Juan(b);
		Fran fran = new Fran(b);
		david.start();juan.start();fran.start();

	}

}
