package resourceexchangearena;

import java.util.ArrayList;
import java.util.Random;

/**
 * Agents represent the individual consumers in the simulation.
 */
class Agent {
    // Unique identifier for the agent.
    int agentID;

    // Instance variables to store the agents state, relations and ongoing exchanges.
    private int agentType;
    private int numberOfTimeSlotsWanted;
    private ArrayList<Integer> requestedTimeSlots = new ArrayList<>();
    private ArrayList<Integer> allocatedTimeSlots = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> favoursOwed = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> favoursGiven = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> exchangeRequestsReceived = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> exchangeRequestsApproved = new ArrayList<>();

    /**
     * This is the constructor for Agent objects.
     * @param agentID This is an integer value that is unique to the individual agent and
     *  used to identify it to others in the ExchangeArena.
     * @param agentType Integer value denoting the agent type, and thus how it will behave.
     * @return Nothing.
     */
    Agent(int agentID, int agentType){
        this.agentID = agentID;
        this.agentType = agentType;

        numberOfTimeSlotsWanted = 4;

        // Add the Agent to the ExchangeArenas list of participating Agents.
        ExchangeArena.agents.add(this);
    }

    /**
     * Identifies all other Agents in the ExchangeArena and initialises counts of favours given to
     * and reveived from each other Agent.
     * 
     * @return Nothing.
     */
    void initializeFavoursStore() {
        if (!favoursGiven.isEmpty()) {
            favoursGiven.clear();
        }
        if (!favoursOwed.isEmpty()) {
            favoursOwed.clear();
        }
        for (Agent a: ExchangeArena.agents) {
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
     * Checks the time slots that exist in the simulations and makes a new request for a number of unique timeslots
     * according to how many slots the Agent wants.
     * 
     * @return ArrayList<Integer> Returns the time slots that the Agent has requested.
     */
    ArrayList<Integer> requestTimeSlots() {
        if (!requestedTimeSlots.isEmpty()){
            requestedTimeSlots.clear();
        }

        for (int i = 1; i <= numberOfTimeSlotsWanted; i++) {
            // Get the simulations seeded Random object.
            Random random = ExchangeArena.random;

            // Selects a random integer representing the time slot between 1 and the total number of available slots.
            // that exist in the simulation.
            int timeSlot = random.nextInt(ExchangeArena.UNIQUE_TIME_SLOTS) + 1;

            // Ensures all requested time slots are unique.
            if (requestedTimeSlots.contains(timeSlot)) {
                i--;
            } else {
                requestedTimeSlots.add(timeSlot);
            }
        }
        return requestedTimeSlots;
    }

    /**
     * Getter method for retrieving the time slots that the Agent has currently requested.
     * 
     * @return ArrayList<Integer> Returns the time slots that the Agent has requested.
     */
    ArrayList<Integer> publishRequestedTimeSlots() {
        return requestedTimeSlots;
    }

    /**
     * Setter method for storing the time slots the Agent has been allocated.
     * 
     * @param allocatedTimeSlots An allocation of time slots given by the ExchangeArena.
     */
    void receiveAllocatedTimeSlots(ArrayList<Integer> allocatedTimeSlots) {
        this.allocatedTimeSlots = allocatedTimeSlots;
    }

    /**
     * Getter method for retrieving the time slots that the Agent is currently allocated.
     * 
     * @return ArrayList<Integer> Returns the time slots that the Agent is allocated.
     */
    ArrayList<Integer> publishAllocatedTimeSlots() {
        return allocatedTimeSlots;
    }

    /**
     * Shares the time slots that are currently allocated to the Agent that it may potentially be willing to
     * exchange under certain circumstances.
     * 
     * @return ArrayList<Integer> Returns the time slots that the Agent is allocated but may potentially exchange.
     */
    ArrayList<Integer> publishUnlockedTimeSlots() {
        ArrayList<Integer> unlockedTimeSlots;
        switch(agentType) {
            case ExchangeArena.SOCIAL:
            case ExchangeArena.SELFISH:
                unlockedTimeSlots = new ArrayList<>(nonExistingTimeSlots(allocatedTimeSlots, requestedTimeSlots));
                break;
            default:
                // If no type specific behavior can be found, the agent will be willing to trade all time slots.
                unlockedTimeSlots = allocatedTimeSlots;
        }
        return unlockedTimeSlots;
    }

    /**
     * Shares the time slots that are currently allocated to the Agent that it hasn't requested.
     * 
     * @return ArrayList<Integer> Returns the time slots that are allocated to the Agent but it doesn't want.
     */
    ArrayList<Integer> publishUnwantedTimeSlots() {
        return nonExistingTimeSlots(allocatedTimeSlots, requestedTimeSlots);
    }

    /**
     * Takes two arrays of time slots, and returns the time slots from the first array that are not present in the
     * second array.
     * 
     * @param potentialTimeSlots the time slots that may be returned if not present in the second array.
     * @param timeSlotsToAvoid the time slots that shouldn't be returned..
     * @return ArrayList<Integer> Returns the time slots from the potentialTimeSlots array that are not present in the
     *  timeSlotsToAvoid array.
     */
    private ArrayList<Integer> nonExistingTimeSlots(
            ArrayList<Integer> potentialTimeSlots,
            ArrayList<Integer> timeSlotsToAvoid) {
        // By making a new copy of the time slots to avoid, the array list can be modified without modifying the
        // referenced list.
        ArrayList<Integer> localTimeSlotsToAvoid = new ArrayList<>(timeSlotsToAvoid);
        ArrayList<Integer> timeSlots = new ArrayList<>();
        for(int i = 0; i < potentialTimeSlots.size(); i++){
            int timeSlot = potentialTimeSlots.get(i);
            if (!localTimeSlotsToAvoid.contains(timeSlot)) {
                timeSlots.add(timeSlot);
            } else {
                // Once a time slot in the list of time slots to avoid has been considered once it is removed encase
                // of duplicates.
                localTimeSlotsToAvoid.remove(Integer.valueOf(timeSlot));
            }
        }
        return timeSlots;
    }

    /**
     * Make an exchange request for a time slot that another Agent has published as a possible exchange,
     * that this Agent wants but has not currently been allocated.
     * 
     * @param advertisingBoard All the time slots that Agents have said they may possibly exchange.
     * @return ArrayList<Integer>|null A time slot owned by the other agent that this Agent is requesting
     *  an exchange for.
     */
    ArrayList<Integer> requestExchange(ArrayList<ArrayList<Integer>> advertisingBoard) {
        ArrayList<Integer> targetTimeSlots = nonExistingTimeSlots(requestedTimeSlots, allocatedTimeSlots);
        // If all requested have been allocated, the Agent has no need to request an exchange.
        if (!targetTimeSlots.isEmpty()) {
            ArrayList<ArrayList<Integer>> potentialExchanges = new ArrayList<>();
            ArrayList<Integer> chosenExchange;

            // Search the advertising board for potential exchanges of interest.
            for (ArrayList<Integer> advert : advertisingBoard) {
                for (int j = 1; j < advert.size(); j++) {
                    if (targetTimeSlots.contains(advert.get(j))) {
                        // Only take the part of the advert that is relevant, and split adverts with multiple
                        // exchangable time slots into multiple adverts.
                        ArrayList<Integer> potentialExchange = new ArrayList<>();
                        potentialExchange.add(advert.get(0));
                        potentialExchange.add(advert.get(j));
                        potentialExchanges.add(potentialExchange);
                    }
                }
            }

            if (potentialExchanges.size() == 0) {
                return null;
            } else if (potentialExchanges.size() == 1) {
                chosenExchange = potentialExchanges.get(0);
            } else {
                // If multiple potential Agents are of equal interest, select one to request at random.
                int selector = ExchangeArena.random.nextInt(potentialExchanges.size());
                chosenExchange = potentialExchanges.get(selector);
            }
            return chosenExchange;
        }
        return null;
    }

    /**
     * Stores a request for an exchange recieved from another Agent.
     * 
     * @param request An Agent's agentID, the time slot that it wants and the time slot that it
     *  is willing to exchange.
     */
    void receiveExchangeRequest(ArrayList<Integer> request) {
        exchangeRequestsReceived.add(request);
    }

    /**
     * For each of the exchange requests currently recieved by the Agent, determine which exchanges
     * the Agent will be willing to accept and adds them to a new list of approved request.
     * 
     * @param advertisingBoard All the time slots that Agents have said they may possibly exchange.
     * @return Nothing.
     */
    void considerRequests() {
        double currentSatisfaction = calculateSatisfaction(null);
        for (ArrayList<Integer> request : exchangeRequestsReceived) {
            // Create a new local list of time slots in order to test how the Agents satisfaciton would 
            // change after the potential exchange.
            ArrayList<Integer> potentialAllocatedTimeSlots = new ArrayList<>(allocatedTimeSlots);
            // Check this Agent still has the time slot requested.
            if (potentialAllocatedTimeSlots.contains(request.get(1))) {
                potentialAllocatedTimeSlots.remove(request.get(1));

                // Replace the requested slot with the requesting agents unwanted time slot.
                potentialAllocatedTimeSlots.add(request.get(2));

                double potentialSatisfaction = calculateSatisfaction(potentialAllocatedTimeSlots);
                if (agentType == ExchangeArena.SOCIAL) {
                    // Social Agents accept offers that improve their satisfaction or if they have negative
                    // social capital with the Agent who made the request.
                    if (potentialSatisfaction > currentSatisfaction) {
                        exchangeRequestsApproved.add(request);
                    } else {
                        int favoursOwedToRequester = 0;
                        int favoursGivenToRequester = 0;
                        for (ArrayList<Integer> favours : favoursOwed) {
                            if (favours.get(0).equals(request.get(0))) {
                                favoursOwedToRequester = favours.get(1);
                                break;
                            }
                        }
                        for (ArrayList<Integer> favours : favoursGiven) {
                            if (favours.get(0).equals(request.get(0))) {
                                favoursGivenToRequester = favours.get(1);
                                break;
                            }
                        }
                        if (favoursOwedToRequester > favoursGivenToRequester) {
                            exchangeRequestsApproved.add(request);
                        }
                    }
                } else {
                    // Selfish Agents and Agents with no known type use the default selfish approach.
                    // Selfish Agents only accept offers that improve their individual satisfaction.
                    if (potentialSatisfaction > currentSatisfaction) {
                        exchangeRequestsApproved.add(request);
                    }
                }
            }
        }
        // Once all requests have been considered, clear the array list for the next exchange round.
        exchangeRequestsReceived.clear();
    }

    /**
     * Getter method for retrieving the exchanges recieved that the Agent has approved to go ahead.
     * 
     * @return ArrayList<ArrayList<Integer>> Returns the approved exchange requests.
     */
    ArrayList<ArrayList<Integer>> getExchangeRequestsApproved() {
        return exchangeRequestsApproved;
    }

    /**
     * Checks whether the Agent still has a requested time slot before exchanging it.
     * 
     * @param timeSlot The time slot to check the agents allocated time slots for.
     * @return boolean Whether or not the time slot belongs to the Agent and so can be exchanged.
     */
    boolean finalCheck(int timeSlot) {
        return allocatedTimeSlots.contains(timeSlot);
    }

    /**
     * Completes an exchange that was originally requested by this Agent, making the exchange and updating
     * this Agents relationship with the other Agent involved.
     * 
     * @param offer The exchange that is to be completed.
     * @param agentID The agentID of the agent that has fulfilled the exchange request.
     * @return Nothing.
     */
    void completeRequestedExchange(ArrayList<Integer> offer, int agentID) {
        // Update the Agents allocated time slots.
        allocatedTimeSlots.remove(offer.get(2));
        allocatedTimeSlots.add(offer.get(1));

        // Update the Agents relationship with the other Agent involved in the exchange.
        if (agentType == ExchangeArena.SOCIAL) {
            for (ArrayList<Integer> favours : favoursOwed) {
                if (favours.get(0).equals(agentID)) {
                    int currentFavour = favours.get(1);
                    favours.set(1, currentFavour + 1);
                    break;
                }
            }
        }
    }

    /**
     * Completes an exchange that was originally requested by another Agent, making the exchange and updating
     * this Agents relationship with the other Agent involved.
     * 
     * @param offer The exchange that is to be completed.
     * @return Nothing.
     */
    void completeReceivedExchange(ArrayList<Integer> offer) {
        double previousSatisfaction = calculateSatisfaction(allocatedTimeSlots);

        // Update the Agents allocated time slots.
        allocatedTimeSlots.remove(offer.get(1));
        allocatedTimeSlots.add(offer.get(2));
        double potentialSatisfaction = calculateSatisfaction(allocatedTimeSlots);


        // Update the Agents relationship with the other Agent involved in the exchange.
        if ((agentType == ExchangeArena.SOCIAL) && (potentialSatisfaction <= previousSatisfaction)) {
            for (ArrayList<Integer> favours : favoursGiven) {
                if (favours.get(0).equals(offer.get(0))) {
                    int currentFavour = favours.get(1);
                    favours.set(1, currentFavour + 1);
                    break;
                }
            }
        }
    }

    /**
     * Resets this agents list of exchanges that it has approved to go ahead.
     *
     * @return Nothing.
     */
    void clearAcceptedRequests() {
        exchangeRequestsApproved.clear();
    }

    /**
     * Calculates the Agents satisfaction with the time slots it has currently been allocated by
     * comparing them with the time slots requested by this Agent. Passes the currently allocated
     * time slots to the calculateSatisfaction method that takes a list of time slots as a parameter.
     *
     * @return Double The Agents satisfaction with the time slots it has currently been allocated.
     * @see calculateSatisfaction(ArrayList<Integer> allocatedTimeSlots)
     */
    double calculateSatisfaction() {
        return (calculateSatisfaction(this.allocatedTimeSlots));
    }

    /**
     * Calculates the Agents satisfaction with a given list of time slots by comparing the list
     * with the time slots requested by this Agent.
     *
     * @param allocatedTimeSlots The set of time slots to consider.
     * @return Double The Agents satisfaction with the time slots given.
     */
    double calculateSatisfaction(ArrayList<Integer> allocatedTimeSlots) {
        if (allocatedTimeSlots == null) {
            allocatedTimeSlots = this.allocatedTimeSlots;
        }

        // Count the number of the given time slots that match the Agents requested time slots.
        int satisfiedSlots = 0;
        for(int i = 1; i <= allocatedTimeSlots.size(); i++){
            int timeSlot = allocatedTimeSlots.get(i - 1);
            if (requestedTimeSlots.contains(timeSlot)) {
                satisfiedSlots++;
            }
        }
        // Return the Agents satisfaction with the given time slots, between 1 and 0.
        return ((double)satisfiedSlots) / numberOfTimeSlotsWanted;
    }
}
