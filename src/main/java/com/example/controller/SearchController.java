package com.example.controller;

import com.example.entity.FileEntity;
import com.example.service.Searcher;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * Created by chenhaorj on 2017/11/15.
 */
@RestController
@RequestMapping(value="/search")
public class SearchController {

    @Autowired
    Searcher searcher;

    @RequestMapping(value = "/{des}", method = RequestMethod.GET)
    public List<FileEntity> getList(@PathVariable String des){
        try {
            return searcher.search(des);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        return null;

    }
}
