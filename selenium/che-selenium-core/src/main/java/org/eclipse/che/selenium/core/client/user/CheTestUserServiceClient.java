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
import java.io.IOException;
import java.net.URLEncoder;
import org.eclipse.che.api.core.BadRequestException;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
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

  @Override
  public User create(User toCreate, String token, boolean isTemporary)
      throws BadRequestException, UnauthorizedException, ConflictException, ServerException {
    throw new RuntimeException("Operation is not supported");
  }

  @Override
  public User getById(String id) throws NotFoundException, ServerException {
    try {
      return adminRequestFactory
          .fromUrl(apiEndpoint + "user/" + id)
          .useGetMethod()
          .request()
          .asDto(UserDto.class);
    } catch (IOException
        | BadRequestException
        | UnauthorizedException
        | ForbiddenException
        | ConflictException ex) {
      throw new ServerException(ex.getMessage(), ex);
    }
  }

  @Override
  public User findByEmail(String email)
      throws BadRequestException, NotFoundException, ServerException {
    try {
      return adminRequestFactory
          .fromUrl(apiEndpoint + "user/find")
          .useGetMethod()
          .addQueryParam("email", URLEncoder.encode(email, "UTF-8"))
          .request()
          .asDto(UserDto.class);
    } catch (IOException | UnauthorizedException | ForbiddenException | ConflictException ex) {
      throw new ServerException(ex.getMessage(), ex);
    }
  }

  @Override
  public User findByName(String name)
      throws BadRequestException, NotFoundException, ServerException {
    try {
      return adminRequestFactory
          .fromUrl(apiEndpoint + "user/find")
          .useGetMethod()
          .addQueryParam("name", name)
          .request()
          .asDto(UserDto.class);
    } catch (IOException | UnauthorizedException | ForbiddenException | ConflictException ex) {
      throw new ServerException(ex.getMessage(), ex);
    }
  }

  @Override
  public void remove(String id) throws ServerException, ConflictException {
    throw new RuntimeException("Operation is not supported");
  }

  public void removeByEmail(String email) throws Exception {
    throw new RuntimeException("Operation is not supported");
  }
}
