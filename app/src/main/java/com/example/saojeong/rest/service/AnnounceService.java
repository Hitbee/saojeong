package com.example.saojeong.rest.service;

import com.example.saojeong.rest.dto.board.BoardDto;
import com.example.saojeong.rest.dto.home.AnnounceDto;
import com.example.saojeong.rest.dto.store.StoreDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AnnounceService {
    @GET("api/board/{category}/content")
    Call<List<BoardDto>> getAnnounce(@Path(value = "category", encoded = false) int category);
}
