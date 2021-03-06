package com.szymon;

import com.szymon.service.TokenRenewService;
import com.szymon.service.TokenService;
import com.szymon.service.UserAuthService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

public class TokenRenewServiceTests {

    @Mock
    private TokenService tokenService;

    @Mock
    private UserAuthService userAuthService;

    @InjectMocks
    private TokenRenewService tokenRenewService = new TokenRenewService();
    private String testSecret = "testSecret";

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(tokenRenewService, "secret", testSecret);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void refreshValidTokenExpirationDate() {
        String stringToken = "token";
        String renewedToken = "renewedToken";

        Mockito.stub(userAuthService.authenticateUserBaseOnToken(stringToken)).toReturn(new ResponseEntity(HttpStatus.OK));
        Mockito.stub(tokenService.updateExpiration(eq(stringToken), eq(testSecret), any())).toReturn(renewedToken);

        ResponseEntity responseEntity = tokenRenewService.renewTokenExpirationDate(stringToken);

        Mockito.verify(userAuthService).authenticateUserBaseOnToken(stringToken);
        Mockito.verify(tokenService).updateExpiration(eq(stringToken), eq(testSecret), any());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(renewedToken, responseEntity.getBody());
    }
}
