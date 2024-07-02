package com.triphippie.userService.service;

import com.triphippie.userService.model.*;
import com.triphippie.userService.repository.UserRepository;
import com.triphippie.userService.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public UserServiceResult updateUser(Integer principal, Integer id, UserInDto userInDto) {
        if(!principal.equals(id)) return UserServiceResult.FORBIDDEN;

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

    public UserServiceResult deleteUserById(Integer principal, Integer id) {
        if(!principal.equals(id)) return UserServiceResult.FORBIDDEN;

        userRepository.deleteById(id);
        return UserServiceResult.SUCCESS;
    }

    public UserServiceResult saveProfileImage(Integer principal, Integer id, MultipartFile image) throws IOException {
        if(!principal.equals(id)) return UserServiceResult.FORBIDDEN;
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

    public UserServiceResult deleteProfileImage(Integer principal, Integer id) throws IOException {
        if(!principal.equals(id)) return UserServiceResult.FORBIDDEN;

        Optional<String> filePath = userRepository.findUserProfileImageUrlById(id);

        if(filePath.isEmpty()) return UserServiceResult.SUCCESS;
        Path imagePath = Path.of("src/main/resources/static/images/profileImages/" + filePath.get());

        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
        }

        userRepository.deleteUserProfileImageUrl(id);
        return UserServiceResult.SUCCESS;
    }

    /* SECURITY METHODS */
    public String login(AuthDto authDto) {
        Optional<User> user = userRepository.findByUsername(authDto.username());
        if(user.isEmpty()) throw new UsernameNotFoundException("User not found");

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDto.username(), authDto.password())
        );
        //SecurityContextHolder.getContext().setAuthentication(auth);

        return "Bearer " + jwtService.generateToken(auth, Map.of("user-id", user.get().getId()));
    }

    public Optional<ValidateUserDto> validateToken(String token) {
        String username = jwtService.validateToken(token);
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent()
                ? Optional.of(new ValidateUserDto(user.get().getId(), user.get().getRole().name()))
                : Optional.empty();
    }
}
