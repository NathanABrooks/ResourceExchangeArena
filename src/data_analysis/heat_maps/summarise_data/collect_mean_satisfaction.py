import ast
import sys
import os
import re
import pandas as pd
from distutils.util import strtobool
from typing import List

""" Create csv files summarising the mean satisfaction levels for each agent type with and without social capital.
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

resultDir = baseOutputDirectory + "/comparativeHeatMaps/data/"
# Create the output directories if they do not already exist.
if not os.path.exists(resultDir):
    os.makedirs(resultDir)

for r in startingRatiosArray:

    ln: List[int] = []
    ex: List[int] = []
    dy: List[int] = []
    sc: List[float] = []
    wsc: List[float] = []
    dif: List[float] = []
    sc_sel: List[float] = []
    sc_soc: List[float] = []
    wsc_sel: List[float] = []
    wsc_soc: List[float] = []
    dif_sel: List[float] = []
    dif_soc: List[float] = []

    for l in learningPercentages:
        for e in exchangesArray:
            sc_path = baseOutputDirectory + "/useSC_true_AType_mixed/EX_" + str(e) +"_AE_" + str(l) + "_SR_" + r + "/data/endOfDaySatisfactions.csv"
            wsc_path = baseOutputDirectory + "/useSC_false_AType_mixed/EX_" + str(e) +"_AE_" + str(l) + "_SR_" + r + "/data/endOfDaySatisfactions.csv"
            sc_df = pd.read_csv(sc_path)
            wsc_df = pd.read_csv(wsc_path)

            for d in daysOfInterest:
                ln.append(l)
                ex.append(e)
                dy.append(d)

                sc_df_day = sc_df.loc[sc_df['Day'] == d]
                wsc_df_day = wsc_df.loc[wsc_df['Day'] == d]

                scMean = sc_df_day['Satisfaction'].mean()
                wscMean = wsc_df_day['Satisfaction'].mean()
                meanDiff = wscMean - scMean

                sc.append(scMean)
                wsc.append(wscMean)
                dif.append(meanDiff)

                scSel_df_day = sc_df_day.loc[sc_df_day['Agent Type'] == 1]
                scSoc_df_day = sc_df_day.loc[sc_df_day['Agent Type'] == 2]
                wscSel_df_day = wsc_df_day.loc[wsc_df_day['Agent Type'] == 1]
                wscSoc_df_day = wsc_df_day.loc[wsc_df_day['Agent Type'] == 2]

                scSelMean = scSel_df_day['Satisfaction'].mean()
                scSocMean = scSoc_df_day['Satisfaction'].mean()
                wscSelMean = wscSel_df_day['Satisfaction'].mean()
                wscSocMean = wscSoc_df_day['Satisfaction'].mean()
                selMeanDiff = wscSelMean - scSelMean
                socMeanDiff = wscSocMean - scSocMean

                sc_sel.append(scSelMean)
                sc_soc.append(scSocMean)
                wsc_sel.append(wscSelMean)
                wsc_soc.append(wscSocMean)
                dif_sel.append(selMeanDiff)
                dif_soc.append(socMeanDiff)

    d = {'Day':dy, '%_Learning':ln, 'Exchanges':ex, 'SC_Satisfaction':sc, 'WSC_Satisfaction':wsc, 'Difference':dif, 'SC_Selfish':sc_sel, 'SC_Social':sc_soc, 'WSC_Selfish':wsc_sel, 'WSC_Social':wsc_soc, 'Selfish_Diff':dif_sel, 'Social_Diff':dif_soc}
    df = pd.DataFrame(data=d)
    fn = resultDir + "/" + r + "_comp_mean_satisfaction.csv"
    df.to_csv(fn, index=False)
