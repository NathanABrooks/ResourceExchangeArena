import ast
import csv
import inflect
import os
import plotly as py
import sys

# Get the current release from command line arguments.
releaseVersion = sys.argv[1]

# Unique identifier to identify which run the produced graphs are associated with.
uniqueTag = sys.argv[2]

# Get the location of the raw data that requires visualising from command line arguments.
endOfDaySatisfactionLevels = sys.argv[3]
duringDaySatisfactionLevels = sys.argv[4]

# Get the specific days to have average satisfaction visualised throughout the day.
daysToAnalyse = ast.literal_eval(sys.argv[5])

# Used to get ordinal word versions of integers for graph titles.
p = inflect.engine()

# Create the output directories if they do not already exist.
OUTPUT_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(__file__))),
                          'outputData/' + releaseVersion + '/' + uniqueTag + '/images')

if not os.path.exists(OUTPUT_DIR):
    os.makedirs(OUTPUT_DIR)

# Generate suitable filenames for the graphs that will be produced.
baseFileName = endOfDaySatisfactionLevels.split('\\')[-1]
convertedBaseFileName = baseFileName.split('.')[0] + '.pdf'

# Store the scope of the data which will be the same for each graph.
days = []
rounds = []
fieldNames = []

# Options for graph data visuals.
colours = ['rgb(205,0,0)', 'rgb(0,0,205)', 'rgb(0,205,0)', 'rgb(205,0,205)', 'rgb(205,205,0)']
lineTypes = ['dot', 'dash', 'longdash', 'dashdot', 'longdashdot', 'solid']

# Update user on progress.
print('Analysing end of day averages...', flush=True)

with open(endOfDaySatisfactionLevels) as endOfDayRawData:
    # Store calculated graph data
    data = []

    setupReader = csv.DictReader(endOfDayRawData)
    fieldNames.extend(setupReader.fieldnames[1:])
    for row in setupReader:
        if row['Day'] in days:
            break
        else:
            days.append(row['Day'])
    endOfDayRawData.seek(0)

    # For each agent type, calculate the average satisfaction for the end of each day.
    reader = csv.reader(endOfDayRawData)
    for i in range(len(fieldNames)):
        endOfDayAverages = []
        endOfDayRawData.seek(0)
        for j in range(len(days)):
            for row in reader:
                if row[0] == days[j]:
                    endOfDayAverages.append((row[i + 1]))
                    break
        print(str(i + 1) + '/' + str(len(fieldNames)) + ' agent types analysed', flush=True)
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
    fileName = convertedBaseFileName.replace('prePreparedEndOfDayAverages', 'endOfDayAverages')
    fullPath = os.path.join(OUTPUT_DIR, fileName)
    py.io.write_image(fig, fullPath)
    print('End of day averages graphed', flush=True)

print('Analysing during day averages...', flush=True)

with open(duringDaySatisfactionLevels) as duringDayRawData:
    setupReader = csv.DictReader(duringDayRawData)
    for row in setupReader:
        if row['Day'] != days[0]:
            break
        elif not row['Round'] in rounds:
            rounds.append(row['Round'])
    duringDayRawData.seek(0)

    reader = csv.reader(duringDayRawData)
    for i in range(len(daysToAnalyse)):
        # Store calculated graph data
        data = []

        for j in range(len(fieldNames) - 2):
            endOfRoundAverages = []
            for k in range(len(rounds)):
                agentSatisfactionLevels = []
                duringDayRawData.seek(0)
                next(reader)
                for row in reader:
                    if int(row[0]) == int(daysToAnalyse[i]) \
                            and int(row[1]) == int(rounds[k]) \
                            and int(row[2]) == int(j + 1):
                        endOfRoundAverages.append(row[3])
                        break

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
        fileName = convertedBaseFileName.replace(
            'prePreparedEndOfDayAverages', 'duringDayAveragesDay' + str(daysToAnalyse[i]))
        fullPath = os.path.join(OUTPUT_DIR, fileName)
        py.io.write_image(fig, fullPath)
        print('During day ' + str(daysToAnalyse[i]) + ' averages graphed', flush=True)
