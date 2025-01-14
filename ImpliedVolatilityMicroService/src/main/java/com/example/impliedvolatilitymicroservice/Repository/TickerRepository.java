package com.example.impliedvolatilitymicroservice.Repository;

import com.example.impliedvolatilitymicroservice.Model.SectorDashboardTicker;
import com.example.impliedvolatilitymicroservice.Model.Ticker;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RepositoryRestResource

@Repository
public interface TickerRepository extends CrudRepository<Ticker,Long> {
    Optional<Ticker> findByTickerAndCik(String ticker, String cik);
}
