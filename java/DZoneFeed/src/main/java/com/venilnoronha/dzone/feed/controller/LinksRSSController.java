package com.venilnoronha.dzone.feed.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.venilnoronha.dzone.feed.model.Link;
import com.venilnoronha.dzone.feed.mongo.LinksRepository;

@Controller
public class LinksRSSController {

	private static final int PAGE_SIZE = 500;
	
	@Autowired
	private LinksRepository linksRepository;
	
	@RequestMapping(value="/rss/links/{pageNo}", method = RequestMethod.GET)
	public ModelAndView linksRss(@PathVariable() int pageNo) {
		ModelAndView mav = generateModelAndView(pageNo);
		return mav;
	}
	
	@RequestMapping(value="/rss/links", method = RequestMethod.GET)
	public ModelAndView linksRss() {
		ModelAndView mav = generateModelAndView(0);
		return mav;
	}

	private ModelAndView generateModelAndView(int pageNo) {
		List<Link> links = new ArrayList<>();
		Page<Link> linkPage = linksRepository.findAll(new PageRequest(pageNo, PAGE_SIZE, Direction.DESC, "linkDate"));
		Iterator<Link> linksIter = linkPage.iterator();
		while (linksIter.hasNext()) {
			Link link = linksIter.next();
			links.add(link);
		}
		ModelAndView mav = new ModelAndView();
		mav.setView(new LinksRSSView(links));
		return mav;
	}
	
}