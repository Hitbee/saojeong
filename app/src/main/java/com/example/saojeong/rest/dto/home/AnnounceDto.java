package com.example.saojeong.rest.dto.home;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AnnounceDto {
    @SerializedName("image")
    @Expose
    private String image;
}
