package model;

import concurrency.ModifiedCountdownLatch;
import concurrency.TeamBarrier;

import java.util.*;

/**
 * Representa o estado de UM jogo ativo no servidor.
 * Guarda equipas, jogadores, perguntas, pontuações e controlo de rondas.
 */
public class GameState {

    // Identificação do jogo (para fases futuras — servidor)
    private String gameCode;

    // Configuração do jogo (servidor valida nº de equipas e jogadores)
    private int expectedNumTeams;
    private int expectedPlayersPerTeam;

    // Equipas e pontuação (Fase 2)
    private Map<String, Team> teams = new HashMap<>();

    // Perguntas e progresso do jogo
    private Quiz quiz;
    private int currentQuestionIndex = 0;

    // Respostas da ronda (para fases futuras — concorrência)
    private Map<Player, Integer> roundAnswers = new HashMap<>();

    // Estruturas para perguntas individuais e de equipa (placeholders)
    private ModifiedCountdownLatch latch;   // perguntas individuais
    private TeamBarrier barrier;            // perguntas de equipa

    // Temporizador da ronda (VISÍVEL NA GUI)
    private Timer roundTimer;
    private int timeRemaining;
    private boolean roundActive = false;
    private boolean gameFinished = false;

    // garante que onTimerFinished é chamado apenas uma vez por ronda
    private boolean timerFired = false;

    // Listener para a GUI saber quando o tempo muda
    public interface TimerUpdateListener {
        void onTimerUpdate(int secondsRemaining);
        void onTimerFinished();
    }

    private TimerUpdateListener timerListener;

    public void setTimerListener(TimerUpdateListener listener) {
        this.timerListener = listener;
    }

    // CONSTRUTORES
    public GameState(Quiz quiz) {
        this.quiz = quiz;
        this.expectedNumTeams = 1;
        this.expectedPlayersPerTeam = 1;
    }

    public GameState(Quiz quiz, String gameCode, int expectedNumTeams, int expectedPlayersPerTeam) {
        this.quiz = quiz;
        this.gameCode = gameCode;
        this.expectedNumTeams = expectedNumTeams;
        this.expectedPlayersPerTeam = expectedPlayersPerTeam;
    }

    // GESTÃO DE EQUIPAS E JOGADORES (fase 2)
    public synchronized boolean addTeam(Team team) {
        teams.put(team.getName(), team);
        return true;
    }

    public synchronized Team getTeam(String name) {
        return teams.get(name);
    }

    public synchronized Collection<Team> getAllTeams() {
        return teams.values();
    }

    public synchronized boolean allPlayersConnected() {
        return teams.values().stream().allMatch(
                t -> t.getMembers().size() == expectedPlayersPerTeam
        );
    }

    // PERGUNTAS E CICLO DE JOGO (fase 2)
    public synchronized Question getCurrentQuestion() {
        if (currentQuestionIndex < quiz.getQuestions().size())
            return quiz.getQuestions().get(currentQuestionIndex);
        return null;
    }

    /**
     * Avança para a próxima pergunta.
     * Cancela qualquer timer em curso para evitar timers órfãos.
     * Limpa respostas da ronda anterior.
     *
     * @return true se existirem mais perguntas; false se chegámos ao fim.
     */
    public synchronized boolean nextQuestion() {
        // cancelar timer activo (se houver)
        cancelRoundTimer();

        currentQuestionIndex++;
        roundAnswers.clear();

        // reset flag de timer para a próxima ronda
        timerFired = false;

        boolean hasMore = currentQuestionIndex < quiz.getQuestions().size();
        if (!hasMore) {
            gameFinished = true;
            roundActive = false;
        }
        return hasMore;
    }

    // PONTUAÇÃO (fase 2)
    public synchronized void addScore(String teamName, int points) {
        Team t = teams.get(teamName);
        if (t != null) t.addPoints(points);
    }

    public synchronized Map<String, Integer> getTeamScores() {
        Map<String, Integer> scores = new HashMap<>();
        for (Team t : teams.values()) scores.put(t.getName(), t.getTotalScore());
        return scores;
    }

    // TEMPORIZADOR (VISÍVEL NA GUI) — já funcional e seguro contra repetição
    /**
     * Inicia um temporizador para a ronda atual. Cancela o timer anterior caso exista.
     *
     * @param seconds segundos iniciais do contador (ex.: 30)
     */
    public synchronized void startRoundTimer(int seconds) {
        // cancelar se anteriormente havia um timer
        cancelRoundTimer();

        this.timeRemaining = seconds;
        this.roundActive = true;
        this.timerFired = false;

        // notifica GUI do valor inicial imediatamente
        if (timerListener != null) {
            try {
                timerListener.onTimerUpdate(timeRemaining);
            } catch (Exception ignored) {}
        }

        roundTimer = new Timer("GameState-RoundTimer-" + System.identityHashCode(this));

        // Agendamos a tarefa com atraso de 1s para evitar decremento imediato
        roundTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                synchronized (GameState.this) {
                    if (timerFired) {
                        // já notificámos fim desta ronda — garantir que a task não faz nada
                        return;
                    }

                    timeRemaining--;

                    // notificar atualização
                    if (timerListener != null) {
                        try {
                            timerListener.onTimerUpdate(timeRemaining);
                        } catch (Exception ignored) {}
                    }

                    if (timeRemaining <= 0 && !timerFired) {
                        timerFired = true;
                        roundActive = false;
                        // cancelamos o timer e notificamos apenas UMA vez
                        cancelRoundTimer();

                        if (timerListener != null) {
                            try {
                                timerListener.onTimerFinished();
                            } catch (Exception ignored) {}
                        }

                        // método para lidar com timeout (placeholder)
                        endRoundDueToTimeout();
                    }
                }
            }
        }, 1000L, 1000L);
    }

    /**
     * Cancela (se existir) o timer da ronda atual.
     */
    private synchronized void cancelRoundTimer() {
        if (roundTimer != null) {
            try {
                roundTimer.cancel();
            } catch (Exception ignored) {}
            roundTimer = null;
        }
    }

    /**
     * Chamado quando o tempo de uma ronda termina. Por agora é placeholder:
     * a GUI é notificada pelo listener e toma as acções visuais / navegação.
     * Na fase servidor este método fará processamento de respostas/fim de ronda.
     */
    public synchronized void endRoundDueToTimeout() {
        // placeholder — implementar lógica de servidor / contagem de respostas daqui a fases futuras
    }

    /**
     * Envia uma resposta (placeholder — será sincronizado com estruturas de concorrência depois).
     */
    public synchronized void submitAnswer(Player p, int answerIndex) {
        roundAnswers.put(p, answerIndex);
    }

    public String getGameCode() { return gameCode; }

    public boolean isGameFinished() { return gameFinished; }
}
