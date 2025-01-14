package com.example.impliedvolatilitymicroservice.MarketPositioningData.Repository;

import com.example.impliedvolatilitymicroservice.IndexData.Model.DollarRate;
import com.example.impliedvolatilitymicroservice.MarketPositioningData.Model.FinraShortSaleDataPoint;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
@RepositoryRestResource

@Repository
public interface FinraShortSaleDataRepository extends ListCrudRepository<FinraShortSaleDataPoint,Long> {

    Optional<FinraShortSaleDataPoint> findFinraShortSaleDataPointByTickerAndDate(String ticker, Date date);

    List<FinraShortSaleDataPoint> findFinraShortSaleDataPointByTickerOrderByDate(String ticker);
}
