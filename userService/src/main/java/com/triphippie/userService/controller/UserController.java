package com.triphippie.userService.controller;

import com.triphippie.userService.model.AuthDto;
import com.triphippie.userService.model.UserInDto;
import com.triphippie.userService.model.UserOutDto;
import com.triphippie.userService.model.ValidateUserDto;
import com.triphippie.userService.service.UserService;
import com.triphippie.userService.service.UserServiceResult;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> postUser(@RequestBody @Valid UserInDto user) {
        UserServiceResult result = userService.createUser(user);
        return switch (result) {
            case SUCCESS -> new ResponseEntity<>(HttpStatus.CREATED);
            case CONFLICT -> throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
            default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
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
        if (user.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        return new ResponseEntity<>(user.get(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    //@PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<?> putUser(
            @RequestHeader("auth-user-id") Integer principal,
            @PathVariable("id") Integer id,
            @RequestBody @Valid UserInDto user
    ) {
        if(!principal.equals(id)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Write access forbidden");
        UserServiceResult result = userService.updateUser(id, user);
        return switch (result) {
            case SUCCESS -> new ResponseEntity<>(HttpStatus.NO_CONTENT);
            case NOT_FOUND -> throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            case CONFLICT -> throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
            default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<?> deleteUser(
            @RequestHeader("auth-user-id") Integer principal,
            @PathVariable("id") Integer id
    ) {
        if(!principal.equals(id)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Write access forbidden");
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
    //@PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<?> postProfileImage(
            @RequestHeader("auth-user-id") Integer principal,
            @PathVariable("id") Integer id,
            @RequestParam("profileImage") MultipartFile file
    ) {
        try {
            if(!principal.equals(id)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Write access forbidden");
            UserServiceResult result = userService.saveProfileImage(id, file);
            return switch (result) {
                case SUCCESS -> new ResponseEntity<>(HttpStatus.CREATED);
                case NOT_FOUND -> throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            };
        } catch(IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}/profileImage")
    //@PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<?> deleteProfileImage(
            @RequestHeader("auth-user-id") Integer principal,
            @PathVariable Integer id
    ) {
        try {
            if(!principal.equals(id)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Write access forbidden");
            UserServiceResult result = userService.deleteProfileImage(id);
            return switch (result) {
                case SUCCESS -> new ResponseEntity<>(HttpStatus.NO_CONTENT);
                default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            };
        } catch(IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /* SECURITY MAPPERS */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid AuthDto authDto) {
        return new ResponseEntity<>(userService.login(authDto), HttpStatus.OK);
    }

    @PostMapping("/validateToken")
    public ResponseEntity<ValidateUserDto> validateToken(@RequestParam(name = "token") String token) {
        Optional<ValidateUserDto> userId = userService.validateToken(token);
        if (userId.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        return new ResponseEntity<>(userId.get(), HttpStatus.OK);
    }
}
