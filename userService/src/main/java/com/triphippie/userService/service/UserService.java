package com.triphippie.userService.service;

import com.triphippie.userService.model.Role;
import com.triphippie.userService.model.User;
import com.triphippie.userService.model.UserInDto;
import com.triphippie.userService.model.UserOutDto;
import com.triphippie.userService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private boolean validateUserInDto(UserInDto userInDto) {
        if(
                userInDto.getUsername() == null ||
                userInDto.getPassword() == null ||
                userInDto.getFirstName() == null ||
                userInDto.getLastName() == null
        ) return false;
        return true;
    }

    private boolean isUsernamePresent(String username) {
        return userRepository.findByUsername(username).size() > 0;
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

    public ServiceResult createUser(UserInDto userInDto) {
        if(!validateUserInDto(userInDto)) return ServiceResult.BAD_REQUEST;
        if(isUsernamePresent(userInDto.getUsername())) return ServiceResult.CONFLICT;

        User newUser = new User();
        newUser.setUsername(userInDto.getUsername());
        newUser.setPassword(userInDto.getPassword());
        newUser.setFirstName(userInDto.getFirstName());
        newUser.setLastName(userInDto.getLastName());
        newUser.setRole(Role.USER);
        newUser.setDateOfBirth(userInDto.getDateOfBirth());
        newUser.setEmail(userInDto.getEmail());
        newUser.setAbout(userInDto.getAbout());
        newUser.setCity(userInDto.getCity());

        userRepository.save(newUser);
        return ServiceResult.SUCCESS;
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
    public ServiceResult updateUser(Integer id, UserInDto userInDto) {
        Optional<User> oldUser = userRepository.findById(id);
        if(oldUser.isEmpty()) return ServiceResult.NOT_FOUND;

        User user = oldUser.get();
        if(isUsernamePresent(userInDto.getUsername()) && !userInDto.getUsername().equals(user.getUsername()))
            return ServiceResult.CONFLICT;

        user.setUsername(userInDto.getUsername());
        if(userInDto.getPassword() != null) user.setPassword(userInDto.getPassword());
        user.setFirstName(userInDto.getFirstName());
        user.setLastName(userInDto.getLastName());
        user.setDateOfBirth(userInDto.getDateOfBirth());
        user.setEmail(userInDto.getEmail());
        user.setAbout(userInDto.getAbout());
        user.setCity(userInDto.getCity());

        userRepository.save(user);
        return ServiceResult.SUCCESS;
    }

    public void deleteUserById(Integer id) {
        userRepository.deleteById(id);
    }

    public void saveProfileImage(Integer id, MultipartFile image) throws IOException {
        String filename = image.getOriginalFilename();
        String newFilename = id + "." + filename.substring(filename.lastIndexOf(".") + 1);
        String uploadPath = "src/main/resources/static/images/profileImages/" + newFilename;
        Files.copy(image.getInputStream(), Path.of(uploadPath), StandardCopyOption.REPLACE_EXISTING);
        userRepository.saveUserProfileImageUrl(id, newFilename);
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
}
