package resource_exchange_arena;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

class Agent {
    // Unique identifier for the agent.
    int agentID;

    // Instance variables to store the agents state, relations and ongoing exchanges.
    private int agentType;
    private final boolean usesSocialCapital;
    private boolean madeInteraction;
    private final int numberOfTimeSlotsWanted;
    private final ArrayList<Integer> requestedTimeSlots = new ArrayList<>();
    private ArrayList<Integer> allocatedTimeSlots = new ArrayList<>();
    private final ArrayList<ArrayList<Integer>> favoursOwed = new ArrayList<>();
    private final ArrayList<ArrayList<Integer>> favoursGiven = new ArrayList<>();
    private ArrayList<Integer> exchangeRequestReceived = new ArrayList<>();
    private boolean exchangeRequestApproved;
    private int totalSocialCapital;
    private int dailySocialCapitalExchanges;
    private int dailyNoSocialCapitalExchanges;
    private int dailyRejectedReceivedExchanges;
    private int dailyRejectedRequestedExchanges;
    private int dailyAcceptedRequestedExchanges;

    /**
     * {@link Agent}s represent the individual consumers in the simulation.
     *
     * @param agentID This is an {@link Integer} value that is unique to the individual {@link Agent} and used to identify it to others in the {@link ResourceExchangeArena}.
     * @param agentType An {@link Integer} value denoting the {@link Agent} type, and thus how it will behave.
     * @param slotsPerAgent An {@link Integer} value representing the number of time slots each {@link Agent} requires.
     * @param agents An {@link ArrayList} of all the {@link Agent}s that exist in the current simulation.
     * @param socialCapital Determines whether the {@link Agent} uses {@code socialCapital}.
     */
    Agent(int agentID, int agentType, int slotsPerAgent, @NotNull ArrayList<Agent> agents, boolean socialCapital){
        this.agentID = agentID;
        this.agentType = agentType;
        this.usesSocialCapital = socialCapital;

        madeInteraction = false;
        numberOfTimeSlotsWanted = slotsPerAgent;
        totalSocialCapital = 0;
        dailySocialCapitalExchanges = 0;
        dailyNoSocialCapitalExchanges = 0;
        dailyRejectedReceivedExchanges = 0;
        dailyRejectedRequestedExchanges = 0;
        dailyAcceptedRequestedExchanges = 0;

        // Add the Agent to the ExchangeArenas list of participating Agents.
        agents.add(this);
    }

    /**
     * Getter for whether the {@link Agent} uses social capital.
     *
     * @return Whether the {@link Agent} uses social capital.
     */
    boolean usesSocialCapital() {
        return usesSocialCapital;
    }

    /**
     * Used to change an {@link Agent}'s type during a simulation.
     *
     * @param type {@link Integer} value representing the type the {@link Agent} should become, types are listed in the {@link ResourceExchangeArena}.
     */
    void setType(int type) {
        agentType = type;
    }

    /**
     * Getter for whether the {@link Agent} has been involved in an interaction.
     *
     * @return Whether the {@link Agent} has been involved in an interaction.
     */
    boolean madeInteraction() {
        return madeInteraction;
    }

    /**
     * Setter for whether the {@link Agent} has been involved in an interaction.
     *
     * @param state {@link Boolean} value representing whether the {@link Agent} has made an interaction during this exchange round.
     */
    void setMadeInteraction(boolean state) {
        madeInteraction = state;
    }

    /**
     * Getter for the number of timeslots requested.
     *
     * @return The number of timeslots the {@link Agent} wants.
     */
    int numberOfTimeSlotsWanted() {
        return numberOfTimeSlotsWanted;
    }

    /**
     * Getter for whether the {@link Agent} approved the current received exchange request.
     *
     * @return Whether the {@link Agent} approved the current received exchange request.
     */
    boolean getExchangeRequestApproved() {
        return exchangeRequestApproved;
    }

    /**
     * Setter for whether the {@link Agent} approved the current received exchange request.
     *
     * @param approved Whether the {@link Agent} has approved the current received exchange request.
     */
    void setExchangeRequestApproved(boolean approved) {
        exchangeRequestApproved = approved;
    }

    /**
     * Getter for the currently received exchange request.
     *
     * @return The currently received exchange request.
     */
    ArrayList<Integer> getExchangeRequestReceived() {
        return exchangeRequestReceived;
    }

    /**
     * Clears the exchange request list.
     */
    void setExchangeRequestReceived() {
        exchangeRequestReceived.clear();
    }

    /**
     * Getter for the favours the {@link Agent} owes.
     *
     * @return All the favours the {@link Agent} owes.
     */
    ArrayList<ArrayList<Integer>> getFavoursOwed() {
        return favoursOwed;
    }

    /**
     * Getter for the favours given by the {@link Agent}.
     *
     * @return All the favours given by the {@link Agent}, i.e. the other {@link Agent}s who owe this {@link Agent} a favour.
     */
    ArrayList<ArrayList<Integer>> getFavoursGiven() {
        return favoursGiven;
    }

    /**
     * Identifies all other {@link Agent}s in the {@link ResourceExchangeArena} and initialises counts of favours given to and received from
     * each other {@link Agent}.
     *
     * @param agents An {@link ArrayList} of all the {@link Agent}s that exist in the current simulation.
     */
    void initializeFavoursStore(ArrayList<Agent> agents) {
        if (usesSocialCapital) {
            if (!favoursGiven.isEmpty()) {
                favoursGiven.clear();
            }
            if (!favoursOwed.isEmpty()) {
                favoursOwed.clear();
            }
            // Initially, no favours are owed or have been given to any other Agent.
            // Store a reference to the new relations in the Agents corresponding lists of relations.
            agents.stream().filter(a -> a.agentID != agentID).forEach(a -> {
                ArrayList<Integer> favoursOwedRelation = new ArrayList<>();
                ArrayList<Integer> favoursGivenRelation = new ArrayList<>();
                favoursOwedRelation.add(a.agentID);
                favoursOwedRelation.add(0);
                favoursGivenRelation.add(a.agentID);
                favoursGivenRelation.add(0);
                favoursOwed.add(favoursOwedRelation);
                favoursGiven.add(favoursGivenRelation);
            });

            totalSocialCapital = 0;
        }
    }

    /**
     * Getter for the current amount of unspent social capital the {@link Agent} has.
     *
     * @return The current amount of unspent social capital the {@link Agent} has.
     */
    int getUnspentSocialCapital() {
        return totalSocialCapital;
    }

    /**
     * Increases the total social capital tracker by 1.
     */
    void gainedSocialCapital() {
        totalSocialCapital++;
    }

    /**
     * Reduces the total social capital tracker by 1.
     */
    void lostSocialCapital() {
        totalSocialCapital--;
    }

    /**
     * Increases the daily counter for exchange requests rejected.
     */
    void requestRejected() {
        dailyRejectedRequestedExchanges++;
    }

    /**
     * Getter method for retrieving the {@link Agent}'s type.
     *
     * @return The {@link Agent}'s type.
     */
    int getAgentType() {
        return agentType;
    }

    /**
     * Resets the daily information being tracked, called once per {@link Day}.
     */
    void resetDailyTracking() {
        dailySocialCapitalExchanges = 0;
        dailyNoSocialCapitalExchanges = 0;
        dailyRejectedReceivedExchanges = 0;
        dailyRejectedRequestedExchanges = 0;
        dailyAcceptedRequestedExchanges = 0;
    }

    /**
     * Getter method for retrieving the number of exchanges approved due to social capital.
     *
     * @return The number of exchanges approved due to social capital.
     */
    int getSocialCapitalExchanges() {
        return dailySocialCapitalExchanges;
    }

    /**
     * Getter method for retrieving the number of exchanges approved without social capital.
     *
     * @return The number of exchanges approved without social capital.
     */
    int getNoSocialCapitalExchanges() {
        return dailyNoSocialCapitalExchanges;
    }

    /**
     * Getter method for retrieving the number of exchanges rejected by this {@link Agent}.
     *
     * @return The number of exchanges rejected by this {@link Agent}.
     */
    int getRejectedReceivedExchanges() {
        return dailyRejectedReceivedExchanges;
    }

    /**
     * Getter method for retrieving the number of exchanges requested by this {@link Agent} which were rejected.
     *
     * @return The number of exchanges requested by this {@link Agent} that were rejected.
     */
    int getRejectedRequestedExchanges() {
        return dailyRejectedRequestedExchanges;
    }

    /**
     * Getter method for retrieving the number of exchanges requested by this {@link Agent} that were accepted.
     *
     * @return The number of exchanges requested by this {@link Agent} that were accepted.
     */
    int getAcceptedRequestedExchanges() {
        return dailyAcceptedRequestedExchanges;
    }

    /**
     * Checks the time slots that exist in the simulation and makes a new request for a number of unique time slots,
     * according to how many slots the {@link Agent} wants and the given demand curve.
     *
     * @param demandCurve An array of {@link Double}s representing the demand curve that the {@link Agent} should base its requests around.
     * @return The time slots that the {@link Agent} has requested.
     */
    ArrayList<Integer> requestTimeSlots(double[] demandCurve, double totalDemand) {

        if (!requestedTimeSlots.isEmpty()) {
            requestedTimeSlots.clear();
        }

        for (int i = 1; i <= numberOfTimeSlotsWanted; i++) {
            // Get the simulations seeded Random object.
            Random random = ResourceExchangeArena.random;

            // Selects a time slot based on the demand curve.
            int wheelSelector = random.nextInt((int)(totalDemand * 10));
            int wheelCalculator = 0;
            int timeSlot = 0;
            while (wheelCalculator < wheelSelector) {
                wheelCalculator = wheelCalculator + ((int)(demandCurve[timeSlot] * 10));
                timeSlot++;
            }

            // Ensures all requested time slots are unique.
            if (requestedTimeSlots.contains(timeSlot)) i--;
            else requestedTimeSlots.add(timeSlot);
        }
        return requestedTimeSlots;
    }

    /**
     * Getter method for retrieving the time slots that the {@link Agent} has currently requested.
     *
     * @return The time slots that the {@link Agent} has requested.
     */
    ArrayList<Integer> publishRequestedTimeSlots() {
        return requestedTimeSlots;
    }

    /**
     * Setter method for storing the time slots the {@link Agent} has been allocated.
     *
     * @param allocatedTimeSlots An allocation of time slots given by the {@link ResourceExchangeArena}.
     */
    void receiveAllocatedTimeSlots(ArrayList<Integer> allocatedTimeSlots) {
        this.allocatedTimeSlots = allocatedTimeSlots;
    }

    /**
     * Getter method for retrieving the time slots that the {@link Agent} is currently allocated.
     *
     * @return The time slots that the {@link Agent} is allocated.
     */
    ArrayList<Integer> publishAllocatedTimeSlots() {
        return allocatedTimeSlots;
    }

    /**
     * Shares the time slots that are currently allocated to the {@link Agent} that it may potentially be willing to
     * exchange under certain circumstances.
     *
     * @return The time slots that the {@link Agent} is allocated but may potentially exchange.
     */
    ArrayList<Integer> publishUnlockedTimeSlots() {
        return new ArrayList<>(nonExistingTimeSlots(allocatedTimeSlots, requestedTimeSlots));
    }

    /**
     * Takes two arrays of time slots, and returns the time slots from the first array that are not present in the
     * second array.
     *
     * @param potentialTimeSlots The time slots that may be returned if not present in the second array.
     * @param timeSlotsToAvoid The time slots that shouldn't be returned.
     * @return The time slots from the {@code potentialTimeSlots} array that are not present in the {@code timeSlotsToAvoid} array.
     */
    private @NotNull ArrayList<Integer> nonExistingTimeSlots(
            @NotNull ArrayList<Integer> potentialTimeSlots,
            ArrayList<Integer> timeSlotsToAvoid) {
        // By making a new copy of the time slots to avoid, the array list can be modified without modifying the
        // referenced list.
        ArrayList<Integer> localTimeSlotsToAvoid = new ArrayList<>(timeSlotsToAvoid);
        ArrayList<Integer> timeSlots = new ArrayList<>();
        potentialTimeSlots.forEach(timeSlot -> {
            if (!localTimeSlotsToAvoid.contains(timeSlot)) {
                timeSlots.add(timeSlot);
            } else {
                // Once a time slot in the list of time slots to avoid has been considered once it is removed encase
                // of duplicates.
                localTimeSlotsToAvoid.remove(Integer.valueOf(timeSlot));
            }
        });
        return timeSlots;
    }

    /**
     * Make an exchange request for a time slot that another {@link Agent} has published as a possible exchange, and that this
     * {@link Agent} wants but has not currently been allocated.
     *
     * @param advertisingBoard The time slots that {@link Agent}s have said they may exchange.
     * @return A time slot owned by another {@link Agent} that this {@link Agent} is requesting an exchange for, {@code null} otherwise.
     */
    ArrayList<Integer> requestExchange(ArrayList<ArrayList<Integer>> advertisingBoard) {
        ArrayList<Integer> targetTimeSlots = nonExistingTimeSlots(requestedTimeSlots, allocatedTimeSlots);
        ArrayList<Integer> potentialExchange = new ArrayList<>();
        // If all requested have been allocated, the Agent has no need to request an exchange.
        if (!targetTimeSlots.isEmpty()) {
            // Search the advertising board for a potential exchange.
            Collections.shuffle(advertisingBoard, ResourceExchangeArena.random);
            advertSelection:
            for (ArrayList<Integer> advert : advertisingBoard) {
                for (int j = 1; j < advert.size(); j++) {
                    if (targetTimeSlots.contains(advert.get(j))) {
                        // Only take the part of the advert that is relevant, and split adverts with multiple
                        // exchangeable time slots into multiple adverts.
                        ArrayList<Integer> selectedAdvert = new ArrayList<>();
                        selectedAdvert.add(advert.get(0));
                        selectedAdvert.add(advert.get(j));
                        potentialExchange = selectedAdvert;
                        break advertSelection;
                    }
                }
            }
        }
        return potentialExchange;
    }

    /**
     * Stores a request for an exchange received from another {@link Agent}.
     *
     * @param request An {@link Agent}'s ID, the time slot that it wants and the time slot that it is willing to exchange.
     * @param partnersAgentType The strategy being used by the {@link Agent} that has fulfilled the exchange request.
     */
    void receiveExchangeRequest(ArrayList<Integer> request, int partnersAgentType) {
        exchangeRequestReceived = request;
        exchangeRequestReceived.add(partnersAgentType);
    }

        
    /**
     * Returns the most recent received from another {@link Agent}.
     *
     * @return The most recent received from another {@link Agent}.
     */
    ArrayList<Integer> getExchangeRequest() {
        return exchangeRequestReceived;
    }

    /**
     * Determine whether the {@link Agent} will be willing to accept a received exchange request.
     * 
     * @return Whether the request was accepted.
     */
    boolean considerRequest() {
        double currentSatisfaction = calculateSatisfaction(null);
        // Create a new local list of time slots in order to test how the Agents satisfaction would change after the
        // potential exchange.
        ArrayList<Integer> potentialAllocatedTimeSlots = new ArrayList<>(allocatedTimeSlots);
        // Check this Agent still has the time slot requested.
        if (potentialAllocatedTimeSlots.contains(exchangeRequestReceived.get(1))) {
            potentialAllocatedTimeSlots.remove(exchangeRequestReceived.get(1));

            // Replace the requested slot with the requesting agents unwanted time slot.
            potentialAllocatedTimeSlots.add(exchangeRequestReceived.get(2));

            double potentialSatisfaction = calculateSatisfaction(potentialAllocatedTimeSlots);
            

            // if (agentType == ResourceExchangeArena.SOCIAL && exchangeRequestReceived.get(3) == ResourceExchangeArena.SOCIAL) {
            if (agentType == ResourceExchangeArena.SOCIAL) {
                // Social Agents accept offers that improve their satisfaction or if they have negative social capital
                // with the Agent who made the request.
                if (Double.compare(potentialSatisfaction, currentSatisfaction) > 0) {
                    exchangeRequestApproved = true;
                    dailyNoSocialCapitalExchanges++;
                } else if (Double.compare(potentialSatisfaction, currentSatisfaction) == 0) {
                    if (usesSocialCapital) {
                        int favoursOwedToRequester;
                        int favoursGivenToRequester;
                        favoursOwedToRequester = favoursOwed.stream()
                                .filter(favours -> favours.get(0).equals(exchangeRequestReceived.get(0)))
                                .findFirst()
                                .map(favours -> favours.get(1))
                                .orElse(0);
                        favoursGivenToRequester = favoursGiven.stream()
                                .filter(favours -> favours.get(0).equals(exchangeRequestReceived.get(0)))
                                .findFirst()
                                .map(favours -> favours.get(1))
                                .orElse(0);

                        if (favoursOwedToRequester > favoursGivenToRequester) {
                            exchangeRequestApproved = true;
                            dailySocialCapitalExchanges++;
                        }
                    } else {
                        // When social capital isn't used, social agents always accept neutral exchanges.
                        exchangeRequestApproved = true;
                        dailyNoSocialCapitalExchanges++;
                    }
                }
            } else {
                // Selfish Agents and Agents with no known type use the default selfish approach.
                // Selfish Agents only accept offers that improve their individual satisfaction.
                if (Double.compare(potentialSatisfaction, currentSatisfaction) > 0) {
                    exchangeRequestApproved = true;
                    dailyNoSocialCapitalExchanges++;
                }
            }
            if (!exchangeRequestApproved) {
                dailyRejectedReceivedExchanges++;
            }
        }

        return exchangeRequestApproved;
    }

    /**
     * Checks whether the {@link Agent} still has a requested time slot before exchanging it.
     *
     * @param timeSlot The time slot to check the {@link Agent}'s allocated time slots for.
     * @return Whether the time slot belongs to the {@link Agent} and so can be exchanged.
     */
    boolean finalCheck(int timeSlot) {
        return allocatedTimeSlots.contains(timeSlot);
    }

    /**
     * Completes an exchange that was originally requested by this {@link Agent}, making the exchange and updating this {@link Agent}'s
     * relationship with the other {@link Agent} involved.
     *
     * @param offer The exchange that is to be completed.
     * @param agentID The ID of the {@link Agent} that has fulfilled the exchange request.
     * @param partnersAgentType The strategy being used by the {@link Agent} that has fulfilled the exchange request.
     * @return Whether the other {@link Agent} gained social capital.
     */
    boolean completeRequestedExchange(@NotNull ArrayList<Integer> offer, int agentID, int partnersAgentType) {
        boolean SCGain = false;

        double previousSatisfaction = calculateSatisfaction(allocatedTimeSlots);
        // Update the Agents allocated time slots.
        allocatedTimeSlots.remove(offer.get(2));
        allocatedTimeSlots.add(offer.get(1));

        double newSatisfaction = calculateSatisfaction(allocatedTimeSlots);

        // Update the Agents relationship with the other Agent involved in the exchange.
        if (usesSocialCapital) {
            // if (Double.compare(newSatisfaction, previousSatisfaction) > 0
            //         && agentType == ResourceExchangeArena.SOCIAL && partnersAgentType == ResourceExchangeArena.SOCIAL) {
                if (Double.compare(newSatisfaction, previousSatisfaction) > 0
                    && agentType == ResourceExchangeArena.SOCIAL) {

                for (ArrayList<Integer> favours : favoursOwed) {
                    if (favours.get(0).equals(agentID)) {
                        int currentFavour = favours.get(1);
                        favours.set(1, currentFavour + 1);
                        break;
                    }
                }
                SCGain = true;
            }
        }
        dailyAcceptedRequestedExchanges++;
        return SCGain;
    }

    /**
     * Completes an exchange that was originally requested by another {@link Agent}, making the exchange and updating
     * this {@link Agent}'s relationship with the other {@link Agent} involved.
     *
     * @param offer The exchange that is to be completed.
     * @param partnersAgentType The strategy being used by the {@link Agent} that requested the exchange request.
     * @return Whether the other {@link Agent} gained social capital.
     */
    boolean completeReceivedExchange(@NotNull ArrayList<Integer> offer, int partnersAgentType) {
        boolean scLoss = false;

        double previousSatisfaction = calculateSatisfaction(allocatedTimeSlots);
        // Update the Agents allocated time slots.
        allocatedTimeSlots.remove(offer.get(1));
        allocatedTimeSlots.add(offer.get(2));
        double newSatisfaction = calculateSatisfaction(allocatedTimeSlots);

        // Update the Agents relationship with the other Agent involved in the exchange.
        if (usesSocialCapital) {
            // if (Double.compare(newSatisfaction, previousSatisfaction) <= 0
            //         && agentType == ResourceExchangeArena.SOCIAL && partnersAgentType == ResourceExchangeArena.SOCIAL) {
                
            if (Double.compare(newSatisfaction, previousSatisfaction) <= 0
                    && agentType == ResourceExchangeArena.SOCIAL) {

                for (ArrayList<Integer> favours : favoursGiven) {
                    if (favours.get(0).equals(offer.get(0))) {
                        int currentFavour = favours.get(1);
                        favours.set(1, currentFavour + 1);
                        break;
                    }
                }
                scLoss = true;
            }
        }
        return scLoss;
    }

    /**
     * Calculates the {@link Agent}'s satisfaction with a given list of time slots by comparing the list with the time slots
     * requested by this {@link Agent}.
     *
     * @param timeSlots The set of time slots to consider.
     * @return The {@link Agent}'s satisfaction with the time slots given.
     */
    double calculateSatisfaction(ArrayList<Integer> timeSlots) {
        if (timeSlots == null) {
            timeSlots = this.allocatedTimeSlots;
        }

        ArrayList<Integer> tempRequestedTimeSlots = new ArrayList<>(requestedTimeSlots);

        // Count the number of the given time slots that match the Agents requested time slots.
        double satisfiedSlots = 0;
        for (int timeSlot : timeSlots) {
            if (tempRequestedTimeSlots.contains(timeSlot)) {
                tempRequestedTimeSlots.remove(Integer.valueOf(timeSlot));
                satisfiedSlots++;
            }
        }
        // Return the Agents satisfaction with the given time slots, between 1 and 0.
        return satisfiedSlots / numberOfTimeSlotsWanted;
    }
}
