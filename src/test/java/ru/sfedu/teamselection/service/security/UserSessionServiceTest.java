package ru.sfedu.teamselection.service.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Arrays;
import java.util.List;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSessionServiceTest {

    @Mock
    private SessionRegistry sessionRegistry;

    @Mock
    private OidcUser oidcUser1;

    @Mock
    private OidcUser oidcUser2;

    @Mock
    private Object nonOidcPrincipal;

    @Mock
    private SessionInformation session1;

    @Mock
    private SessionInformation session2;

    @Mock
    private SessionInformation session3;

    private UserSessionService userSessionService;

    @BeforeEach
    void setUp() {
        // Since we're using @Lazy, we need to manually set the dependency
        userSessionService = new UserSessionService();
        ReflectionTestUtils.setField(userSessionService, "sessionRegistry", sessionRegistry);
    }

    @Test
    void updateUserAuthorities_shouldExpireSessionsForMatchingEmail() {
        // Given
        String targetEmail = "user@example.com";

        List<Object> principals = List.of(oidcUser1, oidcUser2, nonOidcPrincipal);

        when(sessionRegistry.getAllPrincipals()).thenReturn(principals);
        when(oidcUser1.getEmail()).thenReturn("other@example.com");
        when(oidcUser2.getEmail()).thenReturn(targetEmail);

        List<SessionInformation> userSessions = List.of(session1, session2);
        when(sessionRegistry.getAllSessions(oidcUser2, false)).thenReturn(userSessions);

        // When
        userSessionService.updateUserAuthorities(targetEmail);

        // Then
        verify(session1).expireNow();
        verify(session2).expireNow();
        verify(sessionRegistry).getAllPrincipals();
        verify(sessionRegistry).getAllSessions(oidcUser2, false);

        // Verify that non-OidcUser principal and non-matching emails are ignored
        verify(sessionRegistry, never()).getAllSessions(oidcUser1, false);
        verify(sessionRegistry, never()).getAllSessions(nonOidcPrincipal, false);
    }

    @Test
    void updateUserAuthorities_shouldNotExpireSessionsWhenNoMatchingEmail() {
        // Given
        String targetEmail = "user@example.com";

        List<Object> principals = Arrays.asList(oidcUser1, oidcUser2);

        when(sessionRegistry.getAllPrincipals()).thenReturn(principals);
        when(oidcUser1.getEmail()).thenReturn("other1@example.com");
        when(oidcUser2.getEmail()).thenReturn("other2@example.com");

        // When
        userSessionService.updateUserAuthorities(targetEmail);

        // Then
        verify(sessionRegistry, never()).getAllSessions(any(), anyBoolean());
        verify(session1, never()).expireNow();
        verify(session2, never()).expireNow();
        verify(session3, never()).expireNow();
    }

    @Test
    void updateUserAuthorities_shouldHandleEmptyPrincipalsList() {
        // Given
        String targetEmail = "user@example.com";

        List<Object> principals = List.of();

        when(sessionRegistry.getAllPrincipals()).thenReturn(principals);

        // When
        userSessionService.updateUserAuthorities(targetEmail);

        // Then
        verify(sessionRegistry).getAllPrincipals();
        verifyNoMoreInteractions(sessionRegistry);
    }

    @Test
    void updateUserAuthorities_shouldHandleMultipleSessionsForSameUser() {
        // Given
        String targetEmail = "user@example.com";

        List<Object> principals = List.of(oidcUser1);

        when(sessionRegistry.getAllPrincipals()).thenReturn(principals);
        when(oidcUser1.getEmail()).thenReturn(targetEmail);

        List<SessionInformation> userSessions = Arrays.asList(session1, session2, session3);
        when(sessionRegistry.getAllSessions(oidcUser1, false)).thenReturn(userSessions);

        // When
        userSessionService.updateUserAuthorities(targetEmail);

        // Then
        verify(session1).expireNow();
        verify(session2).expireNow();
        verify(session3).expireNow();
        verify(sessionRegistry).getAllSessions(oidcUser1, false);
    }

    @Test
    void updateUserAuthorities_shouldHandleEmptySessionInformationList() {
        // Given
        String targetEmail = "user@example.com";

        List<Object> principals = List.of(oidcUser1);

        when(sessionRegistry.getAllPrincipals()).thenReturn(principals);
        when(oidcUser1.getEmail()).thenReturn(targetEmail);
        when(sessionRegistry.getAllSessions(oidcUser1, false)).thenReturn(List.of());

        // When
        userSessionService.updateUserAuthorities(targetEmail);

        // Then
        verify(sessionRegistry).getAllSessions(oidcUser1, false);
        // No sessions to expire, so no expireNow() calls
    }
}
