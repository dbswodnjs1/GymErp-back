package com.example.gymerp.service;

import java.util.List;

import com.example.gymerp.dto.PostDto;

public interface PostService {
	List<PostDto> list();          // 전체 조회
    PostDto get(int id, boolean inc);
    int create(PostDto dto);
    int edit(PostDto dto);
    int delete(int id);
}
