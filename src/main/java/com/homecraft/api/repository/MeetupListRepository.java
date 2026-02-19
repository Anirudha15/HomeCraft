package com.homecraft.api.repository;

import com.homecraft.api.entity.MeetupList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetupListRepository
        extends JpaRepository<MeetupList, Integer> {

    Optional<MeetupList> findByMeetupId(Integer meetupId);
}