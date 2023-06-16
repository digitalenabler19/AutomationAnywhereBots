# Authors: Srikanth Koorma
# Partner Solution Desk
# Date: 13-08-2021
# V 1.0
# Migrate users from v11 control room to A360 control room
# Pass the parameters as a list in the following order
# V11_SERVER, V11_USERNAME, V11_PASSWORD, A360_SERVER, A360_USERNAME, A360_PASSWORD, LOGGER_FILE_PATH, LOG_LEVEL

import sys
import logging
import inspect
import requests

logger = logging.getLogger(__name__)
created_users = list()


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
        raise Exception(
            "Authentication failure, code: {0}, message: {1}".format(response.status_code, json_str["message"]))


def create_user(token, server_ip, user):
    log('Create user started:', "debug")

    url = server_ip + "/v1/usermanagement/users"
    log("Url: {0}".format(url), 'debug')

    payload = "{\"roles\":[{\"id\":2}],\"email\":\"" + user["email"] + "\",\"enableAutoLogin\":false,\"username\":\"" + \
              user["username"] + "\",\"description\":\"\",\"domain\":\"" + user["domain"] + "\",\"firstName\":\"" + user["firstName"] + "\",\"lastName\":\"" + \
              user["lastName"] + "\",\"disabled\":false,\"password\":\"password\",\"passwordSet\": false,\"licenseFeatures\":[],\"sysAssignedRoles\":[],\"deviceCredentialAttested\":false}"
    log("Payload: {0}".format(payload), 'debug')

    header = {'Content-Type': 'application/json', "X-Authorization": token}

    response = requests.post(url, data=payload, headers=header)
    json_str = response.json()

    if response.status_code == 201:
        log("Created user ({0} {1}) with username: {2}".format(user["firstName"], user["lastName"], user["username"]),
            "info")
        # created_users.append("{\"id\": \"" + str(json_str["id"]) + "\", \"username\": \"" + json_str["username"] + "\"}")
        created_users.append({"id": str(json_str["id"]), "username": json_str["username"]})

    else:
        log("Create user failed, error code: {0}, message: {1}".format(response.status_code, json_str["message"]),
            'error')
        raise Exception(
            "Create user failed, error code: {0}, message: {1}".format(response.status_code, json_str["message"]))


def get_users(token, server_ip):
    log('Get users started:', "debug")

    url = server_ip + "/v1/usermanagement/users/list"
    log("Url: {0}".format(url), 'debug')

    payload = "{\"sort\": [{\"field\": \"username\", \"direction\": \"asc\"}]}"
    log("Payload: {0}".format("{\"sort\": [{\"field\": \"username\", \"direction\": \"asc\"}]}"), 'debug')

    header = {'Content-Type': 'application/json', "X-Authorization": token}

    response = requests.post(url, data=payload, headers=header)
    json_str = response.json()

    if response.status_code == 200:
        log("Successfully retrieved users from control room.", "info")
        return json_str["list"]
    else:
        log("Error message: {0}".format(json_str["message"]), 'error')
        raise Exception("Response code: {0}, message: {1}".format(response.status_code, json_str["message"]))


def check_user_in_a360(user_str, a360_users):
    result = False

    for user in a360_users:
        if user["username"] == user_str:
            print(user)
            result = True
            log("User {0} is found in A360 control room.".format(user_str), "debug")
            break
    return result


def delete_user(token, server_ip, user):
    log('Delete user started:', "debug")

    url = server_ip + "/v1/usermanagement/users/" + str(user["id"])
    log("Url: {0}".format(url), 'debug')

    header = {'Content-Type': 'application/json', "X-Authorization": token}

    response = requests.delete(url, headers=header)
    json_str = response.json()

    if response.status_code == 200:
        log("Successfully deleted user ({0}) from control room.".format(user["username"]), "info")
    else:
        log("Error message: {0}".format(json_str["message"]), 'error')
        raise Exception("Response code: {0}, message: {1}".format(response.status_code, json_str["message"]))


# Pass a list as argument to the method which should have parameters in a sequence given at the script start in comments
def migrate_users(args):
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

        v11_users = a360_users = list()
        a360_auth_token = ""

        if auth_response_v11["status_code"] == 200:
            v11_auth_token = auth_response_v11["auth_token"]
            log("Token {0} is generated by authentication from v11 Control room.".format(v11_auth_token[0:20] + ".********************"), "debug")
            v11_users = get_users(v11_auth_token, v11_server)

        if auth_response_a360["status_code"] == 200:
            a360_auth_token = auth_response_a360["auth_token"]
            log("Token {0} is generated by authentication from A360 Control room.".format(a360_auth_token[0:20] + ".********************"), "debug")
            a360_users = get_users(a360_auth_token, a360_server)

        if len(v11_users) > 0 and len(a360_users) > 0:
            for user in v11_users:
                if not check_user_in_a360(user["username"], a360_users):
                    create_user(a360_auth_token, a360_server, user)

            # ########################################################################
            # Uncomment below block to delete the created users which are used for testing
            # ########################################################################
            """
            for user in created_users:
                delete_user(a360_auth_token, a360_server, user)
            """
            # ########################################################################
        else:
            log("One of the environment has no users.", "error")
            raise Exception("One of the environment has no users.")
    except Exception as err:
        exc_type, exc_obj, exc_tb = sys.exc_info()
        log("Line {0}: {1}: {2}".format(exc_tb.tb_lineno, replace_all(str(exc_type), '', ['<class \'', '\'>']), err),
            'error')


# Pass the parameters in the following order
# V11_SERVER, V11_USERNAME, V11_PASSWORD, A360_SERVER, A360_USERNAME, A360_PASSWORD, LOGGER_FILE_PATH, LOG_LEVEL
# Comment below line if you are running this script in control room
# migrate_users(sys.argv[1:])
