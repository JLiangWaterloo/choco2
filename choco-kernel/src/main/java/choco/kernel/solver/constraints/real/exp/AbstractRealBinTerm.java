/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package choco.kernel.solver.constraints.real.exp;

import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.real.RealExp;
import choco.kernel.solver.variables.real.RealVar;

import java.util.List;
import java.util.Set;

/**
 * A binary real expression.
 */
public abstract class AbstractRealBinTerm extends AbstractRealCompoundTerm {
  protected RealExp exp1, exp2;

  public AbstractRealBinTerm(Solver solver, RealExp exp1, RealExp exp2) {
    super(solver);
    this.exp1 = exp1;
    this.exp2 = exp2;
  }

  public List<RealExp> subExps(List<RealExp> l) {
    exp1.subExps(l);
    exp2.subExps(l);
    l.add(this);
    return l;
  }

  public Set<RealVar> collectVars(Set<RealVar> s) {
    exp1.collectVars(s);
    exp2.collectVars(s);
    return s;
  }

  public boolean isolate(RealVar var, List<RealExp> wx, List<RealExp> wox) {
    boolean dependsOnX = exp1.isolate(var, wx, wox) | exp2.isolate(var, wx, wox);
    if (dependsOnX) wx.add(this); else wox.add(this);
    return dependsOnX;
  }
}
