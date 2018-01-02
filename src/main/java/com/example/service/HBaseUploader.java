package com.example.service;

import com.example.entity.FileEntity;
import com.example.util.FileUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by chenhaorj on 2017/11/14.
 */
@Component
public class HBaseUploader {

    static Configuration configuration = null;
    private Connection connection = null;
    static {
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "manager.bigdata:2181,master1.bigdata:2181,master2.bigdata:2181");
    }

    public HBaseUploader(){
        try {
            connection = ConnectionFactory.createConnection(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean uploadFile(FileEntity fileEntity) throws IOException {
        Table table = connection.getTable(TableName.valueOf("i3"));
        Put put = new Put(fileEntity.getId().getBytes());
        put.addColumn("info".getBytes(),"fileName".getBytes(),fileEntity.getFileName().getBytes());
        put.addColumn("info".getBytes(),"fileSize".getBytes(),(fileEntity.getFileSize()+"").getBytes());
        put.addColumn("info".getBytes(),"fileType".getBytes(),fileEntity.getFileType().getBytes());
        put.addColumn("info".getBytes(),"text".getBytes(),fileEntity.getDescribe().getBytes());
        byte[] fileBytes = FileUtil.getBytes(fileEntity.getFilepath());
        put.addColumn("info".getBytes(),"file".getBytes(),fileBytes);
        table.put(put);
        return true;

    }
}
