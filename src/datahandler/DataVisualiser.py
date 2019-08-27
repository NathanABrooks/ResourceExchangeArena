import ast
import csv
import inflect
import os
import plotly as py
import sys

from typing import Any, Dict, List

""" Takes pre-prepared data from the ExchangeArena.java main method and produces a series of graphs 
visualising the data. The three types of graphs are as follows:
 - A line graph showing the average satisfaction of each agent type at the end of each day, as well as the average
    satisfaction of all agents if time slots were allocated randomly or optimally.
 - Line graphs showing the average satisfaction of each agent type at the end of each round of trading, a graph is
    generated for each of a series of days passed as a parameter.
 - Box and whisker plots showing the distribution of agents satisfaction for each agent type at the end of the same
    series of days for which line graphs are generated.
All graphs use data that has been averaged over a series of simulations by the ExchangeArena.java code.

Parameters
---------
releaseVersion : str
    The version of the ResourceExchangeArena program that the data is coming from.
seed : str
    A unique tag so that generated graphs can easily be associated with their corresponding data sets.
endOfDaySatisfactionLevels: str
    The absolute path of the data set required for generating the line graph showing the average satisfaction of each
    agent type at the end of each day.
duringDaySatisfactionLevels: str
    The absolute path of the data set required for generating the line graphs showing the average satisfaction of each
    agent type at the end of each round of trading.
endOfDaySatisfactionDistributions: str
    The absolute path of the data set required for generating the box and whisker plots showing the distribution of
    agents satisfaction for each agent type at the end of the same series of days for which line graphs are generated.
daysToVisualise: str
    The specific days that will have a line graph of the agent satisfactions at the end of each round throughout the day
    and a box and whisker plot of the agent satisfactions at the end of the day generated.
    Note that this is immediately converted to type List[int].
"""

print('Starting Data visualisation.', flush=True)

# Get the current release from command line arguments.
releaseVersion: str = sys.argv[1]

# Unique identifier to identify which run the produced graphs are associated with.
seed: str = sys.argv[2]

# Get the location of the raw data that requires visualising from command line arguments.
endOfDaySatisfactionLevels: str = sys.argv[3]
duringDaySatisfactionLevels: str = sys.argv[4]
endOfDaySatisfactionDistributions: str = sys.argv[5]

# Get the scope of the data to be visualised.
totalDaysSimulated: int = int(sys.argv[6])
totalExchangesSimulated: int = int(sys.argv[7])

# Get the specific days to have average satisfaction visualised throughout the day.
daysToVisualise: List[int] = ast.literal_eval(sys.argv[8])

# Used to get ordinal word versions of integers for graph titles.
inflect = inflect.engine()

# Get the directories in which the generated graphs will be stored.
baseOutputDirectory: str = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))),
                                        'outputData/'
                                        + releaseVersion
                                        + '/'
                                        + seed
                                        + '/images')
duringDayOutputDirectory: str = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))),
                                             'outputData/'
                                             + releaseVersion
                                             + '/'
                                             + seed
                                             + '/images/duringDayAverages')
distributionsOutputDirectory: str = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))),
                                                 'outputData/'
                                                 + releaseVersion
                                                 + '/'
                                                 + seed
                                                 + '/images/endOfDayDistributions')

# Create the output directories if they do not already exist.
if not os.path.exists(baseOutputDirectory):
    os.makedirs(baseOutputDirectory)
if not os.path.exists(duringDayOutputDirectory):
    os.makedirs(duringDayOutputDirectory)
if not os.path.exists(distributionsOutputDirectory):
    os.makedirs(distributionsOutputDirectory)

# Get suitable filenames format for the graphs that will be produced from existing raw data files.
baseFileName: str = endOfDaySatisfactionLevels.split('\\')[-1]
convertedBaseFileName: str = baseFileName.split('.')[0] + '.pdf'

# Store the scope of the data, which will be the same for each graph, as global lists.
days: List[str] = []
exchanges: List[str] = []
fieldNames: List[str] = []

for i in range(1, totalDaysSimulated + 1):
    days.append(str(i))

for i in range(1, totalExchangesSimulated + 1):
    exchanges.append(str(i))

# Options for graph styling.
colours: List[str] = [
    'rgba(93, 164, 214, 0.8)', 'rgba(255, 144, 14, 0.8)', 'rgba(44, 160, 101, 0.8)',
    'rgba(255, 65, 54, 0.8)', 'rgba(207, 114, 255, 0.8)', 'rgba(127, 96, 0, 0.8)',
]
lineTypes: List[str] = ['solid', 'dot', 'dash', 'dashdot', 'longdashdot']

print('Visualising end of day averages...', flush=True)

# Average consumer satisfactions for each agent type for each day are visualised as a line graph.
# Hypothetical random and optimum allocations are also visualised.
with open(endOfDaySatisfactionLevels) as endOfDayRawData:
    # Store calculated graph data
    data: Any = []

    # Used to distinguish results when many agent types present.
    lineType: int = 0

    # Before visualising the full data set, pull data that can be reused for later visualisations.
    setupReader = csv.DictReader(endOfDayRawData)

    # The first column will always be 'Day', and so this shouldn't be stored with the list of global field names.
    fieldNames.extend(setupReader.fieldnames[1:])

    reader = csv.reader(endOfDayRawData)

    # Each agent type, including random and optimum allocation, is plotted separately.
    for i in range(len(fieldNames)):
        endOfDayAverages: List[int] = []
        endOfDayRawData.seek(0)
        for j in range(len(days)):
            for row in reader:
                if row[0] == days[j]:
                    # Append the column + 1, to account for the 'Day' column.
                    endOfDayAverages.append((row[i + 1]))
                    break
        print('    ' + str(i + 1) + '/' + str(len(fieldNames)) + ' agent types processed.', flush=True)

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
            dtick=5,
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
    fileName: str = convertedBaseFileName.replace('prePreparedEndOfDayAverages', 'endOfDayAverages')
    fullPath: str = os.path.join(baseOutputDirectory, fileName)
    py.io.write_image(fig, fullPath)

    print('End of day averages visualised.', flush=True)
print('Visualising during day averages...', flush=True)

# Average consumer satisfactions for each agent type at the end of each round day are visualised as a line graph.
# Only pre-selected days are visualised to minimise compute time.
with open(duringDaySatisfactionLevels) as duringDayRawData:
    reader = csv.reader(duringDayRawData)

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
                duringDayRawData.seek(0)

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
            'prePreparedEndOfDayAverages', 'duringDayAveragesDay' + str(daysToVisualise[i]))
        fullPath: str = os.path.join(duringDayOutputDirectory, fileName)
        py.io.write_image(fig, fullPath)

        print('    During day ' + str(daysToVisualise[i]) + ' averages graphed.', flush=True)
    print('During day averages visualised.', flush=True)
print('Visualising end of day distributions...', flush=True)

# Consumer satisfactions for each agent at the end of each round are visualised as a box and whisker plot.
# Only pre-selected days are visualised to minimise compute time.
with open(endOfDaySatisfactionDistributions) as boxPlotData:
    reader = csv.reader(boxPlotData)

    # Each pre-selected is visualised in its own graph.
    for i in range(len(daysToVisualise)):
        # Store calculated graph data
        data: Any = []

        # Each agent type is plotted separately.
        for j in range(len(fieldNames) - 2):
            plots: List[int] = []
            boxPlotData.seek(0)

            # The first line contains only headers and so can be skipped.
            next(reader)
            for row in reader:
                if int(row[0]) == int(daysToVisualise[i]) \
                        and int(row[1]) == int(j + 1):
                    plots.append(row[2])

            # Get a new colour for styling.
            colour: int = j
            while colour >= len(colours):
                colour -= len(colours)

            # Add the agent types data plots to the graph data.
            data.append(
                py.graph_objs.Box(
                    x=plots,
                    name=fieldNames[j + 2],
                    boxpoints='all',
                    jitter=0.5,
                    whiskerwidth=0.2,
                    fillcolor=colours[colour],
                    line_width=1,
                    marker_size=2,
                )
            )

        # The day value is converted into the ordinal word form for styling.
        day: str = inflect.number_to_words(inflect.ordinal(daysToVisualise[i]))
        title: str = 'Consumer satisfaction distribution at the end of the ' + day + ' day'

        # Style the graph layout
        layout: any = dict(
            title=title,
            xaxis=dict(
                title='Consumer satisfaction',
                showgrid=True,
                gridcolor='rgb(255, 255, 255)',
                gridwidth=2,
                zeroline=True,
                zerolinecolor='rgb(255, 255, 255)',
                zerolinewidth=2,
                range=[0, 1],
                tickmode='linear',
                tick0=0,
                dtick=0.1,
            ),
            margin=dict(
                l=60,
                r=30,
                b=80,
                t=100,
            ),
            paper_bgcolor='rgb(243, 243, 243)',
            plot_bgcolor='rgb(243, 243, 243)',
            showlegend=False,
        )

        # Create the graph and save the file
        fig: Dict[any, any] = dict(data=data, layout=layout)
        fileName: str = convertedBaseFileName.replace(
            'prePreparedEndOfDayAverages', 'endOfDaySatisfactionDistributionsDay' + str(daysToVisualise[i]))
        fullPath: str = os.path.join(distributionsOutputDirectory, fileName)
        py.io.write_image(fig, fullPath)

        print('    Box plots day ' + str(daysToVisualise[i]) + ' graphed.', flush=True)
    print('End of day distributions visualised.', flush=True)
print('Data visualisation complete.', flush=True)
