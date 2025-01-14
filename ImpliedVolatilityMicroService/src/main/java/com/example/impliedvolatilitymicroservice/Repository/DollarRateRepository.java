package com.example.impliedvolatilitymicroservice.Repository;

import com.example.impliedvolatilitymicroservice.IndexData.Model.DollarRate;
import com.example.impliedvolatilitymicroservice.IndexData.Model.TreasuryRate;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource

@Repository
public interface DollarRateRepository extends ListCrudRepository<DollarRate,Long> {

    Optional<DollarRate> findByDate(Date date);
    Optional<DollarRate> findByDataDate(Date date);
    List<DollarRate> findAll();

}
