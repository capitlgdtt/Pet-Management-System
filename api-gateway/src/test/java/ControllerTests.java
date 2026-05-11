
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lab5.apiGateway.MainApplication;
import org.lab5.apiGateway.Security.Model.SignInRequest;
import org.lab5.apiGateway.Security.Model.SignUpRequest;
import org.lab5.apiGateway.Security.Service.AuthenticationService;
import org.lab5.common.Dto.CatDto;
import org.lab5.common.Dto.OwnerDto;
import org.lab5.common.Repository.OwnerRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = MainApplication.class)
@AutoConfigureMockMvc
@Transactional
@ExtendWith(SpringExtension.class)
public class ControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationService jwtAuthenticationService;

    private String adminToken;
    @Autowired
    private OwnerRepository ownerRepository;

    @BeforeEach
    public void setUp() throws Exception {
        SignInRequest adminRequest = new SignInRequest();
        adminRequest.setUsername("base-admin");
        adminRequest.setPassword("base-password");

        String response = mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        adminToken = response.replace("{\"token\":\"", "").replace("\"}", "");
    }

    @Test
    public void testSignUp() throws Exception {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("newUser");
        signUpRequest.setPassword("password");

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateOwnerAsAdmin() throws Exception {
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setUsername("newAdmin");
        ownerDto.setPassword("newAdminPassword");

        mockMvc.perform(post("/owners")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerDto)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testSignInAsUser() throws Exception {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setUsername("user1");
        signInRequest.setPassword("password");

        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    public void testDeleteCatAsOwner() throws Exception {
        Long catId = 1L;

        mockMvc.perform(delete("/cats/" + catId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testCreateCatAsUser() throws Exception {
        Long ownerId =  ownerRepository.findOwnerByUsername("newName").get().getId();

        CatDto catDto = new CatDto();
        catDto.setName("Tom");
        catDto.setBreed("Siamese");
        catDto.setColor("Gray");
        catDto.setOwnerId(ownerId);

        mockMvc.perform(post("/cats")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(catDto)))
                .andExpect(status().isOk());
    }
}
