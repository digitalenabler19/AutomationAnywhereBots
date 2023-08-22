import xmltodict
import json
import uuid

def convertUIPathXAMLtoJSON(InputFolder):
    with open(InputFolder+'/UIPathBot.xaml') as fd:
        data = xmltodict.parse(fd.read())
    # using json.dumps to convert dictionary to JSON
    json_data_ = json.dumps(data, indent=3)
    json_data = json.loads(json_data_)
    return json_data

def getAABotTemplate(InputFolder):
    with open(InputFolder+'/AABotTemplate.json', 'r') as file:
        json_data = json.load(file)
    return json_data


def getUIPathCommandsJSON(InputFolder):
    with open(InputFolder+'/UIPathCommands.json', 'r') as file:
        json_data = json.load(file)
    return json_data

def insert_json_to_key(target_json, key, json_to_insert):
    target_json[key] = json_to_insert.copy()


def ConvertUIPathBotToA360Bot(InputFolder):
    #Load a sample A360 Bot template. You will get this from .bot file which can be created using "BotApis download bot" command
    AABotTemplate = getAABotTemplate(InputFolder)
    #UIPath will give a .xaml file while exporting a bot. convert XAML to json and loop through the commands and convert to A360 template.
    json_data = convertUIPathXAMLtoJSON(InputFolder)

    sequence = json_data['Activity']['Sequence']
    target_json = ''
    for key in sequence.keys():
        if key.startswith('ui:'):
            UIPathCommand = sequence[key]
            for item in UIPathCommand:
                if key == 'ui:MessageBox':
                    text = item['@Text']
                    UIPathCommands = getUIPathCommandsJSON(InputFolder)
                    text = text.split('"')[1]

                    # addding package
                    json_to_insert = UIPathCommands[key]['AApackage']
                    target_json = AABotTemplate
                    packages = target_json['packages']
                    # check if package is already added
                    packageExist = 0
                    if len(packages) != 0:
                        for package in packages:
                            if package["name"] == UIPathCommands[key]['AAcommandName']:
                                packageExist = 1
                    if packageExist == 0:
                        packages.append(json_to_insert.copy())
                        json_to_insert = packages
                        # Insert json_to_insert into "details" key of target_json
                        insert_json_to_key(target_json, "packages", json_to_insert)
                    # adding nodes
                    AANode_to_insert = UIPathCommands[key]['AANodeTemplate']


                    nodes = target_json['nodes']
                    attributes = UIPathCommands[key]['AANodeTemplate']['attributes']
                    #In A360, each Bot lines UID should be unqiue. so creating random UId for each lines in the bot
                    uid = str(uuid.uuid1())
                    AANode_to_insert['uid'] = uid
                    attributes[1]['value']['string'] = text
                    AANode_to_insert['attributes'] = attributes.copy()
                    nodes.insert(len(nodes),AANode_to_insert.copy())
                    insert_json_to_key(target_json, "nodes", nodes.copy())
    with open(InputFolder+'/A360.bot', 'w') as file:
        file.write(str(target_json))
    return 'Successfully converted UIPath bot to A360 Bot. Please find the A360 Bot file here' + InputFolder+'/A360.bot';

