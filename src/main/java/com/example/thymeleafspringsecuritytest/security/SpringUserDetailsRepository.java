package com.example.thymeleafspringsecuritytest.security;

import com.example.thymeleafspringsecuritytest.repository.UserRepository;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SpringUserDetailsRepository implements ReactiveUserDetailsService {

    private final UserRepository repository;
    private PasswordEncoder passwordEncoder;

    public SpringUserDetailsRepository(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<UserDetails> findByUsername(String usernanme) {
        return repository.findByUsername(usernanme).map(user -> new User(user.getUsername(), user.getPassword(),
                AuthorityUtils.createAuthorityList(user.getRoles())));
    }

    public Mono<com.example.thymeleafspringsecuritytest.model.User> getUserByUserName(String userName) {
        return repository.findByUsername(userName);
    }

    public Mono<Void> updatePassword(String username, String newPassword) {
        return getUserByUserName(username).flatMap(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            return Mono.when(repository.save(user)).log("Save to DB").then().log("updated password done");
        });
    }
}
