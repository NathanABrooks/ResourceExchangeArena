package resource_exchange_arena;

public class SlotSatisfactionPair{
    int timeslot;
    double satisfaction;

    /**
     * Used to associate a timeslot  with it's satisfaction level for an agent.
     *
     * @param timeslot Integer value representing the specific timeslot.
     * @param satisfaction Double value representing the level of satisfaction.
     */
    public SlotSatisfactionPair(int timeslot, double satisfaction) {
        this.timeslot = timeslot;
        this.satisfaction = satisfaction;
    }
}