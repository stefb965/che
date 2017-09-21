package org.eclipse.che.workspace.infrastructure.openshift.project;

/**
 * Helps to create {@link OpenShiftNamespace} instances.
 *
 * @author Anton Korneta
 */
public interface OpenShiftSpaceFactory {
  OpenShiftNamespace createNamespace(String name, String workspaceId);

  OpenShiftNamespace createNamespace(String name);
}
