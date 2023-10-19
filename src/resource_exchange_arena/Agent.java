package resource_exchange_arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class Agent {
    // Unique identifier for the agent.
    int agentID;

    // Instance variables to store the agents state, relations and ongoing exchanges.
    private int agentType;
    private final boolean usesSocialCapital;
    private boolean madeInteraction;
    private final int numberOfTimeSlotsWanted;
    private final int uniqueTimeSlots;
    private ArrayList<Integer> requestedTimeSlots = new ArrayList<>();
    private ArrayList<Integer> allocatedTimeSlots = new ArrayList<>();
    private double[] satisfactionCurve;
    private List<SlotSatisfactionPair> timeSlotSatisfactions = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> favoursOwed = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> favoursGiven = new ArrayList<>();
    private ArrayList<Integer> exchangeRequestReceived = new ArrayList<>();
    private boolean exchangeRequestApproved;
    private int totalSocialCapital;
    private int dailySocialCapitalExchanges;
    private int dailyNoSocialCapitalExchanges;
    private int dailyRejectedReceivedExchanges;
    private int dailyRejectedRequestedExchanges;
    private int dailyAcceptedRequestedExchanges;

    /**
     * Agents represent the individual consumers in the simulation.
     *
     * @param agentID This is an integer value that is unique to the individual agent and used to identify it to others
     *                in the ExchangeArena.
     * @param agentType Integer value denoting the agent type, and thus how it will behave.
     * @param slotsPerAgent Integer value representing the number of time-slots each agent requires.
     * @param uniqueAgentTypes Integer ArrayList containing each unique agent type that exists when the simulation begins.
     * @param agents Array List of all the agents that exist in the current simulation.
     * @param socialCapital determines whether the agent uses socialCapital.
     * @param satisfactionCurve Double array that determines the satisfaction fall off for slots received close to the agents preferences.
     */
    Agent(int agentID, int agentType, int slotsPerAgent, int uniqueTimeSlots, ArrayList<Agent> agents, boolean socialCapital, double[] satisfactionCurve){
        this.agentID = agentID;
        this.agentType = agentType;
        this.usesSocialCapital = socialCapital;
        this.uniqueTimeSlots = uniqueTimeSlots;
        this.satisfactionCurve = satisfactionCurve;

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
     * Getter for whether the Agent uses social capital.
     *
     * @return boolean Returns whether the Agent uses social capital.
     */
    boolean usesSocialCapital() {
        return usesSocialCapital;
    }

    /**
     * Used to change an Agents type during a simulation.
     *
     * @param type Integer value representing the type the agent should become, types are listed in the main
     *             'ResourceExchangeArena' class.
     */
    void setType(int type) {
        agentType = type;
    }

    /**
     * Getter for whether the Agent has been involved in an interaction.
     *
     * @return boolean Returns whether the Agent has been involved in an interaction.
     */
    boolean madeInteraction() {
        return madeInteraction;
    }

    /**
     * Setter for whether the Agent has been involved in an interaction.
     *
     * @param state Boolean value representing whether the agent has made an interaction during this exchange round.
     */
    void setMadeInteraction(boolean state) {
        madeInteraction = state;
    }

    /**
     * Getter for the number of time-slots requested.
     *
     * @return boolean Returns the number of time-slots the Agent wants.
     */
    int numberOfTimeSlotsWanted() {
        return numberOfTimeSlotsWanted;
    }

    /**
     * Getter for whether the Agent approved the current received exchange request.
     *
     * @return boolean Returns whether the Agent approved the current received exchange request.
     */
    boolean getExchangeRequestApproved() {
        return exchangeRequestApproved;
    }

    /**
     * Setter for whether the Agent approved the current received exchange request.
     */
    void setExchangeRequestApproved(boolean approved) {
        exchangeRequestApproved = approved;
    }

    /**
     * Getter for the currently received exchange request.
     *
     * @return boolean Returns the currently received exchange request.
     */
    ArrayList<Integer> getExchangeRequestReceived() {
        return exchangeRequestReceived;
    }

    /**
     * Setter for the currently received exchange request.
     */
    void setExchangeRequestReceived() {
        exchangeRequestReceived.clear();
    }

    /**
     * Getter for the favours the agent owes.
     *
     * @return ArrayList<ArrayList<Integer>> Returns all the favours the agent owes.
     */
    ArrayList<ArrayList<Integer>> getFavoursOwed() {
        return favoursOwed;
    }

    /**
     * Getter for the favours given by the agent.
     *
     * @return ArrayList<ArrayList<Integer>> Returns all the favours given by the agent, i.e. the other agents who owe this agent a favour.
     */
    ArrayList<ArrayList<Integer>> getFavoursGiven() {
        return favoursGiven;
    }

    /**
     * Identifies all other Agents in the ExchangeArena and initialises counts of favours given to and received from
     * each other Agent.
     *
     * @param agents Array List of all the agents that exist in the current simulation.
     */
    void initializeFavoursStore(ArrayList<Agent> agents) {
        if (usesSocialCapital) {
            if (!favoursGiven.isEmpty()) {
                favoursGiven.clear();
            }
            if (!favoursOwed.isEmpty()) {
                favoursOwed.clear();
            }
            for (Agent a : agents) {
                if (a.agentID != agentID) {
                    ArrayList<Integer> favoursOwedRelation = new ArrayList<>();
                    ArrayList<Integer> favoursGivenRelation = new ArrayList<>();

                    // Initially, no favours are owed or have been given to any other Agent.
                    favoursOwedRelation.add(a.agentID);
                    favoursOwedRelation.add(0);

                    favoursGivenRelation.add(a.agentID);
                    favoursGivenRelation.add(0);

                    // Store a reference to the new relations in the Agents corresponding lists of relations.
                    favoursOwed.add(favoursOwedRelation);
                    favoursGiven.add(favoursGivenRelation);
                }
            }

            totalSocialCapital = 0;
        }
    }

    /**
     * Getter for the current amount of unspent social capital the agent has.
     *
     * @return boolean Returns the current amount of unspent social capital the agent has.
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
     * Getter method for retrieving the Agents type.
     *
     * @return int Return the Agents type.
     */
    int getAgentType() {
        return agentType;
    }

    /**
     * Resets the daily information being tracked, called once per day.
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
     * @return int Return the number of exchanges approved due to social capital.
     */
    int getSocialCapitalExchanges() {
        return dailySocialCapitalExchanges;
    }

    /**
     * Getter method for retrieving the number of exchanges approved without social capital.
     *
     * @return int Return the the number of exchanges approved without social capital.
     */
    int getNoSocialCapitalExchanges() {
        return dailyNoSocialCapitalExchanges;
    }

    /**
     * Getter method for retrieving the number of exchanges rejected by this agent.
     *
     * @return int Return the number of exchanges rejected by this agent.
     */
    int getRejectedReceivedExchanges() {
        return dailyRejectedReceivedExchanges;
    }

    /**
     * Getter method for retrieving the number of exchanges requested by this agent that were rejected.
     *
     * @return int Return the number of exchanges requested by this agent that were rejected.
     */
    int getRejectedRequestedExchanges() {
        return dailyRejectedRequestedExchanges;
    }

    /**
     * Getter method for retrieving the number of exchanges requested by this agent that were accepted.
     *
     * @return int Return the number of exchanges requested by this agent that were accepted.
     */
    int getAcceptedRequestedExchanges() {
        return dailyAcceptedRequestedExchanges;
    }

    /**
     * Checks the time-slots that exist in the simulation and makes a new request for a number of unique time-slots
     * according to how many slots the Agent wants and the given demand curve.
     *
     * @param demandCurve Double array representing the demand curve that the agent should base its requests around.
     * @return ArrayList<Integer> Returns the time-slots that the Agent has requested.
     */
    ArrayList<Integer> requestTimeSlots(double[] demandCurve, double totalDemand) {

        if (!requestedTimeSlots.isEmpty()) {
            requestedTimeSlots.clear();
        }

        for (int i = 1; i <= numberOfTimeSlotsWanted; i++) {
            // Get the simulations seeded Random object.
            Random random = ResourceExchangeArena.random;

            // Selects a time-slot based on the demand curve.
            int wheelSelector = random.nextInt((int)(totalDemand * 10)) + 1;
            int wheelCalculator = 0;
            int timeSlot = 0;
            while (wheelCalculator < wheelSelector) {
                wheelCalculator = wheelCalculator + ((int)(demandCurve[timeSlot] * 10));
                timeSlot++;
            }

            // Ensures all requested time-slots are unique.
            if (requestedTimeSlots.contains(timeSlot)) {
                i--;
            } else {
                requestedTimeSlots.add(timeSlot);
            }
        }

        timeSlotSatisfactions = calculateSatisfationPerSlot(requestedTimeSlots);

        return requestedTimeSlots;
    }


    List<SlotSatisfactionPair> calculateSatisfationPerSlot(ArrayList<Integer> requestedSlots) {
        List<SlotSatisfactionPair> tempTimeSlotSatisfactions = new ArrayList<>();
        // Calculate the potential satisfaction that each time-slot could give based on their proximity to requested time-slots.
        Double[] slotSatisfaction = new Double[uniqueTimeSlots];
        for (int i = 0; i < slotSatisfaction.length; i++) {
            slotSatisfaction[i] = 0.0;
        }
        for (int r : requestedTimeSlots) {
            int s = r - 1;
            slotSatisfaction[s] = satisfactionCurve[0];

            // Apply the adjustment values to neighboring elements
            for (int i = 1; i < satisfactionCurve.length; i++) {
                int leftIndex = s - i;
                int rightIndex = s + i;

                if (leftIndex < 0) {leftIndex += slotSatisfaction.length;}
                if (rightIndex >= slotSatisfaction.length) {rightIndex -= slotSatisfaction.length;}

                slotSatisfaction[leftIndex] = Math.max(slotSatisfaction[leftIndex], satisfactionCurve[i]);
                slotSatisfaction[rightIndex] = Math.max(slotSatisfaction[rightIndex], satisfactionCurve[i]);
            }
        }

        for (int i = 0; i < slotSatisfaction.length; i++) {
            tempTimeSlotSatisfactions.add(new SlotSatisfactionPair(i + 1, slotSatisfaction[i]));
        }

        return tempTimeSlotSatisfactions;
    }

    /**
     * Getter method for retrieving the time-slots that the Agent has currently requested.
     *
     * @return ArrayList<Integer> Returns the time-slots that the Agent has requested.
     */
    ArrayList<Integer> publishRequestedTimeSlots() {
        return requestedTimeSlots;
    }

    /**
     * Setter method for storing the time-slots the Agent has been allocated.
     *
     * @param allocatedTimeSlots An allocation of time-slots given by the ExchangeArena.
     */
    void receiveAllocatedTimeSlots(ArrayList<Integer> allocatedTimeSlots) {
        this.allocatedTimeSlots = allocatedTimeSlots;
    }

    /**
     * Getter method for retrieving the time-slots that the Agent is currently allocated.
     *
     * @return ArrayList<Integer> Returns the time-slots that the Agent is allocated.
     */
    ArrayList<Integer> publishAllocatedTimeSlots() {
        return allocatedTimeSlots;
    }

    /**
     * Shares the time-slots that are currently allocated to the Agent that it may potentially be willing to
     * exchange under certain circumstances.
     *
     * @return ArrayList<Integer> Returns the time-slots that the Agent is allocated but may potentially exchange.
     */
    ArrayList<Integer> publishUnlockedTimeSlots() {
        ArrayList<Integer> unlockedTimeSlots;
        unlockedTimeSlots = new ArrayList<>(nonExistingTimeSlots(allocatedTimeSlots, requestedTimeSlots));

        List<SlotSatisfactionPair> unlockedTimeSlotSatisfactions = new ArrayList<>();
        for (SlotSatisfactionPair s : timeSlotSatisfactions) {
            if (unlockedTimeSlots.contains(s.timeSlot)) {unlockedTimeSlotSatisfactions.add(s);}
        }

        Collections.sort(unlockedTimeSlotSatisfactions, (pair1, pair2) -> Double.compare(pair1.satisfaction, pair2.satisfaction));

        ArrayList<Integer> orderedUnlockedTimeSlots = new ArrayList<>();
        for (SlotSatisfactionPair s : unlockedTimeSlotSatisfactions) {
            orderedUnlockedTimeSlots.add(s.timeSlot);
        }

        return unlockedTimeSlots;
    }

    /**
     * Takes two arrays of time-slots, and returns the time-slots from the first array that are not present in the
     * second array.
     *
     * @param potentialTimeSlots the time-slots that may be returned if not present in the second array.
     * @param timeSlotsToAvoid the time-slots that shouldn't be returned..
     * @return ArrayList<Integer> Returns the time-slots from the potentialTimeSlots array that are not present in the
     *                            timeSlotsToAvoid array.
     */
    private ArrayList<Integer> nonExistingTimeSlots(
            ArrayList<Integer> potentialTimeSlots,
            ArrayList<Integer> timeSlotsToAvoid) {
        // By making a new copy of the time-slots to avoid, the array list can be modified without modifying the
        // referenced list.
        ArrayList<Integer> localTimeSlotsToAvoid = new ArrayList<>(timeSlotsToAvoid);
        ArrayList<Integer> timeSlots = new ArrayList<>();
        for (int timeSlot : potentialTimeSlots) {
            if (!localTimeSlotsToAvoid.contains(timeSlot)) {
                timeSlots.add(timeSlot);
            } else {
                // Once a time-slot in the list of time-slots to avoid has been considered once it is removed encase
                // of duplicates.
                localTimeSlotsToAvoid.remove(Integer.valueOf(timeSlot));
            }
        }
        return timeSlots;
    }

    /**
     * Make an exchange request for a time-slot that another Agent has published as a possible exchange, and that this
     * Agent wants but has not currently been allocated.
     *
     * @param advertisingBoard All the time-slots that Agents have said they may possibly exchange.
     * @return ArrayList<Integer>|null A time-slot owned by another agent that this Agent is requesting an exchange for.
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
                        // exchangeable time-slots into multiple adverts.
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
     * Stores a request for an exchange received from another Agent.
     *
     * @param request An Agent's agentID, the time-slot that it wants and the time-slot that it is willing to exchange.
     */
    void receiveExchangeRequest(ArrayList<Integer> request, int partnersAgentType) {
        exchangeRequestReceived = request;
        exchangeRequestReceived.add(partnersAgentType);
    }

        
    /**
     * Returns the most recent received from another Agent.
     *
     * @return ArrayList<Integer> request The most recent received from another Agent.
     */
    ArrayList<Integer> getExchangeRequest() {
        return exchangeRequestReceived;
    }

    /**
     * Determine whether the Agent will be willing to accept a received exchange request.
     * 
     * @return Boolean Whether or not the request was accepted.
     */
    boolean considerRequest() {
        double currentSatisfaction = calculateSatisfaction(null);
        // Create a new local list of time-slots in order to test how the Agents satisfaction would change after the
        // potential exchange.
        ArrayList<Integer> potentialAllocatedTimeSlots = new ArrayList<>(allocatedTimeSlots);
        // Check this Agent still has the time-slot requested.
        if (potentialAllocatedTimeSlots.contains(exchangeRequestReceived.get(1))) {
            potentialAllocatedTimeSlots.remove(exchangeRequestReceived.get(1));

            // Replace the requested slot with the requesting agents unwanted time-slot.
            potentialAllocatedTimeSlots.add(exchangeRequestReceived.get(2));

            double potentialSatisfaction = calculateSatisfaction(potentialAllocatedTimeSlots);
            
            if (agentType == ResourceExchangeArena.SOCIAL) {
                // Social Agents accept offers that improve their satisfaction or if they have negative social capital
                // with the Agent who made the request.
                if (Double.compare(potentialSatisfaction, currentSatisfaction) > 0) {
                    exchangeRequestApproved = true;
                    dailyNoSocialCapitalExchanges++;
                } else if (Double.compare(potentialSatisfaction, currentSatisfaction) == 0) {
                    if (usesSocialCapital) {
                        int favoursOwedToRequester = 0;
                        int favoursGivenToRequester = 0;
                        for (ArrayList<Integer> favours : favoursOwed) {
                            if (favours.get(0).equals(exchangeRequestReceived.get(0))) {
                                favoursOwedToRequester = favours.get(1);
                                break;
                            }
                        }
                        for (ArrayList<Integer> favours : favoursGiven) {
                            if (favours.get(0).equals(exchangeRequestReceived.get(0))) {
                                favoursGivenToRequester = favours.get(1);
                                break;
                            }
                        }
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
            if (exchangeRequestApproved == false) {
                dailyRejectedReceivedExchanges++;
            }
        }

        return exchangeRequestApproved;
    }

    /**
     * Checks whether the Agent still has a requested time-slot before exchanging it.
     *
     * @param timeSlot The time-slot to check the agents allocated time-slots for.
     * @return boolean Whether or not the time-slot belongs to the Agent and so can be exchanged.
     */
    boolean finalCheck(int timeSlot) {
        return allocatedTimeSlots.contains(timeSlot);
    }

    /**
     * Completes an exchange that was originally requested by this Agent, making the exchange and updating this Agents
     * relationship with the other Agent involved.
     *
     * @param offer The exchange that is to be completed.
     * @param agentID The agentID of the agent that has fulfilled the exchange request.
     * @param agentType The strategy being used by the agent that has fulfilled the exchange request.
     * @return Boolean Whether or not the other agent gained social capital.
     */
    boolean completeRequestedExchange(ArrayList<Integer> offer, int agentID, int partnersAgentType) {
        boolean SCGain = false;

        double previousSatisfaction = calculateSatisfaction(allocatedTimeSlots);
        // Update the Agents allocated time-slots.
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
     * Completes an exchange that was originally requested by another Agent, making the exchange and updating this
     * Agents relationship with the other Agent involved.
     *
     * @param offer The exchange that is to be completed.
     * @param agentType The strategy being used by the agent that requested the exchange request.
     * @return Boolean Whether or not the other agent gained social capital.
     */
    boolean completeReceivedExchange(ArrayList<Integer> offer, int partnersAgentType) {
        boolean SCLoss = false;

        double previousSatisfaction = calculateSatisfaction(allocatedTimeSlots);
        // Update the Agents allocated time-slots.
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
                SCLoss = true;
            }
        }
        return SCLoss;
    }

    /**
     * Calculates the Agents satisfaction with a given list of time-slots by comparing the list with the time-slots
     * requested by this Agent.
     *
     * @param timeSlots The set of time-slots to consider.
     * @return Double The Agents satisfaction with the time-slots given.
     */
    double calculateSatisfaction(ArrayList<Integer> timeSlots) {
        if (timeSlots == null) {
            timeSlots = this.allocatedTimeSlots;
        }

        ArrayList<Integer> tempRequestedTimeSlots = new ArrayList<>(requestedTimeSlots);
        ArrayList<Integer> nonRequestedTimeSlots = new ArrayList<>();

        // Count the number of the given time-slots that match the Agents requested time-slots.
        double satisfaction = 0;
        for (int timeSlot : timeSlots) {
            if (tempRequestedTimeSlots.contains(timeSlot)) {
                tempRequestedTimeSlots.remove(Integer.valueOf(timeSlot));
                satisfaction++;
            } else { 
                nonRequestedTimeSlots.add(timeSlot);
            }
        }

        List<SlotSatisfactionPair> tempTimeSlotSatisfactions = calculateSatisfationPerSlot(tempRequestedTimeSlots);

        // Not perfect but it will do for now.
        for (int i = 1; i < satisfactionCurve.length; i++) {
            for (Integer timeSlot: nonRequestedTimeSlots) {
                for (SlotSatisfactionPair p: tempTimeSlotSatisfactions) {
                    if (p.timeSlot == timeSlot) {
                        if (p.satisfaction == satisfactionCurve[i]) {
                            satisfaction += p.satisfaction;
                            int tover = timeSlot + i;
                            int tunder = timeSlot - i;
                            if (tempRequestedTimeSlots.contains(tover)) {
                                tempRequestedTimeSlots.remove(Integer.valueOf(tover));
                                tempTimeSlotSatisfactions = calculateSatisfationPerSlot(tempRequestedTimeSlots);
                            } else if (tempRequestedTimeSlots.contains(tunder)) {
                                tempRequestedTimeSlots.remove(Integer.valueOf(tunder));
                                tempTimeSlotSatisfactions = calculateSatisfationPerSlot(tempRequestedTimeSlots);
                            }
                        }
                        break;
                    }
                }
            }
        }

        // Return the Agents satisfaction with the given time-slots, between 1 and 0.
        return satisfaction / numberOfTimeSlotsWanted;
    }
}
