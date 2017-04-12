package com.keeper.repo;

/*
 * Created by @GoodforGod on 9.04.2017.
 */

import com.keeper.model.dao.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Default Comment
 */
@Repository
//@Qualifier(value = "zoneRepository")
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    List<Zone> findByCity(@Param("city") String city);

    List<Zone> findByCountry(@Param("country") String country);
}