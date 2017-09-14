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
package org.eclipse.che.api.debug.shared.dto;

import org.eclipse.che.api.debug.shared.model.Variable;
import org.eclipse.che.dto.shared.DTO;

/** @author andrew00x */
@DTO
public interface VariableDto extends Variable {
  @Override
  String getName();

  void setName(String name);

  VariableDto withName(String name);

  @Override
  SimpleValueDto getValue();

  void setValue(SimpleValueDto value);

  VariableDto withValue(SimpleValueDto value);

  @Override
  String getType();

  void setType(String type);

  VariableDto withType(String type);

  @Override
  VariablePathDto getVariablePath();

  void setVariablePath(VariablePathDto variablePath);

  VariableDto withVariablePath(VariablePathDto variablePath);

  @Override
  boolean isPrimitive();

  void setPrimitive(boolean primitive);

  VariableDto withPrimitive(boolean primitive);
}
