# ResourceExchangeArena
A simulation aiming to identify responsible social policies to determine institutional rules for self-organising smart grids. [![Version: 0.9](https://img.shields.io/badge/Version-0.9-Green.svg)](#versions) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](/LICENSE.md)

### Table of Contents

[Project Details](#project-details)

[Project Description](#project-description)

[Research Questions](#research-questions)

[Simulation Versions](#simulation-versions)

[Pseudocode](#pseudocode)

[References](#references)

## Project Details
This project is being developed as part of the authors individual research project component for the degree of Master of Science in Advanced Computer Science from [Keele University](https://www.keele.ac.uk). The project is titled *'Evolving responsible social policies to determine institutional rules for self-organising smart grids'*. Feel free to contact the author with any questions.

### Author
**Name:** Nathan Brooks<br/>**Contact Email:** w4r30@students.keele.ac.uk <sub>*Feel free to contact this email with any questions*</sub>

### Supervisors
**Name:** Dr James Borg<br/>**Contact Email:** j.borg@keele.ac.uk

**Name:** Dr Simon Powers<br/>**Contact Email:** S.Powers@napier.ac.uk

## Project Description
Multi-agent systems that utilise social learning have previously been shown to be an effective model for exploring approaches to collective action problems requiring cooperation between self-interested individuals [1][2]. Where common-pool resources are limited, these models have shown that by enforcing their own institutional rules groups of individuals can avoid the tragedy of the commons and improve their collective performance [3][4][5].

One of the most pressing collective action problems in the world today is the issue of climate change. It is therefore crucial that approaches are developed to renewable energy distribution that maximise the use of available resources while also maximising individual’s satisfaction with how resources are distributed. Multi-agent based models have clear potential in this area and could be a major asset to self-organising smart grids [6][7].

In this project, a model will be developed based on previous work in which agents exchange time slots in which they are able to use a shared renewable energy resource in order to maximise their individual satisfaction [8]. By incorporating various social learning strategies, the project will identify how the optimum trading strategies differ based on the distribution of resources.

The model will then go on to incorporate social goals such as trust and fairness [9]. This will be done by introducing opportunities for agents to break rules and utilise time slots that have not been allocated to them and evolving institutional rules to best reward or punish the behaviour of agents [10].

The project will finally go on to introduce real world data for energy demand and availability over time. Using this data to tune the model, suggestions will be developed for how individuals in real smart grids could alter their energy usage in order to maximise the use of renewable resources while minimising the impact on individual satisfaction.

## Research Questions
• How does the evolved trading strategy vary based on the distribution of resources?

• Which social learning strategies most efficiently converge on the optimum trading strategy(s)?

• By incorporating institutional rules into the model, can resources be distributed optimally while maintaining social goals?

• How do both trading strategies and social learning strategies need to adapt for the agents to perform effectively with varying resource availability and volume of agents?

• By introducing real world data, what suggestions can be drawn from the model for real world energy distribution in socio-technical smart grids?

## Simulation Versions

### v1.0 *work in progress*

The initial model aims to reproduce the results previously shown in the *Electricity Exchange Arena* from Petruzzi’s paper titled *Experiments with Social Capital in Multiagent Systems* [8]. The core objective of the new model is to reproduce the existing results [8] with a model that doesn't rely on frameworks and is kept as minimal and accessible as possible. The new model is also highly adaptable allowing a broad range of quantities of agents and resources to be tested.

### v2.0 *planned*

The second iteration of the model introduces social learning into the model, which is crucial in answering the research questions listed. This is implemented with evolution occurring at the end of each day, using a roulette-wheel approach. Payoff-biased transmission and conformity-biased transmission will be added so that the effects can be compared.

### v3.0 *planned*

The third iteration of the model goes beyond simple social learning and adds social goals [9,10] for the agents. Modelling social goals such as trust and fairness is of great benefit when maximising the realism of the model. This is the version of the model to which real world data will be added, so that predictions can be made about how real consumers would act in a similar sociotechnical system.

*If you are viewing this project offline, all versions can be found at the following git repository:
https://github.com/NathanABrooks/ResourceExchangeArena*

## Pseudocode
This is the pseudocode for the current simulation version:

![ExchangeArena Class](https://github.com/NathanABrooks/ResourceExchangeArena/blob/master/pseudocode/v1.0/ExchangeArena.png "ExchangeArena Class")

## References
[1] `Lewis, P. and Ekárt, A. (2017). Social and Asocial Learning in Collective Action Problems: The Rise and Fall of Socially-Beneficial Behaviour. 2017IEEE 2nd International Workshops on Foundations and Applications of Self* Systems, (FAS*W).`

[2] `Marriott, C., Borg, J., Andras, P. and Smaldino, P. (2018). Social Learning and Cultural Evolution in Artificial Life. Artificial Life, 24(1), pp.5-9.`

[3] `Pitt, J. (2017). Interactional Justice and Self-Governance of Open Self-Organising Systems. 2017IEEE 11th International Conference on Self-Adaptive and Self-Organizing Systems, (SASO).`

[4] `Powers, S. (2018). The Institutional Approach for Modelling the Evolution of Human Societies. Artificial Life, 24(1), pp.10-28.`

[5] `Powers, S., Ekárt, A. and Lewis, P. (2018). Modelling enduring institutions: The complementarity of evolutionary and agent-based approaches. Cognitive Systems Research, 52, pp.67-81.`

[6] `Celik, B., Roche, R., Bouquain, D. and Miraoui, A. (2018). Decentralized Neighbourhood Energy Management With Coordinated Smart Home Energy Sharing. IEEE Transactions on Smart Grid, 9(6),pp.6387-6397.`

[7] `Nair, A., Hossen, T., Campion, M., Selvaraj, D., Goveas, N., Kaabouch, N. and Ranganathan, P.(2018). MultiAgentSystems for Resource Allocation and Scheduling in a Smart Grid. Technology andEconomicsof Smart Grids and Sustainable Energy, 3(1).`

[8] `Petruzzi P. E., Busquets D., Pitt J. (2014). Experiments with Social Capital in Multi-agent Systems, 17th International Conference on Principles and Practice of Multi-Agent Systems (PRIMA), Publisher: SPRINGERVERLAG BERLIN, Pages: 18-33, ISSN: 0302-9743.`

[9] `Andras, P., Esterle, L., Guckert, M., Han, T., Lewis, P., Milanovic, K., Payne, T., Perret, C., Pitt, J., Powers, S., Urquhart, N. and Wells, S. (2018). Trusting Intelligent Machines: Deepening Trust Within Socio-Technical Systems. IEEE Technology and Society Magazine, 37(4), pp.76-83.`

[10] `Rauwolf, P. and Bryson, J. (2017). Expectations of Fairness and Trust Co-Evolve in Environments of Partial Information. Dynamic Games and Applications, 8(4), pp.891-917.`
