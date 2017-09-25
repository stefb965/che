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
package org.eclipse.che.selenium.core.client;

import org.eclipse.che.api.core.BadRequestException;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.UnauthorizedException;
import org.eclipse.che.api.core.model.user.User;

/** @author Mihail Kuznyetsov */
public interface TestUserServiceClient {

  User create(User toCreate, String token, boolean isTemporary)
      throws BadRequestException, UnauthorizedException, ConflictException, ServerException;

  User getById(String id) throws NotFoundException, ServerException;

  User findByEmail(String email) throws NotFoundException, ServerException, BadRequestException;

  User findByName(String name) throws NotFoundException, ServerException, BadRequestException;

  void remove(String id) throws ServerException, ConflictException;
}
