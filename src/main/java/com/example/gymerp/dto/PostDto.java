package com.example.gymerp.dto;

import java.sql.Timestamp;

import org.apache.ibatis.type.Alias;

import lombok.Data;


@Alias("PostDto")
@Data
public class PostDto {
	private int postId;
    private String postTitle;
    private String postContent;
    private String postWriter;
    private String postPinned;   // 'Y'/'N'
    private int postViewCnt;
    private Timestamp postCreatedAt;
    private Timestamp postUpdatedAt;
    
    private String writerName;      // EMPLOYEE에서 조인된 이름
}
