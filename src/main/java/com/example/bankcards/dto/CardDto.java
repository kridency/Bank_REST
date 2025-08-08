package com.example.bankcards.dto;

import com.example.bankcards.entity.User;

import java.time.YearMonth;
import java.util.UUID;

public class CardDto {
    private UUID id;
    private String number;
    private User owner;
    private YearMonth expiryDate;

}
