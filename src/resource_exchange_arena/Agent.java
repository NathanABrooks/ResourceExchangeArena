package resource_exchange_arena;

import java.util.ArrayList;
import java.util.Random;

class Agent {
    // Unique identifier for the agent.
    int agentID;

    // Instance variables to store the agents state, relations and ongoing exchanges.
    private int agentType;
    private final boolean usesSocialCapital;
    private boolean madeInteraction;
    private final int numberOfTimeSlotsWanted;
    private ArrayList<Integer> requestedTimeSlots = new ArrayList<>();
    private ArrayList<Integer> allocatedTimeSlots = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> favoursOwed = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> favoursGiven = new ArrayList<>();
    private ArrayList<Integer> exchangeRequestReceived = new ArrayList<>();
    private boolean exchangeRequestApproved;

    /**
     * Agents represent the individual consumers in the simulation.
     *
     * @param agentID This is an integer value that is unique to the individual agent and used to identify it to others
     *                in the ExchangeArena.
     * @param agentType Integer value denoting the agent type, and thus how it will behave.
     * @param slotsPerAgent Integer value representing the number of time slots each agent requires.
     * @param agents Array List of all the agents that exist in the current simulation.
     * @param socialCapital determines whether the agent uses socialCapital.
     */
    Agent(int agentID, int agentType, int slotsPerAgent, ArrayList<Agent> agents, boolean socialCapital){
        this.agentID = agentID;
        this.agentType = agentType;
        this.usesSocialCapital = socialCapital;

        madeInteraction = false;
        numberOfTimeSlotsWanted = slotsPerAgent;

        // Add the Agent to the ExchangeArenas list of participating Agents.
        agents.add(this);
    }

    /**
     * Extended initialisation for clones.
     *
     * @param agentID This is an integer value that is unique to the individual agent and used to identify it to others
     *                in the ExchangeArena.
     * @param agentType Integer value denoting the agent type, and thus how it will behave.
     * @param slotsPerAgent Integer value representing the number of time slots each agent requires.
     * @param agents Array List of all the agents that exist in the current simulation.
     * @param socialCapital determines whether the agent uses socialCapital.
     */
    Agent(int agentID, int agentType, boolean usesSocialCapital, boolean madeInteraction, int numberOfTimeSlotsWanted, ArrayList<Integer>requestedTimeSlots, ArrayList<Integer>allocatedTimeSlots, ArrayList<ArrayList<Integer>>favoursOwed, ArrayList<ArrayList<Integer>>favoursGiven, ArrayList<Integer> exchangeRequestReceived, boolean exchangeRequestApproved){
        this.agentID = agentID;
        this.agentType = agentType;
        this.usesSocialCapital = usesSocialCapital;
        this.madeInteraction = madeInteraction;
        this.numberOfTimeSlotsWanted = numberOfTimeSlotsWanted;
        this.requestedTimeSlots = requestedTimeSlots;
        this.allocatedTimeSlots = allocatedTimeSlots;
        this.favoursOwed = favoursOwed;
        this.favoursGiven = favoursGiven;
        this.exchangeRequestReceived = exchangeRequestReceived;
        this.exchangeRequestApproved = exchangeRequestApproved;
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
     * Getter for the number of timeslots requested.
     *
     * @return boolean Returns the number of timeslots the Agent wants.
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
     * Checks the time slots that exist in the simulations and makes a new request for a number of unique time slots
     * according to how many slots the Agent wants.
     *
     * @param uniqueTimeSlots Integer value representing the number of unique time slots available in the simulation.
     * @return ArrayList<Integer> Returns the time slots that the Agent has requested.
     */
    ArrayList<Integer> requestTimeSlots(int uniqueTimeSlots) {

        if (!requestedTimeSlots.isEmpty()) {
            requestedTimeSlots.clear();
        }

        for (int i = 1; i <= numberOfTimeSlotsWanted; i++) {
            // Get the simulations seeded Random object.
            Random random = ResourceExchangeArena.random;

            // Selects a random integer representing the time slot between 1 and the total number of available slots
            // that exist in the simulation.
            int timeSlot = random.nextInt(uniqueTimeSlots) + 1;

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
        unlockedTimeSlots = new ArrayList<>(nonExistingTimeSlots(allocatedTimeSlots, requestedTimeSlots));

        return unlockedTimeSlots;
    }

    /**
     * Takes two arrays of time slots, and returns the time slots from the first array that are not present in the
     * second array.
     *
     * @param potentialTimeSlots the time slots that may be returned if not present in the second array.
     * @param timeSlotsToAvoid the time slots that shouldn't be returned..
     * @return ArrayList<Integer> Returns the time slots from the potentialTimeSlots array that are not present in the
     *                            timeSlotsToAvoid array.
     */
    private ArrayList<Integer> nonExistingTimeSlots(
            ArrayList<Integer> potentialTimeSlots,
            ArrayList<Integer> timeSlotsToAvoid) {
        // By making a new copy of the time slots to avoid, the array list can be modified without modifying the
        // referenced list.
        ArrayList<Integer> localTimeSlotsToAvoid = new ArrayList<>(timeSlotsToAvoid);
        ArrayList<Integer> timeSlots = new ArrayList<>();
        for (int timeSlot : potentialTimeSlots) {
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
     * Make an exchange request for a time slot that another Agent has published as a possible exchange, and that this
     * Agent wants but has not currently been allocated.
     *
     * @param advertisingBoard All the time slots that Agents have said they may possibly exchange.
     * @return ArrayList<Integer>|null A time slot owned by another agent that this Agent is requesting an exchange for.
     */
    ArrayList<Integer> requestExchange(ArrayList<ArrayList<Integer>> advertisingBoard) {
        ArrayList<Integer> targetTimeSlots = nonExistingTimeSlots(requestedTimeSlots, allocatedTimeSlots);
        ArrayList<Integer> potentialExchange = new ArrayList<>();
        // If all requested have been allocated, the Agent has no need to request an exchange.
        if (!targetTimeSlots.isEmpty()) {
            // Search the advertising board for a potential exchange.
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
     * Stores a request for an exchange received from another Agent.
     *
     * @param request An Agent's agentID, the time slot that it wants and the time slot that it is willing to exchange.
     */
    void receiveExchangeRequest(ArrayList<Integer> request) {
        exchangeRequestReceived = request;
    }

    /**
     * Determine whether the Agent will be willing to accept a received exchange request.
     */
    void considerRequest() {
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
            if (agentType == ResourceExchangeArena.SOCIAL) {
                // Social Agents accept offers that improve their satisfaction or if they have negative social capital
                // with the Agent who made the request.
                if (Double.compare(potentialSatisfaction, currentSatisfaction) > 0){
                    exchangeRequestApproved = true;
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
                        }
                    } else {
                        // When social capital isn't used, social agents always accept neutral exchanges.
                        exchangeRequestApproved = true;
                    }
                }
            } else {
                // Selfish Agents and Agents with no known type use the default selfish approach.
                // Selfish Agents only accept offers that improve their individual satisfaction.
                if (Double.compare(potentialSatisfaction, currentSatisfaction) > 0) {
                    exchangeRequestApproved = true;
                }
            }
        }
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
     * Completes an exchange that was originally requested by this Agent, making the exchange and updating this Agents
     * relationship with the other Agent involved.
     *
     * @param offer The exchange that is to be completed.
     * @param agentID The agentID of the agent that has fulfilled the exchange request.
     */
    void completeRequestedExchange(ArrayList<Integer> offer, int agentID) {
        double previousSatisfaction = calculateSatisfaction(allocatedTimeSlots);
        ArrayList<Integer> oldAllocated = new ArrayList<>(allocatedTimeSlots);
        // Update the Agents allocated time slots.
        allocatedTimeSlots.remove(offer.get(2));
        allocatedTimeSlots.add(offer.get(1));

        double newSatisfaction = calculateSatisfaction(allocatedTimeSlots);

        // Update the Agents relationship with the other Agent involved in the exchange.
        if (usesSocialCapital) {
            if (Double.compare(newSatisfaction, previousSatisfaction) > 0
                    && agentType == ResourceExchangeArena.SOCIAL) {
                for (ArrayList<Integer> favours : favoursOwed) {
                    if (favours.get(0).equals(agentID)) {
                        int currentFavour = favours.get(1);
                        favours.set(1, currentFavour + 1);
                        break;
                    }
                }
            }
        }



        // if (Double.compare(newSatisfaction, previousSatisfaction) <= 0) {
        //     System.out.println("Error - requester   " + previousSatisfaction + "   " + newSatisfaction);
        //     System.out.print("requested: ");
        //     for(Integer i: requestedTimeSlots) {
        //         System.out.print(i + " ");
        //     }
        //     System.out.print("   ");
        //     System.out.print("allocated: ");
        //     for(Integer i: oldAllocated) {
        //         System.out.print(i + " ");
        //     }
        //     System.out.print("   ");
        //     System.out.print("offer: ");
        //     for(Integer i: offer) {
        //         System.out.print(i + " ");
        //     }
        //     System.out.print("   ");
        //     System.out.print("new allocated: ");
        //     for(Integer i: allocatedTimeSlots) {
        //         System.out.print(i + " ");
        //     }
        //     System.out.println("");
        //     System.out.println("");
        // }
    }

    /**
     * Completes an exchange that was originally requested by another Agent, making the exchange and updating this
     * Agents relationship with the other Agent involved.
     *
     * @param offer The exchange that is to be completed.
     */
    void completeReceivedExchange(ArrayList<Integer> offer) {
        double previousSatisfaction = calculateSatisfaction(allocatedTimeSlots);
        ArrayList<Integer> oldAllocated = new ArrayList<>(allocatedTimeSlots);
        // Update the Agents allocated time slots.
        allocatedTimeSlots.remove(offer.get(1));
        allocatedTimeSlots.add(offer.get(2));
        double newSatisfaction = calculateSatisfaction(allocatedTimeSlots);

        // Update the Agents relationship with the other Agent involved in the exchange.
        if (usesSocialCapital) {
            if (Double.compare(newSatisfaction, previousSatisfaction) <= 0
                    && agentType == ResourceExchangeArena.SOCIAL) {
                for (ArrayList<Integer> favours : favoursGiven) {
                    if (favours.get(0).equals(offer.get(0))) {
                        int currentFavour = favours.get(1);
                        favours.set(1, currentFavour + 1);
                        break;
                    }
                }
            }
        }

        // if (Double.compare(newSatisfaction, previousSatisfaction) < 0) {
        //     System.out.println("Error - receiver   " + previousSatisfaction + "   " + newSatisfaction);
        //     System.out.print("requested: ");
        //     for(Integer i: requestedTimeSlots) {
        //         System.out.print(i + " ");
        //     }
        //     System.out.print("   "); 
        //     System.out.print("allocated: ");
        //     for(Integer i: oldAllocated) {
        //         System.out.print(i + " ");
        //     }
        //     System.out.print("   ");
        //     System.out.print("offer: ");
        //     for(Integer i: offer) {
        //         System.out.print(i + " ");
        //     }
        //     System.out.print("   ");
        //     System.out.print("new allocated: ");
        //     for(Integer i: allocatedTimeSlots) {
        //         System.out.print(i + " ");
        //     }
        //     System.out.println("");
        //     System.out.println("");
        // }
    }

    /**
     * Calculates the Agents satisfaction with a given list of time slots by comparing the list with the time slots
     * requested by this Agent.
     *
     * @param allocatedTimeSlots The set of time slots to consider.
     * @return Double The Agents satisfaction with the time slots given.
     */
    double calculateSatisfaction(ArrayList<Integer> allocatedTimeSlots) {
        if (allocatedTimeSlots == null) {
            allocatedTimeSlots = this.allocatedTimeSlots;
        }

        ArrayList<Integer> tempRequestedTimeSlots = new ArrayList<>(requestedTimeSlots);

        // Count the number of the given time slots that match the Agents requested time slots.
        double satisfiedSlots = 0;
        for (int timeSlot : allocatedTimeSlots) {
            if (tempRequestedTimeSlots.contains(timeSlot)) {
                tempRequestedTimeSlots.remove(Integer.valueOf(timeSlot));
                satisfiedSlots++;
            }
        }
        // Return the Agents satisfaction with the given time slots, between 1 and 0.
        return satisfiedSlots / numberOfTimeSlotsWanted;
    }
}
