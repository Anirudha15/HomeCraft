package com.homecraft.api.service;

import com.homecraft.api.dto.CreateMeetupDTO;
import com.homecraft.api.entity.Meetup;
import com.homecraft.api.entity.MeetupList;
import com.homecraft.api.repository.MeetupListRepository;
import com.homecraft.api.repository.MeetupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MeetupServiceImpl implements MeetupService {

    private final MeetupRepository meetupRepo;
    private final MeetupListRepository meetupListRepo;

    public MeetupServiceImpl(
            MeetupRepository meetupRepo,
            MeetupListRepository meetupListRepo
    ) {
        this.meetupRepo = meetupRepo;
        this.meetupListRepo = meetupListRepo;
    }

    /* Create Meetup */

    @Override
    @Transactional
    public void createMeetup(
            CreateMeetupDTO dto,
            String role,
            Integer userId,
            String email
    ) {
        Meetup meetup = new Meetup();
        meetup.setTitle(dto.getTitle());
        meetup.setDescription(dto.getDescription());
        meetup.setTopics(String.join(",", dto.getTopics()));
        meetup.setLocation(dto.getLocation());
        meetup.setDateTime(dto.getDateTime());
        meetup.setCreatedByType(role);
        meetup.setCreatedById(userId);
        meetup.setCreatedAt(LocalDateTime.now());

        meetupRepo.save(meetup);

        MeetupList list = new MeetupList();
        list.setMeetup(meetup);
        list.setCreatorType(role);
        list.setCreatorId(userId);
        list.setAttendees(email); // creator auto-added

        meetupListRepo.save(list);
    }

    /* Fetch Meetups */

    @Override
    public List<Map<String, Object>> getAllMeetups(
            String role,
            Integer userId
    ) {
        List<Meetup> all = meetupRepo.findAllByOrderByCreatedAtDesc();

        List<Map<String, Object>> mine = new ArrayList<>();
        List<Map<String, Object>> others = new ArrayList<>();

        for (Meetup m : all) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", m.getId());
            map.put("title", m.getTitle());
            map.put("description", m.getDescription());
            map.put("topics", m.getTopics());
            map.put("location", m.getLocation());
            map.put("dateTime", m.getDateTime());

            boolean createdByMe =
                    m.getCreatedByType().equals(role)
                            && m.getCreatedById().equals(userId);

            map.put("createdByYou", createdByMe);

            if (createdByMe) {
                mine.add(map);
            } else {
                others.add(map);
            }
        }

        mine.addAll(others);
        return mine;
    }

    /* Join Meetups */

    @Override
    @Transactional
    public void joinMeetup(Integer meetupId, String email) {

        MeetupList list = meetupListRepo
                .findByMeetupId(meetupId)
                .orElseThrow();

        Set<String> attendees = new LinkedHashSet<>(
                Arrays.asList(list.getAttendees().split(","))
        );

        attendees.add(email);

        list.setAttendees(String.join(",", attendees));
        meetupListRepo.save(list);
    }
}