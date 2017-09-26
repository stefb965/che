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
package org.eclipse.che.selenium.core.requestfactory;

import static java.lang.String.format;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.eclipse.che.selenium.core.client.TestAuthServiceClient;
import org.eclipse.che.selenium.core.user.TestUser;

/** @author Dmytro Nochevnov */
@Singleton
public class TestDefaultUserHttpJsonRequestFactory extends TestHttpJsonRequestFactory {

  private final TestUser testUser;
  private final TestAuthServiceClient authServiceClient;

  @Inject
  public TestDefaultUserHttpJsonRequestFactory(
      TestAuthServiceClient authServiceClient, TestUser testUser) {
    this.testUser = testUser;
    this.authServiceClient = authServiceClient;
  }

  @Override
  protected String getAuthToken() {
    try {
      return authServiceClient.login(testUser.getEmail(), testUser.getPassword());
    } catch (Exception ex) {
      throw new RuntimeException(
          format("Failed to access get token for user '%s'", testUser.getName()));
    }
  }
}
