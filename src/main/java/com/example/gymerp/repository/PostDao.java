package com.example.gymerp.repository;

import java.util.List;


import com.example.gymerp.dto.PostDto;

public interface PostDao {
	List<PostDto> selectAll();
    PostDto get(int postId);
    int insert(PostDto dto);
    int update(PostDto dto);
    int delete(int postId);
    int incView(int postId);
}
