package com.example.impliedvolatilitymicroservice.AggregateOptions.Repository;

import com.example.impliedvolatilitymicroservice.AggregateOptions.Model.AggregateOptionDataPoint;
import com.example.impliedvolatilitymicroservice.IndexData.Model.TreasuryRate;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

//@RepositoryRestResource

//@Repository
public interface AggregateOptionDataPointsRepository extends ListCrudRepository<AggregateOptionDataPoint,Long> {

    Optional<AggregateOptionDataPoint> findByDateAndTicker(Date date, String ticker);

    List<AggregateOptionDataPoint> findAll();

}