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

package samples.multicostregular.carsequencing.heuristics;

import choco.kernel.solver.search.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;
import samples.multicostregular.carsequencing.parser.CarSeqInstance;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jan 28, 2009
 * Time: 7:18:16 PM
 */
public class BothValHeur implements ValSelector<IntDomainVar> {


    Integer[] order;

    class Compare implements Comparator<Integer>
    {

        Integer[] nbc;
        Integer[] nbo;
        public Compare(Integer[] nbc, Integer[] nbo)
        {
            this.nbc = nbc;
            this.nbo = nbo;
        }

        public int compare(Integer i, Integer i1) {
            int ret = nbo[i1].compareTo(nbo[i]);
            if (ret == 0)
            {
                ret = nbc[i].compareTo(nbc[i1]);
            }
            return ret;
        }
    }

    public BothValHeur(CarSeqInstance instance)
    {
        order = new Integer[instance.nbClasses];
        Integer[] nbC = new Integer[instance.nbClasses];
        Integer[] nbO = new Integer[instance.nbClasses];

        for (int i = 0 ; i < instance.nbClasses ; i++)
        {
            order[i] = i;
            nbC[i] = instance.optionRequirement[i][1];
            nbO[i] = sum(instance.optionRequirement[i],instance.nbOptions);
        }
        Arrays.sort(order,new Compare(nbC,nbO));
    }

    public int getBestVal(IntDomainVar x) {
        for (Integer anOrder : order) {
            if (x.canBeInstantiatedTo(anOrder)) {
                return anOrder;
            }
        }

        System.err.println("pas normal d'etre la most car val heur");
        return x.getInf();
    }


    private int sum(int[] tab, int lgth)
    {
        int s = 0;
        for (int i  = 0 ; i < lgth ; i++) s+= tab[i+2];
        return s;

    }

}