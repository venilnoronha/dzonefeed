package com.venilnoronha.dzone.feed.mongo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.venilnoronha.dzone.feed.model.Link;

public interface LinksRepository extends MongoRepository<Link, String>, PagingAndSortingRepository<Link, String> {

	public Page<Link> findAll(Pageable pageable);
	public Link findByLinkId(Long linkId);

}