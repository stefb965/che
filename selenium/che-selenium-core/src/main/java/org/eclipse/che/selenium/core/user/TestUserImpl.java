/*
 * Copyright (c) 2012-2017 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.selenium.core.user;

import static java.lang.String.format;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PreDestroy;
import org.eclipse.che.commons.lang.NameGenerator;
import org.eclipse.che.selenium.core.client.TestAuthServiceClient;
import org.eclipse.che.selenium.core.client.TestWorkspaceServiceClient;
import org.eclipse.che.selenium.core.client.user.CheTestUserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Anatolii Bazko */
public class TestUserImpl implements TestUser {
  private static final Logger LOG = LoggerFactory.getLogger(TestUserImpl.class);

  private final String email;
  private final String password;
  private final String name;
  private final String id;
  private final String authToken;

  private final CheTestUserServiceClient userServiceClient;
  private final TestWorkspaceServiceClient workspaceServiceClient;

  @Inject
  public TestUserImpl(
      CheTestUserServiceClient userServiceClient,
      TestWorkspaceServiceClient workspaceServiceClient,
      TestAuthServiceClient authServiceClient)
      throws Exception {
    this(
        userServiceClient,
        workspaceServiceClient,
        authServiceClient,
        NameGenerator.generate("user", 6) + "@some.mail");
  }

  /** To instantiate user with specific e-mail. */
  @AssistedInject
  public TestUserImpl(
      CheTestUserServiceClient userServiceClient,
      TestWorkspaceServiceClient workspaceServiceClient,
      TestAuthServiceClient authServiceClient,
      @Assisted("email") String email)
      throws Exception {
    this(
        userServiceClient,
        workspaceServiceClient,
        authServiceClient,
        email,
        NameGenerator.generate("Pwd1", 6));
  }

  /** To instantiate user with specific e-mail. */
  @AssistedInject
  public TestUserImpl(
      CheTestUserServiceClient userServiceClient,
      TestWorkspaceServiceClient workspaceServiceClient,
      TestAuthServiceClient authServiceClient,
      @Assisted("email") String email,
      @Assisted("password") String password)
      throws Exception {
    this.userServiceClient = userServiceClient;
    this.workspaceServiceClient = workspaceServiceClient;
    this.email = email;
    this.password = password;
    this.name = email.split("@")[0];
    this.authToken = authServiceClient.login(name, password);
    this.id = userServiceClient.getByEmail(email).getId();
    LOG.info("User name='{}', password '{}', id='{}' has been created", name, password, id);
  }

  @Override
  public String getEmail() {
    return email;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getAuthToken() {
    return authToken;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  @PreDestroy
  public void delete() {
    List<String> workspaces = new ArrayList<>();
    try {
      workspaces = workspaceServiceClient.getAll();
    } catch (Exception e) {
      LOG.error("Failed to get all workspaces.", e);
    }

    for (String workspace : workspaces) {
      try {
        workspaceServiceClient.delete(workspace, name);
      } catch (Exception e) {
        LOG.error(
            format("User name='%s' failed to remove workspace name='%s'", workspace, name), e);
      }
    }

    try {
      userServiceClient.deleteByEmail(email);
      LOG.info("User name='{}', id='{}' removed", name, id);
    } catch (Exception e) {
      LOG.error(format("Failed to remove user email='%s', id='%s'", email, id), e);
    }
  }

  @Override
  public String toString() {
    return format(
        "%s{name=%s, email=%s, password=%s}",
        this.getClass().getSimpleName(), this.getName(), this.getEmail(), getPassword());
  }
}
