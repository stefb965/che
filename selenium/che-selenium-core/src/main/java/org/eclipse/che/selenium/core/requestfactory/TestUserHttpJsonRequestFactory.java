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
import com.google.inject.name.Named;
import org.eclipse.che.selenium.core.client.TestAuthServiceClient;

/** @author Dmytro Nochevnov */
@Singleton
public class TestUserHttpJsonRequestFactory extends TestHttpJsonRequestFactory {

  private final String username;
  private final String password;
  private final TestAuthServiceClient authServiceClient;

  @Inject
  public TestUserHttpJsonRequestFactory(
      @Named("che.user.email") String username,
      @Named("che.user.password") String password,
      TestAuthServiceClient authServiceClient) {
    this.username = username;
    this.password = password;
    this.authServiceClient = authServiceClient;
  }

  protected String getAuthToken() {
    try {
      return authServiceClient.login(username, password);
    } catch (Exception ex) {
      throw new RuntimeException(format("Failed to access get token for user '%s'", username));
    }
  }
}
