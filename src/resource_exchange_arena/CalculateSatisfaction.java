package resource_exchange_arena;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.Math.sqrt;

class CalculateSatisfaction {

    /**
     * Takes all {@link Agent}s individual satisfactions and calculates the average satisfaction of all {@link Agent}s in the
     * simulation.
     *
     * @param agents {@link ArrayList} of all the {@link Agent}s that exist in the current simulation.
     * @return The average satisfaction between 0 and 1 of all {@link Agent}s in the simulation.
     */
    static double averageAgentSatisfaction(@NotNull ArrayList<Agent> agents) {
        ArrayList<Double> agentSatisfactions =
                agents.stream()
                        .map(a -> a.calculateSatisfaction(null))
                        .collect(Collectors.toCollection(ArrayList::new));
        return agentSatisfactions.stream().mapToDouble(val -> val).average().orElse(0.0);
    }

    /**
     * Takes all {@link Agent}s of a given types individual satisfactions and calculates the average satisfaction of the {@link Agent}s
     * of that type.
     *
     * @param agents    {@link ArrayList} of all the {@link Agent}s that exist in the current simulation.
     * @param agentType The type for which to calculate the average satisfaction of all {@link Agent}s of that type.
     * @return The average satisfaction between 0 and 1 of all {@link Agent}s of the given type.
     */
    static double averageAgentSatisfaction(@NotNull ArrayList<Agent> agents, int agentType) {
        ArrayList<Double> agentSatisfactions =
                agents.stream()
                        .filter(a -> a.getAgentType() == agentType)
                        .map(a -> a.calculateSatisfaction(null))
                        .collect(Collectors.toCollection(ArrayList::new));
        return agentSatisfactions.stream().mapToDouble(val -> val).average().orElse(0.0);
    }

    /**
     * Takes all {@link Agent}s of a given types individual satisfactions and calculates the variance between the average
     * satisfaction of the {@link Agent}s of that type.
     *
     * @param agents    {@link ArrayList} of all the {@link Agent}s that exist in the current simulation.
     * @param agentType The type for which to calculate the variance between the average satisfactions of all {@link Agent}s of
     *                  that type.
     * @return The variance between the average satisfactions of all {@link Agent}s of the given type.
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
        if (groupSize == 0) return 0.0;

        double populationVariance = sumDiffsSquared / (double) (groupSize);
        return sqrt(populationVariance);
    }

    /**
     * Takes all {@link Agent}s of a given types individual satisfactions and calculates the quartile ranges, min, max, median
     * and 95th percentile satisfaction values for that type.
     *
     * @param agents    {@link ArrayList} of all the {@link Agent}s that exist in the current simulation.
     * @param agentType The agent type for which to calculate the various values.
     * @return The quartile ranges, min, max, median and 95th percentile satisfaction values for
     * {@link Agent}s of the given type.
     */
    static double @NotNull [] statisticalValues(@NotNull ArrayList<Agent> agents, int agentType) {
        ArrayList<Double> agentSatisfactions =
                agents.stream()
                        .filter(a -> a.getAgentType() == agentType)
                        .map(a -> a.calculateSatisfaction(null))
                        .sorted()
                        .collect(Collectors.toCollection(ArrayList::new));

        double[] statValues = new double[6];

        int size = agentSatisfactions.size();

        double uq;
        double lq;
        double ninetyfifth;
        double max = size != 0 ? agentSatisfactions.get(size - 1) : 0;
        double min = size != 0 ? agentSatisfactions.get(0) : 0;
        double median;

        double[] satArray = new double[size];
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
                uq = 0 != uqSet.length ? uqSet[uqSet.length / 2] : 0;
            } else {
                lq = 0 != lqSet.length ? (lqSet[lqSet.length / 2] + lqSet[(lqSet.length / 2) - 1]) / 2 : 0;
                uq = 0 != uqSet.length ? (uqSet[uqSet.length / 2] + uqSet[(uqSet.length / 2) - 1]) / 2 : 0;
            }
        } else {
            median = 0;
            uq = 0;
            lq = 0;
        }

        ninetyfifth = size != 0 ? percentile(satArray, 95) : 0;

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
     * @param p  The percentile to calculate.
     * @return The percentile requested.
     */
    @Contract(pure = true)
    static double percentile(double @NotNull [] xs, int p) {
        // The sorted elements in X are taken as the 100(0.5/n)th, 100(1.5/n)th, ..., 100([n – 0.5]/n)th percentiles.
        int i = (int) (p * xs.length / 100.0 - 0.5);

        // Linear interpolation uses linear polynomials to find yi = f(xi), the values of the underlying function
        // Y = f(X) at the points in the vector or array x. Given the data points (x1, y1) and (x2, y2), where
        // y1 = f(x1) and y2 = f(x2), linear interpolation finds y = f(x) for a given x between x1 and x2 as follows:
        return i != (xs.length - 1) ? xs[i] + (xs[i + 1] - xs[i]) * (p / 100.0 - (i + 0.5) / xs.length) / ((i + 1.5) / xs.length - (i + 0.5) / xs.length) : xs[i];
    }

    /**
     * Returns the optimum average satisfaction possible for all {@link Agent}s given the current requests and allocations in
     * the simulation.
     *
     * @param agents {@link ArrayList} of all the {@link Agent}s that exist in the current simulation.
     * @return The highest possible average satisfaction between 0 and 1 of all {@link Agent}s in the simulation.
     */
    static double optimumAgentSatisfaction(@NotNull ArrayList<Agent> agents) {
        ArrayList<Integer> allRequestedSlots = new ArrayList<>();
        ArrayList<Integer> allAllocatedSlots = new ArrayList<>();

        agents.forEach(a -> {
            allRequestedSlots.addAll(a.publishRequestedTimeSlots());
            allAllocatedSlots.addAll(a.publishAllocatedTimeSlots());
        });

        // Stores the number of slots that could potentially be fulfilled with perfect trading.
        double satisfiedSlots = 0;

        // Stores the total number of slots requested by all {@link Agent}s.
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
