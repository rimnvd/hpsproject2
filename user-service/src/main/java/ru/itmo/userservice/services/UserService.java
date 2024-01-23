package ru.itmo.userservice.services;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itmo.userservice.exceptions.*;
import ru.itmo.userservice.model.dto.UserDto;
import ru.itmo.userservice.model.dto.UserRegisterRequestDto;
import ru.itmo.userservice.model.entity.UserEntity;
import ru.itmo.userservice.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private RoleService roleService;
    private PasswordEncoder passwordEncoder;
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<UserEntity> findById(Long id) {
        return userRepository.findById(id);
    }

    public UserDto getById(Long id) {
        UserEntity user = userRepository.getUserEntityById(id);
        return user != null ? userEntityToDto(user) : null;
    }

    public void save(UserEntity user) {
        userRepository.save(user);
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void updateBalance(Long userId, Integer newBalance) throws NotFoundException {
        UserEntity user = findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        user.setBalance(newBalance);
        userRepository.save(user);
    }

    public void replenishBalance(String username, Integer sum) throws NotFoundException {
        UserEntity user = findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Пользователь с именем " + username + " не найден"));
        user.setBalance(user.getBalance() + sum);
        userRepository.save(user);
    }

    public List<UserDto> getAll(Pageable pageable) {
        return userRepository.findAll(pageable).stream().map(this::userEntityToDto).toList();
    }

    public void buyPremiumAccount(String username) throws NotFoundException, NotEnoughMoneyException {
        UserEntity user = findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Пользователь с именем " + username + " не найден"));
        if (user.getBalance() < 1000) throw new NotEnoughMoneyException();
        user.setBalance(user.getBalance() - 1000);
        user.removeRole(roleService.getStandardUserRole());
        user.addRole(roleService.getPremiumUserRole());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        UserEntity user = findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(
                        String.format("Пользователь с именем '%s' не найден", username)));
        return new User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getRole().name())).collect(Collectors.toList())
        );
    }

    public UserDto register(@Valid UserRegisterRequestDto userRegisterRequest) throws UserAlreadyExistsException {
        if (existsByUsername(userRegisterRequest.getUsername())) {
            throw new UserAlreadyExistsException(String.format("Пользователь с именем '%s' уже существует", userRegisterRequest.getUsername()));
        }
        UserEntity user = UserEntity.builder()
                .username(userRegisterRequest.getUsername())
                .password(passwordEncoder.encode(userRegisterRequest.getPassword()))
                .email(userRegisterRequest.getEmail())
                .balance(0)
                .roles(List.of(roleService.getStandardUserRole()))
                .build();
        userRepository.save(user);
        return userEntityToDto(user);
    }

    private UserDto userEntityToDto(UserEntity user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .balance(user.getBalance())
                .description(user.getDescription())
                .roles(user.getRoles().stream().map(role -> role.getRole().name()).toList())
                .build();
    }

}
