package com.example.impliedvolatilitymicroservice.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SectorDashboardTicker {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    String ticker;


}
