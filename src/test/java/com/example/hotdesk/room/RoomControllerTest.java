package com.example.hotdesk.room;

import com.example.hotdesk.office.dto.AddressDto;
import com.example.hotdesk.office.dto.OfficeCreateDTo;
import com.example.hotdesk.office.dto.OfficeResponseDto;
import com.example.hotdesk.room.dto.RoomCreateDto;
import com.example.hotdesk.room.dto.RoomResponseDto;
import com.example.hotdesk.room.entity.Room;
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

import java.util.Optional;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class RoomControllerTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    RoomRepository roomRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer=new PostgreSQLContainer<>("postgres:16.0")
            .withUsername("postgres")
            .withPassword("postgres");
    @Order(1)
    @Test
    void createRoom() {

        AddressDto addressDto =
                new AddressDto("Uzbekistan", "Tashkent", "Alisher Navoiy", "4");
        OfficeCreateDTo officeCreateDTo = new OfficeCreateDTo("Pdp", addressDto);
        testRestTemplate.postForEntity("/office",officeCreateDTo, OfficeResponseDto.class);


        RoomCreateDto roomCreateDto = new RoomCreateDto(1, "2", RoomType.GAME_ROOM, 1);
        ResponseEntity<RoomResponseDto> responseEntity = testRestTemplate.postForEntity("/room", roomCreateDto, RoomResponseDto.class);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);
        RoomResponseDto body = responseEntity.getBody();
        Assertions.assertNotNull(body);

    }

    @Test
    void getAllRoom() {
    }

    @Test
    void getRoom() {
        ResponseEntity<RoomResponseDto> forEntity = testRestTemplate.getForEntity("/room/1", RoomResponseDto.class);
        RoomResponseDto responseDto = forEntity.getBody();
        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(forEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseDto.getId(),1);
        Assertions.assertEquals(responseDto.getNumber(),"2");
        Assertions.assertEquals(responseDto.getRoomType(),RoomType.GAME_ROOM);
        Assertions.assertEquals(responseDto.getFloorNumber(),1);
        Assertions.assertEquals(responseDto.getOfficeId(),1);

        System.out.println("responseDto = " + responseDto);
    }

    @Test
    void updateRoom() {
    }



    @Test
    void delete() {

        Room room = roomRepository.findAll().stream().findAny().get();
        testRestTemplate.delete("/user/%s".formatted(room.getId()));
        Optional<Room> byId = roomRepository.findById(room.getId());
        Assertions.assertTrue(!byId.isEmpty());
    }
}