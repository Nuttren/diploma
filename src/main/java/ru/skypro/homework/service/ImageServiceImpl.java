package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.skypro.homework.pojo.Ad;
import ru.skypro.homework.pojo.Image;
import ru.skypro.homework.pojo.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.ImageRepository;
import ru.skypro.homework.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Класс для работы с изображениями
 * Работает с ImageRepository, UserRepository, AdRepository
 */
@RequiredArgsConstructor
@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final AdRepository adRepository;

    @Value("${image.upload.directory}")
    private String uploadDirectory;


    /**
     * Cохранение изображения
     */
    @Override
    public Image saveImage(Image image) {
        return imageRepository.save(image);
    }

    /**
     * Загрузка и сохранение изображения
     */
    @Override
    public Image uploadImage(MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            byte[] data = file.getBytes();
            String fileName = file.getOriginalFilename();

            // Сохранение файла на локальный диск
            String filePath = saveFile(data, fileName);

            // Сохранение пути к файлу в базе данных
            Image image = new Image();
            image.setData(file.getBytes());
            image.setImagePath(filePath);
            image.setImageSize(file.getSize());
            image.setImageType(file.getContentType());

            imageRepository.save(image);

            return image;
        } else {
            throw new IllegalArgumentException("Uploaded file is empty");
        }
    }


    /**
     * Cохранение файла
     */
    public String saveFile(byte[] data, String fileName) throws IOException {
        String filePath = Paths.get(uploadDirectory, fileName).toString();
        Path path = Paths.get(filePath);
        Files.write(path, data);
        return filePath;
    }

    /**
     * Загрузка аватара
     */
    public Image uploadAvatar(MultipartFile file, Authentication authentication) throws IOException {
        if (!file.isEmpty()) {
            byte[] data = file.getBytes();
            String fileName = file.getOriginalFilename();

            // Сохранение файла на локальный диск
            String filePath = saveFile(data, fileName);

            // Сохранение пути к файлу в базе данных
            Image image = new Image();
            image.setData(file.getBytes());
            image.setImagePath(filePath);
            image.setImageSize(file.getSize());
            image.setImageType(file.getContentType());

            // Сохранение изображения в базе данных и получение его ID
            Long imageId = imageRepository.save(image).getImageId();

            // Извлечение имени пользователя из объекта Authentication
            String userName = authentication.getName();

            // Обновление поля imageId в сущности User
            User user = userRepository.findUserByUserName(userName);
            if (user != null) {
                user.setAvatarId(imageId);
                userRepository.save(user);
            }

            return image;
        } else {
            throw new IllegalArgumentException("Uploaded file is empty");
        }
    }

    /**
     * Загрузка картинки объявления
     */
    public Image uploadImageByPk(MultipartFile file, Long pk) throws IOException {
        if (!file.isEmpty()) {
            byte[] data = file.getBytes();
            String fileName = file.getOriginalFilename();

            // Сохранение файла на локальный диск
            String filePath = saveFile(data, fileName);

            // Сохранение пути к файлу в базе данных
            Image image = new Image();
            image.setData(file.getBytes());
            image.setImagePath(filePath);
            image.setImageSize(file.getSize());
            image.setImageType(file.getContentType());

            Long imageId = imageRepository.save(image).getImageId();

            Ad ad = adRepository.findByPk(pk);
            ad.setImageId(imageId);
            adRepository.save(ad);

            return image;
        } else {
            throw new IllegalArgumentException("Uploaded file is empty");
        }
    }
}