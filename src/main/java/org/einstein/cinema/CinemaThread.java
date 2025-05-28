package org.einstein.cinema;

import org.einstein.controllers.MainController;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class CinemaThread {
	
	public static final int CAPACITY = 5;
	public static final int EXHIBITION_TIME = 10;

	static Semaphore mutex = new Semaphore(1);
	static Semaphore salaCheia = new Semaphore(0);
	static Semaphore inicioFilme = new Semaphore(0);
	static Semaphore porta = new Semaphore(CAPACITY);

	static AtomicInteger dentro = new AtomicInteger(0);

	public static class Demonstrator extends Thread {
		public void run() {
			while (true) {
				try {
					salaCheia.acquire();
					log("[Demonstrador] Iniciando exibição.");
					inicioFilme.release(CAPACITY);
					Instant inicio = Instant.now();
					while (Duration.between(inicio, Instant.now()).getSeconds() < EXHIBITION_TIME) {}
					log("[Demonstrador] Exibição encerrada.");
				} catch (Exception ignored) {}
			}
		}
	}

	public static class Fan extends Thread {
		String name;
		int coffeeBreakTime;
		volatile boolean active = true;
		
		MainController mainControllerThread;

		public Fan(String name, int coffeeBreakTime, MainController mainController) {
			this.name = name;
			this.coffeeBreakTime = coffeeBreakTime;
			this.mainControllerThread = mainController;
		}

		public void terminate() {
			active = false;
			this.interrupt();
		}

		public String getNameThread() {
			return name;
		}

		@Override
		public void run() {
			log("[Fã " + name + "] Criado");

			while (active) {
				try {
					log("[Fã " + name + "] Na fila...");
					porta.acquire();

					int count = dentro.incrementAndGet();
					log("[Fã " + name + "] Entrou na sala. Total: " + count);

					if (count == CAPACITY) {
						salaCheia.release();
					}

					inicioFilme.acquire();
					log("[Fã " + name + "] Assistindo o filme...");
					Instant exbStart = Instant.now();
					while (Duration.between(exbStart, Instant.now()).getSeconds() < EXHIBITION_TIME) {}

					int restam = dentro.decrementAndGet();
					log("[Fã " + name + "] Saiu da sala. Restam: " + restam);

					if (restam == 0) {
						log("[Fã " + name + "] Fui o último a sair. Liberando próxima turma.");
						porta.release(CAPACITY);
					}

					log("[Fã " + name + "] Indo para o coffee break.");
					Instant cbStart = Instant.now();
					while (Duration.between(cbStart, Instant.now()).getSeconds() < coffeeBreakTime) {}

				} catch (InterruptedException ignored) {
				}
			}
		}
	}

	private static void log(String msg) {
		try {
			mutex.acquire();
			MainController.getInstance().updateTextArea(msg);
		} catch (InterruptedException ignored) {
		} finally {
			mutex.release();
		}
	}
}