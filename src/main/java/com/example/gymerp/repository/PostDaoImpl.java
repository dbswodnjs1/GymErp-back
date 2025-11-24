package com.example.gymerp.repository;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import com.example.gymerp.dto.PostDto;

import lombok.RequiredArgsConstructor;

@Repository
@Primary
@RequiredArgsConstructor
public class PostDaoImpl implements PostDao {

    private final SqlSession session;

    /** 게시글 전체 조회 */
    @Override
    public List<PostDto> selectAll() {
        return session.selectList("PostMapper.selectAll");
    }

    /** 게시글 상세 조회 */
    @Override
    public PostDto get(int postId) {
        return session.selectOne("PostMapper.get", postId);
    }

    /** 게시글 등록 */
    @Override
    public int insert(PostDto dto) {
        return session.insert("PostMapper.insert", dto);
    }

    /** 게시글 수정 */
    @Override
    public int update(PostDto dto) {
        return session.update("PostMapper.update", dto);
    }

    /** 게시글 삭제 */
    @Override
    public int delete(int postId) {
        return session.delete("PostMapper.delete", postId);
    }

    /** 조회수 증가 */
    @Override
    public int incView(int postId) {
        return session.update("PostMapper.incView", postId);
    }
}
