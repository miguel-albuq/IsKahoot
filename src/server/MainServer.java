package server;
import java.net.*;
import java.util.*;
import shared.Codes;

public class MainServer {

        public static void main(String[] args) throws Exception {
            var sc = new Scanner(System.in);
            System.out.print("new <numEquipas> <jogadores/equipa> <numPerguntas>: ");
            int expectedTeams = sc.nextInt(), playersPerTeam = sc.nextInt(), numQ = sc.nextInt();

            String gameCode = UUID.randomUUID().toString().substring(0,8).toUpperCase(); // código único. :contentReference[oaicite:2]{index=2}
            int port = 5050;
            var room = new GameState(gameCode, expectedTeams, playersPerTeam,  numQ , port );

            try (ServerSocket ss = new ServerSocket(port)) {
                System.out.printf("GameState %s aberto no porto %d%n", gameCode, port);
                while (true) new Thread(new DealWithClient(ss.accept(), room)).start(); // DealWithClient. :contentReference[oaicite:3]{index=3}
            }
        }

    // no servidor (por ex. numa classe ServerState)
    private final Set<String> activeCodes = new HashSet<>();

    public String createUniqueGameCode() {
        String code;
        do { code = Codes.newGameCode(); } while (activeCodes.contains(code));
        activeCodes.add(code);
        return code;
    }



}
