package ru.sfedu.teamselection.service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.exception.AzureException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService {
    private final RestClient restClient = RestClient.builder()
            .build();

    private final OAuth2AuthorizedClientService authorizedClientService;

    @Getter
    private byte[] placeholder; // картинка-заглушка для аватарки пользователя

    private final UserService userService;

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("static/images/profile_placeholder.png");
            this.placeholder = StreamUtils.copyToByteArray(resource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить плейсхолдер", e);
        }
    }

    public byte[] getAzureUserPhoto(Long id, OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                "azure",
                authentication.getName()
        );
        String accessToken = client.getAccessToken().getTokenValue();
        User user = userService.findByIdOrElseThrow(id);

        return restClient.get()
                //.uri("https://graph.microsoft.com/v1.0/me/photo/$value")
                .uri("https://graph.microsoft.com/v1.0/users/" + user.getAzureId() + "/photo/$value")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    String errorMessage = "Error while retrieving image from Azure: "
                            + response.getStatusCode().value() + " "
                            + response.getStatusText();
                    log.error(errorMessage);
                    throw new AzureException(errorMessage);
                })
                .body(byte[].class);
    }

    public byte[] getPhotoByUrl(String url) {
        return restClient.get()
                //.uri("/me/photo/$value")
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new RuntimeException(
                            "Error while retrieving image from url " + url + ": "
                                    + response.getStatusCode().value() + " "
                                    + response.getStatusText()
                    );
                })
                .body(byte[].class);
    }
}
