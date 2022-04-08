import ast
import sys
import os
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
from typing import List

""" Creates heat maps summarising the satisfaction levels for each agent type when they make up 100% of the population.
This is completed for each simulation version.

Parameters
---------
folderName : str
    The output destination folder, used to organise output data.
exchangesArray : str
    Array of the various number of exchanges per day that were simulated.
    Note that this is immediately converted to type List[int].
daysOfInterest : str
    Array containing the days to be analysed.
    Note that this is immediately converted to type List[int].
"""

folderName: str = sys.argv[1]
exchangesArray: List[int] = ast.literal_eval(sys.argv[2])
daysOfInterest: List[int] = ast.literal_eval(sys.argv[3])

baseOutputDirectory: str = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__))))), folderName)

dataDir = baseOutputDirectory + "/comparativeHeatMaps/data/"
imageDir = baseOutputDirectory + "/comparativeHeatMaps/images/"

# Create the output directories if they do not already exist.
if not os.path.exists(imageDir):
    os.makedirs(imageDir)

df = pd.read_csv(dataDir + "individual_populations_summary.csv")

selfish = df.pivot("Day", "Exchanges", "Selfish")
social = df.pivot("Day", "Exchanges", "SC_Social")
social_AE = df.pivot("Day", "Exchanges", "WSC_Social")

width: float = 0
height: float = 0
for d in range(len(daysOfInterest) + 1):
    height += 0.5
for e in range(len(exchangesArray) + 2):
    width += 0.5
width = (width * 3) + 0.5
height += 0.5

fig, (ax1, ax2, ax3) = plt.subplots(nrows=1, ncols=3, sharex='col', sharey='row', figsize=(width, height))

sns.heatmap(selfish, cmap="Reds", center= 0.5, vmin=0, vmax=1.0, ax=ax1, linewidths=0.1, linecolor="white", cbar=True, annot=True, annot_kws={"size": 10})
ax1.set_title(r"$\bf{Selfish}$", fontsize=14)
ax1.invert_yaxis()
ax1.set_ylabel('')
ax1.set_xlabel('')
ax1.set_xticklabels(ax1.get_xticklabels(), rotation = 0)

sns.heatmap(social_AE, cmap="Reds", center= 0.5, vmin=0, vmax=1.0, ax=ax2, linewidths=0.1, linecolor="white", cbar=True, annot=True, annot_kws={"size": 10})
ax2.set_title(r"$\bf{Social}$" + " " + r"$\bf{without}$" + " " + r"$\bf{Social}$" + " " + r"$\bf{Capital}$", fontsize=14)
ax2.invert_yaxis()
ax2.set_ylabel('')
ax2.set_xlabel('')
ax2.set_xticklabels(ax1.get_xticklabels(), rotation = 0)

sns.heatmap(social, cmap="Reds", center= 0.5, vmin=0, vmax=1.0, ax=ax3, linewidths=0.1, linecolor="white", cbar=True, annot=True, annot_kws={"size": 10})
ax3.set_title(r"$\bf{Social}$" + " " + r"$\bf{with}$" + " " + r"$\bf{Social}$" + " " + r"$\bf{Capital}$", fontsize=14)
ax3.invert_yaxis()
ax3.set_ylabel('')
ax3.set_xlabel('')
ax3.set_xticklabels(ax1.get_xticklabels(), rotation = 0)

fig.text(0.5, 0.06, 'Exchanges', ha='center', fontsize=16)
fig.text(0.07, 0.5, 'Day', va='center', rotation='vertical', fontsize=16)
fig.suptitle('Average Satisfaction in Single Strategy Populations', fontsize=18)

plt.subplots_adjust(hspace = .2)
plt.subplots_adjust(wspace = .02)
plt.subplots_adjust(top = .8)
plt.subplots_adjust(bottom = .2)
plt.subplots_adjust(left = .12)
plt.subplots_adjust(right = .95)

fname = imageDir + "individual_populations"

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
