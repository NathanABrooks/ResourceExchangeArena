import ast
import sys
import os
import re
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from distutils.util import strtobool
from matplotlib import cm
from matplotlib.colors import ListedColormap
from typing import List

""" Create heat maps summarising the satisfaction levels and population distributions for each agent type with and without social capital.
This is completed for each simulation version.

Parameters
---------
folderName : str
    The output destination folder, used to organise output data.
learningPercentages : str
    Array of the percentage of Agents that possibly used social Learning per day.
    Note that this is immediately converted to type List[int].
exchangesArray : str
    Array of the various number of exchanges per day that were simulated.
    Note that this is immediately converted to type List[int].
startingRatiosArray : str[]
    Array of the various agent type starting ratios that were simulated.
daysOfInterest : str
    Array containing the days to be analysed.
    Note that this is immediately converted to type List[int].
"""

folderName: str = sys.argv[1]
learningPercentages: List[int] = ast.literal_eval(sys.argv[2])
exchangesArray: List[int] = ast.literal_eval(sys.argv[3])

temp: str = re.sub('\[|\]| ', '', sys.argv[4])
startingRatiosArray: List[str] = list(temp.split(","))

daysOfInterest: List[int] = ast.literal_eval(sys.argv[5])

baseOutputDirectory: str = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__))))), folderName)

simulationVersions: List[str] = []
for it in os.scandir(baseOutputDirectory):
    if it.is_dir():
        simulationVersions.append(it.path)

top1 = cm.get_cmap('Purples_r', 128)
bottom1 = cm.get_cmap('Greens', 128)

newcolors1 = np.vstack((top1(np.linspace(0, 1, 128)),
                       bottom1(np.linspace(0, 1, 128))))
newcmp1 = ListedColormap(newcolors1, name='GreenPurple')

width: float = 0
height: float = 0
for d in range(len(daysOfInterest)):
    height += 0.5
for e in range(len(exchangesArray) + 2):
    width += 0.5
width = (width * 3) - 1
height = height + 2.65

for v in simulationVersions:
    if v.endswith('mixed'):
        socialCapital: bool = strtobool((v.split('useSC_')[1]).split('_')[0])

        for r in startingRatiosArray:

            # Create the output directories if they do not already exist.
            resultDir: str = v + "/comparative/images/heat_maps/" + r + "/"
            if not os.path.exists(resultDir):
                os.makedirs(resultDir)

            df_sat = pd.read_csv(v + "/comparative/data/heat_maps/" + r + "/mergedSummary.csv")
            df_pop = pd.read_csv(v + "/comparative/data/heat_maps/" + r + "/mergedPopulationSummary.csv")

            #rounds all value to 2 decimal places (for the sake of the heatmap visualisation
            df_sat = df_sat.round(2)
            df_pop = df_pop.round(0)

            df_sat_l = df_sat.loc[df_sat['%_Learning'] == 100]
            df_pop_l = df_pop.loc[df_pop['%_Learning'] == 100]

            selfish_pivot = df_sat_l.pivot("Day", "Exchanges", "Selfish")
            social_pivot = df_sat_l.pivot("Day", "Exchanges", "Social")
            population_pivot = df_pop_l.pivot("Day", "Exchanges", "%_Social")

            fig, ax = plt.subplots(ncols=3, sharex='col', sharey='row', figsize=(width, height))

            #seaborn heatmap per pivot table
            sns.heatmap(selfish_pivot, cmap="Reds", center= 0.5, vmin=0, vmax=1.0, ax=ax[0], linewidths=0.1, linecolor="white", cbar=True, annot=True, annot_kws={"size": 10})
            title: str = "Average Selfish Satisfaction"
            ax[0].set_title(title, fontsize=16, y=1.11)
            ax[0].invert_yaxis()
            ax[0].set_ylabel('')
            ax[0].set_xlabel('')

            #seaborn heatmap per pivot table
            sns.heatmap(social_pivot, cmap="Reds", center= 0.5, vmin=0, vmax=1.0, ax=ax[1], linewidths=0.1, linecolor="white", cbar=True, annot=True, annot_kws={"size": 10})
            title: str = "Average Social Satisfaction"
            ax[1].set_title(title, fontsize=16, y=1.11)
            ax[1].invert_yaxis()
            ax[1].set_ylabel('')
            ax[1].set_xlabel('')
            
            #seaborn heatmap per pivot table
            sns.heatmap(population_pivot, cmap=newcmp1, center= 50, vmin=0, vmax=100, ax=ax[2], linewidths=0.1, linecolor="white", cbar=True, annot=True, fmt='g', annot_kws={"size": 10})
            title: str = "% Population Using Social Strategy"
            ax[2].set_title(title, fontsize=16, y=1.11)
            ax[2].invert_yaxis()
            ax[2].set_ylabel('')
            ax[2].set_xlabel('')
            ax[2].set_xticklabels(ax[2].get_xticklabels(), rotation = 0)

            #adjust position of subplot
            plt.subplots_adjust(wspace = .05)
            plt.subplots_adjust(top = .8)
            plt.subplots_adjust(bottom = .15)
            plt.subplots_adjust(left = .1)
            plt.subplots_adjust(right = .95)

            #set x and y axis labels and plot title
            fig.text(0.5, 0.06, 'Exchanges', ha='center', fontsize=16)
            fig.text(0.05, 0.5, 'Day', va='center', rotation='vertical', fontsize=16)

            title: str = "Strategy Performance Comparison"

            fig.suptitle(title, fontsize=18)

            fname = resultDir + "/compactMergedSummary"

            plt.savefig(fname,
                dpi=None,
                facecolor='w',
                edgecolor='w',
                orientation='landscape',
                format=None,
                transparent=False,
                bbox_inches=None,
                pad_inches=0,
                metadata=None)
