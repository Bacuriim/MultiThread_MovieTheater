package org.einstein.cinema;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class CinemaThread {

	public static final int CAPACITY = 5;
	public static final int EXHIBITION_TIME = 2; // segundos
	public static final int BREAK_TIME = 3; // segundos

	static Semaphore mutex = new Semaphore(1);
	static Semaphore salaCheia = new Semaphore(0);
	static Semaphore inicioFilme = new Semaphore(0);
	static Semaphore fimFilme = new Semaphore(0);
	static Semaphore porta = new Semaphore(CAPACITY); // controla o número máximo dentro da sala

	static AtomicInteger dentro = new AtomicInteger(0);
	static AtomicInteger total = new AtomicInteger(0);

	public static class Demonstrator extends Thread {
		public void run() {
			while (true) {
				try {
					salaCheia.acquire(); // Aguarda o último fã liberar a entrada

					mutex.acquire();
					System.out.println("[Demonstrador] Iniciando exibição.");
					mutex.release();

					showFilm();

					for (int i = 0; i < CAPACITY; i++) {
						inicioFilme.release();
					}

					for (int i = 0; i < CAPACITY; i++) {
						fimFilme.acquire();
					}

					mutex.acquire();
					System.out.println("[Demonstrador] Exibição encerrada. Próximo ciclo...");
					mutex.release();

					// Libera a entrada para o próximo grupo
					porta.release(CAPACITY);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// ----------------- Fã -----------------
	public static class Fan extends Thread {
		int id;
		int coffeeBreakTime;
		volatile boolean active = true;

		public void terminate() {
			active = false;
			this.interrupt(); // acorda caso esteja dormindo
		}

		public Fan(int id, int coffeeBreakTime) {
			this.id = id;
			this.coffeeBreakTime = coffeeBreakTime;
		}

		public void run() {
			try {
				mutex.acquire();
				total.incrementAndGet();
				System.out.println("[Fã " + id + "] Criado");
				mutex.release();
				while (true) {
					porta.acquire(); // entra na sala se houver espaço

					int count = dentro.incrementAndGet();
					System.out.println("[Fã " + id + "] Entrou na sala. Total: " + count);

					if (count == CAPACITY) {
						salaCheia.release(); // o último avisa o demonstrador
					}
					
					System.out.println("[Fã " + id + "] Assistindo ao filme...");
					
					inicioFilme.acquire(); // espera o demonstrador liberar

					Thread.sleep(EXHIBITION_TIME * 1000L);
					fimFilme.release();

					int restam = dentro.decrementAndGet();
					System.out.println("[Fã " + id + "] Saiu da sala. Restam: " + restam);

					System.out.println("[Fã " + id + "] Indo para o coffee break.");
					coffeeBreak(id, coffeeBreakTime);
				}
			} catch (Exception ignored) {
				
			}
		}
	}

	public static void showFilm() throws InterruptedException {
		System.out.println("[Filme] Exibindo filme...");
		Thread.sleep(EXHIBITION_TIME * 1000L);
		System.out.println("[Filme] Finalizando filme...");
	}

	public static void coffeeBreak(int id, int breakTime) throws InterruptedException {
		System.out.println("[Fã " + id + "] Coffee break...");
		Thread.sleep(breakTime * 1000L);
	}
}