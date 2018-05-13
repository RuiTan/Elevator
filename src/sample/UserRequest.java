package sample;

import static java.lang.Math.abs;

public class UserRequest {
    private static final long TIME_ON_EACH_FLOOR = 1000;
    private static final long WAITING_TIME = 2000;
    private static int UserID = 1;
    // 请求ID
    private int id;
    // 当前层数
    private int presentFloor;
    // 方向，向下或向上
    private boolean direction;

    private int aimFloor;

    private long Time;

    public UserRequest(int id, int presentFloor, int aimFloor) {
        this.id = id;
        this.presentFloor = presentFloor;
        this.aimFloor = aimFloor;
        this.direction = presentFloor < aimFloor;
        this.Time = WAITING_TIME + TIME_ON_EACH_FLOOR*(abs(presentFloor-aimFloor));
    }

    public static int getUserID() {
        return UserID;
    }

    public static void setUserID(int userID) {
        UserID = userID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPresentFloor() {
        return presentFloor;
    }

    public void setPresentFloor(int presentFloor) {
        this.presentFloor = presentFloor;
    }

    public boolean getDirection() {
        return direction;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    public int getAimFloor() {
        return aimFloor;
    }

    public void setAimFloor(int aimFloor) {
        this.aimFloor = aimFloor;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }
}
