package com.homecraft.api.service;

import com.homecraft.api.dto.CreateMeetupDTO;

import java.util.List;
import java.util.Map;

public interface MeetupService {

    void createMeetup(
            CreateMeetupDTO dto,
            String role,
            Integer userId,
            String email
    );

    List<Map<String, Object>> getAllMeetups(
            String role,
            Integer userId
    );

    void joinMeetup(Integer meetupId, String email);
}