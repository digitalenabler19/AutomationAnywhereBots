# Authors: Srikanth Koorma
# Partner Solution Desk
# Date: 13-08-2021
# V 1.0
# Migrate roles from v11 control room to A360 control room
# Pass the parameters as a list in the following order
# V11_SERVER, V11_USERNAME, V11_PASSWORD, A360_SERVER, A360_USERNAME, A360_PASSWORD, LOGGER_FILE_PATH, LOG_LEVEL

import sys
import logging
import inspect
import requests

logger = logging.getLogger(__name__)
created_roles = list()


def log(log_msg, log_level):
    # Automatically log the current function details.
    # Get the previous frame in the stack, otherwise it would be this function!!!
    func = inspect.currentframe().f_back.f_code

    # Dump the message + the name of this function to the log.
    logger.log(level=getattr(logging, log_level.upper(), None), msg='{0}): {1}'.format(func.co_name, log_msg))


def initialize_logger(log_file_path, log_level):
    logger.setLevel(getattr(logging, log_level.upper()))
    file_handler = logging.FileHandler(log_file_path, mode='a')
    formatter = logging.Formatter('%(asctime)s %(levelname)s %(name)s (%(message)s', datefmt='(%d-%m-%Y %I.%M.%S %p)')

    file_handler.setFormatter(formatter)
    logger.addHandler(file_handler)

    log("Log file started.", 'info')


def replace_all(text, value, list_li):
    replaced_text = text
    for char in list_li:
        replaced_text = replaced_text.replace(char, value)
    return replaced_text


def authenticate(server_ip, username, password):
    log('Authentication Started:', "debug")

    url = server_ip + "/v1/authentication"
    log("Url: {0}".format(url), 'debug')

    payload = "{\"username\" : \"" + username + "\",\"password\" : \"" + password + "\"}"
    log("Payload: {0}".format("{\"username\" : \"" + username + "\",\"password\" : \"********\"}"), 'debug')

    response = requests.post(url, data=payload)
    json_str = response.json()

    if response.status_code == 200:
        log("Authentication success and token received", 'info')
        log("Token: {0}".format(json_str["token"][0:20] + ".********************"), 'debug')
        return {"status_code": response.status_code, "auth_token": json_str["token"]}
    else:
        log("Authentication failure, error message: {0}".format(json_str["message"]), 'error')
        raise Exception("Authentication failure, code: {0}, message: {1}".format(response.status_code, json_str["message"]))


def create_role(token, server_ip, role):
    log('Create role started:', "debug")

    url = server_ip + "/v1/usermanagement/roles"
    log("Url: {0}".format(url), 'debug')

    payload = "{\"name\":\"" + role["name"] + "\",\"description\":\"\",\"permissions\":[],\"principals\":[]}"
    log("Payload: {0}".format(payload), 'debug')

    header = {'Content-Type': 'application/json', "X-Authorization": token}

    response = requests.post(url, data=payload, headers=header)
    json_str = response.json()

    if response.status_code == 201:
        log("Created role ({0})".format(role["name"]), "info")
        created_roles.append({"id": str(json_str["id"]), "name": json_str["name"]})

    else:
        log("Create role failed, error code: {0}, message: {1}".format(response.status_code, json_str["message"]), 'error')
        raise Exception("Create role failed, error code: {0}, message: {1}".format(response.status_code, json_str["message"]))


def get_roles(token, server_ip):
    log('Get roles started:', "debug")

    url = server_ip + "/v1/usermanagement/roles/list"
    log("Url: {0}".format(url), 'debug')

    payload = "{\"sort\": [{\"field\": \"name\", \"direction\": \"asc\"}]}"
    log("Payload: {0}".format(payload), 'debug')

    header = {'Content-Type': 'application/json', "X-Authorization": token}

    response = requests.post(url, data=payload, headers=header)
    json_str = response.json()

    if response.status_code == 200:
        log("Successfully retrieved roles from control room.", "info")
        json_list = list()
        for json_role in json_str["list"]:
            if not json_role["createdBy"] == 0:
                json_list.append(json_role)
        return json_list
    else:
        log("Error message: {0}".format(json_str["message"]), 'error')
        raise Exception("Response code: {0}, message: {1}".format(response.status_code, json_str["message"]))


def check_role_in_a360(role_str, a360_roles):
    result = False

    for role in a360_roles:
        if role["name"] == role_str:
            result = True
            log("Role {0} is found in A360 control room.".format(role_str), "debug")
            break
    return result


def delete_role(token, server_ip, role):
    log('Delete role started:', "debug")

    url = server_ip + "/v1/usermanagement/roles/" + str(role["id"])
    log("Url: {0}".format(url), 'debug')

    header = {'Content-Type': 'application/json', "X-Authorization": token}

    response = requests.delete(url, headers=header)
    json_str = response.json()

    if response.status_code == 200:
        log("Successfully deleted role ({0}) from control room.".format(role["name"]), "info")
    else:
        log("Error message: {0}".format(json_str["message"]), 'error')
        raise Exception("Response code: {0}, message: {1}".format(response.status_code, json_str["message"]))


# Pass a list as argument to the method which should have parameters in a sequence given at the script start in comments
def migrate_roles(args):
    try:
        v11_server = args[0]
        v11_username = args[1]
        v11_password = args[2]
        a360_server = args[3]
        a360_username = args[4]
        a360_password = args[5]

        initialize_logger(args[6], args[7])
        auth_response_a360 = authenticate(a360_server, a360_username, a360_password)
        auth_response_v11 = authenticate(v11_server, v11_username, v11_password)

        v11_roles = a360_roles = list()
        a360_auth_token = ""

        if auth_response_v11["status_code"] == 200:
            v11_auth_token = auth_response_v11["auth_token"]
            log("Token {0} is generated by authentication from v11 Control room.".format(v11_auth_token[0:20] + ".********************"), "debug")
            v11_roles = get_roles(v11_auth_token, v11_server)

        if auth_response_a360["status_code"] == 200:
            a360_auth_token = auth_response_a360["auth_token"]
            log("Token {0} is generated by authentication from A360 Control room.".format(a360_auth_token[0:20] + ".********************"), "debug")
            a360_roles = get_roles(a360_auth_token, a360_server)

        if len(v11_roles) > 0 and len(a360_roles) > 0:
            for role in v11_roles:
                if not check_role_in_a360(role["name"], a360_roles):
                    create_role(a360_auth_token, a360_server, role)

            # ########################################################################
            # Uncomment below block to delete the created roles which are used for testing
            # ########################################################################
            """
            for role in created_roles:
                delete_role(a360_auth_token, a360_server, role)
            """
            # ########################################################################
        else:
            log("One of the environment has no roles.", "error")
            raise Exception("One of the environment has no roles.")
    except Exception as err:
        exc_type, exc_obj, exc_tb = sys.exc_info()
        log("Line {0}: {1}: {2}".format(exc_tb.tb_lineno, replace_all(str(exc_type), '', ['<class \'', '\'>']), err),
            'error')


# Pass the parameters in the following order
# V11_SERVER, V11_USERNAME, V11_PASSWORD, A360_SERVER, A360_USERNAME, A360_PASSWORD, LOGGER_FILE_PATH, LOG_LEVEL
# Comment below line if you are running this script in control room
# migrate_roles(sys.argv[1:])
