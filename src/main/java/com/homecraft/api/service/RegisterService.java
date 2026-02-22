package com.homecraft.api.service;

import com.homecraft.api.dto.RegisterCustomerDTO;
import com.homecraft.api.dto.RegisterSellerDTO;

public interface RegisterService {

    void registerCustomer(RegisterCustomerDTO dto) throws Exception;

    void registerSeller(RegisterSellerDTO dto) throws Exception;
}