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
package org.eclipse.che.api.deploy;

import com.google.inject.AbstractModule;
import javax.sql.DataSource;
import org.eclipse.che.api.user.server.jpa.JpaPreferenceDao;
import org.eclipse.che.api.user.server.jpa.JpaUserDao;
import org.eclipse.che.api.user.server.spi.PreferenceDao;
import org.eclipse.che.api.user.server.spi.UserDao;
import org.eclipse.che.inject.DynaModule;
import org.eclipse.che.multiuser.api.permission.server.PermissionChecker;
import org.eclipse.che.multiuser.api.permission.server.PermissionCheckerImpl;
import org.eclipse.che.multiuser.keycloak.server.deploy.KeycloakModule;
import org.eclipse.che.multiuser.organization.api.OrganizationApiModule;
import org.eclipse.che.multiuser.organization.api.OrganizationJpaModule;
import org.eclipse.che.multiuser.resource.api.ResourceModule;
import org.eclipse.che.security.PBKDF2PasswordEncryptor;
import org.eclipse.che.security.PasswordEncryptor;

@DynaModule
public class MultiUserCheWsMasterModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(DataSource.class).toProvider(org.eclipse.che.core.db.JndiDataSourceProvider.class);
    install(new org.eclipse.che.multiuser.api.permission.server.jpa.SystemPermissionsJpaModule());
    install(new org.eclipse.che.multiuser.api.permission.server.PermissionsModule());
    install(
        new org.eclipse.che.multiuser.permission.workspace.server.WorkspaceApiPermissionsModule());
    install(
        new org.eclipse.che.multiuser.permission.workspace.server.jpa
            .MultiuserWorkspaceJpaModule());

    //Permission filters
    bind(org.eclipse.che.multiuser.permission.system.SystemServicePermissionsFilter.class);
    bind(org.eclipse.che.multiuser.permission.user.UserProfileServicePermissionsFilter.class);
    bind(org.eclipse.che.multiuser.permission.user.UserServicePermissionsFilter.class);
    bind(org.eclipse.che.multiuser.permission.factory.FactoryPermissionsFilter.class);
    bind(org.eclipse.che.plugin.activity.ActivityPermissionsFilter.class);
    bind(
        org.eclipse.che.multiuser.permission.resource.filters.ResourceUsageServicePermissionsFilter
            .class);
    bind(
        org.eclipse.che.multiuser.permission.resource.filters
            .FreeResourcesLimitServicePermissionsFilter.class);

    install(new ResourceModule());
    install(new OrganizationApiModule());
    install(new OrganizationJpaModule());

    install(new KeycloakModule());

    //User and profile - use profile from keycloak and other stuff is JPA
    bind(PasswordEncryptor.class).to(PBKDF2PasswordEncryptor.class);
    bind(UserDao.class).to(JpaUserDao.class);
    bind(PreferenceDao.class).to(JpaPreferenceDao.class);
    bind(PermissionChecker.class).to(PermissionCheckerImpl.class);
  }
}
