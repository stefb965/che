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
package org.eclipse.che.selenium.dashboard.organization;

import static org.testng.Assert.assertEquals;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.eclipse.che.commons.lang.NameGenerator;
import org.eclipse.che.multiuser.organization.shared.dto.OrganizationDto;
import org.eclipse.che.selenium.core.client.OnpremTestOrganizationServiceClient;
import org.eclipse.che.selenium.core.user.AdminTestUser;
import org.eclipse.che.selenium.core.user.DefaultTestUser;
import org.eclipse.che.selenium.pageobject.dashboard.ConfirmDialog;
import org.eclipse.che.selenium.pageobject.dashboard.Dashboard;
import org.eclipse.che.selenium.pageobject.dashboard.NavigationBar;
import org.eclipse.che.selenium.pageobject.dashboard.organization.OrganizationListPage;
import org.eclipse.che.selenium.pageobject.dashboard.organization.OrganizationPage;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test validates organization deletion.
 *
 * @author Ann Shumilova
 */
public class DeleteOrganizationTest {
  private OrganizationDto parentOrganization;
  private OrganizationDto childOrganization;

  @Inject private OrganizationListPage organizationListPage;
  @Inject private OrganizationPage organizationPage;
  @Inject private NavigationBar navigationBar;
  @Inject private ConfirmDialog confirmDialog;
  @Inject private Dashboard dashboard;

  @Inject
  @Named("admin")
  private OnpremTestOrganizationServiceClient organizationServiceClient;

  @Inject private DefaultTestUser testUser;
  @Inject private AdminTestUser adminTestUser;

  @BeforeClass
  public void setUp() throws Exception {
    parentOrganization =
        organizationServiceClient.createOrganization(NameGenerator.generate("organization", 5));
    childOrganization =
        organizationServiceClient.createOrganization(NameGenerator.generate("organization", 5));

    organizationServiceClient.addOrganizationAdmin(parentOrganization.getId(), testUser.getId());
    organizationServiceClient.addOrganizationAdmin(childOrganization.getId(), testUser.getId());

    dashboard.open(testUser.getName(), testUser.getPassword());
  }

  @AfterClass
  public void tearDown() throws Exception {
    organizationServiceClient.deleteOrganizationById(childOrganization.getId());
    organizationServiceClient.deleteOrganizationById(parentOrganization.getId());
  }

  @Test(priority = 1)
  public void testSubOrganizationDelete() {
    navigationBar.waitNavigationBar();
    navigationBar.clickOnMenu(NavigationBar.MenuItem.ORGANIZATIONS);
    organizationListPage.waitForOrganizationsToolbar();
    organizationListPage.waitForOrganizationsList();

    organizationListPage.clickOnOrganization(childOrganization.getQualifiedName());

    organizationPage.waitOrganizationName(childOrganization.getName());
    organizationPage.clickDeleteOrganizationButton();
    confirmDialog.waitOpened();

    assertEquals(confirmDialog.getTitle(), "Delete organization");
    assertEquals(
        confirmDialog.getMessage(),
        "Would you like to delete organization '" + childOrganization.getName() + "'?");
    assertEquals(confirmDialog.getConfirmButtonTitle(), "Delete");

    confirmDialog.clickConfirm();
    confirmDialog.waitClosed();

    organizationListPage.waitForOrganizationsList();
    organizationListPage.waitForOrganizationIsRemoved(childOrganization.getQualifiedName());
    assertEquals(navigationBar.getMenuCounterValue(NavigationBar.MenuItem.ORGANIZATIONS), "1");
    assertEquals(organizationListPage.getOrganizationListItemCount(), 1);
  }

  @Test(priority = 2)
  public void testParentOrganizationDeletion() {
    navigationBar.waitNavigationBar();
    navigationBar.clickOnMenu(NavigationBar.MenuItem.ORGANIZATIONS);
    organizationListPage.waitForOrganizationsToolbar();
    organizationListPage.waitForOrganizationsList();

    organizationListPage.clickOnOrganization(parentOrganization.getName());

    organizationPage.waitOrganizationName(parentOrganization.getName());
    organizationPage.clickDeleteOrganizationButton();
    confirmDialog.waitOpened();

    assertEquals(confirmDialog.getTitle(), "Delete organization");
    assertEquals(
        confirmDialog.getMessage(),
        "Would you like to delete organization '" + parentOrganization.getName() + "'?");
    assertEquals(confirmDialog.getConfirmButtonTitle(), "Delete");

    confirmDialog.clickConfirm();
    confirmDialog.waitClosed();

    organizationListPage.waitForOrganizationsEmptyList();
    assertEquals(navigationBar.getMenuCounterValue(NavigationBar.MenuItem.ORGANIZATIONS), "0");
  }
}
