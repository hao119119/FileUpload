package com.example.controller;

import com.example.entity.FileEntity;
import com.example.service.HBaseDownloader;
import com.example.util.ZipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

@SuppressWarnings("all")
@Controller
public class FileDownloadController {
	private Logger logger = LoggerFactory.getLogger(FileUploadController.class);
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
	/**
	 * 打包压缩下载文件
	 */
	@RequestMapping(value = "/downLoadZipFile/{startKey}/{endKey}")
	public void downLoadZipFile(HttpServletResponse response, HttpServletRequest request,@PathVariable String startKey,@PathVariable String endKey) throws IOException{
		String zipName = "file.zip";
		response.setContentType("APPLICATION/OCTET-STREAM");
		response.setHeader("Content-Disposition","attachment; filename="+zipName);
		ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
        String downloadPath = request.getSession().getServletContext().getRealPath("/file/");
		try {
            boolean flag = hbaesDownloader.DownloadPath(downloadPath,startKey,endKey);
            if(flag==true){
                ZipUtils.doCompress(downloadPath, out);
            }else{
                response.getWriter().print("下载错误");
            }
			response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			out.close();
		}
	}
	/**
	 * 根据rowkey删除
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/delete/{id}", method= RequestMethod.GET)
	@ResponseBody
	public boolean deleteByRowkey(@PathVariable String id){
		boolean flag = hbaesDownloader.deleteById(id);
		logger.debug("传入的rowkey："+id);
		return flag;
	}

}
