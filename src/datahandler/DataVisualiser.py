import ast
import csv
import inflect
import os
import plotly as py
import sys

from typing import Any, Dict, List

""" Takes pre-prepared data from the VisualiserInitiator.java method and produces a series of graphs 
visualising the data. The types of graphs are as follows:
 - A line graph showing the average satisfaction of each agent type at the end of each day, as well as the average
    satisfaction of all agents if time slots were allocated randomly or optimally.
 - Line graphs showing the average satisfaction of each agent type at the end of each round of trading, a graph is
    generated for each of a series of days passed as a parameter.
 - A line graph showing how the population distribution alters over the course of the simulation.
All graphs use data that has been averaged over a series of simulations.

Parameters
---------
folderName : str
    The output destination folder, used to organise output data.
seed : str
    A unique tag so that generated graphs can easily be associated with their corresponding data sets.
averageSatisfactionLevels: str
    The absolute path of the data set required for generating the line graph showing the average satisfaction of each
    agent type at the end of each day.
keyDaysSatisfactionLevels: str
    The absolute path of the data set required for generating the line graphs showing the average satisfaction of each
    agent type at the end of each round of trading.
populationDistributions: str
    The absolute path of the data set required for generating the line graph showing the average population
    distributions at the end of each day.
totalDaysSimulated: int
    The total number of days that have been simulated, determines graphs axis dimensions.    
totalExchangesSimulated: int
    The total number of exchanges that have been simulated, determines graphs axis dimensions.    
daysToVisualise: str
    The specific days that will have a line graph of the agent satisfactions at the end of each round throughout the day
    generated. Note that this is immediately converted to type List[int].
"""

print('Starting data visualisation...', flush=True)

# Get the output folder from command line arguments.
folderName: str = sys.argv[1]

# Unique identifier to identify which run the produced graphs are associated with.
seed: str = sys.argv[2]

# Get the location of the raw data that requires visualising from command line arguments.
averageSatisfactionLevels: str = sys.argv[3]
keyDaysSatisfactionLevels: str = sys.argv[4]
populationDistributions: str = sys.argv[5]
individualSatisfactions: str = sys.argv[6]

# Get the scope of the data to be visualised.
totalDaysSimulated: int = int(sys.argv[7])
totalExchangesSimulated: int = int(sys.argv[8])

# Get the specific days to have average satisfaction visualised throughout the day.
daysToVisualise: List[int] = ast.literal_eval(sys.argv[9])

# Used to get ordinal word versions of integers for graph titles.
inflect = inflect.engine()

# Get the directories in which the generated graphs will be stored.
baseOutputDirectory: str = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))),
                                        'results/'
                                        + folderName
                                        + '/'
                                        + seed
                                        + '/images')
duringDayOutputDirectory: str = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))),
                                             'results/'
                                             + folderName
                                             + '/'
                                             + seed
                                             + '/images/duringDayAverages')

# Create the output directories if they do not already exist.
if not os.path.exists(baseOutputDirectory):
    os.makedirs(baseOutputDirectory)
if not os.path.exists(duringDayOutputDirectory):
    os.makedirs(duringDayOutputDirectory)

# Get suitable filenames format for the graphs that will be produced from existing raw data files.
baseFileName: str = averageSatisfactionLevels.split('/')[-1]
convertedBaseFileName: str = baseFileName.split('.')[0] + '.pdf'

# Store the scope of the data, which will be the same for each graph, as global lists.
days: List[str] = []
exchanges: List[str] = []
fieldNames: List[str] = []

for day in range(1, totalDaysSimulated + 1):
    days.append(str(day))

for exchange in range(1, totalExchangesSimulated + 1):
    exchanges.append(str(exchange))

# Options for graph styling.
colours: List[str] = [
    'rgba(93, 164, 214, 0.8)', 'rgba(255, 144, 14, 0.8)', 'rgba(44, 160, 101, 0.8)',
    'rgba(255, 65, 54, 0.8)', 'rgba(207, 114, 255, 0.8)', 'rgba(127, 96, 0, 0.8)',
]
lineTypes: List[str] = ['solid', 'dot', 'dash', 'dashdot', 'longdashdot']

# Average consumer satisfactions for each agent type for each day are visualised as a line graph.
# Hypothetical random and optimum allocations are also visualised.
with open(averageSatisfactionLevels) as dailyAverageSatisfactionLevels:
    # Store calculated graph data
    data: Any = []

    # Used to distinguish results when many agent types present.
    lineType: int = 0

    # Before visualising the full data set, pull data that can be reused for later visualisations.
    setupReader = csv.DictReader(dailyAverageSatisfactionLevels)

    # The first column will always be 'Day', and so this shouldn't be stored with the list of global field names.
    fieldNames.extend(setupReader.fieldnames[1:])

    reader = csv.reader(dailyAverageSatisfactionLevels)

    # Each agent type, including random and optimum allocation, is plotted separately.
    for i in range(len(fieldNames)):
        endOfDayAverages: List[int] = []
        dailyAverageSatisfactionLevels.seek(0)
        for j in range(len(days)):
            for row in reader:
                if row[0] == days[j]:
                    # Append the column + 1, to account for the 'Day' column.
                    endOfDayAverages.append((row[i + 1]))
                    break

        # Get new line styling combination, calculated to match with graphs not including random or optimum allocations.
        colour = i + (len(colours) - 2)
        while colour >= len(colours):
            colour -= len(colours)
        if (i != 0) & (i % len(colours) == 0):
            lineType += 1
        while lineType >= len(lineTypes):
            lineType -= len(lineTypes)

        # Add the agent types data plots to the graph data.
        data.append(
            py.graph_objs.Scatter(
                x=days,
                y=endOfDayAverages,
                name=fieldNames[i],
                line=dict(
                    color=colours[colour],
                    dash=lineTypes[lineType],
                    width=1,
                ),
            )
        )

    # Style the graph layout
    layout: any = dict(
        title='Average consumer satisfaction at the end of each day',
        xaxis=dict(
            title='Day',
            showline=True,
            linecolor='black',
            linewidth=2,
            gridcolor='rgb(255, 255, 255)',
            gridwidth=2,
            range=[days[0], days[-1]],
            tickmode='linear',
            tick0=0,
            dtick=10,
        ),
        yaxis=dict(
            title='Average consumer satisfaction',
            showline=True,
            linecolor='black',
            linewidth=2,
            gridcolor='rgb(255, 255, 255)',
            gridwidth=2,
            range=[0, 1],
            tickmode='linear',
            tick0=0,
            dtick=0.1,
        ),
        margin=dict(
            l=40,
            r=30,
            b=80,
            t=100,
        ),
        paper_bgcolor='rgb(243, 243, 243)',
        plot_bgcolor='rgb(243, 243, 243)',
    )

    # Create the graph and save the file
    fig: Dict[any, any] = dict(data=data, layout=layout)
    fileName: str = convertedBaseFileName
    fullPath: str = os.path.join(baseOutputDirectory, fileName)
    py.io.write_image(fig, fullPath)

# Average consumer satisfactions for each agent type at the end of each round day are visualised as a line graph.
# Only pre-selected days are visualised to minimise compute time.
with open(keyDaysSatisfactionLevels) as duringKeyDaysSatisfactionLevels:
    reader = csv.reader(duringKeyDaysSatisfactionLevels)

    # Each pre-selected is visualised in its own graph.
    for i in range(len(daysToVisualise)):
        # Store calculated graph data
        data: Any = []

        # Used to distinguish results when many agent types present.
        lineType: int = 0

        # Each agent type is plotted separately.
        for j in range(len(fieldNames) - 2):
            endOfRoundAverages: List[int] = []
            for k in range(len(exchanges)):
                duringKeyDaysSatisfactionLevels.seek(0)

                # The first line contains only headers and so can be skipped.
                next(reader)
                for row in reader:
                    # The field type column + 1 used as agent types start at 1 as opposed to 0.
                    if int(row[0]) == int(daysToVisualise[i]) \
                            and int(row[1]) == int(exchanges[k]) \
                            and int(row[2]) == int(j + 1):
                        endOfRoundAverages.append(row[3])
                        break

            # Get new line styling combination.
            colour: int = j
            while colour >= len(colours):
                colour -= len(colours)
                lineType += 1
            while lineType >= len(lineTypes):
                lineType -= len(lineTypes)

            # Add the agent types data plots to the graph data.
            data.append(
                py.graph_objs.Scatter(
                    x=exchanges,
                    y=endOfRoundAverages,
                    name=fieldNames[j + 2],
                    line=dict(
                        color=colours[colour],
                        dash=lineTypes[lineType],
                        width=1,
                    ),
                )
            )

        # The day value is converted into the ordinal word form for styling.
        day: str = inflect.number_to_words(inflect.ordinal(daysToVisualise[i]))
        title: str = 'Average consumer satisfaction during the ' + day + ' day'

        # Style the graph layout
        layout: any = dict(
            title=title,
            xaxis=dict(
                title='Rounds',
                showline=True,
                linecolor='black',
                linewidth=2,
                gridcolor='rgb(255, 255, 255)',
                gridwidth=2,
                range=[exchanges[0], exchanges[-1]],
                tickmode='linear',
                tick0=0,
                dtick=25,
            ),
            yaxis=dict(
                title='Average consumer satisfaction',
                showline=True,
                linecolor='black',
                linewidth=2,
                gridcolor='rgb(255, 255, 255)',
                gridwidth=2,
                range=[0, 1],
                tickmode='linear',
                tick0=0,
                dtick=0.1,
            ),
            margin=dict(
                l=40,
                r=30,
                b=80,
                t=100,
            ),
            paper_bgcolor='rgb(243, 243, 243)',
            plot_bgcolor='rgb(243, 243, 243)',
        )

        # Create the graph and save the file
        fig: Dict[any, any] = dict(data=data, layout=layout)
        fileName: str = convertedBaseFileName.replace(
            'endOfDayAverages', 'duringDayAveragesDay' + str(daysToVisualise[i]))
        fullPath: str = os.path.join(duringDayOutputDirectory, fileName)
        py.io.write_image(fig, fullPath)


# The population distribution for each day is visualised as a line graph.
with open(populationDistributions) as populationData:
    # Store calculated graph data
    data: Any = []

    # Used to distinguish results when many agent types present.
    lineType: int = 0

    reader = csv.reader(populationData)

    # Each agent type is plotted separately.
    for i in range(len(fieldNames) - 2):
        endOfDayPopulations: List[int] = []
        for j in range(len(days)):
            populationData.seek(0)
            for row in reader:
                if row[0] == days[j] \
                        and int(row[1]) == int(i + 1):
                    endOfDayPopulations.append((row[2]))
                    break

        # Get new line styling combination.
        colour: int = i
        while colour >= len(colours):
            colour -= len(colours)
            lineType += 1
        while lineType >= len(lineTypes):
            lineType -= len(lineTypes)

        # Add the agent types data plots to the graph data.
        data.append(
            py.graph_objs.Scatter(
                x=days,
                y=endOfDayPopulations,
                name=fieldNames[i + 2],
                line=dict(
                    color=colours[colour],
                    dash=lineTypes[lineType],
                    width=1,
                ),
            )
        )

    # Style the graph layout
    layout: any = dict(
        title='Population of each Agent type at the end of each day',
        xaxis=dict(
            title='Day',
            showline=True,
            linecolor='black',
            linewidth=2,
            gridcolor='rgb(255, 255, 255)',
            gridwidth=2,
            range=[days[0], days[-1]],
            tickmode='linear',
            tick0=0,
            dtick=5,
        ),
        yaxis=dict(
            title='Population',
            showline=True,
            linecolor='black',
            linewidth=2,
            gridcolor='rgb(255, 255, 255)',
            gridwidth=2,
            range=[0, 96],
            tickmode='linear',
            tick0=0,
            dtick=8,
        ),
        margin=dict(
            l=40,
            r=30,
            b=80,
            t=100,
        ),
        paper_bgcolor='rgb(243, 243, 243)',
        plot_bgcolor='rgb(243, 243, 243)',
    )

    # Create the graph and save the file
    fig: Dict[any, any] = dict(data=data, layout=layout)
    fileName: str = convertedBaseFileName.replace('endOfDayAverages', 'populationDistribution')
    fullPath: str = os.path.join(baseOutputDirectory, fileName)
    py.io.write_image(fig, fullPath)

# Average consumer satisfactions for each agent type at the end of each round day are visualised as a line graph.
# Only pre-selected days are visualised to minimise compute time.
with open(individualSatisfactions) as individualSatisfactionDeviations:
    reader = csv.reader(individualSatisfactionDeviations)

    # Each pre-selected is visualised in its own graph.
    for i in range(len(daysToVisualise)):

        # Store calculated graph data
        data: Any = []

        # Used to distinguish results when many agent types present.
        lineType: int = 0

        # Each agent type is plotted separately.
        for j in range(len(fieldNames) - 2):
            satisfactions: List[int] = []
            individualSatisfactionDeviations.seek(0)

            # The first line contains only headers and so can be skipped.
            next(reader)
            for row in reader:
                # The field type column + 1 used as agent types start at 1 as opposed to 0.
                if int(row[0]) == int(daysToVisualise[i]) \
                        and int(row[1]) == int(j + 1):
                    satisfactions.append(row[2])

            # Get new line styling combination.
            colour: int = j
            while colour >= len(colours):
                colour -= len(colours)
                lineType += 1
            while lineType >= len(lineTypes):
                lineType -= len(lineTypes)

            # Add the agent types data plots to the graph data.
            data.append(
                py.graph_objs.Violin(
                    y=satisfactions,
                    name=fieldNames[j + 2],
                    line=dict(
                        color=colours[colour],
                        width=1,
                    ),
                    meanline_visible=True,
                    scalemode='count',
                    spanmode='hard',
                )
            )

        # The day value is converted into the ordinal word form for styling.
        day: str = inflect.number_to_words(inflect.ordinal(daysToVisualise[i]))
        title: str = 'Satisfaction deviation during the ' + day + ' day'

        # Style the graph layout
        layout: any = dict(
            title=title,
            violinmode='overlay',
            violingap=0,
            violingroupgap=0,
            paper_bgcolor='rgb(243, 243, 243)',
            plot_bgcolor='rgb(243, 243, 243)',
        )

        # Create the graph and save the file
        fig: Dict[any, any] = dict(data=data, layout=layout)
        fileName: str = convertedBaseFileName.replace(
            'endOfDayAverages', 'satisfactionDeviationsDay' + str(daysToVisualise[i]))
        fullPath: str = os.path.join(duringDayOutputDirectory, fileName)
        py.io.write_image(fig, fullPath)
print('Data visualisation complete.', flush=True)
