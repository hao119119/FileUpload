package com.example.controller;

import com.example.entity.FileEntity;
import com.example.service.HBaseDownloader;
import com.example.service.HBaseUploader;
import com.example.util.FileUoloadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SuppressWarnings("all")
@Controller
public class FileDownloadController {
	@Autowired
	private HBaseDownloader hbaesDownloader;

	/**
	 * 下载文件
	 * 
	 * @return
	 */
	@RequestMapping(value="/download/{id}", method= RequestMethod.GET)
	public ResponseEntity<byte[]> downloadFile(@PathVariable  String id) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		try {
			FileEntity fileEntity = hbaesDownloader.getFileEntity(id);
			if(fileEntity==null){
				return new ResponseEntity<byte[]>(null, new HttpHeaders(), HttpStatus.NOT_FOUND);
			}
			headers.setContentDispositionFormData("attachment", fileEntity.getFileName());
			return new ResponseEntity<byte[]>(hbaesDownloader.getFile(id),
                    headers, HttpStatus.CREATED);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ResponseEntity<byte[]>(null, new HttpHeaders(), HttpStatus.NOT_FOUND);

	}

}
