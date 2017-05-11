package com.softniche.oss.publishing.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ad  {

  @JsonProperty("partner_id")
  String pid;
  @JsonProperty("duration")
  int timeDuration;
  @JsonProperty("ad_content")
  String content;
  
  @Transient
  Date expiredDate;  // set when saved
  
  public String getPid() {
    return pid;
  }
  
  public void setPid(String pid) {
    this.pid = pid;
  }
  
  public int getTimeDuration() {
    return timeDuration;
  }
  
  public void setTimeDuration(int timeDuration) {
    this.timeDuration = timeDuration;
  }
  
  public String getContent() {
    return content;
  }
  
  public void setContent(String content) {
    this.content = content;
  }

  
  public Date getExpiredDate() {
    return expiredDate;
  }

  
  public void setExpiredDate(Date expiredDate) {
    this.expiredDate = expiredDate;
  }

 
}
