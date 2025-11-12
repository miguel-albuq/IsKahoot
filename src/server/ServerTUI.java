package server;
import java.util.Scanner;
import shared.Codes;

public class ServerTUI {

    // server/ServerTUI.java

        public GameRoom createGameInteractively() {
            Scanner sc = new Scanner(System.in);
            System.out.print("new <numEquipas> <jogadoresPorEquipa> <numPerguntas>: ");
            int teams = sc.nextInt(), ppl = sc.nextInt(), q = sc.nextInt();
            String code = Codes.newGameCode();
            int port = 5050; // ou leres de args; servidor lança sem args e só aceita clientes após "new" (enunciado). :contentReference[oaicite:1]{index=1}
            return new GameRoom(code, teams, ppl, q, port);
        }



}
