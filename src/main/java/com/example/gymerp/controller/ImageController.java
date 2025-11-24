package com.example.gymerp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.gymerp.config.FileStorageProperties;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden //  Swagger에서 이 컨트롤러 제외
@Controller
public class ImageController {
	
    private final FileStorageProperties fileStorageProperties;

    public ImageController(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }
	
	
	/*
	 *  "/upload/xxx.jpg"
	 *  "/upload/yyy.png"
	 *  "/upload/zzz.gif"  
	 *  패턴의 요청이 오면 실제 해당 이미지를 읽어서 실제 이미지 데이터를 응답하는 컨트롤러 메소드 만들기 
	 */
	@GetMapping("/upload/{saveFileName}")
	public ResponseEntity<InputStreamResource> image(@PathVariable("saveFileName") String name) throws IOException{
		/*
		 *  {saveFileName} 경로 변수에 담긴 내용을 추출해서 String name 매개변수에 담는 기능을 수행하는
		 *  
		 *  @PathVariable 어노테이션 
		 */
	//이미지의 이름을 이용해서 응답할 이미지가 어디에 있는지 전체 경로를 구성한다. 
	File file=new File(fileStorageProperties.getUploadDir().resolve(name).toString());
		//파일이 존재하지 않으면 예외 발생
		if(!file.exists()) {
			throw new RuntimeException("file not found!");
		}
		// mime type 알아내기
		String mimeType=Files.probeContentType(file.toPath());
		//InputStremResource 객체 얻어내기
		InputStreamResource isr=new InputStreamResource(new FileInputStream(file));
		
		// 이미지 데이터를 응답하는 ResponseEntity 객체를 구성해서 리턴해 준다.
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(mimeType))
				.contentLength(file.length())
				.body(isr);
	}
	
}
