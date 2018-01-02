package com.example.service;

import com.example.entity.FileEntity;
import com.example.util.FileUoloadUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
}
