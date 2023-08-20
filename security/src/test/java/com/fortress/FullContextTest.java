package com.fortress;

import com.fortress.entity.Role;
import com.fortress.entity.User;
import com.fortress.dto.UserDTO;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WithMockUser(username="admin", roles="ADMIN")
@AutoConfigureMockMvc
@AutoConfigureDataMongo
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FullContextTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String userID;
    private UserDTO userDTO;

    @BeforeAll
    void setup() {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Test
    @Order(1)
    void addUser() throws Exception {
        var userDTO = new UserDTO("alik5@cardiff.ac.uk",
                "Kirsten Ali", Role.ADMIN);

        var jsonPayload = objectMapper.writeValueAsString(userDTO);

        performPost(jsonPayload, "/user/add");

        setUserId();
        setUserDTO();
    }

    @Test
    @Order(2)
    void getUserTest() throws Exception {
        getResultActions("/user/get/"+userID)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userID));
    }

    @Test
    @Order(2)
    void getAllUsers() throws Exception {
        MvcResult mvcResult = getMVCResult("/user/getAll");

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<User> users = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        int expectedLength = 1;
        assertEquals(expectedLength, users.size());
    }

    @Test
    @Order(2)
    void findUserByUsername() throws Exception {
        getResultActions("/user/findByUsername/alik5@cardiff.ac.uk")
                .andExpect(jsonPath("$.username")
                        .value("alik5@cardiff.ac.uk"));
    }

    @Test
    @Order(2)
    void adminResetPassword() throws Exception {
        var user = getUserDTO();
        var oldPassword = user.getPassword();

        var jsonPayload = objectMapper.writeValueAsString(user);

        performPost(jsonPayload, "/user/emailTempPassword");

        user = getUserDTO();
        var newPassword = user.getPassword();

        assertNotEquals(oldPassword, newPassword);
    }

    @Test
    @Order(2)
    void changePassword() throws Exception {

    }

    @Test
    @Order(2)
    void updateUser() throws Exception {
        var updatedName = "Kirsten1";
        var updatedUsername = "ikirstenhd@gmail.com";
        var updatedRole = Role.STANDARD;

        var userDTO = new UserDTO(updatedUsername, updatedName, updatedRole);
        userDTO.setId(userID);

        var jsonPayload = objectMapper.writeValueAsString(userDTO);

        performPost(jsonPayload,"/user/update");

        testUpdate(updatedName, updatedUsername, updatedRole);
    }

    @Test
    @Order(3)
    void deleteUser() throws Exception {
        var jsonPayload = objectMapper.writeValueAsString(userDTO);

        performPost(jsonPayload,"/user/delete");

        getResultActions("/user/get/"+userID)
                .andExpect(content().string(""));
    }

    void setUserId() throws Exception {
        MvcResult mvcResult = getMVCResult("/user/findByUsername/alik5@cardiff.ac.uk");

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        User user = objectMapper.readValue(jsonResponse, User.class);

        userID = user.getId();
    }

    void setUserDTO() {
        userDTO = new UserDTO();
        userDTO.setId(userID);
    }

    void testUpdate(String updatedName,
                    String updatedUsername,
                    Role updatedRole) throws Exception {

        getResultActions("/user/get/"+userID)
                .andExpect(jsonPath("$.name").value(updatedName))
                .andExpect(jsonPath("$.username").value(updatedUsername))
                .andExpect(jsonPath("$.role").value(updatedRole.toString()));
    }

    User getUserDTO() throws Exception {
        MvcResult mvcResult = getMVCResult("/user/get/"+userID);

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        return objectMapper.readValue(jsonResponse, User.class);
    }

    MvcResult getMVCResult(String endpoint) throws Exception {
        return getResultActions(endpoint).andReturn();
    }

    ResultActions getResultActions(String endpoint) throws Exception {
        return mvc.perform(get(endpoint))
                .andExpect(status().isOk());
    }

    void performPost(String jsonPayload, String endpoint) throws Exception {
        mvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk());
    }

}
