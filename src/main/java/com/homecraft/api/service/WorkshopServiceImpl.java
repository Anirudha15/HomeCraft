package com.homecraft.api.service;

import com.homecraft.api.dto.*;
import com.homecraft.api.entity.Customer;
import com.homecraft.api.entity.Seller;
import com.homecraft.api.entity.Workshop;
import com.homecraft.api.entity.WorkshopCustomer;
import com.homecraft.api.repository.CustomerRepository;
import com.homecraft.api.repository.SellerRepository;
import com.homecraft.api.repository.WorkshopCustomerRepository;
import com.homecraft.api.repository.WorkshopRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkshopServiceImpl implements WorkshopService {

    private final WorkshopRepository workshopRepository;
    private final SellerRepository sellerRepository;
    private final CustomerRepository customerRepository;
    private final WorkshopCustomerRepository workshopCustomerRepository;

    public WorkshopServiceImpl(
            WorkshopRepository workshopRepository,
            SellerRepository sellerRepository,
            CustomerRepository customerRepository,
            WorkshopCustomerRepository workshopCustomerRepository
    ) {
        this.workshopRepository = workshopRepository;
        this.sellerRepository = sellerRepository;
        this.customerRepository = customerRepository;
        this.workshopCustomerRepository = workshopCustomerRepository;
    }

    /* Seller*/

    @Override
    public void createWorkshop(CreateWorkshopDTO dto, String sellerEmail) {

        int sellerId = Integer.parseInt(sellerEmail);
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        Workshop workshop = new Workshop();
        workshop.setSeller(seller);
        workshop.setTitle(dto.getTitle());
        workshop.setDescription(dto.getDescription());
        workshop.setTopics(String.join(",", dto.getTopics()));
        workshop.setLocation(dto.getLocation());
        workshop.setDateTime(dto.getDateTime());
        workshop.setDurationMinutes(dto.getDurationMinutes());

        boolean paid = Boolean.TRUE.equals(dto.getIsPaid());
        workshop.setPaid(paid);

        if (paid && dto.getPrice() != null && dto.getPrice() > 0) {
            workshop.setPrice(dto.getPrice());
        } else {
            workshop.setPrice(null);
        }

        workshop.setStatus("ACTIVE");
        workshop.setCreatedAt(LocalDateTime.now());

        workshopRepository.save(workshop);
    }

    @Override
    public List<Workshop> getWorkshops(String sellerEmail) {

        int sellerId = Integer.parseInt(sellerEmail);

        sellerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        return workshopRepository.findBySellerId(sellerId);
    }

    @Override
    public List<WorkshopParticipantDTO> getWorkshopParticipants(
            int workshopId,
            String sellerPrincipal
    ) {

        int sellerId = Integer.parseInt(sellerPrincipal);

        Workshop workshop = workshopRepository.findById(workshopId)
                .orElseThrow(() -> new RuntimeException("Workshop not found"));

        if (workshop.getSeller().getId() != sellerId) {
            throw new RuntimeException("Unauthorized");
        }

        WorkshopCustomer wc = workshopCustomerRepository
                .findByWorkshopId(workshopId)
                .orElse(null);

        if (wc == null || wc.getCustList() == null || wc.getCustList().isBlank()) {
            return List.of();
        }

        String[] emails = wc.getCustList().split(",");

        List<WorkshopParticipantDTO> result = new ArrayList<>();
        int sno = 1;

        for (String email : emails) {
            Customer c = customerRepository.findByEmail(email.trim()).orElse(null);
            if (c != null) {
                result.add(new WorkshopParticipantDTO(
                        sno,
                        c.getName(),
                        c.getEmail()
                ));
                sno++;
            }
        }

        return result;
    }

    /* Customer*/

    private Customer resolveCustomer(String principalValue) {

        Optional<Customer> byEmail = customerRepository.findByEmail(principalValue);
        if (byEmail.isPresent()) {
            return byEmail.get();
        }

        try {
            int id = Integer.parseInt(principalValue);
            return customerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Customer not found");
        }
    }

    @Override
    public List<Workshop> getAvailableWorkshopsForCustomer(
            String customerPrincipal,
            String topic,
            String locationFilter
    ) {

        Customer customer = resolveCustomer(customerPrincipal);

        Set<Integer> registeredWorkshopIds =
                workshopCustomerRepository
                        .findByCustListContaining(customer.getEmail())
                        .stream()
                        .map(wc -> wc.getWorkshop().getId())
                        .collect(Collectors.toSet());

        return workshopRepository.findAll()
                .stream()
                .filter(w -> "ACTIVE".equals(w.getStatus()))
                .filter(w -> !registeredWorkshopIds.contains(w.getId()))
                .filter(w ->
                        topic == null || topic.isBlank()
                                || w.getTopics().contains(topic)
                )
                .filter(w -> {
                    if (locationFilter == null || locationFilter.equals("ALL"))
                        return true;
                    if (locationFilter.equals("MY"))
                        return w.getLocation().equals(customer.getLocation());
                    return w.getLocation().equals(locationFilter);
                })
                .toList();
    }

    @Override
    public List<Workshop> getRegisteredWorkshopsForCustomer(String customerPrincipal) {

        Customer customer = resolveCustomer(customerPrincipal);

        return workshopCustomerRepository
                .findByCustListContaining(customer.getEmail())
                .stream()
                .map(WorkshopCustomer::getWorkshop)
                .toList();
    }

    @Override
    public void registerCustomerForWorkshop(int workshopId, String customerPrincipal) {

        Customer customer = resolveCustomer(customerPrincipal);

        Workshop workshop = workshopRepository.findById(workshopId)
                .orElseThrow(() -> new RuntimeException("Workshop not found"));

        WorkshopCustomer wc = workshopCustomerRepository
                .findByWorkshopId(workshopId)
                .orElse(null);

        if (wc == null) {
            wc = new WorkshopCustomer();
            wc.setWorkshop(workshop);
            wc.setSeller(workshop.getSeller());
            wc.setCustList(customer.getEmail());
        } else {
            List<String> emails =
                    new ArrayList<>(Arrays.asList(wc.getCustList().split(",")));

            if (emails.contains(customer.getEmail())) {
                throw new RuntimeException("Already registered");
            }

            emails.add(customer.getEmail());
            wc.setCustList(String.join(",", emails));
        }

        workshopCustomerRepository.save(wc);
    }
}