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
package org.eclipse.che.workspace.infrastructure.openshift.project;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.openshift.api.model.Project;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.client.OpenShiftClient;
import org.eclipse.che.api.workspace.server.spi.InfrastructureException;
import org.eclipse.che.workspace.infrastructure.openshift.OpenShiftClientFactory;

/**
 * Defines an internal API for managing subset of objects inside {@link Project} instance.
 *
 * @author Sergii Leshchenko
 */
public class OpenShiftNamespace {

  public static final String CHE_WORKSPACE_LABEL = "CHE_WORKSPACE_ID";

  private final OpenShiftPods pods;
  private final OpenShiftServices services;
  private final OpenShiftRoutes routes;
  private final OpenShiftPersistentVolumeClaims pvcs;

  @Inject
  public OpenShiftNamespace(
      OpenShiftClientFactory clientFactory, @Assisted String namespace, @Assisted String workspaceId)
      throws InfrastructureException {
    this.pods = new OpenShiftPods(namespace, workspaceId, clientFactory);
    this.services = new OpenShiftServices(namespace, workspaceId, clientFactory);
    this.routes = new OpenShiftRoutes(namespace, workspaceId, clientFactory);
    this.pvcs = new OpenShiftPersistentVolumeClaims(namespace, workspaceId, clientFactory);

    try (OpenShiftClient client = clientFactory.create()) {
      if (get(namespace, client) == null) {
        create(namespace, client);
      }
    }
  }

  @Inject
  public OpenShiftNamespace(
      OpenShiftClientFactory clientFactory, @Assisted String name) {
    this.pods = new OpenShiftPods(name, name, clientFactory);
    this.services = new OpenShiftServices(name, name, clientFactory);
    this.routes = new OpenShiftRoutes(name, name, clientFactory);
    this.pvcs = new OpenShiftPersistentVolumeClaims(name, name, clientFactory);
  }

  /** Returns object for managing {@link Pod} instances inside project. */
  public OpenShiftPods pods() {
    return pods;
  }

  /** Returns object for managing {@link Service} instances inside project. */
  public OpenShiftServices services() {
    return services;
  }

  /** Returns object for managing {@link Route} instances inside project. */
  public OpenShiftRoutes routes() {
    return routes;
  }

  /** Returns object for managing {@link PersistentVolumeClaim} instances inside project. */
  public OpenShiftPersistentVolumeClaims persistentVolumeClaims() {
    return pvcs;
  }

  /** Removes all object except persistent volume claim inside project. */
  public void cleanUp() throws InfrastructureException {
    pods.stopWatch();
    pods.delete();
    services.delete();
    routes.delete();
  }

  private void create(String projectName, OpenShiftClient client) throws InfrastructureException {
    try {
      client
          .projectrequests()
          .createNew()
          .withNewMetadata()
          .withName(projectName)
          .endMetadata()
          .done();
    } catch (KubernetesClientException e) {
      throw new InfrastructureException(e.getMessage(), e);
    }
  }

  private Project get(String projectName, OpenShiftClient client) throws InfrastructureException {
    try {
      return client.projects().withName(projectName).get();
    } catch (KubernetesClientException e) {
      if (e.getCode() == 403) {
        // project is foreign or doesn't exist
        return null;
      } else {
        throw new InfrastructureException(e.getMessage(), e);
      }
    }
  }
}
