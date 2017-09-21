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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.eclipse.che.selenium.core.client.CheTestUserServiceClient;
import org.eclipse.che.selenium.core.client.TestAuthServiceClient;
import org.eclipse.che.selenium.core.client.TestWorkspaceServiceClient;

/**
 * Default {@link TestUser} that will be created before all tests and will be deleted after them.
 * All tests share the same default user.
 *
 * <p>To have move users per tests see {@link InjectTestUser}.
 *
 * @author Anatolii Bazko
 */
@Singleton
public class DefaultTestUser implements TestUser {

  private static final String CHE_USER_NAME = "che.user.name";
  private static final String CHE_USER_PASSWORD = "che.user.password";

  private final TestUser testUser;

  @Inject
  public DefaultTestUser(
      CheTestUserServiceClient testUserServiceClient,
      TestWorkspaceServiceClient workspaceServiceClient,
      TestAuthServiceClient authServiceClient,
      @Named(CHE_USER_NAME) String name,
      @Named(CHE_USER_PASSWORD) String password)
      throws Exception {
    this.testUser =
        new TestUserImpl(
            name, password, testUserServiceClient, workspaceServiceClient, authServiceClient);
  }

  @Override
  public String getEmail() {
    return testUser.getEmail();
  }

  @Override
  public String getPassword() {
    return testUser.getPassword();
  }

  @Override
  public String getAuthToken() {
    return testUser.getAuthToken();
  }

  @Override
  public String getName() {
    return testUser.getName();
  }

  @Override
  public String getId() {
    return testUser.getId();
  }

  @Override
  public void delete() {
    testUser.delete();
  }
}
