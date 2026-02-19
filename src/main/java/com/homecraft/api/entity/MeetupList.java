package com.homecraft.api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "meetup_list")
public class MeetupList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "meetup_id", nullable = false)
    private Meetup meetup;

    @Column(name = "creator_type", nullable = false, length = 20)
    private String creatorType; // CUSTOMER / SELLER

    @Column(name = "creator_id", nullable = false)
    private Integer creatorId;

    @Column(name = "attendees", nullable = false, length = 2000)
    private String attendees; // comma-separated emails


    public Integer getId() {
        return id;
    }

    public Meetup getMeetup() {
        return meetup;
    }

    public void setMeetup(Meetup meetup) {
        this.meetup = meetup;
    }

    public String getCreatorType() {
        return creatorType;
    }

    public void setCreatorType(String creatorType) {
        this.creatorType = creatorType;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public String getAttendees() {
        return attendees;
    }

    public void setAttendees(String attendees) {
        this.attendees = attendees;
    }
}