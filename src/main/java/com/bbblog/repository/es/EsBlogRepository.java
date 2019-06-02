package com.bbblog.repository.es;

import com.bbblog.entity.es.EsBlog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsBlogRepository extends ElasticsearchRepository<EsBlog,String> {

    /**
     * 模糊查询（去重）
     * @param title
     * @param summary
     * @param content
     * @param tags
     * @param pageable
     * @return
     */
    Page<EsBlog> findDistinctEsBlogByTitleContainingOrSummaryContainingOrContentContainingOrTagsContaining(String title, String summary, String content, String tags, Pageable pageable);
    EsBlog findByBlogId(Long blogId);
}
