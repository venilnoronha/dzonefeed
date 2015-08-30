package com.venilnoronha.dzone.feed.mongo;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.venilnoronha.dzone.feed.model.Link;

public interface LinksRepository extends MongoRepository<Link, String>, PagingAndSortingRepository<Link, String> {

	public Link findByLinkId(Long linkId);

	public Page<Link> findAll(Pageable pageable);

	public Page<Link> findByLinkDateBetween(Date since, Date until, Pageable page);

}