package server;

import concurrency.ModifiedCountdownLatch;
import concurrency.TeamBarrier;

import java.util.*;

import java.util.concurrent.*;
import model.*;

/**
 * Representa o estado de UM jogo ativo no servidor, com a gestão das equipas, jogadores, rondas e comunicação.
 */
public class GameState {

    private String gameCode;
    private  int expectedNumTeams;
    private  int expectedPlayersPerTeam;
    private  int numQuestions;
    private  int port;

    private Map<String, Team> teams = new HashMap<>();
    private Set<Integer> readyTeams = ConcurrentHashMap.newKeySet();
    private Set<String> allUsernames = ConcurrentHashMap.newKeySet();

    private Quiz quiz;
    private int currentQuestionIndex = 0;

    private Map<Player, Integer> roundAnswers = new HashMap<>();

    private ModifiedCountdownLatch latch;   // perguntas individuais
    private TeamBarrier barrier;            // perguntas de equipa

    // Na classe GameState, substituindo a variável roundTimer de tipo Timer por uma instância do seu Timer
    private utils.Timer roundTimer;
    private int timeRemaining;
    private boolean roundActive = false;
    private boolean gameFinished = false;
    private boolean timerFired = false;

    private List<DealWithClient> clients = Collections.synchronizedList(new ArrayList<>());

    // Listener para a GUI saber quando o tempo muda
    public interface TimerUpdateListener {
        void onTimerUpdate(int secondsRemaining);
        void onTimerFinished();
    }

    private TimerUpdateListener timerListener;

    public void setTimerListener(TimerUpdateListener listener) {
        this.timerListener = listener;
    }

    // Construtores
    public GameState(String gameCode, int expectedNumTeams, int expectedPlayersPerTeam, int numQuestions, int port) {
        this.gameCode = gameCode;
        this.expectedNumTeams = expectedNumTeams;
        this.expectedPlayersPerTeam = expectedPlayersPerTeam;
        this.numQuestions = numQuestions;
        this.port = port;
    }

    public GameState(Quiz quiz) {
        this.quiz = quiz;
    }

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


    // Gestão de Equipas e Jogadores
    public synchronized String tryJoin(String username, int teamNo) {
        if (teamNo < 1) return "Equipa inválida";
        if (!allUsernames.add(username)) return "Username já em uso";
        var team = teams.computeIfAbsent("Team " + teamNo, k -> new Team("Team " + teamNo));
        if (team.getMembers().size() >= expectedPlayersPerTeam) return "Equipa cheia";
        team.addPlayer(new Player(username, String.valueOf(teamNo))); // Adiciona jogador
        if (team.getMembers().size() == expectedPlayersPerTeam) readyTeams.add(teamNo);
        return null;
    }

    public int teamsReady() {
        return readyTeams.size();
    }

    public boolean shouldStart() {
        return readyTeams.size() >= expectedNumTeams;
    }

    public void addClient(DealWithClient h) {
        clients.add(h);
    }

    public void broadcastStart() {
        synchronized (clients) { clients.forEach(DealWithClient::sendStart); }
    }

    public void broadcastLobby() {
        synchronized (clients) { clients.forEach(DealWithClient::sendLobby); }
    }

    // Perguntas e Ciclo de Jogo
    public synchronized Question getCurrentQuestion() {
        if (currentQuestionIndex < quiz.getQuestions().size())
            return quiz.getQuestions().get(currentQuestionIndex);
        return null;
    }

    public synchronized boolean nextQuestion() {
        cancelRoundTimer();
        currentQuestionIndex++;
        roundAnswers.clear();
        timerFired = false;

        boolean hasMore = currentQuestionIndex < quiz.getQuestions().size();
        if (!hasMore) {
            gameFinished = true;
            roundActive = false;
        }
        return hasMore;
    }

    // Pontuação
    public synchronized void addScore(String teamName, int points) {
        Team t = teams.get(teamName);
        if (t != null) t.addPoints(points);
    }

    public synchronized Map<String, Integer> getTeamScores() {
        Map<String, Integer> scores = new HashMap<>();
        for (Team t : teams.values()) scores.put(t.getName(), t.getTotalScore());
        return scores;
    }

    // Temporizador
    public synchronized void startRoundTimer(int seconds) {
        cancelRoundTimer(); // Cancela o timer anterior, se existir

        this.timeRemaining = seconds;
        this.roundActive = true;
        this.timerFired = false;

        if (timerListener != null) {
            try {
                timerListener.onTimerUpdate(timeRemaining);
            } catch (Exception ignored) {}
        }

        // Inicializa o novo temporizador da classe utils.Timer
        roundTimer = new utils.Timer(timeRemaining, new utils.Timer.TimerUpdateListener() {
            @Override
            public void onTimerUpdate(int secondsRemaining) {
                synchronized (GameState.this) {
                    timeRemaining = secondsRemaining;
                    if (timerListener != null) {
                        try {
                            timerListener.onTimerUpdate(timeRemaining);
                        } catch (Exception ignored) {}
                    }
                }
            }

            @Override
            public void onTimerFinished() {
                synchronized (GameState.this) {
                    roundActive = false;
                    if (timerListener != null) {
                        try {
                            timerListener.onTimerFinished();
                        } catch (Exception ignored) {}
                    }
                    endRoundDueToTimeout(); // A lógica de término da rodada
                }
            }
        });

        // Inicia o temporizador
        roundTimer.start();
    }


    private synchronized void cancelRoundTimer() {
        if (roundTimer != null) {
            try {
                roundTimer.cancel();
            } catch (Exception ignored) {}
            roundTimer = null;
        }
    }

    public synchronized void endRoundDueToTimeout() {
        // Placeholder para a lógica de servidor / contagem de respostas
    }

    public synchronized void submitAnswer(Player p, int answerIndex) {
        roundAnswers.put(p, answerIndex);
    }

    public String getGameCode() {
        return gameCode;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    // Inside GameState class
    public int expectedTeams() {
        return expectedNumTeams;
    }

}
