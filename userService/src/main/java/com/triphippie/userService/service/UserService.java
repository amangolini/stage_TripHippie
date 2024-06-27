package com.triphippie.userService.service;

import com.triphippie.userService.model.Role;
import com.triphippie.userService.model.User;
import com.triphippie.userService.model.UserInDto;
import com.triphippie.userService.model.UserOutDto;
import com.triphippie.userService.repository.UserRepository;
import com.triphippie.userService.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    private boolean isUsernamePresent(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private static UserOutDto mapToUserOut(User user) {
        return new UserOutDto(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getDateOfBirth(),
                user.getEmail(),
                user.getAbout(),
                user.getCity()
        );
    }

    public UserServiceResult createUser(UserInDto userInDto) {
        if(isUsernamePresent(userInDto.getUsername())) return UserServiceResult.CONFLICT;

        User newUser = new User();
        newUser.setUsername(userInDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(userInDto.getPassword()));
        newUser.setFirstName(userInDto.getFirstName());
        newUser.setLastName(userInDto.getLastName());
        newUser.setRole(Role.USER);
        newUser.setDateOfBirth(userInDto.getDateOfBirth());
        newUser.setEmail(userInDto.getEmail());
        newUser.setAbout(userInDto.getAbout());
        newUser.setCity(userInDto.getCity());

        userRepository.save(newUser);
        return UserServiceResult.SUCCESS;
    }

    public List<UserOutDto> findAllUsers(Integer size, Integer page) {
        List<User> users = userRepository.findUsersWithOffset(size * page, size);
        List<UserOutDto> outUsers = new ArrayList<>();
        for (User u : users) {
            outUsers.add(mapToUserOut(u));
        }
        return outUsers;
    }

    public List<UserOutDto> findAllUsersByUsername(Integer size, Integer page, String username) {
        List<User> users = userRepository.findUsersByUsernameWithOffset(size * page, size, username);
        List<UserOutDto> outUsers = new ArrayList<>();
        for (User u : users) {
            outUsers.add(mapToUserOut(u));
        }
        return outUsers;
    }

    public Optional<UserOutDto> findUserById(Integer id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(UserService::mapToUserOut);
    }

    /*
    * MIGLIORARE EVENTUALMENTE GESTIONE PASSWORD
    * */
    public UserServiceResult updateUser(Integer id, UserInDto userInDto) {
        Optional<User> oldUser = userRepository.findById(id);
        if(oldUser.isEmpty()) return UserServiceResult.NOT_FOUND;

        User user = oldUser.get();
        if(isUsernamePresent(userInDto.getUsername()) && !userInDto.getUsername().equals(user.getUsername()))
            return UserServiceResult.CONFLICT;

        user.setUsername(userInDto.getUsername());
        user.setPassword(passwordEncoder.encode(userInDto.getPassword()));
        user.setFirstName(userInDto.getFirstName());
        user.setLastName(userInDto.getLastName());
        user.setDateOfBirth(userInDto.getDateOfBirth());
        user.setEmail(userInDto.getEmail());
        user.setAbout(userInDto.getAbout());
        user.setCity(userInDto.getCity());

        userRepository.save(user);
        return UserServiceResult.SUCCESS;
    }

    public void deleteUserById(Integer id) {
        userRepository.deleteById(id);
    }

    public UserServiceResult saveProfileImage(Integer id, MultipartFile image) throws IOException {
        if(findUserById(id).isEmpty()) return UserServiceResult.NOT_FOUND;
        String filename = image.getOriginalFilename();
        String newFilename = id + "." + filename.substring(filename.lastIndexOf(".") + 1);
        String uploadPath = "src/main/resources/static/images/profileImages/" + newFilename;
        Files.copy(image.getInputStream(), Path.of(uploadPath), StandardCopyOption.REPLACE_EXISTING);
        userRepository.saveUserProfileImageUrl(id, newFilename);
        return UserServiceResult.SUCCESS;
    }

    public Optional<byte[]> findProfileImage(Integer id) throws IOException {
        Optional<String> filePath = userRepository.findUserProfileImageUrlById(id);

        if(filePath.isEmpty()) return Optional.empty();
        Path imagePath = Path.of("src/main/resources/static/images/profileImages/" + filePath.get());

        if (Files.exists(imagePath)) {
            byte[] imageBytes = Files.readAllBytes(imagePath);
            return Optional.of(imageBytes);
        } else {
            return Optional.empty();
        }
    }

    public void deleteProfileImage(Integer id) throws IOException {
        Optional<String> filePath = userRepository.findUserProfileImageUrlById(id);

        if(filePath.isEmpty()) return;
        Path imagePath = Path.of("src/main/resources/static/images/profileImages/" + filePath.get());

        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
        }

        userRepository.deleteUserProfileImageUrl(id);
    }

    /* SECURITY METHODS */
    public String login(String username, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        return "Bearer " + jwtService.generateToken(auth);
    }

    public Optional<Integer> validateToken(String token) {
        String username = jwtService.validateToken(token);
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent() ? Optional.of(user.get().getId()) : Optional.empty();
    }
}
