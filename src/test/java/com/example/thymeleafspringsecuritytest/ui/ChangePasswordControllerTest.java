package com.example.thymeleafspringsecuritytest.ui;

import com.example.thymeleafspringsecuritytest.ThymeleafSpringSecurityTestApplication;
import com.example.thymeleafspringsecuritytest.security.SpringUserDetailsRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.security.web.server.context.SecurityContextServerWebExchangeWebFilter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;

@RunWith(SpringRunner.class)
@Import({ThymeleafAutoConfiguration.class})
@ContextConfiguration(classes = ThymeleafSpringSecurityTestApplication.class)
public class ChangePasswordControllerTest {

    @Autowired
    ApplicationContext context;

    WebTestClient webTestClient;

    @MockBean
    SpringUserDetailsRepository userDetailsRepository;

    @Before
    public void setUp() throws Exception {
        webTestClient = WebTestClient.bindToController(new ChangePasswordController(userDetailsRepository))
                .webFilter(new SecurityContextServerWebExchangeWebFilter())
                .apply(springSecurity())
                .configureClient()
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Test
    public void getChangePasswordPage() {
        EntityExchangeResult<String> result = webTestClient
                .get().uri("/profile/change-password")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).returnResult();

        assertThat(result.getResponseBody(), stringContainsInOrder(Arrays.asList("<title>Change Password</title>",
                "<input type=\"password\" class=\"form-control\" id=\"password1\" name=\"password1\">")));
    }

    @Test
    public void changePasswordSubmit() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.put("password1", Collections.singletonList("password"));
        formData.put("password2", Collections.singletonList("password"));

//        given(userDetailsRepository.updatePassword(any(), any())).willReturn(Mono.empty());

        webTestClient.mutateWith(csrf()).post().uri("/profile/change-password").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData)).exchange().expectStatus().isSeeOther().expectHeader().valueEquals(HttpHeaders.LOCATION, "/page/1");

//        verify(userDetailsRepository).updatePassword(captor.capture(), captor.capture());
        doNothing().when(userDetailsRepository).updatePassword(any(), any());
    }
}