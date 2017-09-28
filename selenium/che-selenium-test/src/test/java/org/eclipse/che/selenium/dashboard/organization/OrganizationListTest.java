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
import static org.testng.Assert.assertTrue;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.che.commons.lang.NameGenerator;
import org.eclipse.che.multiuser.organization.shared.dto.OrganizationDto;
import org.eclipse.che.selenium.core.client.OnpremTestOrganizationServiceClient;
import org.eclipse.che.selenium.core.user.AdminTestUser;
import org.eclipse.che.selenium.pageobject.dashboard.Dashboard;
import org.eclipse.che.selenium.pageobject.dashboard.NavigationBar;
import org.eclipse.che.selenium.pageobject.dashboard.organization.OrganizationListPage;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test validates the main UI elements on the organization list page.
 *
 * @author Ann Shumilova
 */
public class OrganizationListTest {
  private List<OrganizationDto> organizations;

  @Inject private OrganizationListPage organizationListPage;
  @Inject private NavigationBar navigationBar;
  @Inject private Dashboard dashboard;

  @Inject
  @Named("admin")
  private OnpremTestOrganizationServiceClient organizationServiceClient;

  @Inject private AdminTestUser adminTestUser;

  private OrganizationDto organization;

  @BeforeClass
  public void setUp() throws Exception {
    dashboard.open(adminTestUser.getName(), adminTestUser.getPassword());
    organization =
        organizationServiceClient.createOrganization(NameGenerator.generate("organization", 5));
    organizations = organizationServiceClient.getOrganizations();
  }

  @AfterClass
  public void tearDown() throws Exception {
    organizationServiceClient.deleteOrganizationById(organization.getId());
  }

  @Test
  public void testOrganizationListComponents() {
    navigationBar.waitNavigationBar();
    int organizationsCount = organizations.size();
    navigationBar.clickOnMenu(NavigationBar.MenuItem.ORGANIZATIONS);
    organizationListPage.waitForOrganizationsToolbar();
    organizationListPage.waitForOrganizationsList();

    assertEquals(
        navigationBar.getMenuCounterValue(NavigationBar.MenuItem.ORGANIZATIONS),
        String.valueOf(organizationsCount));
    assertEquals(organizationListPage.getOrganizationsToolbarTitle(), "Organizations");
    assertEquals(
        navigationBar.getMenuCounterValue(NavigationBar.MenuItem.ORGANIZATIONS),
        String.valueOf(organizationsCount));
    assertEquals(organizationListPage.getOrganizationListItemCount(), organizationsCount);
    assertTrue(organizationListPage.isAddOrganizationButtonVisible());
    assertTrue(organizationListPage.isSearchInputVisible());
    //Check all headers are present:
    ArrayList<String> headers = organizationListPage.getOrganizationListHeaders();
    assertTrue(headers.contains(OrganizationListPage.OrganizationListHeader.NAME.getTitle()));
    assertTrue(headers.contains(OrganizationListPage.OrganizationListHeader.MEMBERS.getTitle()));
    assertTrue(headers.contains(OrganizationListPage.OrganizationListHeader.TOTAL_RAM.getTitle()));
    assertTrue(
        headers.contains(OrganizationListPage.OrganizationListHeader.AVAILABLE_RAM.getTitle()));
    assertTrue(
        headers.contains(OrganizationListPage.OrganizationListHeader.SUB_ORGANIZATIONS.getTitle()));
    assertTrue(headers.contains(OrganizationListPage.OrganizationListHeader.ACTIONS.getTitle()));
  }
}
