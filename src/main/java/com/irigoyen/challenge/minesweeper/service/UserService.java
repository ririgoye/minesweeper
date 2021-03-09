package com.irigoyen.challenge.minesweeper.service;

import com.irigoyen.challenge.minesweeper.infrastructure.DynamicBody;
import com.irigoyen.challenge.minesweeper.infrastructure.Response;
import com.irigoyen.challenge.minesweeper.model.User;
import com.irigoyen.challenge.minesweeper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    /**
     * Signs in/up a user
     * @param userData user information to use/check
     * @return valid user information
     */
    public Response<User> upsert(DynamicBody userData) {
        //TODO: I'm using this as login/signup option.
        // This should be moved to some OAuth service
        Long userId = userData.getLong("id");
        String name = userData.getString("name");
        String password = userData.getString("password");
        User dbUser = null;
        if (userId != null) {
            dbUser = userRepository.findById(userId).orElse(null);
            if (dbUser == null)
                return new Response<>(HttpStatus.BAD_REQUEST, "User id doesn't exist. User can't be updated");
        } else {
            List<User> dbUsers = userRepository.findByName(name);
            if (dbUsers.size() > 0)
                dbUser = dbUsers.get(0);
        }
        User user;
        if (dbUser != null) {
            if(!dbUser.validatePassword(password))
                return new Response<>(HttpStatus.FORBIDDEN, "Invalid username or password");
            user = dbUser;
        }
        else {
            user=new User();
            user.setPassword(password);
            //We don't want to store passwords in clear text.
            user.encodePassword();
        }
        user.setName(name);
        user.setImage(userData.getString("image"));
        user = userRepository.save(user);
        return new Response<>(user);
    }
}
