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

i_name = df[(~df['Day'].isin(daysOfInterest)) | (~df['Exchanges'].isin(exchangesArray))].index
df = df.drop(i_name)

selfish = df.pivot("Day", "Exchanges", "Selfish")
social = df.pivot("Day", "Exchanges", "SC_Social")
social_AE = df.pivot("Day", "Exchanges", "WSC_Social")

fig, (ax1, ax2, ax3) = plt.subplots(1, 3, sharex='col', sharey='row')

sns.heatmap(selfish, cmap="Reds", center= 0.5, vmin=0, vmax=1.0, ax=ax1, linewidths=0.1, linecolor="white", cbar=True, annot=True, annot_kws={"size": 10})
ax1.set_title(r"$\bf{Selfish}$", fontsize=12)
ax1.invert_yaxis()
ax1.set_ylabel('')
ax1.set_xlabel('')

sns.heatmap(social_AE, cmap="Reds", center= 0.5, vmin=0, vmax=1.0, ax=ax2, linewidths=0.1, linecolor="white", cbar=True, annot=True, annot_kws={"size": 10})
ax2.set_title(r"$\bf{Social}$" + " " + r"$\bf{without}$" + " " + r"$\bf{Social}$" + " " + r"$\bf{Capital}$", fontsize=12)
ax2.invert_yaxis()
ax2.set_ylabel('')
ax2.set_xlabel('')

sns.heatmap(social, cmap="Reds", center= 0.5, vmin=0, vmax=1.0, ax=ax3, linewidths=0.1, linecolor="white", cbar=True, annot=True, annot_kws={"size": 10})
ax3.set_title(r"$\bf{Social}$" + " " + r"$\bf{with}$" + " " + r"$\bf{Social}$" + " " + r"$\bf{Capital}$", fontsize=12)
ax3.invert_yaxis()
ax3.set_ylabel('')
ax3.set_xlabel('')

fig.text(0.5, 0.02, 'Exchanges', ha='center', fontsize=14)
fig.text(0.04, 0.5, 'Day', va='center', rotation='vertical', fontsize=14)
fig.suptitle('Average Satisfaction in Single Strategy Populations', fontsize=14)
plt.subplots_adjust(hspace = .2)
plt.subplots_adjust(wspace = .2)
plt.subplots_adjust(top = .8)
plt.subplots_adjust(bottom = .2)
plt.subplots_adjust(left = .12)
plt.subplots_adjust(right = .95)

fname = imageDir + "individual_populations"

plt.savefig(fname,
        dpi=None,
        facecolor='w',
        edgecolor='w',
        orientation='portrait',
        format=None,
        transparent=False,
        bbox_inches=None,
        pad_inches=0.1,
        metadata=None)
