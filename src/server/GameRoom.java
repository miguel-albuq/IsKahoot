package server;
import java.util.*; import java.util.concurrent.*;


public class GameRoom {

        private final String gameCode;
        private final int expectedTeams, playersPerTeam;
        private final int numQeustions;
        private final int port;
        private final Map<Integer, Set<String>> teamUsers = new ConcurrentHashMap<>();
        private final Set<String> allUsernames = ConcurrentHashMap.newKeySet();
        private final Set<Integer> readyTeams = ConcurrentHashMap.newKeySet();
        private final List<PlayerHandler> clients = Collections.synchronizedList(new ArrayList<>());

        public GameRoom(String code, int expectedTeams, int playersPerTeam, int numQeustions, int port){
            this.gameCode=code; this.expectedTeams=expectedTeams; this.playersPerTeam=playersPerTeam; this.numQeustions=numQeustions; this.port=port;
            for (int t=1;t<=expectedTeams;t++) teamUsers.put(t, ConcurrentHashMap.newKeySet());
        }

        public synchronized String tryJoin(String username, int teamNo){
            if (teamNo < 1) return "Equipa inválida";
            if (!allUsernames.add(username)) return "Username já em uso"; // unicidade exigida. :contentReference[oaicite:4]{index=4}
            var team = teamUsers.get(teamNo);
            if (team.size() >= playersPerTeam) return "Equipa cheia";
            team.add(username);
            if (team.size()==playersPerTeam) readyTeams.add(teamNo);
            return null;
        }

        public int teamsReady(){ return readyTeams.size(); }
        public int expectedTeams(){ return expectedTeams; }
        public String gameCode(){ return gameCode; }

        public void addClient(PlayerHandler h){ clients.add(h); }
        public synchronized boolean shouldStart(){ return readyTeams.size() >= expectedTeams; } // “quando 4 equipas…”
        public void broadcastStart(){
            synchronized (clients){ clients.forEach(PlayerHandler::sendStart); }
        }
        public void broadcastLobby(){
            synchronized (clients){ clients.forEach(PlayerHandler::sendLobby); }
        }




}
