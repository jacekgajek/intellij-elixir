/*
 * Copyright 2012-2014 Sergey Ignatov
 * Copyright 2017 Jake Becker
 * Copyright 2017 Luke Imhoff
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elixir_lang.debugger.xdebug.xvalue;

import com.ericsson.otp.erlang.OtpErlangTuple;
import com.intellij.xdebugger.frame.XValueChildrenList;
import org.jetbrains.annotations.NotNull;

class ElixirTupleXValue extends ElixirArrayXValueBase<OtpErlangTuple> {
  ElixirTupleXValue(@NotNull OtpErlangTuple value) {
    super(value, value.arity());
  }

  @Override
  protected void computeChild(@NotNull XValueChildrenList children, int childIdx) {
    addIndexedChild(children, getValue().elementAt(childIdx), childIdx);
  }
}