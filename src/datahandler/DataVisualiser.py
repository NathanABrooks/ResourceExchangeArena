import ast
import csv
import inflect
import numpy as np
import os
import plotly as py
import sys

# Get the current release from command line arguments.
releaseVersion = sys.argv[1]

# Unique identifier pairing raw data with visualised data.
uniqueTag = sys.argv[2]

# Get the location of the data from command line arguments.
averageAgentSatisfactionLevels = sys.argv[3]
individualAgentSatisfactionLevels = sys.argv[4]

# Get the specific days to analyse exchanges during the day from command line arguments.
daysToAnalyse = ast.literal_eval(sys.argv[5])

# Used to get ordinal word versions of integers for graph titles.
p = inflect.engine()

# Create the output directories if they do not already exist.
OUTPUT_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))),
                          'outputData/' + releaseVersion + '/' + uniqueTag + '/images')

if not os.path.exists(OUTPUT_DIR):
    os.makedirs(OUTPUT_DIR)

# Generate suitable filenames for the graphs that will be produced, either data file can be used here for the structure.
baseFileName = averageAgentSatisfactionLevels.split('\\')[-1]
convertedBaseFileName = baseFileName.split('.')[0] + '.pdf'

# Store the scope of the data which will be the same for each graph
days = []
rounds = []
fieldNames = []

# Options for graph data visuals
colours = ['rgb(205,0,0)', 'rgb(0,0,205)', 'rgb(0,205,0)', 'rgb(205,0,205)', 'rgb(205,205,0)']
lineTypes = ['solid', 'dot', 'dash', 'longdash', 'dashdot', 'longdashdot']

print('Analysing end of day averages...', flush=True)

# Start by calculating the scope of the data.
with open(averageAgentSatisfactionLevels) as averagesRawData:
    # Store calculated graph data
    data = []

    setupReader = csv.DictReader(averagesRawData)
    fieldNames.extend(setupReader.fieldnames[2:])
    for row in setupReader:
        if row['Day'] in days:
            break
        else:
            days.append(row['Day'])
    averagesRawData.seek(0)

    # For each agent type, calculate the average satisfaction for the end of each day.
    reader = csv.reader(averagesRawData)
    for i in range(len(fieldNames)):
        endOfDayAverages = []
        for j in range(len(days)):
            dailyAverages = []
            for row in reader:
                if row[1] == days[j]:
                    dailyAverages.append((row[i + 2]))
            if dailyAverages:
                npDailyAverages = np.array(dailyAverages).astype(np.float)
                dailyAverageOverSimulationRuns = np.mean(npDailyAverages)
                endOfDayAverages.append(dailyAverageOverSimulationRuns)
            averagesRawData.seek(0)

        # Get new line styling combination.
        colour = i
        while colour > len(colours) - 1:
            colour -= len(colours)

        lineType = i
        while lineType > len(lineTypes) - 1:
            lineType -= len(lineTypes)

        # Add the agents data plots to the graph data.
        data.append(py.graph_objs.Scatter(
            x=days,
            y=endOfDayAverages,
            name=fieldNames[i],
            line=dict(
                color=colours[colour],
                width=2,

                dash=lineTypes[lineType])
        ))

    # Edit the layout
    layout = dict(title='Average consumer satisfaction at the end of each day',
                  xaxis=dict(title='Day', zeroline=True),
                  yaxis=dict(title='Average consumer satisfaction', zeroline=True))

    # Create the file
    fig = dict(data=data, layout=layout)
    fileName = convertedBaseFileName.replace('averages', 'endOfDayAverages')
    fullPath = os.path.join(OUTPUT_DIR, fileName)
    py.io.write_image(fig, fullPath)

print('Analysing during day averages...', flush=True)

with open(individualAgentSatisfactionLevels) as individualsRawData:
    setupReader = csv.DictReader(individualsRawData)
    for row in setupReader:
        if row['Day'] != days[0]:
            break
        elif not row['Round'] in rounds:
            rounds.append(row['Round'])
    individualsRawData.seek(0)

    reader = csv.reader(individualsRawData)
    next(reader)
    for i in range(len(daysToAnalyse)):
        # Store calculated graph data
        data = []

        for j in range(len(fieldNames) - 2):
            endOfRoundAverages = []
            for k in range(len(rounds)):
                agentSatisfactionLevels = []
                for row in reader:
                    if int(row[1]) == int(daysToAnalyse[i]) \
                            and int(row[2]) == int(rounds[k]) \
                            and int(row[4]) == int(j + 1):
                        agentSatisfactionLevels.append(row[5])
                if agentSatisfactionLevels:
                    npAgentSatisfactionLevels = np.array(agentSatisfactionLevels).astype(np.float)
                    duringDayAverageOverSimulationRuns = np.mean(npAgentSatisfactionLevels)
                    endOfRoundAverages.append(duringDayAverageOverSimulationRuns)
                individualsRawData.seek(0)
                next(reader)
                print("AGENT TYPE: " + fieldNames[j + 2] + "  DAY: " + str(daysToAnalyse[i]) + "  ROUND: " +
                      str(rounds[k]), flush=True)

            # Get new line styling combination.
            colour = j
            while colour > len(colours) - 1:
                colour -= len(colours)

            lineType = j
            while lineType > len(lineTypes) - 1:
                lineType -= len(lineTypes)

            # Add the agents data plots to the graph data.
            data.append(py.graph_objs.Scatter(
                x=rounds,
                y=endOfRoundAverages,
                name=fieldNames[j + 2],
                line=dict(
                    color=colours[colour],
                    width=2,
                    dash=lineTypes[lineType])
            ))

        day = p.number_to_words(p.ordinal(daysToAnalyse[i]))
        title = 'Average consumer satisfaction during the ' + day + ' day'

        # Edit the layout
        layout = dict(title=title,
                      xaxis=dict(title='Rounds', zeroline=True),
                      yaxis=dict(title='Average consumer satisfaction', zeroline=True))

        # Create the file
        fig = dict(data=data, layout=layout)
        fileName = convertedBaseFileName.replace('averages', 'duringDayAveragesDay' + str(daysToAnalyse[i]))
        fullPath = os.path.join(OUTPUT_DIR, fileName)
        py.io.write_image(fig, fullPath)
