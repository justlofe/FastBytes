package lofe.fastbytes.socket;

public enum CloseCode {

    CLOSE_NORMAL(1000),
    CLOSE_GOING_AWAY(1001),
    CLOSE_PROTOCOL_ERROR(1002),
    REFUSE(1003);

    public final int code;

    CloseCode(int code) {
        this.code = code;
    }

}
