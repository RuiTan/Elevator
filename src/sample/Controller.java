package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class Controller {

    static boolean RUNNED = false;

    static int RequestID = 1;

    static int ElevatorID = 1;

    static final int ELEVATORS = 5;

    static AddRequest addRequest;

    static ArrayList<Elevator> elevators;

    @FXML
    private TextField presentFloor;

    @FXML
    private TextField aimFloor;

    @FXML
    private Label elevator1;
    @FXML
    private Label elevator2;
    @FXML
    private Label elevator3;
    @FXML
    private Label elevator4;
    @FXML
    private Label elevator5;
    ArrayList<Label> labels;

    @FXML
    private Label state1;
    @FXML
    private Label state2;
    @FXML
    private Label state3;
    @FXML
    private Label state4;
    @FXML
    private Label state5;
    ArrayList<Label> states;

    @FXML
    private TextArea log;

    public static boolean SystemRunning = false;

    @FXML
    protected void onClicked() {
        if (SystemRunning)
            addRequest.userRequests.put(new UserRequest(RequestID++, Integer.parseInt(presentFloor.getText()), Integer.parseInt(aimFloor.getText())));
    }

    @FXML
    protected synchronized void run(){
        if (!SystemRunning){
            RUNNED = true;
            log.appendText("电梯恢复运行！\n");
            SystemRunning = true;

            if (labels == null) {
                labels = new ArrayList<>();
                labels.add(elevator1);
                labels.add(elevator2);
                labels.add(elevator3);
                labels.add(elevator4);
                labels.add(elevator5);
            }

            if (states == null) {
                states = new ArrayList<>();
                states.add(state1);
                states.add(state2);
                states.add(state3);
                states.add(state4);
                states.add(state5);
            }

            if (elevators != null){
                for (Elevator e : elevators){
                    log.appendText("提示：电梯" + e.getId() + "状态" + e.getStatus() + "\n");
                    if (RUNNED) {
                        e.setStatus(2);
                    }
                    else {
                        e.setStatus(1);
                    }
                }
            }
            else {
                elevators = new ArrayList<>();
                for (int i = 0; i < ELEVATORS; i++){
                    Elevator elevator = new Elevator(ElevatorID++, labels.get(i), states.get(i), log);
                    elevators.add(elevator);
                    elevator.start();
                }
            }


            if (addRequest != null){
                if (RUNNED)
                    addRequest.setStatus(2);
                else
                    addRequest.setStatus(1);
            }else {
                addRequest = new AddRequest(elevators);
                addRequest.start();
            }

        }
    }

    @FXML
    protected synchronized void stop() throws InterruptedException {
        if (SystemRunning){
            SystemRunning = false;

            log.appendText("所有电梯运行状态已被中断！\n");

            addRequest.setStatus(0);
            for (Elevator e : elevators){
                e.setStatus(0);
                log.appendText("提示：电梯" + e.getId() + "状态" + e.getStatus() + "\n");
            }
        }


    }
}
