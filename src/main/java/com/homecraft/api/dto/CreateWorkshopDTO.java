package com.homecraft.api.dto;

import java.util.List;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateWorkshopDTO {

    private String title;
    private String description;
    private List<String> topics;
    private String location;
    private LocalDateTime dateTime;
    private int durationMinutes;
    @JsonProperty("isPaid")
    private Boolean isPaid;
    private Double price;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getTopics() {
        return topics;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }
    public Double getPrice() {
        return price;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
}