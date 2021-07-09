import ast
import sys
import os
import re
import pandas as pd
from distutils.util import strtobool
from typing import List

""" Creates csv files summarising the satisfaction levels for each agent type with and without social capital as a single file.
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
    sc_sel: List[float] = []
    sc_soc: List[float] = []
    wsc_sel: List[float] = []
    wsc_soc: List[float] = []
    sel_dif: List[float] = []
    soc_dif: List[float] = []

    for l in learningPercentages:
        for e in exchangesArray:
            scPath = baseOutputDirectory + "/useSC_true_AType_mixed/EX_" + str(e) +"_AE_" + str(l) + "_SR_" + r + "/data/endOfDayAverages.csv"
            wscPath = baseOutputDirectory + "/useSC_false_AType_mixed/EX_" + str(e) +"_AE_" + str(l) + "_SR_" + r + "/data/endOfDayAverages.csv"
            sc_df = pd.read_csv(scPath)
            wsc_df = pd.read_csv(wscPath)
            for d in daysOfInterest:
                sc_df_day = sc_df.loc[sc_df['Day'] == d]
                wsc_df_day = wsc_df.loc[wsc_df['Day'] == d]

                ln.append(l)
                ex.append(e)
                dy.append(d)

                socialCapitalSelfish = 0
                socialCapitalSocial = 0
                noSocialCapitalSelfish = 0
                noSocialCapitalSocial = 0

                for i in sc_df_day.index:
                    socialCapitalSelfish = float(sc_df_day['Selfish'][i])
                    socialCapitalSocial = float(sc_df_day['Social'][i])

                for i in wsc_df_day.index:
                    noSocialCapitalSelfish = float(wsc_df_day['Selfish'][i])
                    noSocialCapitalSocial = float(wsc_df_day['Social'][i])

                sc_sel.append(socialCapitalSelfish)
                sc_soc.append(socialCapitalSocial)
                wsc_sel.append(noSocialCapitalSelfish)
                wsc_soc.append(noSocialCapitalSocial)

                selfish_dif = socialCapitalSelfish - noSocialCapitalSelfish
                social_dif = socialCapitalSocial - noSocialCapitalSocial
                sel_dif.append(selfish_dif)
                soc_dif.append(social_dif)

    d = {'%_Learning':ln, 'Exchanges':ex, 'Day':dy, 'SC_Selfish':sc_sel, 'SC_Social':sc_soc, 'WSC_Selfish':wsc_sel, 'WSC_Social':wsc_soc, 'Selfish_Diff':sel_dif, 'Social_Diff':soc_dif}
    df = pd.DataFrame(data=d)
    fn = resultDir + "/" + r + "_comp_merged_summary.csv"
    df.to_csv(fn, index=False)