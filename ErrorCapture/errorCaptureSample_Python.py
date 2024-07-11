import json
import os
import logging
import datetime
import traceback
#Author:Prasad Katankot
#Global Solution Desk
#ii-July-2024

def logConfig(path, botName):
    # if you are invoking directly or indirectly from a bot, give a path other than the default path of python which
    # is os.getcwd(). The reason is file creation will not be allowed in default path of bot run time which will also
    # be the python run time.

    if path != "":
        os.chdir(path)
    currDate = str(datetime.date.today())
    logger = logging.getLogger(__name__)
    logger.setLevel(logging.INFO)
    formatter = logging.Formatter('%(asctime)s:%(levelname)s:%(name)s:%(message)s')
    file_handler = logging.FileHandler(botName+'_'+currDate+".log")
    file_handler.setFormatter(formatter)
    logger.addHandler(file_handler)
    return logger

def python_error_sample(botData):
    try:
        botData = json.loads(botData)
        #to save python level logs in the local disk of the bot runner
        logPath = botData['logPath']
        botName = botData['botName']
        logger = logConfig(logPath, botName)
        logger.info("------------------------EXCEPTION FOUND--------------------")
        logger.info(botData)
        x = 1/0
        return 'success'
    except Exception as innerException:
        logger.info("------------------------EXCEPTION FOUND--------------------")
        # Get the current line number where the exception occurred
        line_number = traceback.extract_tb(innerException.__traceback__)[-1].lineno
        # Get the error message
        error_message = str(innerException)
        print(line_number, error_message)
        logger.error(f"line_number: {line_number}, error_message:{error_message}")
        return "Python Error Encountered in line number:"+ str(line_number)+' with error message:'+error_message
if __name__ == "__main__":
    #f = open(logPath + "/myfile.txt", "w")
    botdata = {"logPath":"C:\\Users\\prasad.katankot\\OneDrive - Automation Anywhere\Documents\\Official\\PythonLogs","botName":"documentUpdateDictionary","daData":"test", "daData_Save":"TRUE"}
    print(python_error_sample(json.dumps(botdata)))