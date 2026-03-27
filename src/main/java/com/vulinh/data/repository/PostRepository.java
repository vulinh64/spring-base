package com.vulinh.data.repository;

import module java.base;

import com.vulinh.data.dto.projection.PostSearchProjection;
import com.vulinh.data.dto.projection.PrefetchPostProjection;
import com.vulinh.data.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends BaseRepository<Post, UUID> {

  @Query(
      """
      select
      p.id as id, p.title as title,
      p.excerpt as excerpt,
      p.slug as slug,
      p.createdDateTime as createdDateTime,
      p.updatedDateTime as updatedDateTime,
      p.authorId as authorId,
      p.category as category
      from Post p
      """)
  // Cannot fetch tags without combining same post entities, for now
  Page<PrefetchPostProjection> findPrefetchPosts(Pageable pageable);

  Optional<Post> findBySlug(String slug);

  @Query(
      "select p.slug from Post p where p.slug = :baseSlug or p.slug like concat(:baseSlug, '-%')")
  List<String> findSlugsByBaseSlug(String baseSlug);

  @Query(
      "select distinct p.authorId from Post p where p.authorId not in (select a.id from Author a)")
  List<UUID> findAuthorIdsNotInAuthorTable();

  @Query(
      value =
          """
          select p.id as id, p.title as title, p.excerpt as excerpt, p.slug as slug,
                 p.created_date_time as "createdDateTime",
                 p.updated_date_time as "updatedDateTime",
                 p.author_id as "authorId",
                 c.id as "categoryId", c.category_slug as "categorySlug", c.display_name as "displayName"
          from post p
          left join category c on p.category_id = c.id
          where p.search_vector @@ plainto_tsquery('english', :query)
          order by ts_rank(p.search_vector, plainto_tsquery('english', :query)) desc
          """,
      countQuery =
          """
          select count(*)
          from post p
          where p.search_vector @@ plainto_tsquery('english', :query)
          """,
      nativeQuery = true)
  Page<PostSearchProjection> searchPosts(String query, Pageable pageable);

  @Query(
      """
        select
        p.id as id, p.title as title,
        p.excerpt as excerpt,
        p.slug as slug,
        p.createdDateTime as createdDate,
        p.updatedDateTime as updatedDate,
        p.authorId as authorId,
        p.category as category
        from Post p
        where p.category.categorySlug = :categorySlug
        """)
  Page<PrefetchPostProjection> findPostsByCategorySlug(String categorySlug, Pageable pageable);
}
