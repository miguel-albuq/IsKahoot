package shared;
import java.io.Serializable;
public final class Messages {
    private Messages() {}
    public enum MsgType implements Serializable { JOIN_REQ, JOIN_OK, JOIN_ERR, LOBBY, START }

    public record Envelope(MsgType type, Serializable body) implements Serializable {
        public static Envelope of(MsgType t, Serializable b){ return new Envelope(t,b); }
    }
    // payloads:
    public record JoinReq(String username, int teamNo) implements Serializable {}
    public record JoinOk(String gameCode) implements Serializable {}
    public record JoinErr(String reason) implements Serializable {}
    public record Lobby(int teamsReady, int expectedTeams) implements Serializable {}
    public record Start(String gameCode) implements Serializable {}
}



