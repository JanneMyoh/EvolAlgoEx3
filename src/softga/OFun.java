/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package softga;

/**
 * The funktion for this problem. For diferent problem, a new class must be created and implement ObjectFunktion interface
 * @author janne
 */
public class OFun implements ObjectFunktion {

    private int numberOfVariables = 30;
    
    /**
     *
     */
    public OFun (){
    }
    
    /**
     * returns the number of variables in this function
     * @return number of variables in this function
     */
    @Override
    public int getNumberOfVariables() {
        return numberOfVariables;
    }

    /**
     * calculates the value of this function at given point
     * @param variables point where the value is valculated
     * @return value of this function at given point
     */
    @Override
    public double[] calculate(double[] variables) {
        double[] solutions = new double[2];
        solutions[0] = variables[0];
        solutions[1] = g(variables)*h(solutions[0], g(variables));
        return solutions;
    }
    
    private double h(double f1, double gval)
    {
        return 1- Math.sqrt(f1/gval);
    }
    
    private double g (double[] variables)
    {
        return 1 + (9/(numberOfVariables-1))*sum(variables);
    }
    
    private double sum (double[] variables)
    {
        double summa = 0;
        for(int i = 1; i < variables.length; i++)
        {
            summa = summa + variables[i];
        }
        return summa;
    }

    /**
     * returns a clone of this function
     * @return clone of this funktion
     */
    @Override
    public ObjectFunktion giveClone() {
        return new OFun();
    }

    /**
     * Gives the fitnes of this function at given point
     * @param variables the point where we want to know the fitnes
     * @return fitnes of this function at given point
     */
    
}
