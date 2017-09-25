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
import com.google.inject.Singleton;
import org.eclipse.che.api.core.BadRequestException;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.model.user.User;
import org.eclipse.che.selenium.core.client.TestUserServiceClient;

/** @author Anton Korneta */
@Singleton
public class CheUserTestUserServiceClient implements TestUserServiceClient {

  private final TestUserServiceClientImpl delegate;

  @Inject
  public CheUserTestUserServiceClient(TestUserServiceClientImpl delegate) {
    this.delegate = delegate;
  }

  @Override
  public void create(String name, String email, String password)
      throws BadRequestException, ConflictException, ServerException {}

  @Override
  public User getById(String id) throws NotFoundException, ServerException {
    return delegate.getById(id);
  }

  @Override
  public User findByEmail(String email)
      throws NotFoundException, ServerException, BadRequestException {
    return delegate.findByEmail(email);
  }

  @Override
  public User findByName(String name)
      throws NotFoundException, ServerException, BadRequestException {
    return delegate.findByName(name);
  }

  @Override
  public void remove(String id) throws ServerException, ConflictException {}
}
