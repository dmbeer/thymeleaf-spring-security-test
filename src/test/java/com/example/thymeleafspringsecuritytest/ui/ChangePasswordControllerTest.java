package com.example.thymeleafspringsecuritytest.ui;

import com.example.thymeleafspringsecuritytest.ThymeleafSpringSecurityTestApplication;
import com.example.thymeleafspringsecuritytest.security.SpringUserDetailsRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.server.context.SecurityContextServerWebExchangeWebFilter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;

@RunWith(SpringRunner.class)
@Import({ThymeleafAutoConfiguration.class})
@WebFluxTest(controllers = ChangePasswordController.class)
@WithMockUser(username = "test", authorities = {"ROLE_ADMIN"})
@ContextConfiguration(classes = ThymeleafSpringSecurityTestApplication.class)
public class ChangePasswordControllerTest {

    @Autowired
    ApplicationContext context;

    WebTestClient webTestClient;

    @MockBean
    SpringUserDetailsRepository userDetailsRepository;

    @Captor
    private ArgumentCaptor<String> captor;

    @Before
    public void setUp() throws Exception {
        webTestClient = WebTestClient.bindToApplicationContext(context)
                .webFilter(new SecurityContextServerWebExchangeWebFilter())
                .apply(springSecurity())
                .configureClient()
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

        given(userDetailsRepository.updatePassword(any(), any())).willReturn(Mono.empty());

        webTestClient.mutateWith(csrf()).post().uri("/profile/change-password").contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(formData)).exchange().expectStatus().isSeeOther().expectHeader().valueEquals(HttpHeaders.LOCATION, "/profile/change-password");

        verify(userDetailsRepository).updatePassword(captor.capture(), captor.capture());

    }
}