package com.app2.engine.controller;

import com.app2.engine.service.LitigationUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/litigationUpdate")
public class LitigationUpdateController {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LitigationUpdateService litigationUpdateService;

    @GetMapping("/bkc")
    public void bkc(){
        litigationUpdateService.bkc();
    }

    @GetMapping("/bko")
    public void bko(){
        litigationUpdateService.bko();
    }

    @GetMapping("/cva")
    public void cva(){
        litigationUpdateService.cva();
    }

    @GetMapping("/cvc")
    public void cvc(){
        litigationUpdateService.cvc();
    }

    @GetMapping("/cvo")
    public void cvo(){
        litigationUpdateService.cvo();
    }
}
