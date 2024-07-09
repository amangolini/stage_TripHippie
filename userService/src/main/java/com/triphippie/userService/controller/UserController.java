package com.triphippie.userService.controller;

import com.triphippie.userService.model.user.UserInDto;
import com.triphippie.userService.model.user.UserOutDto;
import com.triphippie.userService.model.user.UserPatchDto;
import com.triphippie.userService.service.UserService;
import com.triphippie.userService.service.UserServiceException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("api/users")
@EnableMethodSecurity
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /* CRUD MAPPERS */
    @PostMapping
    public ResponseEntity<?> postUser(@RequestBody @Valid UserInDto user) {
        try {
            userService.createUser(user);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (UserServiceException ex) {
            switch (ex.getError()) {
                case CONFLICT -> throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @GetMapping
    public ResponseEntity<List<UserOutDto>> getUsers(
            @RequestParam("usersSize") int usersSize,
            @RequestParam("page") int page,
            @RequestParam("username") Optional<String> username )
    {
        return (username.isEmpty())
                ? new ResponseEntity<>(userService.findAllUsers(usersSize, page), HttpStatus.OK)
                : new ResponseEntity<>(userService.findAllUsersByUsername(usersSize, page, username.get()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") Integer id) {
        Optional<UserOutDto> user = userService.findUserById(id);
        if (user.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        return new ResponseEntity<>(user.get(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal")
    public ResponseEntity<?> putUser(
            @PathVariable("id") Integer id,
            @RequestBody @Valid UserInDto user
    ) {
        try {
            userService.replaceUser(id, user);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (UserServiceException ex) {
            switch (ex.getError()) {
                case NOT_FOUND -> throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                case CONFLICT -> throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("#id == authentication.principal")
    public ResponseEntity<?> patchUser(
            @PathVariable("id") Integer id,
            @RequestBody @Valid UserPatchDto user
    ) {
        try {
            userService.updateUser(id, user);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (UserServiceException ex) {
            switch (ex.getError()) {
                case NOT_FOUND -> throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                case CONFLICT -> throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.principal")
    public ResponseEntity<?> deleteUser(
            @PathVariable("id") Integer id
    ) {
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/profileImage")
    public ResponseEntity<?> getProfileImage(@PathVariable Integer id) {
        try {
            Optional<byte[]> image = userService.findProfileImage(id);
            if(image.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.IMAGE_PNG)
                    .body(image.get());
        } catch(IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/profileImage")
    @PreAuthorize("#id == authentication.principal")
    public ResponseEntity<?> postProfileImage(
            @PathVariable("id") Integer id,
            @RequestParam("profileImage") MultipartFile file
    ) {
        try {
            userService.saveProfileImage(id, file);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (UserServiceException ex) {
            switch (ex.getError()) {
                case BAD_REQUEST -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                case NOT_FOUND -> throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                case UNSUPPORTED -> throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @DeleteMapping("/{id}/profileImage")
    @PreAuthorize("#id == authentication.principal")
    public ResponseEntity<?> deleteProfileImage(
            @PathVariable Integer id
    ) {
        try {
            userService.deleteProfileImage(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch(IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
