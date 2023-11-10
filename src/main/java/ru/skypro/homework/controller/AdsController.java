package ru.skypro.homework.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.pojo.Image;
import ru.skypro.homework.service.AdsService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Класс-кондроллер для работы с объявлениями (Ad)
 */
@RestController
@RequestMapping("/ads")
@CrossOrigin(value = "http://localhost:3000")
public class AdsController {

    private final AdsService adsService;
    private final ImageService imageService;

    public AdsController(AdsService adsService, ImageService imageService, UserService userService) {
        this.adsService = adsService;
        this.imageService = imageService;
    }

    /**
     * Создание нового объявления
     * @param authentication
     * @param adCreateDTO
     * @param image (MULTIPART_FORM_DATA_VALUE)
     * @return adCreateDTO
     * HTTPStatus 200, 401
     */
    @PostMapping(value ="", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdCreateDTO> createAd(
            Authentication authentication,
            @RequestPart("properties") AdCreateDTO adCreateDTO,
            @RequestPart("image") MultipartFile image
    ) {
        String userName = authentication.getName();
        // Вызываем сервис для создания объявления
        AdCreateDTO createdAd = adsService.createAd(userName, adCreateDTO, image);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdAd);
    }


    /**
     * Получение списка всех объявлений
     * Доступен в том числе для неавторизованных пользователей
     * @return HashMap(key - количество объявлений, value - список объявлений)
     * HTTPStatus 200
     */
    @GetMapping("")
    public ResponseEntity <?> getAllAds() {
        List<AllAdDTO> ads = adsService.getAllAds();
        Map<String, Object> response = new HashMap<>();
        response.put("count", ads.size());
        response.put("results", ads);

        return ResponseEntity.ok(response);
    }

    /**
     * Получение полной информации об объявлении
     * @param pk - id объявления
     * @return AdInfoDTO (pk, authorFirstName, authorLastName, description
     *                    email, image, phone, price, title)
     * HTTPStatus 200, 401, 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<AdInfoDTO> getAdInfo(@PathVariable ("id")Long pk) {
        AdInfoDTO adInfoDTO = adsService.getAdsInfo(pk);

        if (adInfoDTO != null) {
            return ResponseEntity.ok(adInfoDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Удаление объявления
     * @param authentication
     * @param pk - id объявления
     * @return
     * HTTPStatus 401, 403, 404
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAd(
            Authentication authentication,
            @PathVariable ("id") Long pk) {
        String resultMessage = adsService.deleteAd(pk, authentication);

        if (resultMessage.equals("Объявление успешно удалено")) {
            return ResponseEntity.ok(resultMessage);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultMessage);
        }
    }

    /**
     * Обновление информации об объявлении
     * @param authentication
     * @param pk - id объявления
     * @param updatedAd (title, price, description)
     * @return updatedAd (title, price, description)
     * HTTPStatus 200, 401, 403, 404
     */
    @PatchMapping("/{id}")
    public ResponseEntity<AdUpdateDTO> updateAd(
            Authentication authentication,
            @PathVariable("id") Long pk,
            @RequestBody AdUpdateDTO updatedAd) {
        AdUpdateDTO updated = adsService.updateAd(authentication, pk, updatedAd);

        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Получение объявлений авторизованного пользователя
     * @param authentication
     * @return HashMap(key - количество объявлений, value - список объявлений)
     * HTTPStatus 200, 401, 403, 404
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getUserAds(Authentication authentication) {
        String username = authentication.getName();
        List<AllAdDTO> ads = adsService.getAdsForUser(username);

        Map<String, Object> response = new HashMap<>();
        response.put("count", ads.size());
        response.put("results", ads);

        return ResponseEntity.ok(response);
    }

    /**
     * Обновление картинки объявления
     * @param pk - id объявления
     * @param image (MULTIPART_FORM_DATA_VALUE)
     * @return String - сообщение)
     * HTTPStatus 200, 401, 403, 404
     */
    @PatchMapping(value ="/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateAdImage(
            @PathVariable("id") Long pk,
            @RequestParam("image") MultipartFile image
    ) {
        try {
            Image newImage = imageService.uploadImageByPk(image, pk);
            adsService.updateAdImage(pk, newImage);
            return ResponseEntity.ok("Изображение успешно обновлено");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось обновить изображение");
        }
    }
}
