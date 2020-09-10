# Resource Exchange Arena 1.0
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](/LICENSE.md)

### Authors
**Name:** Nathan Brooks<br/>**Contact Email:** n.a.brooks@keele.ac.uk <sub>*Feel free to contact this email with any questions*</sub>

**Name:** Dr James Borg<br/>**Contact Email:** j.borg@keele.ac.uk

**Name:** Dr Simon Powers<br/>**Contact Email:** S.Powers@napier.ac.uk

### Academic Paper
Brooks, N. A., Powers, S. T., & Borg, J. M. (2020, July). A mechanism to promote social behaviour in household load balancing. In Artificial Life Conference Proceedings (pp. 95-103). One Rogers Street, Cambridge, MA 02142-1209 USA journals-info@ mit. edu: MIT Press.

https://www.mitpressjournals.org/doi/pdf/10.1162/isal_a_00290

### Description

The Energy Exchange Simulation (ResourceExchangeArena) has been developed in order to better understand how social capital, in the form of trust, can influence direct interactions between agents in pairwise situations, and how this in turn can impact on then success of a population in solving a multi-objective optimisation problem.

The model has been built to represent a smart energy network consisting of 96 individual agents who have varying preferences for which time slots they wish to use high-powered appliances. Each day agents request 4 out of a possible 24, hour long time-slots in which they require electricity from the community energy system. All requests are for 1KWh of energy and there can never be more than 16 agents using the same time-slot, as this is considered the peak capacity of the system. As time-slots are initially allocated at random at the start of the day, few agents are likely to have their allocation match their requested 4 time-slots. Because of this, after the initial distribution agents can partake in pairwise exchanges where one agent requests to swap one of its time-slots with a second agent, and the second agent decides whether to fulfil the request.

Agents can be either social or selfish, which impacts how they react to incoming requests for exchanges. Selfish agents will only accept exchanges that are in their interest. This means that selfish agents need to be offered a time-slot that they have initially requested in order for them to agree to the exchange. Social agents would also agree to these mutually beneficial exchanges, however they also make decisions based on social capital, in the form of repaying previous favours given to them by other agents. When a social agent's request is accepted, they record it as a favour given to them. When a social agent receives a request from another agent who previously gave them a favour, they will accept the request if it is not detrimental to their own satisfaction, improving the satisfaction of the other agent while earning themselves more social capital. This leads to a system of social agents earning and repaying favours among one another, increasing then number of accepted exchange requests.

Exchanges begin once each agent has received their initial allocation and decided which of these time slots they wish to keep. They then anonymously advertise slots that they have been allocated but do not want to an 'advertising board'. A number of exchange rounds then take place in which agents can request a time slot from the board, so long as they haven't already received a request from another agent during the round. Agents accept or refuse requests based on whether it would increase their individual satisfaction or in the case of social agents, whether they owe a favour to another agent sending them a request. Only social capital, social agents' memory of favours, remains between days.

At the end of each day, a percentage of the agents are able to alter their strategy between being selfish and social. These agents observe a randomly selected second agent. If the observed agent outperforms the agent in question, the first agent has a chance to copy the strategy of the observed agent, with the likelihood being proportionate to the difference between the two agents individual levels of satisfaction.

### Pseudocode
![Pseudocode](/pseudocode/EnergyExchangeSimulation.png)
