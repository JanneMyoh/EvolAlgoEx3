/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softga;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 * Main program that implements genetic algorithm
 *
 * @author janne
 */
public class overseer {

    private static solution[] ratkaisu = new solution[20];
    private static solution[] unculledPopulation = new solution[ratkaisu.length * 2];
    private static ArrayList<solution>[] dominanceRanks = new ArrayList[ratkaisu.length * 2];
    private static int[] dominanceCont = new int[ratkaisu.length * 2];
    private static double breedChanse = 0.8;
    private static double mutationChanse = 0.03;
    private static int numberOfGenerations = 500;

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        OFun target = new OFun();
        for (int i = 0; i < ratkaisu.length; i++) {
            ratkaisu[i] = new solution(1, 0, 30, target);

        }
        TableModel model = new DefaultTableModel();
        final File file = new File("results.ods");
        try {
            SpreadSheet.createEmpty(model).saveAs(file);
            Sheet sheet = SpreadSheet.createFromFile(file).getSheet(0);
            sheet.ensureColumnCount(2);
            sheet.ensureRowCount(numberOfGenerations * 21);
            sheet.getSpreadSheet().saveAs(file);

        } catch (IOException ex) {
            System.out.println("Tapahtui virhe");
        }
        for (int i = 0; i < numberOfGenerations; i++) {
            crossBreed();
            determineDominance();
            determineRanks();
            fillNextGeneration();
            try {
                addToTAble(file, i);
            } catch (IOException ex) {
                System.out.println("failed to record data");
            }
        }

    }

    /**
     *Used to build the next generation
     */
    public static void fillNextGeneration() {
        int currentlyAtPopulation = 0;
        int j = 0;
        while (currentlyAtPopulation + dominanceRanks[j].size() < ratkaisu.length) {
            for (int k = 0; k < dominanceRanks[j].size(); k++) {
                ratkaisu[currentlyAtPopulation + k] = dominanceRanks[j].get(k);
            }
            currentlyAtPopulation = currentlyAtPopulation + dominanceRanks[j].size();
        }
        if (currentlyAtPopulation < ratkaisu.length) {
            solution[] sorted = determineCrowding(j);
            int k = 0;
            while (currentlyAtPopulation < ratkaisu.length) {
                ratkaisu[currentlyAtPopulation] = sorted[k];
                currentlyAtPopulation++;
                k++;
            }
        }
    }

    /**
     * determines the crowding distance for each individual
     * @param j the dominanse rank that is being operated
     * @return array with indivifuals in decending crowfing distance
     */
    public static solution[] determineCrowding(int j) {
        solution[] sorted = new solution[dominanceRanks[j].size()];
        for (int m = 0; m < dominanceRanks[j].size(); m++) {
            dominanceRanks[j].get(m).resetCrowding();
        }
        int[] max = new int[]{1, 1};
        int[] min = new int[]{0, 0};
        for (int k = 0; k < 2; k++) {
            sorted = sort(k, j);
            sorted[0].addToCrowding(10000000);
            sorted[sorted.length - 1].addToCrowding(10000000);
            for (int l = 1; l < sorted.length - 1; l++) {
                sorted[l].addToCrowding((sorted[l + 1].evaluate()[k] - sorted[l - 1].evaluate()[k]) / (max[k] - min[k]));
            }
        }
        sorted = sortCrowding(sorted);
        return sorted;
    }

    /**
     * Adds the values of current solutions to .ods file
     * @param target the target file
     * @param gen number of current generation
     * @throws IOException throws if something goes wrong
     */
    public static void addToTAble(File target, int gen) throws IOException {
        Sheet sheet = SpreadSheet.createFromFile(target).getSheet(0);
        int numbA = 0;
        for (int i = 0; i < ratkaisu.length; i++) {

            numbA = gen * 21 + i + 1;
            sheet.setValueAt(ratkaisu[i].evaluate()[0], 0, numbA);
            sheet.setValueAt(ratkaisu[i].evaluate()[1], 1, numbA);
        }
        sheet.getSpreadSheet().saveAs(target);
    }


    /**
     * sorts the given array in decending crowding disane
     * @param target array to be sorted
     * @return a sorted array
     */
    public static solution[] sortCrowding(solution[] target) {
        boolean muutettu = true;
        while (muutettu) {
            muutettu = false;
            for (int i = 0; i < target.length - 1; i++) {
                if (target[i].getCrowding() < target[i + 1].getCrowding()) {
                    target = swap(target, i, i + 1);
                    muutettu = true;
                }
            }
        }
        return target;
    }

    /**
     * sorts the determined dominance rank according to the determined dunction in ascending oder
     * @param funktion what function is used to determine the order
     * @param front what front is being sorted
     * @return sorted array
     */
    public static solution[] sort(int funktion, int front) {
        solution[] toBReturned = new solution[dominanceRanks[front].size()];
        for (int i = 0; i < toBReturned.length; i++) {
            toBReturned[i] = dominanceRanks[front].get(i);
        }
        boolean muutettu = true;
        while (muutettu) {
            muutettu = false;
            for (int i = 0; i < toBReturned.length - 1; i++) {
                if (toBReturned[i].evaluate()[funktion] > toBReturned[i + 1].evaluate()[funktion]) {
                    toBReturned = swap(toBReturned, i, i + 1);
                    muutettu = true;
                }
            }
        }
        return toBReturned;
    }

    /**
     * swaps the i:th and j:th object in given array
     * @param target the array that houses the desired objects
     * @param i index of first object to be swaped
     * @param j index of the second object to be swaped
     * @return the array with two objects swaped
     */
    public static solution[] swap(solution[] target, int i, int j) {
        solution temp = target[i];
        target[i] = target[j];
        target[j] = temp;
        return target;
    }

    /**
     * determines the ranks of solutions
     */
    public static void determineRanks() {
        for (int i = 0; i < dominanceRanks.length; i++) {
            dominanceRanks[i] = new ArrayList<solution>();
        }
        int rank = 0;
        while (hasSolutions()) {
            for (int i = 0; i < unculledPopulation.length; i++) {
                if (unculledPopulation[i] != null && unculledPopulation[i].dominatedCount() == 0) {
                    dominanceRanks[rank].add(unculledPopulation[i]);
                    unculledPopulation[i] = null;

                }
            }
            for (int i = 0; i < dominanceRanks[rank].size(); i++) {
                ArrayList<solution> dominatedList = dominanceRanks[rank].get(i).getDOminates();
                for (int j = 0; j < dominatedList.size(); j++) {
                    dominatedList.get(i).removeDominance();
                }
            }
            rank++;
        }
    }

    /**
     * Inspects if unculledPopulation still has solutions
     * @return true if there still are solutions in array
     */
    public static boolean hasSolutions() {
        boolean value = false;
        int i = 0;
        while (!value && i < unculledPopulation.length) {
            value = (null != unculledPopulation[i]);
            i++;
        }
        return value;
    }

    /**
     *Determines who dominates who in unculledPopulation
     */
    public static void determineDominance() {
        for (int i = 0; i < unculledPopulation.length; i++) {
            unculledPopulation[i].resetDominance();
        }
        for (int i = 0; i < unculledPopulation.length; i++) {
            double[] values1 = unculledPopulation[i].evaluate();
            for (int j = 0; j < unculledPopulation.length; j++) {
                int dominates = 1;
                double[] values2 = unculledPopulation[j].evaluate();
                for (int k = 0; k < values1.length; k++) {
                    if (values1[k] > values2[k]) {
                        if (dominates == 1) {
                            dominates = 2;
                        } else if (dominates == 0) {
                            dominates = 1;
                            // System.out.println("Dominance lost on " + i +" against " + j + " at " + k);
                            break;
                        }

                    }
                    if (values1[k] < values2[k]) {
                        if (dominates == 1) {
                            dominates = 0;
                        } else if (dominates == 2) {
                            dominates = 1;
                            break;
                        }
                    }
                }
                if (dominates == 2) {
                    unculledPopulation[i].addDominated(unculledPopulation[j]);
                    //System.out.println(i + " dominates " + j);
                } else if (dominates == 0) {
                    unculledPopulation[i].addDominance();
                }
            }
        }
    }



    /**
     * creates two new solutions by mating two random solutions
     */
    public static void crossBreed() {
        boolean[] used = new boolean[ratkaisu.length];
        for (int i = 0; i < used.length; i++) {
            used[i] = false;
            unculledPopulation[i * 2] = null;
            unculledPopulation[i * 2 + 1] = null;
        }
        solution[] temp = new solution[2];
        for (int i = 0; i < (ratkaisu.length / 2); i++) {
            int j = i;
            while (used[j]) {
                j++;
                if (j >= used.length) {
                    j = 0;
                }
            }
            used[j] = true;
            int partner = (int) (Math.random() * ratkaisu.length);
            while (used[partner]) {
                partner++;
                if (partner >= ratkaisu.length) {
                    partner = 0;
                }
                if (partner == j) {
                    partner++;
                }
            }
            used[partner] = true;
            temp = ratkaisu[j].crosover(ratkaisu[partner]);
            temp[0].mutate(mutationChanse);
            temp[1].mutate(mutationChanse);
            unculledPopulation[i * 4] = ratkaisu[j];
            unculledPopulation[i * 4 + 1] = ratkaisu[partner];
            unculledPopulation[i * 4 + 2] = temp[0];
            unculledPopulation[i * 4 + 3] = temp[1];

        }
    }



}
