package com.rupiksha.aeps.service;

import com.rupiksha.aeps.dto.SignupRequest;
import com.rupiksha.aeps.entity.User;
import com.rupiksha.aeps.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public User signup(SignupRequest request){

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setDob(request.getDob());

        return userRepository.save(user);

    }

    public boolean userExists(String mobile){

        return userRepository.findByMobile(mobile).isPresent();

    }
}