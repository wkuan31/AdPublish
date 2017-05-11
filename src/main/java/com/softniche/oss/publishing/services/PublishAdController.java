package com.softniche.oss.publishing.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.softniche.oss.publishing.model.Ad;

@RestController
public class PublishAdController {

  Log log = LogFactory.getLog(PublishAdController.class);
  
  Map<String, List<Ad>> ads = new ConcurrentHashMap<>();
 
  // Assume Ad Duration is counted by days
  // The partner id id unique
  // Not remove expired ads
  // Assume publishers and consumers in same time zone
  @RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody Ad save(@RequestBody Ad req) {
    // validate the input
    Ad ad = new Ad();
    if (StringUtils.isBlank(req.getPid())) {
      ad.setContent("Missing Partner ID");
    }
    else if (req.getTimeDuration() <= 0) {
      ad.setContent("No Duration available");
    }
    else if (StringUtils.isBlank(req.getContent())) {
      ad.setContent("Missing Ad Content");
    }
    else {
      ad.setPid(req.getPid());
      ad.setTimeDuration(req.getTimeDuration());
      ad.setContent(req.getContent());
      Calendar then = Calendar.getInstance();
      then.setTime(new Date());
      then.add(Calendar.DAY_OF_YEAR, req.getTimeDuration());
      ad.setExpiredDate(then.getTime());
      
      // save the input into storage (memory).
      // if the content is same, overwrite it because of resetting with new date. 
      // Otherwise, add it into a list as new ad
      List<Ad> list = ads.get(req.getPid());
      if (list == null) {
        list = new ArrayList<Ad>();
      }
      boolean found = false;
      for (Ad _ad : list) {
        if (_ad.getContent().equalsIgnoreCase(req.getContent())) {
          _ad.setTimeDuration(req.getTimeDuration());
          Calendar now = Calendar.getInstance();
          now.setTime(new Date());
          now.add(Calendar.DAY_OF_YEAR, req.getTimeDuration());
          _ad.setExpiredDate(now.getTime());
          found = true;
          break;
        }
      }
      if (!found) list.add(ad);
      ads.put(req.getPid(), list);
    }
    return ad; // invalid if no partner id is set
  }
  
  // Return all available ads from all partners
  @RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody List<Ad> allCompaigns() {
    List<Ad> list = new ArrayList<>();
    for (String pid : ads.keySet()) {
      List<Ad> _list = ads.get(pid);
      if (_list != null) {
        Calendar today = Calendar.getInstance();
        Calendar day = Calendar.getInstance();
        today.setTime(new Date());
        for (Ad ad : _list) {
          day.setTime(ad.getExpiredDate());
          if (day.after(today)) {
            list.add(ad);
          }
        }
      }
    }
    return list;
  }
  
  // May return multiple ads because of different contents
  @RequestMapping(value = "/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody List<Ad> getCompaigns(@PathVariable("pid") String pid) {
    List<Ad> list = new ArrayList<>();
    List<Ad> _list = ads.get(pid);
    if (_list != null) {
      Calendar today = Calendar.getInstance();
      Calendar day = Calendar.getInstance();
      today.setTime(new Date());
      for (Ad ad : _list) {
        day.setTime(ad.getExpiredDate());
        if (day.after(today)) {
          list.add(ad);
        }
      }
    }
    return list;
  }
  
}
