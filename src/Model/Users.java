package Model;

public final class Users {
    public static UserMap globalUserList = new UserMap();

    private Users(){}

    public static User getUser(String id) {
        return globalUserList.get(id);
    }

    public static void addUser(User user) {
        globalUserList.put(user.getID(), user);
    }

    public static int getNumberOfUsers() {
        return globalUserList.size();
    }

}
