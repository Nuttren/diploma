package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.skypro.homework.pojo.Ad;

@Data
public class AdCreateDTO {

    @Schema(hidden = true)
    private Long author;
    @Schema(hidden = true)
    private String image;
    @Schema(hidden = true)
    private Long pk;
    private Long price;
    private String title;
    private String description;

    public static AdCreateDTO fromAd(Ad ad) {
        AdCreateDTO createdAdDTO = new AdCreateDTO();
        createdAdDTO.setAuthor(ad.getUserID()); // Устанавливаем идентификатор пользователя
        createdAdDTO.setDescription(ad.getDescription());
        createdAdDTO.setPrice(ad.getPrice());
        createdAdDTO.setTitle(ad.getTitle());
        createdAdDTO.setPk(ad.getPk());
        createdAdDTO.setImage("/" + ad.getImage().getImagePath().replace("\\", "/"));

        return createdAdDTO;
    }
}
