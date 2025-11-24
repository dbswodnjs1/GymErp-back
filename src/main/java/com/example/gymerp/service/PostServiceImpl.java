package com.example.gymerp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gymerp.dto.PostDto;
import com.example.gymerp.repository.PostDao;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
	
	final private PostDao postDao;
	
	@Transactional(readOnly = true)
    public List<PostDto> list() {
        return postDao.selectAll();
    }

    @Transactional
    public PostDto get(int id, boolean inc) {
        if (inc) postDao.incView(id);
        return postDao.get(id);
    }

    @Transactional
    public int create(PostDto dto) {
        if (dto.getPostPinned() == null) dto.setPostPinned("N");
        return postDao.insert(dto);
    }

    @Transactional
    public int edit(PostDto dto) {
        return postDao.update(dto);
    }

    @Transactional
    public int delete(int id) {
        return postDao.delete(id);
    }
}
