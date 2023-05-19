package pro.sky.telegrambot.Service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class  UserService {
    private  final Map<Long, User> userMap = new HashMap<>();

    public void  addLogin(Long userId, String login) {
        User user = new User();
        user.login = login;
        userMap.put(userId, user);
    }

    public void  addPassword(Long userId, String password) {
        User user = userMap.get(userId);
        user.password = password;
    }

    public User  getUser(Long id) {
        return userMap.get(id);
    }

}
