package org.einstein.cinema;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class CinemaThread {

	public static final int CAPACITY = 5;
	public static final int EXHIBITION_TIME = 10; // segundos

	static Semaphore mutex = new Semaphore(1);
	static Semaphore salaCheia = new Semaphore(0);
	static Semaphore inicioFilme = new Semaphore(0);
	static Semaphore porta = new Semaphore(CAPACITY);

	static AtomicInteger dentro = new AtomicInteger(0);

	public static class Demonstrator extends Thread {
		Instant inicio;
		public void run() {
			while (true) {
				try {
					salaCheia.acquire();

					log("[Demonstrador] Iniciando exibição.");
					inicioFilme.release(CAPACITY);
					
					inicio = Instant.now();
					while (Duration.between(inicio, Instant.now()).getSeconds() < EXHIBITION_TIME) {
						// Esperando o tempo de exibição
					}

					log("[Demonstrador] Exibição encerrada. Aguardando todos saírem...");
				} catch (Exception ignored) {
				}
			}
		}
	}

	public static class Fan extends Thread {
		String name;
		int coffeeBreakTime;
		volatile boolean active = true;
		Instant cbStart;
		Instant exbStart;

		public Fan(String name, int coffeeBreakTime) {
			this.name = name;
			this.coffeeBreakTime = coffeeBreakTime;
		}

		public void terminate() {
			active = false;
			this.interrupt();
		}

		public void run() {
			try {
				log("[Fã " + name + "] Criado");

				while (true) {
					porta.acquire();

					int count = dentro.incrementAndGet();
					log("[Fã " + name + "] Entrou na sala. Total: " + count);

					if (count == CAPACITY) {
						salaCheia.release(); // último avisa o demonstrador
					}

					inicioFilme.acquire();

					exbStart = Instant.now();
					while (Duration.between(exbStart, Instant.now()).getSeconds() < EXHIBITION_TIME) {
						// Esperando o tempo de exibição
					}

					int restam = dentro.decrementAndGet();
					log("[Fã " + name + "] Saiu da sala. Restam: " + restam);

					if (restam == 0) {
						log("[Fã " + name + "] Fui o último a sair. Liberando próxima turma.");
						porta.release(CAPACITY);
					}

					log("[Fã " + name + "] Indo para o coffee break.");
					cbStart = Instant.now();
					coffeeBreak();
				}
			} catch (InterruptedException ignored) {
			}
		}

		private void coffeeBreak() throws InterruptedException {
			log("[Fã " + name + "] Coffee break...");
			while (Duration.between(cbStart, Instant.now()).getSeconds() < coffeeBreakTime) {
				// Simulando pausa
			}
		}
	}

	private static void log(String msg) {
		try {
			mutex.acquire();
			System.out.println(msg);
		} catch (InterruptedException ignored) {
		} finally {
			mutex.release();
		}
	}
}