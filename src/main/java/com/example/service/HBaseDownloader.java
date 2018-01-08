package com.example.service;

import com.example.entity.FileEntity;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenhaorj on 2017/11/14.
 */
@Component
public class HBaseDownloader {
    static Configuration configuration = null;
    private Connection connection = null;
    private Logger log = LoggerFactory.getLogger(HBaseDownloader.class);
    static {
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "manager.bigdata:2181,master1.bigdata:2181,master2.bigdata:2181");
    }

    public HBaseDownloader(){
        try {
            connection = ConnectionFactory.createConnection(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getFile(String id) throws IOException {
        Table table = connection.getTable(TableName.valueOf("i3"));
        Get scan = new Get(id.getBytes());// 根据rowkey查询
        Result r = table.get(scan);
        for (Cell keyValue : r.rawCells()) {
            String family;
            family = new String(CellUtil.cloneFamily(keyValue));
            String qualifier;
            qualifier = new String(CellUtil.cloneQualifier(keyValue));
            if("info".equals(family)&&"file".equals(qualifier)){
                return CellUtil.cloneValue(keyValue);
            }
        }
        return null;

    }

    public FileEntity getFileEntity(String id) throws IOException {
        Table table = connection.getTable(TableName.valueOf("i3"));
        Get scan = new Get(id.getBytes());// 根据rowkey查询
        FileEntity fileEntity = new FileEntity();
        fileEntity.setId(id);
        Result r = table.get(scan);
        if(r.isEmpty()){
            log.debug("对象不存在");
            return null;
        }
        for (Cell keyValue : r.rawCells()) {
            String family;
            family = new String(CellUtil.cloneFamily(keyValue));
            String qualifier;
            qualifier = new String(CellUtil.cloneQualifier(keyValue));
            if ("info".equals(family)) {
                if("fileName".equals(qualifier)){
                    fileEntity.setFileName(new String(CellUtil.cloneValue(keyValue)));
                }
                if("fileType".equals(qualifier)){
                    fileEntity.setFileType(new String(CellUtil.cloneValue(keyValue)));
                }
                if("fileSize".equals(qualifier)){
                    fileEntity.setFileSize(Long.parseLong(new String(CellUtil.cloneValue(keyValue))));
                }
                if("text".equals(qualifier)){
                    fileEntity.setDescribe(new String(CellUtil.cloneValue(keyValue)));
                }
            }
        }
        return fileEntity;

    }

    /**
     * 拷贝Hbase文件到临时目录
     * @param filePath
     * @param startKey
     * @param endKey
     * @return
     */
    public Boolean DownloadPath(String filePath,String startKey,String endKey){
        Boolean flag = false;
        HTable hTable = null;
        ResultScanner rs = null;
        String name = "" ;
        byte[] b = null;
        try{
            hTable = new HTable(configuration,TableName.valueOf("i3"));
            Scan scan = new Scan();
            scan.setStartRow(startKey.getBytes());
            scan.setStopRow(endKey.getBytes());
            rs = hTable.getScanner(scan);
            if(null!=rs && !"".equals(rs)){
                for (Result result : rs) {
                    for (Cell kv : result.rawCells()) {
                        String family;
                        family = new String(CellUtil.cloneFamily(kv));
                        String qualifier;
                        qualifier = new String(CellUtil.cloneQualifier(kv));
                        if("info".equals(family)){
                            if("fileName".equals(qualifier)){
                                name = new String(CellUtil.cloneValue(kv));
                            }
                            if("file".equals(qualifier)){
                                b =  CellUtil.cloneValue(kv);
                            }
                        }
                    }
                    log.debug("开始的rowkey" + startKey + "------------" + "结束的key" + endKey);
                    writeFile(filePath, name, b);
                    flag = true;
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return  flag;
    }

    /**
     * 将文件写入临时目录
     * @param filePath
     * @param bytes
     * @return
     */
    public void writeFile(String filePath,String fileName,byte[] bytes){
        FileOutputStream outputStream =null;
        log.info("文件缓存路径"+filePath);
        try{
            File file = new File(filePath);
            if(!file.exists()){
                file.mkdir();
            }
            File f = new File(filePath+fileName);
            outputStream = new FileOutputStream(f);
            outputStream.write(bytes,0,bytes.length);
        }
        catch(IOException e){
            log.error("文件写入临时目录失败");
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 根据rowkey删除
     * @param id
     * @throws IOException
     */
    public boolean  deleteById(String id){
        try {
            Table table = connection.getTable(TableName.valueOf("i3"));
            Delete delete = new Delete(id.getBytes());
            List<Delete> list = new ArrayList<Delete>();
            list.add(delete);
            table.delete(list);
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return  true;
    }
}
