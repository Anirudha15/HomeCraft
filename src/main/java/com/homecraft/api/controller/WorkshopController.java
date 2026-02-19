package com.homecraft.api.controller;

import com.homecraft.api.dto.*;
import com.homecraft.api.entity.Workshop;
import com.homecraft.api.service.WorkshopService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class WorkshopController {

    private final WorkshopService workshopService;

    public WorkshopController(WorkshopService workshopService) {
        this.workshopService = workshopService;
    }

    /*Seller */

    @PostMapping("/api/seller/workshops")
    public String createWorkshop(
            @RequestBody CreateWorkshopDTO dto,
            Principal principal
    ) {
        workshopService.createWorkshop(dto, principal.getName());
        return "Workshop created successfully";
    }

    @GetMapping("/api/seller/workshops")
    public List<Workshop> getMyWorkshops(Principal principal) {
        return workshopService.getWorkshops(principal.getName());
    }

    @GetMapping("/api/seller/workshops/{id}/participants")
    public List<WorkshopParticipantDTO> getWorkshopParticipants(
            @PathVariable int id,
            Principal principal
    ) {
        return workshopService.getWorkshopParticipants(id, principal.getName());
    }

    /* Customer */

    @GetMapping("/api/customer/workshops/available")
    public List<Workshop> getAvailableWorkshops(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false, defaultValue = "ALL") String location,
            Principal principal
    ) {
        return workshopService.getAvailableWorkshopsForCustomer(
                principal.getName(),
                topic,
                location
        );
    }

    @GetMapping("/api/customer/workshops/registered")
    public List<Workshop> getRegisteredWorkshops(Principal principal) {
        return workshopService.getRegisteredWorkshopsForCustomer(principal.getName());
    }

    @PostMapping("/api/customer/workshops/register")
    public String registerWorkshop(
            @RequestParam int workshopId,
            Principal principal
    ) {
        workshopService.registerCustomerForWorkshop(workshopId, principal.getName());
        return "Registered successfully";
    }
}