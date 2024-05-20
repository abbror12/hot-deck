package com.example.hotdesk.desk;

import com.example.hotdesk.desk.dto.DeskCreateDto;
import com.example.hotdesk.desk.dto.DeskResponseDto;
import com.example.hotdesk.desk.entity.Accessories;
import com.example.hotdesk.desk.entity.Desk;
import com.example.hotdesk.office.dto.AddressDto;
import com.example.hotdesk.office.dto.OfficeCreateDTo;
import com.example.hotdesk.office.dto.OfficeResponseDto;
import com.example.hotdesk.room.dto.RoomCreateDto;
import com.example.hotdesk.room.dto.RoomResponseDto;
import com.example.hotdesk.room.entity.RoomType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.util.List;
import java.util.Optional;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class DeskControllerTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    DeskRepository deskRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer=new PostgreSQLContainer<>("postgres:16.0")
            .withUsername("postgres")
            .withPassword("postgres");

    @Test
    @Order(1)
    void createDesk() {
        AddressDto addressDto =
                new AddressDto("Uzbekistan", "Tashkent", "Alisher Navoiy", "4");
        OfficeCreateDTo officeCreateDTo = new OfficeCreateDTo("Pdp", addressDto);
        testRestTemplate.postForEntity("/office",officeCreateDTo, OfficeResponseDto.class);

        RoomCreateDto roomCreateDto = new RoomCreateDto(1, "2", RoomType.GAME_ROOM, 1);
        testRestTemplate.postForEntity("/room", roomCreateDto, RoomResponseDto.class);
        DeskCreateDto deskCreateDto=new DeskCreateDto(1, List.of(Accessories.ETHERNET,Accessories.PLANT));
        ResponseEntity<RoomResponseDto> responseEntity = testRestTemplate
                .postForEntity("/desk", deskCreateDto, RoomResponseDto.class);
        RoomResponseDto responseDto = responseEntity.getBody();
        Assertions.assertEquals(responseEntity.getStatusCode(),HttpStatus.CREATED);
        Assertions.assertNotNull(responseDto);
        Assertions.assertNotNull(responseDto.getId());


    }

    @Test
    @Order(2)
    void get() {

        ResponseEntity<DeskResponseDto> forEntity = testRestTemplate.getForEntity("/desk/1", DeskResponseDto.class);
        DeskResponseDto responseDto = forEntity.getBody();
        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(forEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseDto.getId(),1);
        Assertions.assertEquals(responseDto.getRoomId(),1);
        Assertions.assertEquals(responseDto.getAccessories(),List.of(Accessories.ETHERNET,Accessories.PLANT));
        System.out.println("responseDto = " + responseDto);
    }


    @Test
    @Order(3)
    void delete() {

        Desk desk = deskRepository.findAll().stream().findAny().get();
        testRestTemplate.delete("/user/%s".formatted(desk.getId()));
        Optional<Desk> byId = deskRepository.findById(desk.getId());
        Assertions.assertTrue(!byId.isEmpty());
    }
}