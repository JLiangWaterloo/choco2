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

package samples.tutorials.database;

import choco.kernel.common.logging.ChocoLogging;
import db.DbTables;
import db.EmbeddedDbConnector;
import db.IDbConnector;
import db.RemoteDbConnector;
import org.kohsuke.args4j.Option;
import samples.tutorials.Example;
import samples.tutorials.PatternExample;
import samples.tutorials.puzzles.GolombRuler;
import samples.tutorials.puzzles.Queen;
import samples.tutorials.to_sort.MinimumEdgeDeletion;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class DbExample implements Example {

    @Option(name = "-url", usage = "Database URL", required = false)
    String url = "";

    public final static int NB_RUNS = 3; //5 seconds

    public final static int TIME_LIMIT = 5000; //5 seconds

    public final static TimeLimitWrapper EX_WRAPPER = new TimeLimitWrapper();

    public IDbConnector dbConnector;

    public void executeEx(String name, String... args) {
        EX_WRAPPER.execute(args);
        dbConnector.getDatabaseManager().insertSolver(EX_WRAPPER.solver, name);
    }

    public void solveGolombRulers() {
        EX_WRAPPER.setSource(new GolombRuler());
        for (int i = 5; i < 12; i++) {
            executeEx(
                    "Golomb-" + i,
                    "-s", Integer.toString(i), "-allDiff"
            );
        }
    }

    public void solveQueens() {
        EX_WRAPPER.setSource(new Queen());
        for (int i = 2; i < 5; i++) {
            executeEx("N-Queens-" + i, Integer.toString(i));
        }
        for (int i = 40; i < 45; i++) {
            executeEx("N-Queens-" + i, Integer.toString(i));
        }
    }

    public void solveMED() {
        EX_WRAPPER.setSource(new MinimumEdgeDeletion());
        for (int i = 12; i < 15; i++) {
            executeEx("med-" + i,
                    "-n", Integer.toString(i),
                    "-p", Double.toString(0.5),
                    "-seed", Long.toString(i));
        }
    }

    protected void executeBenchmark() {
        solveGolombRulers(); //solve instances
        solveQueens();
        solveMED();
    }

    @Override
    public void execute(String... args) {
        if (!url.equals("")) {
            //network database
            dbConnector = new RemoteDbConnector(url);
        } else {
            try {
                File output = File.createTempFile("testdb-", ".odb");
                dbConnector = new EmbeddedDbConnector(output);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "dbex...[export:FAIL]", e);
                dbConnector = new EmbeddedDbConnector();
            }
        }
        dbConnector.setUp();
        executeBenchmark();
        dbConnector.getDatabaseManager().printTable(DbTables.T_SOLVERS);
        dbConnector.tearDown();
        ChocoLogging.flushLogs();
    }

    public static void main(String[] args) {
        final DbExample dbex = new DbExample();
        if (args.length == 0) dbex.execute();
        else dbex.execute(args[0]);
    }


    static class TimeLimitWrapper extends PatternExample {

        public PatternExample source;


        public final PatternExample getSource() {
            return source;
        }

        public final void setSource(PatternExample source) {
            this.source = source;
        }


        @Override
        public void buildModel() {
            source.buildModel();
            model = source.model;

        }

        @Override
        public void buildSolver() {
            source.buildSolver();
            solver = source.solver;
            solver.setTimeLimit(TIME_LIMIT);

        }

        @Override
        public void prettyOut() {
            source.prettyOut();

        }

//        public void readArgs(String[] args) {
//            source.readArgs(args);
//        }

        @Override
        public void solve() {
            source.solve();

        }


    }
}
