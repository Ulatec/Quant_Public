package com.example.impliedvolatilitymicroservice.Repository;

import com.example.impliedvolatilitymicroservice.Model.Watchlist;
import com.example.impliedvolatilitymicroservice.Range.Utils.Model.RangeSummary;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RepositoryRestResource

@Repository
public interface RangeSummaryRepository extends ListCrudRepository<RangeSummary,Long> {
    Optional<RangeSummary> findByTickerString(String tickerString);
}
