package sample;

import java.util.Comparator;

public class UserRequestComparator implements Comparator<UserRequest> {
    @Override
    public int compare(UserRequest o1, UserRequest o2) {
        return (int) (o1.getTime() - o2.getTime());
    }
}
