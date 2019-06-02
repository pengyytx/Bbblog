package com.bbblog.service;

import com.bbblog.entity.Catalog;
import com.bbblog.entity.User;
import com.bbblog.repository.CatalogReporisity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogServiceImp implements CatalogService {
    @Autowired
    private CatalogReporisity catalogReporisity;

    @Override
    public Catalog saveCatalog(Catalog catalog) {
        //判断重复
        List<Catalog> list = catalogReporisity.findByUserAndName(catalog.getUser(),catalog.getName());
        if(list != null && list.size()>0){
            throw new IllegalArgumentException("该分类已经存在");
        }
        return catalogReporisity.save(catalog);
    }

    @Override
    public void removeCatalog(Long id) {

        catalogReporisity.deleteById(id);
    }

    @Override
    public Catalog getCatalogById(Long id) {
        return catalogReporisity.getOne(id);
    }

    @Override
    public List<Catalog> listCatalogs(User user) {
        return catalogReporisity.findByUser(user);
    }
}
