package com.example.impliedvolatilitymicroservice.Repository;

import com.example.impliedvolatilitymicroservice.IndexData.Model.TreasuryRate;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource

@Repository
public interface TreasuryRateRepository extends ListCrudRepository<TreasuryRate,Long> {

    Optional<TreasuryRate> findByDate(Date date);

    List<TreasuryRate> findAll();

}
