package io.es.web;

import io.es.entity.User;
import io.es.repository.RepositoriesInitializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private RepositoriesInitializer initializer;

  private final User user = User.builder().username("guest").password("q").build();

  @Before
  public void initialize() {
    initializer.initialize();
  }

  @Test
  public void login() throws Exception {
    mvc.perform(formLogin().user(user.getUsername()).password(user.getPassword())).
      andExpect(authenticated());
  }

  @Test
  public void loginFailed() throws Exception {
    mvc.perform(formLogin().user(user.getUsername()).password("<incorrect>")).
      andExpect(unauthenticated());
  }

  @Test
  public void accessProtectedResourcesAuthenticated() throws Exception {
    mvc.perform(get("/api/resources").session(getSession())).
      andExpect(status().isOk());
  }

  @Test
  public void accessProtectedResourcesUnauthenticated() throws Exception {
    mvc.perform(get("/api/resources")).
      andExpect(status().is4xxClientError());
  }

  private MockHttpSession getSession() throws Exception {
    return (MockHttpSession)
      mvc.perform(formLogin().user(user.getUsername()).password(user.getPassword())).
        andExpect(authenticated()).
        andReturn().getRequest().getSession(false);
  }

}
