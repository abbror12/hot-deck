package com.example.hotdesk.office.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.stereotype.Service;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponseDto {
    private Integer id;
    @NotBlank
    private String country;
    @NotBlank
    private String city;
    @NotBlank
    private String street;
    @NotBlank
    private String buildingNumber;
}
