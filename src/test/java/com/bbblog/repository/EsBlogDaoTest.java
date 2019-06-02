//package com.bbblog.repository;
//
//import com.bbblog.entity.es.EsBlog;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import static org.junit.Assert.*;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class EsBlogDaoTest {
//
//    @Autowired
//    private EsBlogDao esBlogDao;
//    @Before
//    public void initDaoData(){
//        esBlogDao.deleteAll();
//        esBlogDao.save(new EsBlog("登鹳雀楼","王之涣","白日依山尽"));
//        esBlogDao.save(new EsBlog("相思","王维 ","红豆生南国"));
//        esBlogDao.save(new EsBlog("静夜思","李白","床前明月光"));
//    }
//
//    @Test
//    public void findDistinctEsBlogByTitleContainingOrSummaryContainingOrContentContaining() {
//        Pageable pageable = new PageRequest(0,20);
//        String title = "思";
//        String summary = "王";
//        String content = "红";
//        Page<EsBlog> page = esBlogDao.findDistinctEsBlogByTitleContainingOrSummaryContainingOrContentContaining(title,summary,content);
//    }
//}