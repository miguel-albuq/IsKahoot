package server;
import shared.Messages.*;

import java.net.*; import java.io.*;

public class DealWithClient implements Runnable {

// server/DealWithClient.java

        private final Socket s; private final GameState room;
        private ObjectInputStream in; private ObjectOutputStream out;

        public DealWithClient(Socket s, GameState r){ this.s=s; this.room=r; }

        @Override public void run(){
            try (s) {
                out = new ObjectOutputStream(s.getOutputStream());
                in  = new ObjectInputStream(s.getInputStream());
                room.addClient(this);

                Envelope env = (Envelope) in.readObject();
                if (env.type()!=MsgType.JOIN_REQ) return;
                var jr = (JoinReq) env.body();

                String err = room.tryJoin(jr.username(), jr.teamNo());
                if (err != null) { send(Envelope.of(MsgType.JOIN_ERR, new JoinErr(err))); return; }

                send(Envelope.of(MsgType.JOIN_OK, new JoinOk(room.getGameCode())));
                room.broadcastLobby();

                if (room.shouldStart()) room.broadcastStart(); // começa quando 4 equipas estiverem prontas

                // manter viva a ligação (listener do jogo) — nesta fase basta estacionar
                while (true) Thread.sleep(1000);
            } catch (Exception ignored) {}
        }

        private void send(Envelope e){ try { out.writeObject(e); out.flush(); } catch(IOException ignored){} }
        public void sendLobby(){ send(Envelope.of(MsgType.LOBBY, new Lobby(room.teamsReady(), room.expectedTeams()))); }
        public void sendStart(){ send(Envelope.of(MsgType.START, new Start(room.getGameCode()))); }



}
