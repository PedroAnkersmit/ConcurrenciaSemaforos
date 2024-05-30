package tmecanico;

import java.util.concurrent.locks.Condition;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Taller {

	Lock l = new ReentrantLock();
	Condition esperaHayClientes = l.newCondition();
	Condition esperaRampaOcupada = l.newCondition();
	Condition esperaArregloCliente = l.newCondition();
	Condition esperaArregloAdministrativo = l.newCondition();
	Condition esperaFacturaLista = l.newCondition();

	boolean rampaOcupada = false;
	boolean arregloTerminado = false;
	boolean facturaTerminada = false;
	boolean clienteEsperando = false;
	public void esperaParaRevisar() throws InterruptedException{
		l.lock();
		try {
			while(!clienteEsperando) {
				System.out.println("MECANICO: No hay clientes, me espero");
				esperaHayClientes.await();
			}
			System.out.println("MECANICO: Llego un cliente, habra que trabajar");
		} finally {
			l.unlock();
		}
	}
	
	public void finRevision(){
		l.lock();
		try {
			arregloTerminado = true;
			System.out.println("MECANICO: Termine el arreglo, a cobrar (aviso al cliente y al administrativo)");
			esperaArregloCliente.signal();
			esperaArregloAdministrativo.signal();
			clienteEsperando = false;
		}finally {
			l.unlock();
		}
	}

	public void esperaParaFacturar() throws InterruptedException{
		l.lock();
		try {
			while (!arregloTerminado){
				System.out.println("ADMIN: No hay nada que cobrar aun");
			 esperaArregloAdministrativo.await();
			}
			arregloTerminado = false;
			System.out.println("ADMIN: Arreglos terminados, a hacer la factura");
		}finally {
			l.unlock();
		}
	}
	
	public void finFactura(){
		l.lock();
		try {
			facturaTerminada = true;
			System.out.println("ADMIN: Factura hecha, aviso al cliente");
			esperaFacturaLista.signal();
		}finally {
			l.unlock();
		}
	}	
	
	public void revisarCoche(int id) throws InterruptedException{
		l.lock();
		try {
			while (rampaOcupada) {
				System.out.println("CLIENTE(" + id + "): La rampa esta ocupada, me espero");
				esperaRampaOcupada.await();
			}
			System.out.println("CLIENTE(" + id + "): Dejo el coche en la rampa y aviso al mecanico" );
			rampaOcupada = true;
			clienteEsperando = true;
			esperaHayClientes.signal();
			while(!arregloTerminado) {
				System.out.println("CLIENTE(" + id + "): Me tomo un cafelito mientras me arreglan");
				esperaArregloCliente.await();
			}//arregloTerminado = false;
			while(!facturaTerminada) {
				System.out.println("CLIENTE(" + id + "): Me espero a que este la factura");
				esperaFacturaLista.await();
			}
			facturaTerminada = false;
			rampaOcupada = false;
			System.out.println("CLIENTE(" + id + "): Ya tengo mi factura y me voy, aviso al siguiente cliente");
			esperaRampaOcupada.signal();
		}finally {
			l.unlock();
		}

	}
}
