package com.example.impliedvolatilitymicroservice.Repository;

import com.example.impliedvolatilitymicroservice.Model.SectorDashboardTicker;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource

@Repository
public interface SectorDashboardTickerRepository extends CrudRepository<SectorDashboardTicker,Long> {

}
