package org.einstein.cinema;

import org.einstein.controllers.MainController;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class CinemaThread {

	public static AtomicInteger capacity = new AtomicInteger(5);
	public static AtomicInteger exhibitionTime = new AtomicInteger(30);

	static Semaphore mutex = new Semaphore(1);
	static Semaphore salaCheia = new Semaphore(0);
	static Semaphore inicioFilme = new Semaphore(0);
	static Semaphore porta = new Semaphore(capacity.get());

	public static volatile boolean paused = false;
	static AtomicInteger dentro = new AtomicInteger(0);

	public static class Demonstrator extends Thread {
		DemonstratorStatus status;
		MainController controller = MainController.getInstance();

		volatile boolean isRunning = false;

		public Status getStatus() {
			return status;
		}

		public void setCapacity(int capacity) {
			log("[Demonstrador] Capacidade alterada: " + capacity);
			CinemaThread.capacity.set(capacity);
			CinemaThread.porta = new Semaphore(capacity);
		}

		public void setExhibitionTime(int exhibitionTime) {
			log("[Demonstrador] Tempo de Exibicao alterado: " + exhibitionTime);
			CinemaThread.exhibitionTime.set(exhibitionTime);
		}

		public void pauseDemonstrator() {
			isRunning = false;
			CinemaThread.pauseAll();
			if (this.isAlive())
				this.interrupt();
		}

		public void startDemonstrator() {
			isRunning = true;
			CinemaThread.resumeAll();
			if (!this.isAlive())
				this.start();
		}

		public Demonstrator(int exhibitionTime, int capacity) {
			log("[Demonstrador] Criado!");
			CinemaThread.exhibitionTime.set(exhibitionTime);
			CinemaThread.capacity.set(capacity);
			CinemaThread.porta = new Semaphore(capacity);
		}

		public void run() {
			while (true) {

				while (!isRunning) {
					LockSupport.parkNanos(1_000_000);
				}

				try {
					status = DemonstratorStatus.ESPERANDO_FANS;
					log("[Demonstrador] " + status.getDesc());
					salaCheia.acquire();
					status = DemonstratorStatus.EXIBINDO_FILME;
					log("[Demonstrador] " + status.getDesc());
					inicioFilme.release(capacity.get());
					Instant inicio = Instant.now();
					while (Duration.between(inicio, Instant.now()).getSeconds() < exhibitionTime.get()) {
						LockSupport.parkNanos(1_000_000);
					}
					status = DemonstratorStatus.EXIBICAO_ENCERRADA;
					log("[Demonstrador] " + status.getDesc());
				} catch (InterruptedException ignored) {
				}
			}
		}
	}

	public static class Fan extends Thread {
		String name;
		int coffeeBreakTime;
		volatile boolean active = true;
		volatile boolean watchAtLeastOnce = false;
		FanStatus status;
		MainController controller = MainController.getInstance();

		public Fan(String name, int coffeeBreakTime) {
			this.name = name;
			this.coffeeBreakTime = coffeeBreakTime;
		}

		public void terminate() {
			active = false;
			log("[Fan " + getNameThread() + "] Terminando.");
			this.interrupt();
		}

		public String getNameThread() {
			return name;
		}

		public int getBreakTimeThread() {
			return coffeeBreakTime;
		}

		public FanStatus getStatus() {
			return status == null ? FanStatus.NA_FILA : status;
		}

		@Override
		public void run() {
			log("[Fã " + name + "] Criado");

			while (active) {
				try {
					while (CinemaThread.paused) {
						LockSupport.parkNanos(1_000_000);
					}

					if (watchAtLeastOnce)
						status = FanStatus.VOLTANDO_DO_LANCHE;
					else
						status = FanStatus.NA_FILA;
					controller.updateFanStatus();

					log("[Fã " + name + "] " + status.getDesc());
					porta.acquire();

					int count = dentro.incrementAndGet();
					status = FanStatus.ESPERANDO_O_FILME;
					controller.updateFanStatus();
					log("[Fã " + name + "] " + status.getDesc() + " Total: " + count);

					if (count == capacity.get()) {
						salaCheia.release();
					}

					inicioFilme.acquire();
					status = FanStatus.ASSISTINDO;
					controller.updateFanStatus();
					log("[Fã " + name + "] " + status.getDesc());
					watchAtLeastOnce = true;
					Instant exbStart = Instant.now();
					while (Duration.between(exbStart, Instant.now()).getSeconds() < exhibitionTime.get()) {
						while (CinemaThread.paused) {
							LockSupport.parkNanos(1_000_000);
						}
						LockSupport.parkNanos(1_000_000);
					}

					int restam = dentro.decrementAndGet();
					status = FanStatus.SAIU_DA_SALA;
					controller.updateFanStatus();
					log("[Fã " + name + "] " + status.getDesc() + " Restam: " + restam);

					if (restam == 0) {
						log("[Fã " + name + "] Fui o último a sair. Liberando próxima turma.");
						porta.release(capacity.get());
					}

					status = FanStatus.LANCHANDO;
					controller.updateFanStatus();
					log("[Fã " + name + "] " + status.getDesc());
					Instant cbStart = Instant.now();
					while (Duration.between(cbStart, Instant.now()).getSeconds() < coffeeBreakTime) {
						while (CinemaThread.paused) {
							LockSupport.parkNanos(1_000_000);
						}
						LockSupport.parkNanos(1_000_000);
					}
				} catch (InterruptedException e) {
					if (!active) {
						if (FanStatus.ESPERANDO_O_FILME.equals(status)) {
							dentro.decrementAndGet();
						}
						porta.release();
						break;
					}
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

	public enum FanStatus {
		NA_FILA("Esta aguardando na fila."),
		VOLTANDO_DO_LANCHE("Voltou do lanche e esta aguardando na fila."),
		ESPERANDO_O_FILME("Entrou na sala e esta aguardando o inicio do filme."),
		ASSISTINDO("Esta assistindo o filme..."),
		SAIU_DA_SALA("Saiu da sala de cinema."),
		LANCHANDO("Esta lanchando...");

		private String desc;

		FanStatus(String desc) {
			this.desc = desc;
		}

		public String getDesc() {
			return desc;
		}
	}

	public enum DemonstratorStatus {
		ESPERANDO_FANS("Esperando fans."),
		EXIBINDO_FILME("Iniciando exibição."),
		EXIBICAO_ENCERRADA("Exibicao encerrada. Esperando fans.");

		private String desc;

		DemonstratorStatus(String desc) {
			this.desc = desc;
		}

		public String getDesc() {
			return desc;
		}
	}

	public static void pauseAll() {
		paused = true;
		log("[Sistema] Pausado!");
	}

	public static void resumeAll() {
		paused = false;
		log("[Sistema] Retomado!");
	}
}