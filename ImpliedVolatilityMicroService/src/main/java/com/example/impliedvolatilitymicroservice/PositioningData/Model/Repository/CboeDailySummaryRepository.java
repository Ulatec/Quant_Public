package com.example.impliedvolatilitymicroservice.PositioningData.Model.Repository;

import com.example.impliedvolatilitymicroservice.MarketPositioningData.Model.FinraShortSaleDataPoint;
import com.example.impliedvolatilitymicroservice.PositioningData.Model.CboeDailySummary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource

@Repository
public interface CboeDailySummaryRepository extends CrudRepository<CboeDailySummary, Long> {
    Optional<CboeDailySummary> findCboeDailySummaryByDate(Date date);

}
