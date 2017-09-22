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
package org.eclipse.che.selenium.core.client.user;

import com.google.inject.Inject;
import java.net.URLEncoder;
import org.eclipse.che.api.core.model.user.User;
import org.eclipse.che.api.user.shared.dto.UserDto;
import org.eclipse.che.selenium.core.client.TestUserServiceClient;
import org.eclipse.che.selenium.core.provider.TestApiEndpointUrlProvider;
import org.eclipse.che.selenium.core.requestfactory.TestAdminHttpJsonRequestFactory;

/** @author Musienko Maxim */
public class CheTestUserServiceClient implements TestUserServiceClient {
  private final String apiEndpoint;
  private final TestAdminHttpJsonRequestFactory adminRequestFactory;

  @Inject
  public CheTestUserServiceClient(
      TestApiEndpointUrlProvider apiEndpointProvider,
      TestAdminHttpJsonRequestFactory requestFactory) {
    this.apiEndpoint = apiEndpointProvider.get().toString();
    this.adminRequestFactory = requestFactory;
  }

  public User getByEmail(String email) throws Exception {
    return adminRequestFactory
        .fromUrl(apiEndpoint + "user/find")
        .useGetMethod()
        .addQueryParam("email", URLEncoder.encode(email, "UTF-8"))
        .request()
        .asDto(UserDto.class);
  }

  public void deleteByEmail(String email) throws Exception {
    throw new RuntimeException("Operation is not supported");
  }

  public User create(String name, String email, String password) throws Exception {
    throw new RuntimeException("Operation is not supported");
  }
}
