import ast
import sys
import os
import re
import pandas as pd
from distutils.util import strtobool
from typing import List

""" Creates csv files summarising the population distributions with and without social capital.
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

for v in simulationVersions:
    if v.endswith('mixed'):
        socialCapital: bool = strtobool((v.split('useSC_')[1]).split('_')[0])
        for r in startingRatiosArray:

            ln: List[int] = []
            ex: List[int] = []
            dy: List[int] = []
            sel: List[float] = []
            soc: List[float] = []
            pSoc: List[float] = []

            for l in learningPercentages:
                for e in exchangesArray:
                    path = v + "/EX_" + str(e) +"_AE_" + str(l) + "_SR_" + r + "/data/populationDistributions.csv"
                    df = pd.read_csv(path)
                    for d in daysOfInterest:
                        df_day = df.loc[df['Day'] == d]
                        selPop = 0
                        socPop = 0
                        totalPop = 0
                        for i in df_day.index:
                            if int(df_day['Agent Type'][i]) == 1:
                                selPop += float(df_day['Population'][i])
                            elif int(df_day['Agent Type'][i]) == 2:
                                socPop += float(df_day['Population'][i])
                            totalPop += float(df_day['Population'][i])
                        ln.append(l)
                        ex.append(e)
                        dy.append(d)
                        sel.append(selPop)
                        soc.append(socPop)
                        pSoc.append((socPop / totalPop) * 100)

            d = {'%_Learning':ln, 'Exchanges':ex, 'Day':dy, 'Selfish':sel, 'Social':soc, '%_Social':pSoc}
            df = pd.DataFrame(data=d)
            dir = v + "/comparative/data/heat_maps/" + r
            # Create the output directories if they do not already exist.
            if not os.path.exists(dir):
                os.makedirs(dir)
            fn = dir + "/mergedPopulationSummary.csv"
            df.to_csv(fn, index=False)
