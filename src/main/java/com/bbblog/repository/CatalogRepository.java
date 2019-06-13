package com.bbblog.repository;

import com.bbblog.entity.Catalog;
import com.bbblog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatalogRepository extends JpaRepository<Catalog,Long> {
    List<Catalog> findByUser(User user);

    List<Catalog> findByUserAndName(User user, String name);
}
