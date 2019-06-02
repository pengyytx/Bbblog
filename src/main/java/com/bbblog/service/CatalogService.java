package com.bbblog.service;

import com.bbblog.entity.Catalog;
import com.bbblog.entity.User;

import java.util.List;

public interface CatalogService {
    Catalog saveCatalog(Catalog catalog);

    void removeCatalog(Long id);

    Catalog getCatalogById(Long id);

    List<Catalog> listCatalogs(User user);
}
