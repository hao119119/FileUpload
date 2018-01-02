package com.example.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.example.service.HBaseUploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.entity.FileEntity;
import com.example.util.FileUoloadUtil;

@SuppressWarnings("all")
@Controller
public class FileUploadController {
	@Autowired
	private FileUoloadUtil fileUploadUtil;

	@Autowired
	private HBaseUploader hbaesUploader;

	/**
	 * 首页
	 * 
	 * @return
	 */
	@RequestMapping("/")
	public String index() {
		return "file";
	}

	/**
	 * 文件和元数据一起提交
	 * 
	 * @return
	 */
	@RequestMapping("/uploadFile")
	public String uploadFile(HttpServletRequest request,
			@RequestParam(value = "file", required = false) MultipartFile multipartFile,
			@RequestParam(value = "key") String key,
			@RequestParam(value = "describe") String describe) {
		String result ="";
		String filepath = "";
		try {
			if (multipartFile != null && multipartFile.getOriginalFilename() != null
					&& !multipartFile.getOriginalFilename().equals("")) {
				FileEntity fileEntity = new FileEntity();
				fileEntity.setId(key);
				fileEntity.setFileName(multipartFile.getOriginalFilename());
				fileEntity.setFileSize(multipartFile.getSize());
				fileEntity.setFileType(multipartFile.getContentType());
				fileEntity.setDescribe(describe);
				// 上传的绝对路径
				String uploadPath = request.getSession().getServletContext().getRealPath("/upload/");
				// 修改文件名，按照上传日期，好定位
				int index = multipartFile.getOriginalFilename().lastIndexOf(".") + 1;
				String fileType = multipartFile.getOriginalFilename().substring(index);
				Date date = new Date();
				SimpleDateFormat sFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				String aString = (int) (Math.random() * 10000) + "";
				String filename = sFormat.format(date) + aString + "." + fileType;
				fileUploadUtil.createFolder(uploadPath);
				// 上传的路径，也就是相当于文件在服务其中的位置
				filepath = uploadPath + filename;
				fileEntity.setFilepath(filepath);
				File file = new File(filepath);
				multipartFile.transferTo(file);
				if(hbaesUploader.uploadFile(fileEntity)){
					request.setAttribute("fileEntity", fileEntity);
					file.delete();
					result = "success";
				}
				else
					result = "false";
			}
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = "error";
		}
		return result;
	}

	/**
	 * 上传方法的实现类，支持多文件上传(此方法会提取request中的文件，适合ajax先上传文件，返回文件信息，在提交元数据的提交方式)
	 * 
	 * @param request
	 * @throws IOException
	 */
	@RequestMapping("/upload")
	public void upload(HttpServletRequest request) throws IOException {
		String filepath = "";
		try {
			// 上传的绝对路径
			String uploadPath = request.getSession().getServletContext().getRealPath("/upload/");
			// 抓取request中的文件
			List<MultipartFile> multipartFiles = fileUploadUtil.extractedFiles(request);
			if (multipartFiles.size() < 0) {
				return;
			}
			for (MultipartFile multipartFile : multipartFiles) {
				// 修改文件名，按照上传日期，好定位
				int index = multipartFile.getOriginalFilename().lastIndexOf(".") + 1;
				String fileType = multipartFile.getOriginalFilename().substring(index);
				Date date = new Date();
				SimpleDateFormat sFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				String aString = (int) (Math.random() * 10000) + "";
				String filename = sFormat.format(date) + aString + "." + fileType;
				fileUploadUtil.createFolder(uploadPath);
				System.err.println(uploadPath);
				// 上传的路径，也就是相当于文件在服务其中的位置
				filepath = uploadPath + filename;
				File file = new File(filepath);
				multipartFile.transferTo(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
