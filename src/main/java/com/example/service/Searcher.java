package com.example.service;

import com.example.entity.FileEntity;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenhaorj on 2017/11/15.
 */

@Component
public class Searcher {
    private static String zkHost = "manager.bigdata:2181,master1.bigdata:2181,master2.bigdata:2181/solr";
    private static String collection = "c3";
    private CloudSolrClient client;

    @Autowired
    private HBaseDownloader hBaseDownloader;

    public Searcher() {
        client = new CloudSolrClient(zkHost);
        client.setDefaultCollection(collection);
        client.connect();
    }

    public List<FileEntity> search(String describe) throws IOException, SolrServerException {
        SolrQuery parameters = new SolrQuery();
        String queryString = "text_t:"+describe;
        parameters.set("q", queryString);
        ArrayList<FileEntity> fileEntities = new ArrayList<>();
        QueryResponse response = client.query(parameters);
        SolrDocumentList list = response.getResults();
        for (int i = 0, len = list.size(); i < len; ++i) {
            SolrDocument curDoc = list.get(i);
            String id = curDoc.getFieldValue("id").toString();
            FileEntity fileEntity = hBaseDownloader.getFileEntity(id);
            fileEntities.add(fileEntity);

        }
        return fileEntities;
    }

}
