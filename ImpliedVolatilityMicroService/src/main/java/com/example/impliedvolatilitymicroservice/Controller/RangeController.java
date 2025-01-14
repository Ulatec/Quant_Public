package com.example.impliedvolatilitymicroservice.Controller;

import com.example.impliedvolatilitymicroservice.Controller.ReturnObjects.BarRanges;
import com.example.impliedvolatilitymicroservice.Model.Bar;
import com.example.impliedvolatilitymicroservice.Repository.ImpliedVolatilityRepository;
import com.example.impliedvolatilitymicroservice.Repository.TreasuryRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RangeController {


    @Autowired
    ImpliedVolatilityRepository impliedVolatilityRepository;
    @Autowired
    TreasuryRateRepository treasuryRateRepository;
    private Logger logger = LoggerFactory.getLogger(ImpliedVolatilityController.class);

    @GetMapping("/range/{ticker}/")
    public BarRanges getRange(@PathVariable String ticker){
        return null;
    }




    public Bar generateRangeForBar(){
        return null;
    }
}
