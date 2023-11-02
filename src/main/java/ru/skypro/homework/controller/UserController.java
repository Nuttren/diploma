package ru.skypro.homework.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.service.AuthService;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;


@RestController
@RequestMapping("/users")
@CrossOrigin ("http://localhost:3000/")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final ImageService imageService;


    @GetMapping("/me")
    public MeDTO me(Authentication authentication) {
        UserDetailsDTO userDetailsDTO = (UserDetailsDTO) authentication.getPrincipal();
        return MeDTO.fromUser(userDetailsDTO);
    }


    @PostMapping("/set_password")
    public NewPassword setPassword(@RequestBody NewPassword newPassword, Authentication authentication) {
        UserDetailsDTO userDetailsDTO = (UserDetailsDTO) authentication.getPrincipal();
        NewPassword resultPassword = new NewPassword();

        // Устанавливаем currentPassword в результат перед вызовом changePassword
        resultPassword.setCurrentPassword(newPassword.getCurrentPassword());

        authService.changePassword(
                        userDetailsDTO.getUsername(),
                        newPassword.getCurrentPassword(),
                        newPassword.getNewPassword()
                )
                .ifPresent(resultPassword::setNewPassword);
        return resultPassword;

    }

    @PatchMapping("/me")
    public ResponseEntity<UpdateUserDTO> updateUserInfo(@RequestBody UpdateUserDTO updateUserDTO, Authentication authentication) {    try {
        // Вызовем метод из вашего сервиса для обновления информации пользователя
        authService.updateUserInfo(authentication, updateUserDTO);

        // вернем полученный в запросе объект, так как он уже содержит обновленные данные
        return ResponseEntity.ok(updateUserDTO);
    } catch (EntityNotFoundException ex) {
        // Если пользователя не существует, кинем 404 Not Found
        return ResponseEntity.notFound().build();
    }
    }

    @PatchMapping(value ="/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateAvatar(Authentication authentication, @RequestParam("image") MultipartFile image) {
        UserDetailsDTO userDetailsDTO = (UserDetailsDTO) authentication.getPrincipal();

        if (userDetailsDTO != null) {
            try {
                imageService.uploadAvatar(image, authentication);
                return ResponseEntity.ok().build();
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update avatar");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");
        }
    }

}
