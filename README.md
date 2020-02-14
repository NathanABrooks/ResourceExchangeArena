# ResourceExchangeArena
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](/LICENSE.md)

### Authors
**Name:** Nathan Brooks<br/>**Contact Email:** n.a.brooks@keele.ac.uk <sub>*Feel free to contact this email with any questions*</sub>

**Name:** Dr James Borg<br/>**Contact Email:** j.borg@keele.ac.uk

**Name:** Dr Simon Powers<br/>**Contact Email:** S.Powers@napier.ac.uk


### Description

The Resource Exchange Arena simulation has been developed in order to better understand how social capital, in the form of trust, can influence direct interactions between agents in pairwise situations, and how this in turn can impact on then success of a population in solving a resource allocaiton problem.

The model has been built to represent a smart energy network in which agents are allocated time slots in which they have access to electricity from a shared generator. Each day agents request 4 out of a possible 24 hour long timeslots in which they require electricity. All requests are for 1KwH of energy and there can never be more than 16 agents using the same timeslot, as this is considered the peak capacity of the system. As timeslots are initially allocated at random each day, few agents are likely to have their allocation match their requested 4 timeslots. Because of this, after the initial distribution agents can partake in pairwise exchanges where one agent requests to swap one of its timeslots with a second agent, and the second agent decides whether or not to fulfill the request.

Agents can be either social or selfish, which impacts how they react to incoming requests for exchanges. Selfish agents will only accept exchanges that are in their interest. This means that selfish agents need to be offered a timeslot that they have initially requested in order for them to agree to the exchange. Social agents would also agree to these mutually benefitial exchanges, however they also make decisions based on social capital, in the form of repaying previous favours given to them by other agents. When a social agent's request is accepted, they record it as a favour given to them. When a social agent recieves a request from another agent who previously gave them a favour, they will accept the request, improving the satisfaction of the other agent while earning themselves more social capital. This leads to a system of social agents earning and repaying favours among one another, increasing then number of accepted exchange requests.

Each day consists of all agents making their request for 4 timeslots and recieving a random allocation out of the 24 possibilities. They then anonomously advertise slots that they have been allocated but do not want to an 'advertising board'. A number of exchange rounds then take place in which agents can request a time slot from the board or recieve a request from another agent. Agents accept or refuse requests based on whether it would increase their individual satisfaction or in the case of social agents, whether they owe a favour to another agent sending them a request. Only social capital, social agents memory of favours, remains between days.

At the end of each day, a number of the least satisfied agents are able to alter their strategy between being selfish and social. These agents use fitness proportionate selection to identify a strategy used by one of their peers, retaining or changing their strategy to match.

The simulation results are averaged over 500 runs for each variation of simulation parameters, those being the number of exchange rounds per day and the number of agents evolving their stragegy at the end of each day.