package com.drunkenlion.alcoholfriday.domain.admin.customerservice.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminCustomerServiceImpl {
}
