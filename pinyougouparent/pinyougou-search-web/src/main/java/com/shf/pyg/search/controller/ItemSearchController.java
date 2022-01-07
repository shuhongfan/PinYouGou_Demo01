package com.shf.pyg.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shf.pyg.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/itemsearch")
public class ItemSearchController {
    @Reference
    private ItemSearchService itemSearchService;

    @RequestMapping("/search")
    public Map<String,Object> searchMap(@RequestBody Map searchMap){
        return itemSearchService.search(searchMap);
    }
}
