package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDTO;
import ru.skypro.homework.dto.AdInfoDTO;
import ru.skypro.homework.dto.AdUpdateDTO;
import ru.skypro.homework.dto.AllAdDTO;
import ru.skypro.homework.pojo.Ad;
import ru.skypro.homework.pojo.Image;

import java.io.IOException;
import java.util.List;

public interface AdsService {
    AdDTO createAd(AdDTO adDTO, MultipartFile imageFile);

    List<AllAdDTO> getAllAds();

    AdInfoDTO getAdsInfo(Long pk);

    String deleteAd(Long pk);

    AdUpdateDTO updateAd(Long pk, AdUpdateDTO adUpdateDTO);

    List<AllAdDTO> getAdsForUser(String userName);


    void updateAdImage(Long pk, Image newImage) throws IOException;
}
