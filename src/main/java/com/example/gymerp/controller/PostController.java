package com.example.gymerp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.gymerp.dto.PostDto;
import com.example.gymerp.service.PostService;

import lombok.RequiredArgsConstructor;


@RequestMapping("/v1")
@RequiredArgsConstructor
@RestController
public class PostController {
	 
	private final PostService postService;

    // ✅ (1) 게시글 전체 조회
    // 프론트 요청: GET /v1/post
    @GetMapping("/post")
    public List<PostDto> list() {
        return postService.list(); // 정렬은 Mapper에서 pinned DESC, postId DESC
    }

    // ✅ (2) 게시글 상세 조회 (조회수 증가 옵션: ?inc=true)
    // 프론트 요청: GET /v1/post/{postId}?inc=true
    @GetMapping("/post/{postId}")
    public PostDto detail(@PathVariable int postId,
                          @RequestParam(defaultValue = "true") boolean inc) {
        return postService.get(postId, inc);
    }

    // ✅ (3) 게시글 등록
    // 프론트 요청: POST /v1/post
    @PostMapping("/post")
    public ResponseEntity<Integer> create(@RequestBody PostDto dto) {
        postService.create(dto);
        return ResponseEntity.ok(dto.getPostId()); // 생성된 PK 반환
    }

    // ✅ (4) 게시글 수정 (PUT: 전체 갱신 용도)
    // 프론트 요청: PUT /v1/post/{postId}
    @PutMapping("/post/{postId}")
    public ResponseEntity<Void> updatePut(@PathVariable int postId, @RequestBody PostDto dto) {
        dto.setPostId(postId);
        postService.edit(dto);
        return ResponseEntity.noContent().build();
    }

    // ✅ (5) 게시글 수정 (PATCH: 일부 필드만 변경 시 사용 가능)
    // 프론트 요청: PATCH /v1/post/{postId}
    @PatchMapping("/post/{postId}")
    public ResponseEntity<Void> updatePatch(@PathVariable int postId, @RequestBody PostDto dto) {
        dto.setPostId(postId);
        postService.edit(dto); // 단순화: Service 레벨에서 부분/전체 업데이트 동일 처리
        return ResponseEntity.noContent().build();
    }

    // ✅ (6) 게시글 삭제
    // 프론트 요청: DELETE /v1/post/{postId}
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> delete(@PathVariable int postId) {
        postService.delete(postId);
        return ResponseEntity.noContent().build();
    }
}
