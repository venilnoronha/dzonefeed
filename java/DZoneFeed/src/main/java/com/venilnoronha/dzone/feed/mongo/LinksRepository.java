/*
 * Copyright 2015 Venil Noronha
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.venilnoronha.dzone.feed.mongo;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.venilnoronha.dzone.feed.model.Link;

/**
 * 
 * @author Venil Noronha
 */
public interface LinksRepository extends MongoRepository<Link, String>, PagingAndSortingRepository<Link, String> {

	public Link findByLinkId(Long linkId);

	public Page<Link> findAll(Pageable pageable);

	public Page<Link> findByLinkDateBetween(Date since, Date until, Pageable page);

}