package resource_exchange_arena;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static java.lang.Math.sqrt;

class CalculateSatisfaction {
    /**
     * Takes all Agents individual satisfactions and calculates the average satisfaction of all Agents in the
     * simulation.
     *
     * @param agents Array List of all the agents that exist in the current simulation.
     * @return Double Returns the average satisfaction between 0 and 1 of all agents in the simulation.
     */
    static double averageAgentSatisfaction(ArrayList<Agent> agents) {
        ArrayList<Double> agentSatisfactions = new ArrayList<>();
        for (Agent a : agents) {
            agentSatisfactions.add(a.calculateSatisfaction(null));
        }
        return agentSatisfactions.stream().mapToDouble(val -> val).average().orElse(0.0);
    }

    /**
     * Takes all Agents of a given types individual satisfactions and calculates the average satisfaction of the Agents
     * of that type.
     *
     * @param agents Array List of all the agents that exist in the current simulation.
     * @param agentType The type for which to calculate the average satisfaction of all Agents of that type.
     * @return Double Returns the average satisfaction between 0 and 1 of all agents of the given type.
     */
    static double averageAgentSatisfaction(ArrayList<Agent> agents, int agentType) {
        ArrayList<Double> agentSatisfactions = new ArrayList<>();
        for (Agent a : agents) {
            if (a.getAgentType() == agentType) {
                agentSatisfactions.add(a.calculateSatisfaction(null));
            }
        }
        return agentSatisfactions.stream().mapToDouble(val -> val).average().orElse(0.0);
    }

    /**
     * Takes all Agents of a given types individual satisfactions and calculates the variance between the average
     * satisfaction of the Agents of that type.
     *
     * @param agents Array List of all the agents that exist in the current simulation.
     * @param agentType The type for which to calculate the variance between the average satisfactions of all Agents of
     *                  that type.
     * @return Double Returns the variance between the average satisfactions of all agents of the given type.
     */
    static double averageSatisfactionStandardDeviation(ArrayList<Agent> agents, int agentType) {
        double sumDiffsSquared = 0.0;
        double averageSatisfaction = averageAgentSatisfaction(agents, agentType);
        int groupSize = 0;
        for (Agent a : agents) {
            if (a.getAgentType() == agentType) {
                double diff = a.calculateSatisfaction(null) - averageSatisfaction;
                diff *= diff;
                sumDiffsSquared += diff;
                groupSize++;
            }
        }
        if (groupSize == 0) {
            return 0.0;
        }

        double populationVariance = sumDiffsSquared / (double)(groupSize);
        return sqrt(populationVariance);
    }

    /**
     * Takes all Agents of a given types individual satisfactions and calculates the quartile ranges, min, max, median
     * and 95th percentile satisfaction values for that type.
     *
     * @param agents Array List of all the agents that exist in the current simulation.
     * @param agentType The agent type for which to calculate the various values.
     * @return Double[] Returns the quartile ranges, min, max, median and 95th percentile satisfaction values for 
     *         agents of the given type.
     */
    static double[] statisticalValues(ArrayList<Agent> agents, int agentType) {
        ArrayList<Double> agentSatisfactions = new ArrayList<>();
        for (Agent a : agents) {
            if (a.getAgentType() == agentType) {
                agentSatisfactions.add(a.calculateSatisfaction(null));
            }
        }
        Collections.sort(agentSatisfactions);
        double[] statValues = new double [6];

        int size = agentSatisfactions.size();
        
        double uq;
        double lq;
        double ninetyfifth;
        double max = size != 0 ? agentSatisfactions.get(size - 1) : 0;
        double min = size != 0 ? agentSatisfactions.get(0) : 0;
        double median;
        
        double[] satArray = new double [size];
        int i = 0;
        for (double a : agentSatisfactions) {
            satArray[i] = a;
            i++;
        }

        double[] lqSet;
        double[] uqSet;

        if (size != 0) {
            if (size % 2 == 1) {
                median = satArray[size / 2];
                lqSet = Arrays.copyOfRange(satArray, 0, (size / 2));
                uqSet = Arrays.copyOfRange(satArray, (size / 2) + 1, size);
            } else {
                median = (satArray[size / 2] + satArray[(size / 2) - 1]) / 2;
                lqSet = Arrays.copyOfRange(satArray, 0, size / 2);
                uqSet = Arrays.copyOfRange(satArray, (size / 2), satArray.length);
            }

            if (lqSet.length % 2 == 1) {
                lq = 0 != lqSet.length ? lqSet[lqSet.length / 2] : 0;
                uq = 0!= uqSet.length ? uqSet[uqSet.length / 2] : 0;
            } else {
                lq = 0 != lqSet.length ? (lqSet[lqSet.length / 2] + lqSet[(lqSet.length / 2) - 1]) / 2 : 0;
                uq = 0 != uqSet.length ? (uqSet[uqSet.length / 2] + uqSet[(uqSet.length / 2) - 1]) / 2 : 0;
            }
        } else {
            median = 0;
            uq = 0;
            lq = 0;
        }
        
        ninetyfifth = size != 0 ? percentile(satArray,95) : 0;

        statValues[0] = uq;
        statValues[1] = lq;
        statValues[2] = ninetyfifth;
        statValues[3] = max;
        statValues[4] = min;
        statValues[5] = median;

        return statValues;
    }

    /**
     * Use linear interpolation to calculate a percentile from an array of data.
     *
     * @param xs Array of values from which the percentile is calculated.
     * @param p The percentile to calculate.
     * @return Double value of the percentile requested.
     */
    static double percentile(double[] xs, int p) {
        // The sorted elements in X are taken as the 100(0.5/n)th, 100(1.5/n)th, ..., 100([n – 0.5]/n)th percentiles.
        int i = (int) (p * xs.length / 100.0 - 0.5);

        // Linear interpolation uses linear polynomials to find yi = f(xi), the values of the underlying function
        // Y = f(X) at the points in the vector or array x. Given the data points (x1, y1) and (x2, y2), where
        // y1 = f(x1) and y2 = f(x2), linear interpolation finds y = f(x) for a given x between x1 and x2 as follows:
        return i != (xs.length - 1) ? xs[i] + (xs[i + 1] - xs[i]) * (p / 100.0 - (i + 0.5) / xs.length) / ((i + 1.5) / xs.length - (i + 0.5) / xs.length) : xs[i];
    }

    /**
     * Returns the optimum average satisfaction possible for all agents given the current requests and allocations in
     * the simulation.
     *
     * @param agents Array List of all the agents that exist in the current simulation.
     * @return Double Returns the highest possible average satisfaction between 0 and 1 of all agents in the simulation.
     */
    static double optimumAgentSatisfaction(ArrayList<Agent> agents) {
        ArrayList<Integer> allRequestedSlots = new ArrayList<>();
        ArrayList<Integer> allAllocatedSlots = new ArrayList<>();

        for (Agent a : agents) {
            allRequestedSlots.addAll(a.publishRequestedTimeSlots());
            allAllocatedSlots.addAll(a.publishAllocatedTimeSlots());
        }

        // Stores the number of slots that could potentially be fulfilled with perfect trading.
        double satisfiedSlots = 0;

        // Stores the total number of slots requested by all Agents.
        double totalSlots = allRequestedSlots.size();

        for (Integer slot : allRequestedSlots) {
            if (allAllocatedSlots.contains(slot)) {
                // For each request, if it has been allocated to any agent, increase the number of satisfied slots.
                satisfiedSlots++;

                // Remove the slot from the list of all allocated slots so no slots can be allocated twice.
                allAllocatedSlots.remove(slot);
            }
        }
        return satisfiedSlots / totalSlots;
    }
}
