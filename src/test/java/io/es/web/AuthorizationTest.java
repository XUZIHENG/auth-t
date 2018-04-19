package io.es.web;

import io.es.entity.User;
import io.es.repository.RepositoriesInitializer;
import lombok.val;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthorizationTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private RepositoriesInitializer initializer;

  private final User guest = User.builder().username("guest-1-1").password("q").build();

  private final User admin = User.builder().username("admin-1-1").password("q").build();

  @Before
  public void initialize() {
    initializer.initialize();
  }

  @Test
  public void accessProtectedResourcesHasPermission() throws Exception {
    mvc.perform(get("/api/resources/p/r").session(getSession(guest))).
      andExpect(status().isOk());
  }

  @Test
  public void accessProtectedResourcesHasNoPermission() throws Exception {
    mvc.perform(get("/api/resources/p/w").session(getSession(guest))).
      andExpect(status().isForbidden());
  }

  @Test
  public void accessResourcesWithinDistrict() throws Exception {
    val request = get("/api/resources/" + initializer.getResources().get("resource-1-1").getId());
    mvc.perform(request.session(getSession(guest))).
      andExpect(status().isOk());
  }

  @Test
  public void accessResourcesNotWithinDistrict() throws Exception {
    val request = get("/api/resources/" + initializer.getResources().get("resource-1-2").getId());
    mvc.perform(request.session(getSession(guest))).
      andExpect(status().isForbidden());
  }

  @Test
  public void modifyResourcesWithinDistrict() throws Exception {
    val request = delete("/api/resources/" + initializer.getResources().get("resource-1-1").getId());
    mvc.perform(request.with(csrf()).session(getSession(admin))).
      andExpect(status().isNoContent());
  }

  @Test
  public void modifyResourcesNotWithinDistrict() throws Exception {
    val request = delete("/api/resources/" + initializer.getResources().get("resource-1-2").getId());
    mvc.perform(request.with(csrf()).session(getSession(admin))).
      andExpect(status().isForbidden());
  }

  private MockHttpSession getSession(User user) throws Exception {
    return (MockHttpSession)
      mvc.perform(formLogin().user(user.getUsername()).password(user.getPassword())).
        andExpect(authenticated()).
        andReturn().getRequest().getSession(false);
  }

}
