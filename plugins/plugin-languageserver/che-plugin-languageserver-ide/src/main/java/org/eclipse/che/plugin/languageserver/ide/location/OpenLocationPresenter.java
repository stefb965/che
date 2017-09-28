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
package org.eclipse.che.plugin.languageserver.ide.location;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.util.List;
import org.eclipse.che.api.languageserver.shared.model.ExtendedLocation;
import org.eclipse.che.api.languageserver.shared.util.Constants;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.api.editor.text.TextPosition;
import org.eclipse.che.ide.api.editor.text.TextRange;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.notification.StatusNotification;
import org.eclipse.che.ide.api.parts.PartStackType;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.api.parts.base.BasePresenter;
import org.eclipse.che.plugin.languageserver.ide.LanguageServerResources;
import org.eclipse.che.plugin.languageserver.ide.service.TextDocumentServiceClient;
import org.eclipse.che.plugin.languageserver.ide.util.OpenFileInEditorHelper;
import org.eclipse.lsp4j.Range;
import org.vectomatic.dom.svg.ui.SVGResource;

/** @author Evgen Vidolob */
public class OpenLocationPresenter extends BasePresenter
    implements OpenLocationView.ActionDelegate {

  private final LanguageServerResources resources;
  private final OpenLocationView view;
  private final WorkspaceAgent workspaceAgent;
  private final OpenFileInEditorHelper helper;
  private final NotificationManager notificationManager;
  private final String title;
  private final TextDocumentServiceClient textDocumentService;

  @Inject
  public OpenLocationPresenter(
      LanguageServerResources resources,
      OpenLocationView view,
      WorkspaceAgent workspaceAgent,
      OpenFileInEditorHelper helper,
      NotificationManager notificationManager,
      TextDocumentServiceClient textDocumentService,
      @Assisted String title) {
    this.resources = resources;
    this.view = view;
    this.workspaceAgent = workspaceAgent;
    this.helper = helper;
    this.notificationManager = notificationManager;
    this.textDocumentService = textDocumentService;
    this.title = title;
    view.setDelegate(this);
    view.setTitle(title);
  }

  //TODO maybe we should use some generic data object not a DTO
  public void openLocation(Promise<List<ExtendedLocation>> promise) {
    promise
        .then(
            new Operation<List<ExtendedLocation>>() {
              @Override
              public void apply(List<ExtendedLocation> arg) throws OperationException {
                showLocations(arg);
              }
            })
        .catchError(
            new Operation<PromiseError>() {
              @Override
              public void apply(PromiseError arg) throws OperationException {
                showError(arg);
              }
            });
  }

  public void showError(PromiseError arg) {
    notificationManager.notify(
        title,
        arg.getMessage(),
        StatusNotification.Status.FAIL,
        StatusNotification.DisplayMode.FLOAT_MODE);
  }

  private void showLocations(List<ExtendedLocation> arg) {
    view.setLocations(arg);
    openPart();
  }

  @Override
  public SVGResource getTitleImage() {
    return resources.fieldItem();
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public IsWidget getView() {
    return view;
  }

  @Override
  public String getTitleToolTip() {
    return null;
  }

  @Override
  public void go(AcceptsOneWidget container) {
    container.setWidget(view);
  }

  private void openPart() {
    workspaceAgent.openPart(this, PartStackType.INFORMATION);
    workspaceAgent.setActivePart(this);
  }

  @Override
  public void onLocationSelected(ExtendedLocation location) {
    Range range = location.getLocation().getRange();
    String uri = location.getLocation().getUri();
    TextRange selectionRange =
        new TextRange(
            new TextPosition(range.getStart().getLine(), range.getStart().getCharacter()),
            new TextPosition(range.getEnd().getLine(), range.getEnd().getCharacter()));
    if (uri.startsWith(Constants.CHE_WKSP_SCHEME)) {
      helper.openPath(
          location.getLocation().getUri().substring(Constants.CHE_WKSP_SCHEME.length()),
          selectionRange);
    } else {
      helper.openFile(new LanguageServerFile(textDocumentService, location), selectionRange);
    }
  }
}
