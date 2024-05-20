package com.example.hotdesk.office;

import com.example.hotdesk.office.dto.*;

import com.example.hotdesk.office.entity.Office;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class OfficeControllerTest {
    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    OfficeRepository officeRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer=new PostgreSQLContainer<>("postgres:16.0")
            .withUsername("postgres")
            .withPassword("postgres");

    @Test
    @Order(1)
    void createOffice() {
        AddressDto addressDto =
                new AddressDto("Uzbekistan", "Tashkent", "Alisher Navoiy", "4");
        OfficeCreateDTo officeCreateDTo = new OfficeCreateDTo("Pdp", addressDto);
        ResponseEntity<OfficeResponseDto> response = testRestTemplate
                .postForEntity("/office",officeCreateDTo, OfficeResponseDto.class);
        OfficeResponseDto responseDto = response.getBody();
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(responseDto.getName(), officeCreateDTo.getName());

        AddressResponseDto getAddress = responseDto.getAddress();
        Assertions.assertNotNull(responseDto.getAddress());
        Assertions.assertEquals(getAddress.getCountry(),addressDto.getCountry());
        Assertions.assertEquals(getAddress.getCity(),addressDto.getCity());
        Assertions.assertEquals(getAddress.getStreet(),addressDto.getStreet());
        Assertions.assertEquals(getAddress.getBuildingNumber(),addressDto.getBuildingNumber());
        System.out.println("responseDto = " + responseDto.getId());
    }



    @Test
    @Order(2)
    void get() {
        ResponseEntity<OfficeResponseDto> forEntity = testRestTemplate.getForEntity("/office/1", OfficeResponseDto.class);
        OfficeResponseDto responseDto = forEntity.getBody();
        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(forEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseDto.getId(),1);
        Assertions.assertEquals(responseDto.getName(),"Pdp");
        Assertions.assertEquals(responseDto.getAddress().getCountry(),"Uzbekistan");
        Assertions.assertEquals(responseDto.getAddress().getCity(),"Tashkent");
        Assertions.assertEquals(responseDto.getAddress().getStreet(),"Alisher Navoiy");
        Assertions.assertEquals(responseDto.getAddress().getBuildingNumber(),"4");
        System.out.println("responseDto = " + responseDto);
    }

    @Test
    @Order(3)
    void update() {
        OfficeUpdateDto updateDto = new OfficeUpdateDto("Pdp Academy", new AddressDto("Uzbekistan", "Tashkent", "Alisher Navoiy", "5"));
        HttpEntity<OfficeUpdateDto> officeUpdateDto = new HttpEntity<>(updateDto);
        ResponseEntity<OfficeResponseDto> exchange = testRestTemplate
                .exchange("/office/1", HttpMethod.PUT, officeUpdateDto , new ParameterizedTypeReference<>() {});
        OfficeResponseDto responseDto = exchange.getBody();
        /*System.out.println("exchange.getStatusCode() = " + exchange.getStatusCode());
        System.out.println("exchange.getBody() = " + exchange.getBody());*/
        Assertions.assertEquals(exchange.getStatusCode(), HttpStatus.OK);
        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(responseDto.getName(), updateDto.getName());

        AddressResponseDto getAddress = responseDto.getAddress();
        Assertions.assertNotNull(responseDto.getAddress());
        Assertions.assertEquals(getAddress.getCountry(),responseDto.getAddress().getCountry());
        Assertions.assertEquals(getAddress.getCity(),responseDto.getAddress().getCity());
        Assertions.assertEquals(getAddress.getStreet(),responseDto.getAddress().getStreet());
        Assertions.assertEquals(getAddress.getBuildingNumber(),responseDto.getAddress().getBuildingNumber());
        System.out.println("responseDto = " + responseDto.getId());

    }

    @Test
    @Order(4)
    void delete() {
        Office office = officeRepository.findAll().stream().findAny().get();
        testRestTemplate.delete("/user/%s".formatted(office.getId()));
        Optional<Office> optionalUser =officeRepository.findById(office.getId());
        Assertions.assertTrue(optionalUser.isEmpty());
    }
}