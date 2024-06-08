package func;

import entity.User;

public class UserFunc {
    public boolean checkUser(User user) {
        if (user != null) {
            if ("admin".equals(user.getUserName()) 
                    && "123".equals(user.getPassword())) {
                return true;
            }
        }
        return false;
    }
}
