package sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

import static java.lang.Math.abs;
import static java.lang.Math.min;

public class AddRequest extends Thread {
    private static final long TIME_ON_EACH_FLOOR = 1000;
    private static final long WAITING_TIME = 2000;
    private static final int THREADSTOP = -1;
    private static final int THREADRUNNING = 1;
    private static final int THREADWAITING = 0;
    private static final int THREADRESUME = 2;
    private static final int ELEVATORS = 5;

    PriorityBlockingQueue<UserRequest> userRequests;

    ArrayList<Elevator> elevators;

    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    AddRequest(ArrayList<Elevator> elevators){
        this.userRequests = new PriorityBlockingQueue<>(200, new UserRequestComparator());
        this.elevators = elevators;
        this.status = THREADRUNNING;
    }

    @Override
    public synchronized void run() {
        while (true){
            if (status != THREADWAITING){
                UserRequest userRequest = userRequests.poll();
                if (userRequest != null){
                    int choose = ChooseElevator(userRequest);
                    elevators.get(choose).addWaitingRequest(userRequest);
                    System.out.println("用户" + userRequest.getId() + "进入电梯" + elevators.get(choose).getId() + "的排队序列");
                }
            }
        }
    }

    public int ChooseElevator(UserRequest userRequest){
        ArrayList<Double> chooses = new ArrayList<>();
        for (Elevator e : elevators){
            chooses.add(CalcWaitingTime(e, userRequest));
        }
        double minTime = chooses.get(0);
        int index = 0;
        for (int i = 1; i < chooses.size(); i++){
            if (chooses.get(i) < minTime){
                minTime = chooses.get(i);
                index = i;
            }
        }
        return index;
    }

    private double CalcWaitingTime(Elevator e, UserRequest userRequest){
        if (!e.isRun){
            return abs(e.getPresentFloor() - userRequest.getPresentFloor())*TIME_ON_EACH_FLOOR;
        }
        else {
            if ((e.getDirection() && e.getPresentFloor() >= userRequest.getPresentFloor()) || (!e.getDirection() && e.getPresentFloor() < userRequest.getPresentFloor())){
                return abs(e.getAimFloor() - e.getPresentFloor())*2*TIME_ON_EACH_FLOOR + WAITING_TIME + abs(e.getPresentFloor() - userRequest.getPresentFloor())*TIME_ON_EACH_FLOOR;
            }
            else {
                return abs(e.getPresentFloor() - userRequest.getPresentFloor())*TIME_ON_EACH_FLOOR;
            }
        }
    }
}
