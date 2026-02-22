package com.homecraft.api.repository;

import com.homecraft.api.entity.Meetup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetupRepository extends JpaRepository<Meetup, Integer> {

    List<Meetup> findAllByOrderByCreatedAtDesc();

    List<Meetup> findByCreatedByTypeAndCreatedById(
            String createdByType,
            Integer createdById
    );
}