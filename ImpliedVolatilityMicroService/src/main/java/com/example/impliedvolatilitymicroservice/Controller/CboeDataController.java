package com.example.impliedvolatilitymicroservice.Controller;


import com.example.impliedvolatilitymicroservice.MarketPositioningData.Repository.FinraShortSaleDataRepository;
import com.example.impliedvolatilitymicroservice.PositioningData.Model.Repository.CboeDailySummaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
public class CboeDataController {
    @Autowired
    CboeDailySummaryRepository cboeDailySummaryRepository;
    private Logger logger = LoggerFactory.getLogger(ImpliedVolatilityController.class);

//    @GetMapping
//    public
}
