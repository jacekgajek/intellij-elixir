// This is a generated file. Not intended for manual editing.
package org.elixir_lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.elixir_lang.psi.ElixirTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.elixir_lang.psi.*;
import com.ericsson.otp.erlang.OtpErlangObject;

public class ElixirInterpolatedRegexHeredocLineImpl extends ASTWrapperPsiElement implements ElixirInterpolatedRegexHeredocLine {

  public ElixirInterpolatedRegexHeredocLineImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof ElixirVisitor) ((ElixirVisitor)visitor).visitInterpolatedRegexHeredocLine(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ElixirHeredocLinePrefix getHeredocLinePrefix() {
    return findNotNullChildByClass(ElixirHeredocLinePrefix.class);
  }

  @Override
  @NotNull
  public ElixirInterpolatedRegexBody getInterpolatedRegexBody() {
    return findNotNullChildByClass(ElixirInterpolatedRegexBody.class);
  }

  public Body getBody() {
    return ElixirPsiImplUtil.getBody(this);
  }

  @NotNull
  public OtpErlangObject quote(Heredoc heredoc, int prefixLength) {
    return ElixirPsiImplUtil.quote(this, heredoc, prefixLength);
  }

}
