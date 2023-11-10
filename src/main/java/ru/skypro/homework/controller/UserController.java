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

/**
 * Класс-контроллер для работы с пользователем
 */
@RestController
@RequestMapping("/users")
@CrossOrigin ("http://localhost:3000/")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final ImageService imageService;

    /**
     * Получение информации об авторизованном пользователе
     * @param authentication
     * @return MeDTO (id, email, firstName, lastName, phone, role, image)
     * HTTPStatus 200, 401
     */
    @GetMapping("/me")
    public MeDTO me(Authentication authentication) {
        UserDetailsDTO userDetailsDTO = (UserDetailsDTO) authentication.getPrincipal();
        return MeDTO.fromUser(userDetailsDTO);
    }

    /**
     * Обновление пароля
     * @param newPassword (currentPassword, newPassword)
     * @param authentication
     * @return newPassword (currentPassword, newPassword)
     * HTTPStatus 200, 401, 403
     */
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

    /**
     * Обновление информации об авторизованном пользователе
     * @param updateUserDTO (firstName, lastName, phone)
     * @param authentication
     * @return updateUserDTO (firstName, lastName, phone)
     * HTTPStatus 200, 401
     */
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

    /**
     * Обновление аватара авторизованного пользователя
     * @param authentication
     * @param image (MediaType.MULTIPART_FORM_DATA_VALUE)
     * @return
     * HTTPStatus 200, 401, 500
     */
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
