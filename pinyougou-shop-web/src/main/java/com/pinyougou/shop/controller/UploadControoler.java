package com.pinyougou.shop.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.Result;
import util.FastDFSClient;

@RestController
public class UploadControoler {

	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL;
	
	@RequestMapping("/upload")
	public Result uploadFile(MultipartFile file){
		String name=file.getOriginalFilename();
		String type=name.substring(name.lastIndexOf(".")+1);
		try {
			FastDFSClient fastDFSClient=new FastDFSClient("classpath:config/fdfs_client.conf");
			String path=fastDFSClient.uploadFile(file.getBytes(), type);
			return new Result(true,FILE_SERVER_URL+path);
		} catch (Exception e) {
			e.getStackTrace();
			System.out.println(e.getMessage());
			return new Result(false,"上传失败！");
		}
	}
}
