package sample;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.util.concurrent.PriorityBlockingQueue;

import static java.lang.Math.abs;

public class Elevator extends Thread implements Comparable{
    private static final String RUN_UP = "上行";
    private static final String RUN_DOWN = "下行";
    private static final String STOP = "停止";
    private static final String OPEN = "开门";
    private static final String CLOSE = "关门";
    private static final long TIME_ON_EACH_FLOOR = 1000;
    private static final long WAITING_TIME = 2000;
    private static final int THREADSTOP = -1;
    private static final int THREADRUNNING = 1;
    private static final int THREADWAITING = 0;
    private static final int THREADRESUME = 2;
    private int status;
    // 电梯编号
    private int id;
    // 电梯当前楼层
    private int presentFloor;

    private int aimFloor;

    private boolean direction;

    private UserRequest runningRequest;

    private UserRequest lastRequest;

    private PriorityBlockingQueue<UserRequest> waitingRequest;

    private double waitingTime;

    private Label label;

    private Label state;

    private TextArea log;

    public boolean isRun;

    public Elevator(int id){
        this.id = id;
        this.presentFloor = 1;
        this.aimFloor = 1;
        this.direction = true;
        this.runningRequest = null;
        this.waitingRequest = new PriorityBlockingQueue<>(200, new UserRequestComparator());
        this.waitingTime = 0.0;
        this.status = THREADRUNNING;
        this.isRun = false;
        this.label = null;
        this.state = null;
        this.log = null;
    }

    public Elevator(int id, Label label, Label state, TextArea log){
        this.id = id;
        this.presentFloor = 1;
        this.aimFloor = 1;
        this.direction = true;
        this.runningRequest = null;
        this.waitingRequest = new PriorityBlockingQueue<UserRequest>(200, new UserRequestComparator());
        this.waitingTime = 0.0;
        this.status = THREADRUNNING;
        this.isRun = false;
        this.label = label;
        this.state = state;
        this.log = log;
    }

    // getter & setter


    public UserRequest getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(UserRequest lastRequest) {
        this.lastRequest = lastRequest;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
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

    public int getAimFloor() {
        return aimFloor;
    }

    public void setAimFloor(int aimFloor) {
        this.aimFloor = aimFloor;
    }

    public boolean getDirection() {
        return direction;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    public UserRequest getRunningRequest() {
        return runningRequest;
    }

    public void setRunningRequest(UserRequest runningRequest) {
        this.runningRequest = runningRequest;
    }

    public double getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(double waitingTime) {
        this.waitingTime = waitingTime;
    }

    public synchronized void runARequest() throws InterruptedException {

        if (waitingRequest.isEmpty()){
            runningRequest = null;
            isRun = false;
        }else {
            runningRequest = waitingRequest.poll();
        }

        if (runningRequest != null){

            isRun = true;
            aimFloor = runningRequest.getPresentFloor();
            direction = aimFloor > presentFloor;
            if (direction)
                Platform.runLater(()-> state.setText(RUN_UP));
            else
                Platform.runLater(()-> state.setText(RUN_DOWN));

            while (presentFloor != aimFloor){
                if (status != THREADWAITING) {
                    Thread.sleep(TIME_ON_EACH_FLOOR);
                    if (direction)
                        presentFloor++;
                    else
                        presentFloor--;
                    Platform.runLater(() -> {
                        if (label != null)
                            label.setText(String.valueOf(presentFloor));
                    });
                    waitingTime -= TIME_ON_EACH_FLOOR;
//            System.out.println("电梯号：" + this.id + ",当前楼层：" + presentFloor + ",等待时间：" + waitingTime);
                }
            }

            isRun = false;
            Platform.runLater(()->state.setText(OPEN));
            Thread.sleep(3000);
            Platform.runLater(()->state.setText(CLOSE));
            Thread.sleep(1000);

            isRun = true;
            waitingTime -= WAITING_TIME;
            aimFloor = runningRequest.getAimFloor();
            String logStr = "电梯日志——进电梯 : " + id + "号电梯有用户进入，需前往第" + aimFloor + "层\n";
            final String finalLogStr = logStr;
            Platform.runLater(()-> log.appendText(finalLogStr));
//        System.out.println("电梯号：" + this.id + ",当前楼层：" + presentFloor + ",用户进入：" + runningRequest.getId());
            direction = aimFloor > presentFloor;

            if (direction)
                Platform.runLater(()-> state.setText(RUN_UP));
            else
                Platform.runLater(()-> state.setText(RUN_DOWN));

            while (presentFloor != aimFloor){
                if (status != THREADWAITING) {
                    Thread.sleep(TIME_ON_EACH_FLOOR);
                    if (direction)
                        presentFloor++;
                    else
                        presentFloor--;
                    Platform.runLater(() -> {
                        if (label != null)
                            label.setText(String.valueOf(presentFloor));
                    });
                    System.out.println("电梯"+id+"当前楼层"+presentFloor);
                }
            }
            waitingTime -= runningRequest.getTime();
            logStr = "电梯日志——出电梯 : " + id + "号电梯有用户出电梯，停在第" + presentFloor + "层\n";
            final String finalLogStr1 = logStr;
            Platform.runLater(()-> log.appendText(finalLogStr1));
//        System.out.println("电梯号：" + this.id + ",当前楼层：" + presentFloor + ",用户出电梯：" + runningRequest.getId());

            Platform.runLater(()-> state.setText(OPEN));
            Thread.sleep(3000);
            Platform.runLater(()-> state.setText(CLOSE));
            Thread.sleep(1000);
            Platform.runLater(()-> state.setText(STOP));
        }
    }

    @Override
    public synchronized void run(){
        while (true){
            if (status == THREADWAITING){
//                try {
//                    Thread.sleep(1000);
//                    System.out.println("电梯" + id + "停止运行");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
            else{
//                if (runningRequest == null && waitingRequest.isEmpty()){
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
////                System.out.println("电梯号： " + this.id + " 当前处于停滞状态，当前楼层： " + this.presentFloor);
//                }
//                if (runningRequest == null && !waitingRequest.isEmpty()){
//                this.resume();
//                    runningRequest = waitingRequest.poll();
                    try {
                        runARequest();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                }
//                while (runningRequest != null){
//                    try {
//                        runARequest();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        }
    }

    public void addWaitingRequest(UserRequest userRequest){
        waitingRequest.put(userRequest);
        if (lastRequest == null)
            waitingTime += (userRequest.getTime() + abs(presentFloor-userRequest.getPresentFloor())*TIME_ON_EACH_FLOOR + WAITING_TIME);
        else
            waitingTime += (userRequest.getTime() + abs(lastRequest.getAimFloor() - userRequest.getPresentFloor())*TIME_ON_EACH_FLOOR + WAITING_TIME);
        lastRequest = userRequest;
        direction = userRequest.getAimFloor() >= presentFloor;
        isRun = true;
//        System.out.println("电梯号：" + this.id + "，添加了用户：" + userRequest.getId());
    }

    @Override
    public int compareTo(Object o) {
        Elevator elevator = (Elevator) o;
        if (this.waitingTime >= elevator.waitingTime){
            return 1;
        }
        else {
            return -1;
        }
    }
}
