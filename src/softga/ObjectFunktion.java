/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package softga;

/**
 *
 * @author janne
 */
public interface ObjectFunktion {
           
    /**
     *Returns the amount of variables needed in funktion
     * @return number of diferent variables in funktion
     */
    public int getNumberOfVariables();

    /**
     * Calculates the value at given point
     * @param variables list of variables for the funktion
     * @return the funktions value at given point
     */
    public double[] calculate (double[] variables);

    /**
     * gives a clone of the funktion object
     * @return clone of the funktion object
     */
    public ObjectFunktion giveClone();

    
}
