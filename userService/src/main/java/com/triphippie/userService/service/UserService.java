package com.triphippie.userService.service;

import com.triphippie.userService.model.user.*;
import com.triphippie.userService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

    public void createUser(UserInDto userInDto) throws UserServiceException {
        if(isUsernamePresent(userInDto.getUsername())) throw new UserServiceException(UserServiceError.CONFLICT);

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

    public void replaceUser(Integer principalId, Integer id, UserInDto userInDto) throws UserServiceException {
        if(!principalId.equals(id)) throw new UserServiceException(UserServiceError.FORBIDDEN);

        Optional<User> oldUser = userRepository.findById(id);
        if(oldUser.isEmpty()) throw new UserServiceException(UserServiceError.NOT_FOUND);

        User user = oldUser.get();
        if(isUsernamePresent(userInDto.getUsername()) && !userInDto.getUsername().equals(user.getUsername()))
            throw new UserServiceException(UserServiceError.CONFLICT);

        user.setUsername(userInDto.getUsername());
        user.setPassword(passwordEncoder.encode(userInDto.getPassword()));
        user.setFirstName(userInDto.getFirstName());
        user.setLastName(userInDto.getLastName());
        user.setDateOfBirth(userInDto.getDateOfBirth());
        user.setEmail(userInDto.getEmail());
        user.setAbout(userInDto.getAbout());
        user.setCity(userInDto.getCity());

        userRepository.save(user);
    }

    public void updateUser(Integer principalId, Integer id, UserPatchDto patchDto) throws UserServiceException {
        if(!principalId.equals(id)) throw new UserServiceException(UserServiceError.FORBIDDEN);

        Optional<User> oldUser = userRepository.findById(id);
        if(oldUser.isEmpty()) throw new UserServiceException(UserServiceError.NOT_FOUND);

        User user = oldUser.get();

        if(patchDto.getUsername() != null) {
            if(isUsernamePresent(patchDto.getUsername()) && !patchDto.getUsername().equals(user.getUsername()))
                throw new UserServiceException(UserServiceError.CONFLICT);
            user.setUsername(patchDto.getUsername());
        }
        if(patchDto.getPassword() != null) user.setPassword(passwordEncoder.encode(patchDto.getPassword()));
        if(patchDto.getFirstName() != null) user.setFirstName(patchDto.getFirstName());
        if(patchDto.getLastName() != null) user.setLastName(patchDto.getLastName());
        if(patchDto.getDateOfBirth() != null) user.setDateOfBirth(patchDto.getDateOfBirth());
        if(patchDto.getEmail() != null) user.setEmail(patchDto.getEmail());
        if(patchDto.getAbout() != null) user.setAbout(patchDto.getAbout());
        if(patchDto.getCity() != null) user.setCity(patchDto.getCity());

        userRepository.save(user);
    }

    public void deleteUserById(Integer principalId, Integer id) throws UserServiceException {
        if(!principalId.equals(id)) throw new UserServiceException(UserServiceError.FORBIDDEN);

        try {
            deleteProfileImage(principalId, id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        userRepository.deleteById(id);
    }

    public void saveProfileImage(Integer principalId, Integer id, MultipartFile image) throws IOException, UserServiceException {
        if(image.isEmpty()) throw new UserServiceException(UserServiceError.BAD_REQUEST);
        if(!principalId.equals(id)) throw new UserServiceException(UserServiceError.FORBIDDEN);
        if(findUserById(id).isEmpty()) throw new UserServiceException(UserServiceError.NOT_FOUND);

        deleteProfileImage(principalId, id);
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

    public void deleteProfileImage(Integer principalId, Integer id) throws IOException, UserServiceException {
        if(!principalId.equals(id)) throw new UserServiceException(UserServiceError.FORBIDDEN);

        Optional<String> filePath = userRepository.findUserProfileImageUrlById(id);

        if(filePath.isEmpty()) return;
        Path imagePath = Path.of("src/main/resources/static/images/profileImages/" + filePath.get());

        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
        }

        userRepository.deleteUserProfileImageUrl(id);
    }
}
