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
package org.eclipse.che.plugin.languageserver.ide.util;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.gwt.core.client.Scheduler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.editor.OpenEditorCallbackImpl;
import org.eclipse.che.ide.api.editor.text.TextRange;
import org.eclipse.che.ide.api.editor.texteditor.TextEditor;
import org.eclipse.che.ide.api.resources.File;
import org.eclipse.che.ide.api.resources.VirtualFile;
import org.eclipse.che.ide.resource.Path;

/**
 * Util class, helps to open file by path in editor
 *
 * @author Evgen Vidolob
 */
@Singleton
public class OpenFileInEditorHelper {

  private final EditorAgent editorAgent;
  private final AppContext appContext;

  @Inject
  public OpenFileInEditorHelper(EditorAgent editorAgent, AppContext appContext) {
    this.editorAgent = editorAgent;
    this.appContext = appContext;
  }

  public void openPath(final String filePath, final TextRange selectionRange) {
    doIfUnopened(
        filePath,
        selectionRange,
        () -> {
          appContext.getWorkspaceRoot().getFile(filePath).then(openNode(selectionRange));
        });
  }

  public void openFile(VirtualFile file, TextRange selectionRange) {
    doIfUnopened(
        file.getLocation().toString(), selectionRange, () -> doOpenFile(file, selectionRange));
  }

  private void doIfUnopened(final String filePath, final TextRange selectionRange, Runnable r) {
    if (Strings.isNullOrEmpty(filePath)) {
      return;
    }

    EditorPartPresenter editorPartPresenter = editorAgent.getOpenedEditor(Path.valueOf(filePath));
    if (editorPartPresenter != null) {
      editorAgent.activateEditor(editorPartPresenter);
      fileOpened(editorPartPresenter, selectionRange);
      return;
    }
    r.run();
  }

  public void openFile(String filePath) {
    openPath(filePath, null);
  }

  private Function<Optional<File>, Optional<File>> openNode(final TextRange selectionRange) {
    return new Function<Optional<File>, Optional<File>>() {
      @Override
      public Optional<File> apply(Optional<File> node) {
        if (node.isPresent()) {
          doOpenFile(node.get(), selectionRange);
        }
        return node;
      }
    };
  }

  private void doOpenFile(VirtualFile result, final TextRange selectionRange) {
    editorAgent.openEditor(
        result,
        new OpenEditorCallbackImpl() {
          @Override
          public void onEditorOpened(EditorPartPresenter editor) {
            fileOpened(editor, selectionRange);
          }
        });
  }

  private void fileOpened(final EditorPartPresenter editor, final TextRange selectionRange) {
    if (editor instanceof TextEditor && selectionRange != null) {
      Scheduler.get()
          .scheduleDeferred(
              new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                  ((TextEditor) editor).getDocument().setSelectedRange(selectionRange, true);
                }
              });
    }
  }
}
