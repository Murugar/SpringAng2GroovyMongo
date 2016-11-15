package com.iqmsoft.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iqmsoft.dto.PageParams;
import com.iqmsoft.dto.PostDTO;
import com.iqmsoft.service.MicropostService;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    @SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    private final MicropostService micropostService;

    @Autowired
    public FeedController(MicropostService micropostService) {
        this.micropostService = micropostService;
    }

    @RequestMapping
    public List<PostDTO> feed(PageParams pageParams) {
        return micropostService.findAsFeed(pageParams);
    }

}
