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
boxPlotSatisfactionLevels = sys.argv[5]

# Get the specific days to have average satisfaction visualised throughout the day.
daysToAnalyse = ast.literal_eval(sys.argv[6])

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
colours = ['rgba(93, 164, 214, 0.8)', 'rgba(255, 144, 14, 0.8)', 'rgba(44, 160, 101, 0.8)',
           'rgba(255, 65, 54, 0.8)', 'rgba(207, 114, 255, 0.8)', 'rgba(127, 96, 0, 0.8)']
lineTypes = ['solid', 'dot', 'dash', 'dashdot', 'longdashdot']

# Update user on progress.
print('Analysing end of day averages...', flush=True)

with open(endOfDaySatisfactionLevels) as endOfDayRawData:
    # Store calculated graph data
    data = []

    # Used to distinguish results when many agent types present.
    lineType = 0

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
            lineType += 1

        while lineType > len(lineTypes) - 1:
            lineType -= len(lineTypes)

        # Add the agents data plots to the graph data.
        data.append(
            py.graph_objs.Scatter(
                x=days,
                y=endOfDayAverages,
                name=fieldNames[i],
                line=dict(
                    color=colours[colour],
                    width=1,
                    dash=lineTypes[lineType]
                )
            )
        )

    # Edit the layout
    layout = dict(
        title='Average consumer satisfaction at the end of each day',
        xaxis=dict(
            title='Day',
            showline=True,
            linecolor='black',
            linewidth=2,
            gridcolor='rgb(255, 255, 255)',
            gridwidth=2,
            range=[days[0], days[-1]]
        ),
        yaxis=dict(
            title='Average consumer satisfaction',
            showline=True,
            linecolor='black',
            linewidth=2,
            gridcolor='rgb(255, 255, 255)',
            gridwidth=2,
            range=[0, 1]
        ),
        margin=dict(
            l=40,
            r=30,
            b=80,
            t=100,
        ),
        paper_bgcolor='rgb(243, 243, 243)',
        plot_bgcolor='rgb(243, 243, 243)'
        )

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

        # Used to distinguish results when many agent types present.
        lineType = 0

        for j in range(len(fieldNames) - 2):
            endOfRoundAverages = []
            for k in range(len(rounds)):
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
                lineType += 1

            while lineType > len(lineTypes) - 1:
                lineType -= len(lineTypes)

            # Add the agents data plots to the graph data.
            data.append(
                py.graph_objs.Scatter(
                    x=rounds,
                    y=endOfRoundAverages,
                    name=fieldNames[j + 2],
                    line=dict(
                        color=colours[colour],
                        width=1,
                        dash=lineTypes[lineType]
                    )
                )
            )

        day = p.number_to_words(p.ordinal(daysToAnalyse[i]))
        title = 'Average consumer satisfaction during the ' + day + ' day'

        # Edit the layout
        layout = dict(
            title=title,
            xaxis=dict(
                title='Rounds',
                showline=True,
                linecolor='black',
                linewidth=2,
                gridcolor='rgb(255, 255, 255)',
                gridwidth=2,
                range=[rounds[0], rounds[-1]]
            ),
            yaxis=dict(
                title='Average consumer satisfaction',
                showline=True,
                linecolor='black',
                linewidth=2,
                gridcolor='rgb(255, 255, 255)',
                gridwidth=2,
                range=[0, 1]
            ),
            margin=dict(
                l=40,
                r=30,
                b=80,
                t=100,
            ),
            paper_bgcolor='rgb(243, 243, 243)',
            plot_bgcolor='rgb(243, 243, 243)'
            )

        # Create the file
        fig = dict(data=data, layout=layout)
        fileName = convertedBaseFileName.replace(
            'prePreparedEndOfDayAverages', 'duringDayAveragesDay' + str(daysToAnalyse[i]))
        fullPath = os.path.join(OUTPUT_DIR, fileName)
        py.io.write_image(fig, fullPath)
        print('During day ' + str(daysToAnalyse[i]) + ' averages graphed', flush=True)

with open(boxPlotSatisfactionLevels) as boxPlotData:
    reader = csv.reader(boxPlotData)
    for i in range(len(daysToAnalyse)):
        # Store calculated graph data
        data = []

        for j in range(len(fieldNames) - 2):
            plots = []
            boxPlotData.seek(0)
            next(reader)
            for row in reader:
                if int(row[0]) == int(daysToAnalyse[i]) \
                        and int(row[1]) == int(j + 1):
                    plots.append(row[2])

            # Get new colour.
            colour = j
            while colour > len(colours) - 1:
                colour -= len(colours)

            data.append(
                py.graph_objs.Box(
                    x=plots,
                    name=fieldNames[j + 2],
                    boxpoints='all',
                    jitter=0.5,
                    whiskerwidth=0.2,
                    fillcolor=colours[colour],
                    marker_size=2,
                    line_width=1
                )
            )

        day = p.number_to_words(p.ordinal(daysToAnalyse[i]))
        title = 'Consumer satisfaction distribution at the end of the ' + day + ' day'

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
                zerolinewidth=2
            ),
            margin=dict(
                l=60,
                r=30,
                b=80,
                t=100
            ),
            paper_bgcolor='rgb(243, 243, 243)',
            plot_bgcolor='rgb(243, 243, 243)',
            showlegend=False
        )

        fig = dict(data=data, layout=layout)
        fileName = convertedBaseFileName.replace(
            'prePreparedEndOfDayAverages', 'endOfDaySatisfactionDistributionsDay' + str(daysToAnalyse[i]))
        fullPath = os.path.join(OUTPUT_DIR, fileName)
        py.io.write_image(fig, fullPath)
        print('Box plots day ' + str(daysToAnalyse[i]) + ' graphed', flush=True)
