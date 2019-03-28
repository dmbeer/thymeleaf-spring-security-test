package com.example.thymeleafspringsecuritytest.service;

import com.example.thymeleafspringsecuritytest.model.User;
import com.example.thymeleafspringsecuritytest.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserDetailsService {

    private UserRepository userRepository;

    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<Long> count() {
        return userRepository.count();
    }

    public Mono<User> findByUserName(String username) {
        return userRepository.findByUsername(username);
    }


}
