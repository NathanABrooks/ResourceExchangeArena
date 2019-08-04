import ast
import csv
import inflect
import os
import plotly as py
import sys

# Update the user on the visualisers progress.
print('Starting Data visualisation.', flush=True)

# Get the current release from command line arguments.
releaseVersion = sys.argv[1]

# Unique identifier to identify which run the produced graphs are associated with.
uniqueTag = sys.argv[2]

# Get the location of the raw data that requires visualising from command line arguments.
endOfDaySatisfactionLevels = sys.argv[3]
duringDaySatisfactionLevels = sys.argv[4]
endOfDaySatisfactionDistributions = sys.argv[5]

# Get the specific days to have average satisfaction visualised throughout the day.
daysToAnalyse = ast.literal_eval(sys.argv[6])

# Used to get ordinal word versions of integers for graph titles.
inflect = inflect.engine()

# Get the directories in which the generated graphs will be stored.
baseOutputDirectory = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))),
                                   'outputData/'
                                   + releaseVersion
                                   + '/'
                                   + uniqueTag
                                   + '/images')

duringDayOutputDirectory = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))),
                                        'outputData/'
                                        + releaseVersion
                                        + '/'
                                        + uniqueTag
                                        + '/images/duringDayAverages')

distributionsOutputDirectory = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))),
                                            'outputData/'
                                            + releaseVersion
                                            + '/'
                                            + uniqueTag
                                            + '/images/endOfDayDistributions')

# Create the output directories if they do not already exist.
if not os.path.exists(baseOutputDirectory):
    os.makedirs(baseOutputDirectory)

if not os.path.exists(duringDayOutputDirectory):
    os.makedirs(duringDayOutputDirectory)

if not os.path.exists(distributionsOutputDirectory):
    os.makedirs(distributionsOutputDirectory)

# Generate suitable filenames for the graphs that will be produced.
baseFileName = endOfDaySatisfactionLevels.split('\\')[-1]
convertedBaseFileName = baseFileName.split('.')[0] + '.pdf'

# Store the scope of the data which will be the same for each graph.
days = []
rounds = []
fieldNames = []

# Options for graph line visuals.
colours = [
    'rgba(93, 164, 214, 0.8)', 'rgba(255, 144, 14, 0.8)', 'rgba(44, 160, 101, 0.8)',
    'rgba(255, 65, 54, 0.8)', 'rgba(207, 114, 255, 0.8)', 'rgba(127, 96, 0, 0.8)',
]
lineTypes = ['solid', 'dot', 'dash', 'dashdot', 'longdashdot']

# Update the user on the visualisers progress.
print('Analysing end of day averages...', flush=True)

# Average consumer satisfactions for each agent type for each day are visualised as a line graph.
# Hypothetical random and optimal allocations are also visualised.
with open(endOfDaySatisfactionLevels) as endOfDayRawData:
    # Store calculated graph data
    data = []

    # Used to distinguish results when many agent types present.
    lineType = 0

    # Before visualising the full data set, pull data that can be reused for later visualisations.
    setupReader = csv.DictReader(endOfDayRawData)

    # The first column will always be 'Day', and so this shouldn't be stored with the list of global field names.
    fieldNames.extend(setupReader.fieldnames[1:])

    # Store the different days for which data exists as a global list.
    for row in setupReader:
        if row['Day'] in days:
            break
        else:
            days.append(row['Day'])

    # Return to the start of the file before data visualisation.
    endOfDayRawData.seek(0)

    # Standard reader used to easily iterate through the data.
    reader = csv.reader(endOfDayRawData)

    # Each agent type, including random and optimal allocation, is plotted separately.
    for i in range(len(fieldNames)):
        # The average satisfaction for the agent type at the end of each day is used as an axis variable.
        endOfDayAverages = []

        # After each run through of the file, the position is reset to the start.
        endOfDayRawData.seek(0)

        for j in range(len(days)):
            for row in reader:
                if row[0] == days[j]:
                    # Append the column + 1, to account for the 'Day' column.
                    endOfDayAverages.append((row[i + 1]))
                    break

        # Update the user on the visualisers progress.
        print(str(i + 1) + '/' + str(len(fieldNames)) + ' agent types analysed.', flush=True)

        # Get new line styling combination, calculated to match with graphs not including random or optimal allocations.
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
                    width=1,
                    dash=lineTypes[lineType],
                ),
            )
        )

    # Style the graph layout
    layout = dict(
        title='Average consumer satisfaction at the end of each day',
        xaxis=dict(
            title='Day',
            showline=True,
            tickmode='linear',
            tick0=0,
            dtick=5,
            linecolor='black',
            linewidth=2,
            gridcolor='rgb(255, 255, 255)',
            gridwidth=2,
            range=[days[0], days[-1]],
        ),
        yaxis=dict(
            title='Average consumer satisfaction',
            showline=True,
            tickmode='linear',
            tick0=0,
            dtick=0.1,
            linecolor='black',
            linewidth=2,
            gridcolor='rgb(255, 255, 255)',
            gridwidth=2,
            range=[0, 1],
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
    fig = dict(data=data, layout=layout)
    fileName = convertedBaseFileName.replace('prePreparedEndOfDayAverages', 'endOfDayAverages')
    fullPath = os.path.join(baseOutputDirectory, fileName)
    py.io.write_image(fig, fullPath)

    # Update the user on the visualisers progress.
    print('End of day averages graphed.', flush=True)

# Update the user on the visualisers progress.
print('Analysing during day averages...', flush=True)

# Average consumer satisfactions for each agent type at the end of each round day are visualised as a line graph.
# Only pre-selected days are visualised to minimise compute time.
with open(duringDaySatisfactionLevels) as duringDayRawData:
    # Before visualising the full data set, pull data that can be reused for later visualisations.
    setupReader = csv.DictReader(duringDayRawData)

    # Store the different rounds for which data exists as a global list.
    for row in setupReader:
        if row['Day'] != days[0]:
            break
        elif row['Round'] not in rounds:
            rounds.append(row['Round'])

    # Return to the start of the file before data visualisation.
    duringDayRawData.seek(0)

    # Standard reader used to easily iterate through the data.
    reader = csv.reader(duringDayRawData)

    # Each pre-selected is visualised in its own graph.
    for i in range(len(daysToAnalyse)):
        # Store calculated graph data
        data = []

        # Used to distinguish results when many agent types present.
        lineType = 0

        # Each agent type is plotted separately.
        for j in range(len(fieldNames) - 2):
            # The average satisfaction for the agent type at the end of each round is used as an axis variable.
            endOfRoundAverages = []

            for k in range(len(rounds)):
                # After each run through of the file, the position is reset to the start.
                duringDayRawData.seek(0)

                # The first line contains only headers and so can be skipped.
                next(reader)

                for row in reader:
                    # The field type column + 1 used as agent types start at 1 as opposed to 0.
                    if int(row[0]) == int(daysToAnalyse[i]) \
                            and int(row[1]) == int(rounds[k]) \
                            and int(row[2]) == int(j + 1):
                        endOfRoundAverages.append(row[3])
                        break

            # Get new line styling combination.
            colour = j
            while colour >= len(colours):
                colour -= len(colours)
                lineType += 1

            while lineType >= len(lineTypes):
                lineType -= len(lineTypes)

            # Add the agent types data plots to the graph data.
            data.append(
                py.graph_objs.Scatter(
                    x=rounds,
                    y=endOfRoundAverages,
                    name=fieldNames[j + 2],
                    line=dict(
                        color=colours[colour],
                        width=1,
                        dash=lineTypes[lineType],
                    ),
                )
            )

        # The day value is converted into the ordinal word form for styling.
        day = inflect.number_to_words(inflect.ordinal(daysToAnalyse[i]))
        title = 'Average consumer satisfaction during the ' + day + ' day'

        # Style the graph layout
        layout = dict(
            title=title,
            xaxis=dict(
                title='Rounds',
                showline=True,
                tickmode='linear',
                tick0=0,
                dtick=25,
                linecolor='black',
                linewidth=2,
                gridcolor='rgb(255, 255, 255)',
                gridwidth=2,
                range=[rounds[0], rounds[-1]],
            ),
            yaxis=dict(
                title='Average consumer satisfaction',
                showline=True,
                tickmode='linear',
                tick0=0,
                dtick=0.1,
                linecolor='black',
                linewidth=2,
                gridcolor='rgb(255, 255, 255)',
                gridwidth=2,
                range=[0, 1],
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
        fig = dict(data=data, layout=layout)
        fileName = convertedBaseFileName.replace(
            'prePreparedEndOfDayAverages', 'duringDayAveragesDay' + str(daysToAnalyse[i]))
        fullPath = os.path.join(duringDayOutputDirectory, fileName)
        py.io.write_image(fig, fullPath)

        # Update the user on the visualisers progress.
        print('During day ' + str(daysToAnalyse[i]) + ' averages graphed.', flush=True)

# Consumer satisfactions for each agent at the end of each round are visualised as a box and whisker plot.
# Only pre-selected days are visualised to minimise compute time.
with open(endOfDaySatisfactionDistributions) as boxPlotData:
    # Standard reader used to easily iterate through the data.
    reader = csv.reader(boxPlotData)

    # Each pre-selected is visualised in its own graph.
    for i in range(len(daysToAnalyse)):
        # Store calculated graph data
        data = []

        # Each agent type is plotted separately.
        for j in range(len(fieldNames) - 2):
            # The satisfaction for the agent at the end of the day is used as an axis variable.
            plots = []

            # After each run through of the file, the position is reset to the start.
            boxPlotData.seek(0)

            # The first line contains only headers and so can be skipped.
            next(reader)

            for row in reader:
                if int(row[0]) == int(daysToAnalyse[i]) \
                        and int(row[1]) == int(j + 1):
                    plots.append(row[2])

            # Get a new colour for styling purposes.
            colour = j
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
                    marker_size=2,
                    line_width=1,
                )
            )

        # The day value is converted into the ordinal word form for styling.
        day = inflect.number_to_words(inflect.ordinal(daysToAnalyse[i]))
        title = 'Consumer satisfaction distribution at the end of the ' + day + ' day'

        # Style the graph layout
        layout = dict(
            title=title,
            xaxis=dict(
                title='Consumer satisfaction',
                range=[0, 1],
                showgrid=True,
                zeroline=True,
                tickmode='linear',
                tick0=0,
                dtick=0.1,
                gridcolor='rgb(255, 255, 255)',
                gridwidth=2,
                zerolinecolor='rgb(255, 255, 255)',
                zerolinewidth=2,
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
        fig = dict(data=data, layout=layout)
        fileName = convertedBaseFileName.replace(
            'prePreparedEndOfDayAverages', 'endOfDaySatisfactionDistributionsDay' + str(daysToAnalyse[i]))
        fullPath = os.path.join(distributionsOutputDirectory, fileName)
        py.io.write_image(fig, fullPath)

        # Update the user on the visualisers progress.
        print('Box plots day ' + str(daysToAnalyse[i]) + ' graphed.', flush=True)

# Update the user on the visualisers progress.
print('Data visualisation complete.', flush=True)
