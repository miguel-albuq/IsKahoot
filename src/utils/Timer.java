package utils;

public class Timer {
    private int timeRemaining;
    private boolean timerActive;
    private TimerUpdateListener listener;
    private Thread timerThread;

    // Interface para notificar a GUI sobre o tempo restante
    public interface TimerUpdateListener {
        void onTimerUpdate(int secondsRemaining);
        void onTimerFinished();
    }

    // Construtor
    public Timer(int initialTime, TimerUpdateListener listener) {
        this.timeRemaining = initialTime;
        this.listener = listener;
        this.timerActive = false;
    }

    // Inicia o temporizador
    public synchronized void start() {
        if (timerActive) {
            System.out.println("O timer já está a correr!");
            return;
        }

        timerActive = true;

        // Cria e inicia a thread do temporizador
        timerThread = new Thread(() -> {
            while (timeRemaining > 0 && timerActive) {
                try {
                    Thread.sleep(1000);  // Espera 1 segundo
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                synchronized (this) {
                    if (!timerActive) break;  // Se o timer for parado, sai do loop
                    timeRemaining--;          // Decrementa o tempo

                    // Notifica a GUI sobre o tempo restante
                    if (listener != null) {
                        listener.onTimerUpdate(timeRemaining);
                    }

                    // Se o tempo terminar, notifica o fim do timer
                    if (timeRemaining <= 0 && listener != null) {
                        listener.onTimerFinished();
                    }
                }
            }
        });

        timerThread.start();
    }

    // Pausa o temporizador
    public synchronized void pause() {
        timerActive = false;
    }

    // Cancela o temporizador
    public synchronized void cancel() {
        timerActive = false;
        if (timerThread != null) {
            timerThread.interrupt();
        }
    }

    // Retorna o tempo restante
    public synchronized int getTimeRemaining() {
        return timeRemaining;
    }

    // Verifica se o timer está ativo
    public synchronized boolean isTimerActive() {
        return timerActive;
    }
}
