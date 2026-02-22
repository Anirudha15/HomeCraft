package com.homecraft.api.service;

import com.homecraft.api.dto.*;
import com.homecraft.api.entity.Workshop;

import java.util.List;

public interface WorkshopService {

    // Seller
    void createWorkshop(CreateWorkshopDTO dto, String sellerEmail);
    List<Workshop> getWorkshops(String sellerEmail);

    // Customer
    List<Workshop> getAvailableWorkshopsForCustomer(
            String customerEmail,
            String topic,
            String locationFilter
    );

    List<Workshop> getRegisteredWorkshopsForCustomer(String customerEmail);

    void registerCustomerForWorkshop(int workshopId, String customerEmail);

    List<WorkshopParticipantDTO> getWorkshopParticipants(
            int workshopId,
            String sellerPrincipal
    );
}