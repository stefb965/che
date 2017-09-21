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
package org.eclipse.che.selenium.core;

import com.google.inject.AbstractModule;
import org.eclipse.che.api.core.rest.HttpJsonRequestFactory;
import org.eclipse.che.selenium.core.client.KeycloakTestAuthServiceClient;
import org.eclipse.che.selenium.core.client.TestAuthServiceClient;
import org.eclipse.che.selenium.core.client.TestUserServiceClient;
import org.eclipse.che.selenium.core.client.user.CheUserTestUserServiceClient;
import org.eclipse.che.selenium.core.requestfactory.TestDefaultUserHttpJsonRequestFactory;
import org.eclipse.che.selenium.core.requestfactory.TestHttpJsonRequestFactory;
import org.eclipse.che.selenium.core.user.AdminTestUser;
import org.eclipse.che.selenium.core.user.CheAdminTestUser;
import org.eclipse.che.selenium.core.user.CheDefaultTestUser;
import org.eclipse.che.selenium.core.user.CheTestUserNamespaceResolver;
import org.eclipse.che.selenium.core.user.TestUser;
import org.eclipse.che.selenium.core.user.TestUserNamespaceResolver;
import org.eclipse.che.selenium.core.workspace.CheTestWorkspaceUrlResolver;
import org.eclipse.che.selenium.core.workspace.TestWorkspaceUrlResolver;

/** @author Anton Korneta */
public class CheSeleniumMultiUserModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(TestAuthServiceClient.class).to(KeycloakTestAuthServiceClient.class);

    bind(TestUser.class).to(CheDefaultTestUser.class);
    bind(AdminTestUser.class).to(CheAdminTestUser.class);

    bind(TestUserServiceClient.class).to(CheUserTestUserServiceClient.class);
    bind(HttpJsonRequestFactory.class).to(TestHttpJsonRequestFactory.class);
    bind(TestHttpJsonRequestFactory.class).to(TestDefaultUserHttpJsonRequestFactory.class);

    bind(TestWorkspaceUrlResolver.class).to(CheTestWorkspaceUrlResolver.class);
    bind(TestUserNamespaceResolver.class).to(CheTestUserNamespaceResolver.class);
  }
}
