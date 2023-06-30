package resource_exchange_arena;

import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Day {
    // List of all the possible allocations that exist in the current simulation.
    private final ArrayList<Integer> availableTimeSlots = new ArrayList<>();

    int socPop;
    int selPop;
    double socSat;
    double selSat;
    double socSD;
    double selSD;

    double[] socialStatValues;
    double[] selfishStatValues;

    double randomAllocations;
    double optimumAllocations;

    /**
     * @param demandCurves           {@link Double} arrays of demand used by the {@link Agent}s, when multiple curves are used the {@link Agent}s are split equally between the curves.
     * @param totalDemandValues      {@link Double} values representing the sum of all values in their associated demand curves.
     * @param availabilityCurve      {@link Integer} array representing the amount of energy available at each timeslot.
     * @param totalAvailability      {@link Integer} value representing the total energy available throughout the {@link Day}.
     * @param day                    {@link Integer} value representing the current {@link Day} being simulated.
     * @param maxExchanges           Stores the highest number of exchange rounds reached each simulation.
     * @param populationSize         {@link Integer} value representing the size of the initial {@link Agent} population.
     * @param uniqueTimeSlots        {@link Integer} value representing the number of unique time slots available in the simulation.
     * @param slotsPerAgent          {@link Integer} value representing the number of time slots each {@link Agent} requires.
     * @param numberOfAgentsToEvolve {@link Integer} value representing the number of {@link Agent}s whose strategy will change at the end of each {@link Day}.
     * @param uniqueAgentTypes       {@link Integer} {@link ArrayList} containing each unique {@link Agent} type that exists when the simulation begins.
     * @param agents                 {@link ArrayList} of all the {@link Agent}s that exist in the current simulation.
     * @param dailyDataWriter        Used to store data regarding the state of the system at the end of each {@link Day}.
     * @param perAgentDataCSVWriter  Used to store data regarding the state of the {@link Agent} at the end of each {@link Day}.
     * @param eachRoundDataCSVWriter Used to store data regarding the state of the system at the end of each round.
     * @param run                    {@link Integer} value identifying the current simulation run.
     * @throws IOException On input error
     */
    Day(
            double[][] demandCurves,
            double[] totalDemandValues,
            int[] availabilityCurve,
            int totalAvailability,
            int day,
            ArrayList<Integer> maxExchanges,
            int populationSize,
            int uniqueTimeSlots,
            int slotsPerAgent,
            int numberOfAgentsToEvolve,
            ArrayList<Integer> uniqueAgentTypes,
            ArrayList<Agent> agents,
            FileWriter dailyDataWriter,
            FileWriter perAgentDataCSVWriter,
            FileWriter eachRoundDataCSVWriter,
            int run
    ) throws IOException {

        if (!availableTimeSlots.isEmpty()) {
            availableTimeSlots.clear();
        }

        // Fill the available time slots with all the slots that exist each day.
        int requiredTimeSLots = populationSize * slotsPerAgent;

        for (int i = 1; i <= requiredTimeSLots; i++) {
            // Get the simulations seeded Random object.
            Random random = ResourceExchangeArena.random;

            // Selects a time slot based on the demand curve.
            int wheelSelector = random.nextInt(totalAvailability);
            int wheelCalculator = 0;
            int timeSlot = 0;
            while (wheelCalculator < wheelSelector) {
                wheelCalculator = wheelCalculator + (availabilityCurve[timeSlot]);
                timeSlot++;
            }
            availableTimeSlots.add(timeSlot);
        }


        // Agents start the day by requesting and receiving an allocation of time slots.
        Collections.shuffle(agents, ResourceExchangeArena.random);
        ArrayList<Integer> curves = new ArrayList<>();

        int curve = 0;
        for (int i = 0; i < agents.size(); i++) {
            curves.add(curve);
            curve++;
            if (curve >= demandCurves.length) {
                curve = 0;
            }
        }
        Collections.shuffle(curves);

        for (Agent a : agents) {
            a.resetDailyTracking();
            int selector = curves.remove(0);
            ArrayList<Integer> requestedTimeSlots = a.requestTimeSlots(demandCurves[selector], totalDemandValues[selector]);
            ArrayList<Integer> allocatedTimeSlots = getRandomInitialAllocation(requestedTimeSlots);
            a.receiveAllocatedTimeSlots(allocatedTimeSlots);
        }

        // The random and optimum average satisfaction scores are calculated before exchanges take place.
        randomAllocations = CalculateSatisfaction.averageAgentSatisfaction(agents);
        optimumAllocations = CalculateSatisfaction.optimumAgentSatisfaction(agents);

        // A pre-determined number of pairwise exchanges take place, during each exchange all agents have a chance to
        // trade with another agent.
        int currentExchanges = 0;
        int timeout = 0;
        int maxTimeout = 10;

        while (timeout < maxTimeout) {
            Exchange current = new Exchange(
                    run,
                    day,
                    currentExchanges,
                    uniqueAgentTypes,
                    agents,
                    eachRoundDataCSVWriter
            );

            if (current.noExchanges) timeout++;
            else timeout = 0;

            currentExchanges++;
        }

        maxExchanges.add(currentExchanges);

        socPop = 0;
        selPop = 0;

        for (Agent a : agents) {
            if (a.getAgentType() == ResourceExchangeArena.SOCIAL) {
                socPop++;
            } else if (a.getAgentType() == ResourceExchangeArena.SELFISH) {
                selPop++;
            }
        }

        socSat = CalculateSatisfaction.averageAgentSatisfaction(agents, ResourceExchangeArena.SOCIAL);
        selSat = CalculateSatisfaction.averageAgentSatisfaction(agents, ResourceExchangeArena.SELFISH);
        socSD = CalculateSatisfaction.averageSatisfactionStandardDeviation(agents, ResourceExchangeArena.SOCIAL);
        selSD = CalculateSatisfaction.averageSatisfactionStandardDeviation(agents, ResourceExchangeArena.SELFISH);

        socialStatValues = CalculateSatisfaction.statisticalValues(agents, ResourceExchangeArena.SOCIAL);
        selfishStatValues = CalculateSatisfaction.statisticalValues(agents, ResourceExchangeArena.SELFISH);

        dailyDataWriter.append(String.valueOf(run));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(day));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(socPop));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(selPop));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(socSat));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(selSat));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(socSD));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(selSD));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(socialStatValues[0]));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(selfishStatValues[0]));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(socialStatValues[1]));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(selfishStatValues[1]));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(socialStatValues[2]));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(selfishStatValues[2]));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(socialStatValues[3]));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(selfishStatValues[3]));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(socialStatValues[4]));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(selfishStatValues[4]));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(socialStatValues[5]));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(selfishStatValues[5]));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(randomAllocations));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(optimumAllocations));
        dailyDataWriter.append("\n");


        for (Agent a : agents) {
            perAgentDataCSVWriter.append(String.valueOf(run));
            perAgentDataCSVWriter.append(",");

            perAgentDataCSVWriter.append(String.valueOf(day));
            perAgentDataCSVWriter.append(",");

            perAgentDataCSVWriter.append(String.valueOf(a.getAgentType()));
            perAgentDataCSVWriter.append(",");

            perAgentDataCSVWriter.append(String.valueOf(a.calculateSatisfaction(null)));
            perAgentDataCSVWriter.append(",");

            perAgentDataCSVWriter.append(String.valueOf(a.getRejectedReceivedExchanges()));
            perAgentDataCSVWriter.append(",");

            perAgentDataCSVWriter.append(String.valueOf(a.getSocialCapitalExchanges() + a.getNoSocialCapitalExchanges()));
            perAgentDataCSVWriter.append(",");

            perAgentDataCSVWriter.append(String.valueOf(a.getRejectedRequestedExchanges()));
            perAgentDataCSVWriter.append(",");

            perAgentDataCSVWriter.append(String.valueOf(a.getAcceptedRequestedExchanges()));
            perAgentDataCSVWriter.append(",");

            perAgentDataCSVWriter.append(String.valueOf(a.getSocialCapitalExchanges()));
            perAgentDataCSVWriter.append(",");

            perAgentDataCSVWriter.append(String.valueOf(a.getNoSocialCapitalExchanges()));
            perAgentDataCSVWriter.append(",");

            perAgentDataCSVWriter.append(String.valueOf(a.getUnspentSocialCapital()));
            perAgentDataCSVWriter.append("\n");
        }

        /*
         * To facilitate social learning, for the number of the agents who are able to consider changing their strategy,
         * an Agent is selected at random, and then a second agent is selected to be observed. The first agent selected
         * checks whether their performance was weaker than the agent observed, if so they have a chance to copy the
         * strategy used by the observed agent in the previous day, with the likelihood of copying their strategy
         * proportional to the difference between their individual satisfactions.
         *
         * @param agents {@link ArrayList} of all the agents that exist in the current simulation.
         * @param slotsPerAgent Integer value representing the number of time slots each agent requires.
         * @param numberOfAgentsToEvolve Integer value representing the number of Agents whose strategy may change at
         *                               the end of each day.
         */
        new SocialLearning(agents, slotsPerAgent, numberOfAgentsToEvolve);
    }

    /**
     * Gives a random initial time slot allocation to an {@link Agent} based on the number of time slots it requests and the
     * time slots that are currently available.
     *
     * @param requestedTimeSlots The time slots that the {@link Agent} has requested.
     * @return A list of time slots to allocated to the {@link Agent}.
     */
    private @NotNull ArrayList<Integer> getRandomInitialAllocation(@NotNull ArrayList<Integer> requestedTimeSlots) {
        ArrayList<Integer> timeSlots = new ArrayList<>();

        for (int requestedTimeSlot = 1; requestedTimeSlot <= requestedTimeSlots.size(); requestedTimeSlot++) {
            // Only allocate time slots if there are slots available to allocate.
            if (!availableTimeSlots.isEmpty()) {
                int selector = ResourceExchangeArena.random.nextInt(availableTimeSlots.size());
                int timeSlot = availableTimeSlots.get(selector);

                timeSlots.add(timeSlot);
                availableTimeSlots.remove(selector);
            } else {
                System.out.println("Error: No Timeslots Available");
            }
        }
        return timeSlots;
    }
}
