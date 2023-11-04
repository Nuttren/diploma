package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.exception.AdNotFoundException;
import ru.skypro.homework.exception.ForbittenException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.pojo.Ad;
import ru.skypro.homework.pojo.Comment;
import ru.skypro.homework.pojo.Image;
import ru.skypro.homework.pojo.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdsServiceImpl implements AdsService {

    private final AdRepository adRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;


    @Override
    public AdCreateDTO createAd(String userName, AdCreateDTO adCreateDTO, MultipartFile image) {
        // Находим пользователя по его имени (userName)
        User user = userRepository.findUserByUserName(userName);

        if (user != null) {
            // Создаем объявление
            Ad ad = new Ad();
            ad.setUser(user); // Устанавливаем связь между объявлением и пользователем
            ad.setPrice(adCreateDTO.getPrice());
            ad.setTitle(adCreateDTO.getTitle());
            ad.setDescription(adCreateDTO.getDescription());

            Ad createdAd = adRepository.save(ad);

            try {
                // Вызываем метод uploadImage и передаем в него imageFile
                Image uploadedImage = imageService.uploadImage(image);

                // Получаем идентификатор сохраненного изображения
                Long imageId = uploadedImage.getImageId();

                // Связываем изображение с объявлением
                createdAd.setImageId(imageId);

                // Пересохраняем объявление с обновленным image_id
                adRepository.save(createdAd);

                return AdCreateDTO.fromAd(createdAd);
            } catch (IOException e) {
                e.printStackTrace();
                // Обработка ошибки загрузки изображения
                throw new RuntimeException("Failed to upload image", e);
            }
        } else {
            // Если пользователь не найден, обработка ошибки
            throw new UserNotFoundException();
        }
    }

    @Override
    public List<AllAdDTO> getAllAds() {
        List<Ad> ads = adRepository.findAll(); // Извлекаем все объявления из базы данных
        List<AllAdDTO> adsRequestDTOs = new ArrayList<>();

        for (Ad ad : ads) {
            adsRequestDTOs.add(AllAdDTO.fromAd(ad));
        }

        return adsRequestDTOs;
    }

    @Override
    public AdInfoDTO getAdsInfo(Long pk) {
        // инфа об объявлении по pk из базы данных
        Optional<Ad> adOptional = adRepository.findById(pk);

        if (adOptional.isPresent()) {
            Ad ad = adOptional.get();

            // Создание объекта AdsInfoDTO и заполнение его данными из объявления
            return AdInfoDTO.fromAd(ad);
        } else {
            // Если объявление не найдено бросаем 404
            throw new AdNotFoundException();
        }
    }

    @Override
    public String deleteAd(Long pk, Authentication authentication) {
        if (isAdmin(authentication) || isAuthor(authentication, pk)) {
            // Находим по pk
            Optional<Ad> adOptional = adRepository.findById(pk);

            if (adOptional.isPresent()) {

                // Объявление найдено
                Long ad = adOptional.get().getPk();

                // Находим все комментарии, связанные с этим объявлением
                List<Comment> comments = commentRepository.findByPk(ad);
                commentRepository.deleteAll(comments);
                // Если объявление найдено, сносим
                adRepository.deleteById(pk);

                return "Объявление успешно удалено";
            } else {
                // Если объявление не найдено, вернем сообщение об ошибке
                return "Объявление с указанным ID не найдено";
            }
        }
        throw new ForbittenException();
    }

    @Override
    public AdUpdateDTO updateAd(Authentication authentication, Long pk, AdUpdateDTO adUpdateDTO) {
        if (isAdmin(authentication) || isAuthor(authentication, pk)) {
            Optional<Ad> optionalAd = adRepository.findById(pk);
            if (optionalAd.isPresent()) {
                Ad ad = optionalAd.get();
                AdUpdateDTO.toAd(adUpdateDTO);

                // Сохраняем обновленное объявление в базу
                adRepository.save(ad);

                // Создаем объект AdsDTO для ответа с нужными полями
                return AdUpdateDTO.fromAd(ad);
            } else {
                // Если объявление не найдено бросаем 404
                throw new AdNotFoundException();
            }
        }
        throw new ForbittenException();
    }

    @Override
    public List<AllAdDTO> getAdsForUser(String userName) {
        User user = userRepository.findUserByUserName(userName);

        List<Ad> userAds = adRepository.findAdsByUserUserID(user.getUserID());
        List<AllAdDTO> allAdDTOs = new ArrayList<>();

        for (Ad ad : userAds) {
            allAdDTOs.add(AllAdDTO.fromAd(ad));
        }

        return allAdDTOs;
    }

    @Override
    public void updateAdImage(Long pk, Image newImage) {
        Ad ad = adRepository.findById(pk).orElse(null);
        if (ad != null) {
            // Сохранение изображения в базе данных
            Image savedImage = imageService.saveImage(newImage);
            ad.setImage(savedImage);
            adRepository.save(ad);
        }
    }


    public boolean isAdmin(Authentication authentication) {
        User user = userRepository.findUserByUserName(authentication.getName());
        return user.getRole().equals(Role.ADMIN);
    }

    public boolean isAuthor(Authentication authentication, Long adId) {
        //        Находим объявление по id
        Ad ad = adRepository.findById(adId).orElseThrow(
                () -> {
                    throw new AdNotFoundException();
                });
        //        Находим автора по id
        User adUser = userRepository.findById(ad.getUserID()).orElseThrow(
                () -> {
                    throw new UserNotFoundException();
                });
        //        Находим текущего юзера
        User currentUser = userRepository.findUserByUserName(authentication.getName());
        //        сравниваем автора и юзера
        return adUser.getUserID().equals(currentUser.getUserID());
    }


}


