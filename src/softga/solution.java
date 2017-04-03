/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softga;

import java.util.ArrayList;
import java.util.Random;


/**
 * A single solution in problem
 * @author janne
 */
public class solution {

    private double[] chromosome;
    private double[] value;
    private double max;
    private double min;
    private ObjectFunktion objFkt;
    private int dominated = 0;
    private ArrayList<solution> dominates = new ArrayList<solution>();
    private double crowding = 0;

    /**
     *
     * @param upper upper limit for the values
     * @param lower lower limit for the values
     * @param length desired length for the cromosome
     * @param target the funktion that is going to be optimised
     */
    public solution(double upper, double lower, int length, ObjectFunktion target) {
        max = upper;
        min = lower;
        chromosome = new double[target.getNumberOfVariables()];
        objFkt = target;
        Random rand = new Random();
        for (int i = 0; i < chromosome.length; i++) {
           chromosome[i] = min + rand.nextDouble()*(max-min);
        }
        value = evaluate();
    }

    /**
     *
     * @param upper the upper limit
     * @param lower the lower limit
     * @param length the length of chromosome
     * @param target the funktion that is going to be optimised
     * @param givenSome a desired chromosme for this solution
     */
    
    public solution(double upper, double lower, int length, ObjectFunktion target, double[] givenSome) {
        max = upper;
        min = lower;
        objFkt = target;
        chromosome = givenSome;
        Random rand = new Random();
        evaluate();
    }

   

    /**
     * 
     * calculates the value of target funktion, using the values of this chromosome
     * @return calculated value
     */
    
    //Tätä kutsutaan aikamonta kertaa kun haetaan dominansee... oiskohan fiksumpaa laskea se jokaiselle solutionille kerran ja tallettaa. Ei tarttis pyöräyttää summa foria koko ajan.
    public double[] evaluate() {
        value = objFkt.calculate(chromosome);
        return new double[]{value[0],value[1]};
    }


    /**
     *mutates the solution
     * @param chanse chanse for each bit to mutate
     */
    public void mutate(double chanse) {
        Random rand = new Random();
        for (int i = 0; i < chromosome.length; i++) {
            if (rand.nextDouble() < chanse) {
               chromosome[i] = (1-((rand.nextDouble()/2) - 0.25))*chromosome[i];
               if(chromosome[i] < min) chromosome[i] = min;
               if(chromosome[i] > max) chromosome[i] = max;
                }
            }
        
        
    }

    /**
     * returns a copy of this solutions chromosome
     * @return
     */
    public double[] getChromosome() {
        double[] returned = new double[chromosome.length];
        for (int i = 0; i < chromosome.length; i++) {
            returned[i] = chromosome[i];
        }
        return returned;
    }

    /**
     * inserts the given chromosome to this solution
     * @param newSome desired chromosome
     */
    public void setChromosome(double[] newSome) {
        for (int i = 0; i < newSome.length; i++) {
            chromosome[i] = newSome[i];

        }
    }
    /**
     *produces offspring by crosbreeding this solution and given solution.
     * @param parent the other parent solution
     * @param xoverPoint list of points where the crossover will be done.
     * @param chanse chanse fr crosover happening
     * @return the child solutions.
     */
    public solution[] crosover(solution parent, int[] xoverPoint, double chanse) {
        double[] child1some = this.getChromosome();
        double[] child2some = parent.getChromosome();
        double storage;
        Random rand = new Random();
        if (rand.nextDouble() < chanse) {
            for (int i = 0; i < xoverPoint.length; i++) {

                for (int j = xoverPoint[i]; j < chromosome.length; j++) {
                    storage = child1some[j];
                    child1some[j] = child2some[j];
                    child2some[j] = storage;
                }
            }
        }
        solution[] childs = new solution[]{new solution(max, min, chromosome.length, objFkt.giveClone(), child1some), new solution(max, min, chromosome.length, objFkt.giveClone(), child2some)};
        return childs;
    }
    
    public solution[] crosover(solution parent) {
        double[] child1some = new double[chromosome.length];
        double[] child2some = new double[chromosome.length];
        double[] parentSome = parent.getChromosome();
        Random rand = new Random();
        double lambda = 0;
            for (int i = 0; i < chromosome.length; i++) {
                lambda = rand.nextDouble();
                child1some[i] = lambda*chromosome[i] + (1-lambda)*parentSome[i];
                child2some[i] = lambda*parentSome[i] + (1-lambda)*chromosome[i];
                
            
        }
        solution[] childs = new solution[]{new solution(max, min, chromosome.length, objFkt.giveClone(), child1some), new solution(max, min, chromosome.length, objFkt.giveClone(), child2some)};
        childs[0].evaluate(); childs[1].evaluate();
        return childs;
    }

    /**
     *gets the fitnes value of this solution
     * @return fitnes value of this solution
     */
    public double getFitne() {
        return 0;
    }
    
    public solution getClone ()
    {
        return new solution(max, max, chromosome.length, objFkt.giveClone(), getChromosome());
    }
    
    public void addDominance()
    {
        dominated++;
    }
    
    public void removeDominance()
    {
        dominated--;
    }
    
    public int dominatedCount()
    {
        return dominated;
    }
    
    public void resetDominance ()
    {
        dominated = 0;
        dominates.clear();
    }
    
    public void addDominated (solution target)
    {
        dominates.add(target);
    }
    
    public ArrayList<solution> getDOminates ()
    {
        return dominates;
    }
    
    public void resetCrowding ()
    {
        crowding = 0;
    }
    
    public void addToCrowding(double x)
    {
        crowding = x + crowding;
    }
    
    public double getCrowding ()
    {
        return crowding;
    }

}
