package com.example.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * 上传工具类
 * @author admin
 *
 */
@SuppressWarnings("all")
@Component
public class FileUoloadUtil {
	private Logger log = LoggerFactory.getLogger(FileUoloadUtil.class);
	
	
	 /**
     *多文件上传的通用工具类（SpringMvc的)
     * @param request
     * @return
     */
    public List<MultipartFile> extractedFiles(HttpServletRequest request) {
        List<MultipartFile> multipartFiles = new ArrayList<MultipartFile>();
        // 创建一个通用的多部分解析器
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        // 判断 request 是否有文件上传,即多部分请求
        if (multipartResolver.isMultipart(request)) {
            // 转换成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            // 取得request中的所有文件名
            Iterator<String> iterator = multiRequest.getFileNames();
            while (iterator.hasNext()) {
                // 记录上传过程起始时的时间，用来计算上传时间
                long pre = System.currentTimeMillis();
                // 获取上传文件
                MultipartFile file = multiRequest.getFile(iterator.next());
                if (file != null) {
                    multipartFiles.add(file);
                    // 记录上传该文件后的时间
                    long finaltime = System.currentTimeMillis();
                    log.debug("文件：{},上传时间{}ms", file.getOriginalFilename(), (finaltime - pre));
                }
            }
        }
        return multipartFiles;
    }
	 /**
     *创建文件夹
     * @param filePath
     */
    public void createFolder(String filePath) {
        File file = new File(filePath);
        // 如果文件夹不存在则创建    
        if (!file.exists() && !file.isDirectory()) {
            log.debug("创建目录:"+filePath);
            file.mkdirs();
        } else {
            log.debug("目录:"+filePath);
        }
    }
}
