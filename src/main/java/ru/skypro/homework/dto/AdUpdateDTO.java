package ru.skypro.homework.dto;

import lombok.Data;
import ru.skypro.homework.pojo.Ad;

@Data
public class AdUpdateDTO {
    private String title;
    private Long price;
    private String description;


    public static AdUpdateDTO fromAd(Ad in) {
        AdUpdateDTO out = new AdUpdateDTO();
        out.setPrice(in.getPrice());
        out.setTitle(in.getTitle());
        out.setDescription(in.getDescription());

        return out;
    }

    public static Ad toAd(AdUpdateDTO in) {
        Ad out = new Ad();
        out.setTitle(in.getTitle());
        out.setPrice(in.getPrice());
        out.setDescription(in.getDescription());
        return out;
    }

}
