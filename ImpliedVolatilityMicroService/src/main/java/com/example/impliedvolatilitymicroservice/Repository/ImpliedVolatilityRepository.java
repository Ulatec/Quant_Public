package com.example.impliedvolatilitymicroservice.Repository;


import com.example.impliedvolatilitymicroservice.Model.ImpliedVolatility;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource

@Repository
public interface ImpliedVolatilityRepository extends CrudRepository<ImpliedVolatility,Long> {

    Optional<ImpliedVolatility> findByTickerAndDaysVolAndDate(String ticker, int daysVol, Date date);
    List<ImpliedVolatility> findByTickerAndDaysVol(String ticker, int daysVol);
}
