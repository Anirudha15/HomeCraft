package com.homecraft.api.controller;

import com.homecraft.api.dto.CreateMeetupDTO;
import com.homecraft.api.service.MeetupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meetups")
public class MeetupController {

    private final MeetupService meetupService;

    public MeetupController(MeetupService meetupService) {
        this.meetupService = meetupService;
    }

    private Integer userId(Authentication auth) {
        return Integer.parseInt(auth.getName());
    }

    private String role(Authentication auth) {
        return auth.getAuthorities().iterator().next().getAuthority()
                .replace("ROLE_", "");
    }

    private String email(Authentication auth) {
        return (String) auth.getCredentials();
    }

    /* Create */

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody CreateMeetupDTO dto,
            Authentication auth
    ) {
        meetupService.createMeetup(
                dto,
                role(auth),
                userId(auth),
                email(auth)
        );
        return ResponseEntity.ok().build();
    }

    /* List*/

    @GetMapping
    public ResponseEntity<?> list(Authentication auth) {
        return ResponseEntity.ok(
                meetupService.getAllMeetups(
                        role(auth),
                        userId(auth)
                )
        );
    }

    /* Join */

    @PostMapping("/join")
    public ResponseEntity<?> join(
            @RequestParam Integer meetupId,
            Authentication auth
    ) {
        meetupService.joinMeetup(meetupId, email(auth));
        return ResponseEntity.ok().build();
    }
}