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

package choco.visu.brick;

import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.visu.components.ColorConstant;
import choco.visu.components.bricks.AChocoBrick;
import choco.visu.components.papplets.AChocoPApplet;
import choco.visu.papplet.ColoringPApplet;
import processing.core.PShape;
/* 
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 31 oct. 2008
 */

public class StateColorBrick extends AChocoBrick{

    private boolean isinstanciated;
    private int value;

    public StateColorBrick(final AChocoPApplet chopapplet, final Var var) {
        super(chopapplet, var);
        this.isinstanciated = false;
    }

    /**
     * Refresh data of the PApplet in order to refresh the visualization
     *
     * @param arg an object to precise the refreshing
     */
    public void refresh(Object arg) {
        if(var.isInstantiated()){
            isinstanciated = true;
            value= ((IntDomainVar)var).getVal();
        }else{
            isinstanciated = false;
            value= -1;
        }
    }


    /**
     * Draw the graphic representation of the var associated to the brick
     */
    public void drawBrick(final int x, final int y, final int width, final int height) {
        if(isinstanciated){
            PShape state = ((ColoringPApplet)chopapplet).card.getChild(var.getName());
            state.disableStyle();
            switch (value){
                case 1:
                    chopapplet.fill(ColorConstant.BROWN1);
                    break;
                case 2:
                    chopapplet.fill(ColorConstant.BROWN2);
                    break;
                case 3:
                    chopapplet.fill(ColorConstant.BROWN3);
                    break;
                case 4:
                    chopapplet.fill(ColorConstant.BROWN4);
                    break;
            }
            chopapplet.shape(state, 0, 0);
            chopapplet.noFill();
        }
    }
}
