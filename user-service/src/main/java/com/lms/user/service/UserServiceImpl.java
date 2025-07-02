package com.lms.user.service;

import com.lms.user.model.User;
import com.lms.user.model.UserStatus;
import com.lms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Mono<User> createUser(User user) {
        return Mono.fromCallable(() -> {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<User> updateUser(Long id, User user) {
        return Mono.fromCallable(() -> 
            userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setFirstName(user.getFirstName());
                    existingUser.setLastName(user.getLastName());
                    existingUser.setEmail(user.getEmail());
                    if (user.getPassword() != null) {
                        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                    }
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new RuntimeException("User not found"))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<User> getUserById(Long id) {
        return Mono.fromCallable(() -> 
            userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<User> getUserByUsername(String username) {
        return Mono.fromCallable(() -> 
            userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<User> getAllUsers() {
        return Flux.fromIterable(userRepository.findAll())
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<Void> deleteUser(Long id) {
        return Mono.fromRunnable(() -> 
            userRepository.deleteById(id)
        ).subscribeOn(Schedulers.boundedElastic())
        .then();
    }

    @Override
    @Transactional
    public Mono<User> updateUserStatus(Long id, String status) {
        return Mono.fromCallable(() -> 
            userRepository.findById(id)
                .map(user -> {
                    user.setStatus(UserStatus.valueOf(status.toUpperCase()));
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"))
        ).subscribeOn(Schedulers.boundedElastic());
    }
}