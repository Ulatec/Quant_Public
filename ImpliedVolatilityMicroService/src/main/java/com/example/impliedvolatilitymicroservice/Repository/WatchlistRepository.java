package com.example.impliedvolatilitymicroservice.Repository;

import com.example.impliedvolatilitymicroservice.IndexData.Model.TreasuryRate;
import com.example.impliedvolatilitymicroservice.Model.Watchlist;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource

@Repository
public interface WatchlistRepository extends ListCrudRepository<Watchlist,Long> {
}
