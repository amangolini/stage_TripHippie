package com.triphippie.userService.controller;

import com.triphippie.userService.model.UserInDto;
import com.triphippie.userService.model.UserOutDto;
import com.triphippie.userService.service.UserService;
import com.triphippie.userService.service.UserServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("api/users")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /* CRUD MAPPERS */
    @PostMapping
    public ResponseEntity<?> postUser(@RequestBody UserInDto user) {
        UserServiceResult result = userService.createUser(user);
        return switch (result) {
            case SUCCESS -> new ResponseEntity<>(HttpStatus.CREATED);
            case BAD_REQUEST -> new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            case CONFLICT -> new ResponseEntity<>(HttpStatus.CONFLICT);
            default -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        };
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
        return (user.isPresent())
                ? new ResponseEntity<>(user.get(), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<?> putUser(@PathVariable("id") Integer id, @RequestBody UserInDto user) {
        UserServiceResult result = userService.updateUser(id, user);
        return switch (result) {
            case SUCCESS -> new ResponseEntity<>(HttpStatus.NO_CONTENT);
            case NOT_FOUND -> new ResponseEntity<>(HttpStatus.NOT_FOUND);
            case CONFLICT -> new ResponseEntity<>(HttpStatus.CONFLICT);
            default -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Integer id) {
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
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/profileImage")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<?> postProfileImage(@PathVariable Integer id, @RequestParam("profileImage") MultipartFile file) {
        try {
            UserServiceResult result = userService.saveProfileImage(id, file);
            return switch (result) {
                case SUCCESS -> new ResponseEntity<>(HttpStatus.CREATED);
                case NOT_FOUND -> new ResponseEntity<>(HttpStatus.NOT_FOUND);
                default -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            };
        } catch(IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}/profileImage")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<?> deleteProfileImage(@PathVariable Integer id) {
        try {
            userService.deleteProfileImage(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch(IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /* SECURITY MAPPERS */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestParam(name = "username") String username,
            @RequestParam(name = "password") String password
    ) {
        return new ResponseEntity<>(userService.login(username,password), HttpStatus.OK);
    }

    @PostMapping("/validateToken")
    public ResponseEntity<Boolean> validateToken(@RequestParam(name = "token") String token) {
        return new ResponseEntity<>(userService.validateToken(token), HttpStatus.OK);
    }
}
