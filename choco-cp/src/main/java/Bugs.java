
import choco.cp.solver.CPSolver;
import choco.cp.solver.variables.set.BitSetEnumeratedDomain;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.set.SetVar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jimmy
 */
public class Bugs {

    public static void main(String[] args) throws Exception {
        negativeOpenDomainBug();
        bitsetGetPrevBug();
    }

    public static void negativeOpenDomainBug() throws ContradictionException {
        Solver solver = new CPSolver();

        SetVar set = solver.createEnumSetVar("set", -3, 1);

        DisposableIntIterator it = set.getDomain().getOpenDomainIterator();

        List<Integer> vals = new ArrayList<Integer>();

        try {
            while (it.hasNext()) {
                vals.add(it.next());
            }
        } finally {
            it.dispose();
        }

        if (!vals.equals(Arrays.asList(-3, -2, -1, 0, 1))) {
            System.out.println("Negatives... bad");
            System.out.println(vals);
            throw new Error("Bug still not fixed: http://sourceforge.net/projects/choco/forums/forum/335512/topic/6880032");
        }
        System.out.println("Negatives... okay");
    }

    public static void bitsetGetPrevBug() {
        Solver solver = new CPSolver();

        SetVar set = solver.createEnumSetVar("set", 0, 3);

        BitSetEnumeratedDomain dom = (BitSetEnumeratedDomain) set.getDomain().getEnveloppeDomain();

        List<Integer> vals = new ArrayList<Integer>();
        int val = dom.getLastVal();
        vals.add(val);
        for (int i = 0; i < 3; i++) {
            val = dom.getPrevValue(val);
            vals.add(val);
        }

        if (!vals.equals(Arrays.asList(3, 2, 1, 0))) {
            System.out.println("getPrev... bad");
            System.out.println(vals);
            throw new Error("Bug still not fixed: http://sourceforge.net/projects/choco/forums/forum/335512/topic/6890143");
        }
        System.out.println("getPrev... okay");
    }
}
