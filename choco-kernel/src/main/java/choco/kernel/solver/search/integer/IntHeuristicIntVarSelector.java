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

package choco.kernel.solver.search.integer;

import java.util.ArrayList;
import java.util.List;

import choco.kernel.memory.IStateInt;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * A class the selects the variables which minimizes a heuristic
 *  (such classes support ties)
 */
public abstract class IntHeuristicIntVarSelector extends HeuristicIntVarSelector {

	public IntHeuristicIntVarSelector(Solver solver) {
		super(solver);
	}

	public IntHeuristicIntVarSelector(Solver solver, IntDomainVar[] vars) {
		super(solver, vars);
	}



	/**
	 * the heuristic that is minimized in order to find the best IntVar
	 */
	public abstract int getHeuristic(IntDomainVar v);

	public final int getHeuristic(AbstractIntSConstraint c, int i) {
		return getHeuristic(c.getVar(i));
	}

	/**
	 * @param vars the set of vars among which the variable is returned
	 * @return the first variable minimizing a given heuristic
	 */
	@Override
	public final IntDomainVar getMinVar(List<IntDomainVar> vars) {
		int minValue = IStateInt.MAXINT;
		IntDomainVar v0 = null;
		for (IntDomainVar v:vars) {
			if (!v.isInstantiated()) {
				int val = getHeuristic(v);
				if (val < minValue)  {
					minValue = val;
					v0 = v;
				}
			}
		}
		return v0;
	}
	/**
	 * @param vars the set of vars among which the variable is returned
	 * @return the first variable minimizing a given heuristic
	 */
	@Override
	public final IntDomainVar getMinVar(IntDomainVar[] vars) {
		int minValue = IStateInt.MAXINT;
		IntDomainVar v0 = null;
		for (IntDomainVar v:vars) {
			if (!v.isInstantiated()) {
				int val = getHeuristic(v);
				if (val < minValue)  {
					minValue = val;
					v0 = v;
				}
			}
		}
		return v0;
	}

	

	@Override
	public IntDomainVar getMinVar(AbstractIntSConstraint c) {
		double minValue = Double.POSITIVE_INFINITY;
		IntDomainVar v0 = null;
		for (int i=0; i<c.getNbVars(); i++) {
			IntDomainVar v = c.getVar(i);
			if (!v.isInstantiated()) {
				double val = getHeuristic(c,i);
				if (val < minValue)  {
					minValue = val;
					v0 = v;
				}
			}
		}
		return v0;
	}



	@Override
	public List<IntDomainVar> getAllMinVars(IntDomainVar[] vars) {
		List<IntDomainVar> res = new ArrayList<IntDomainVar>();
		int minValue = IStateInt.MAXINT;
		for (IntDomainVar v:vars) {
			if (!v.isInstantiated()) {
				int val = getHeuristic(v);
				if (val < minValue)  {
					res.clear();
					res.add(v);
					minValue = val;
				} else if (val == minValue) {
					res.add(v);
				}
			}
		}
		return res;
	}

	@Override
	public final List<IntDomainVar> getAllMinVars(AbstractIntSConstraint c) {
		List<IntDomainVar> res = new ArrayList<IntDomainVar>();
		int minValue = IStateInt.MAXINT;
		for (int i = 0; i < c.getNbVars(); i++) {
			IntDomainVar v = c.getVar(i);
			if (!v.isInstantiated()) {
				int val = getHeuristic(v);
				if (val < minValue)  {
					res.clear();
					res.add(v);
					minValue = val;
				} else if (val == minValue) {
					res.add(v);
				}
			}
		}
		return res;
	}
}
