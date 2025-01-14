package com.example.frontendlistener.Repository;

import com.example.frontendlistener.Model.DatabaseBar;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@RepositoryRestResource

@Repository
public interface DatabaseBarRepository extends CrudRepository<DatabaseBar,Long> {
    List<DatabaseBar> findAllByTickerAndDate(String ticker, Date date);
    List<DatabaseBar> findAllByTickerAndDateAfterAndDateBefore(String ticker, Date after, Date before);
}
