from google.oauth2.credentials import Credentials
from googleapiclient.discovery import build
import pandas as pd
import requests

# 🔹 Step 1: Set Up Credentials

CLIENT_ID = "716331754991-f.apps.googleusercontent.com"
CLIENT_SECRET = "WCulr6Jg4pZI6FK"
REFRESH_TOKEN = "1//0g1YH

TOKEN_URI = "https://oauth2.googleapis.com/token"
SCOPES = ["https://www.googleapis.com/auth/spreadsheets"]


def authenticate_google_sheets():
    creds = Credentials(
        None,
        refresh_token=REFRESH_TOKEN,
        token_uri=TOKEN_URI,
        client_id=CLIENT_ID,
        client_secret=CLIENT_SECRET,
        scopes=SCOPES
    )

    service = build("sheets", "v4", credentials=creds)
    return service


# 🔹 Step 2: Read Data from Google Sheet
def read_google_sheet(spreadsheet_id, sheet_name):
    service = authenticate_google_sheets()
    sheet = service.spreadsheets()

    # Read entire sheet
    range_name = f"{sheet_name}"
    result = sheet.values().get(spreadsheetId=spreadsheet_id, range=range_name).execute()
    values = result.get("values", [])

    if not values:
        print("No data found.")
        return None

    # Convert to Pandas DataFrame
    df = pd.DataFrame(values[1:], columns=values[0])
    return df


# 🔹 Step 3: Update a specific cell in a row
def update_cell(spreadsheet_id, sheet_name, row_number, column_letter, new_value):
    service = authenticate_google_sheets()
    sheet = service.spreadsheets()

    # Define cell range (e.g., "B3" for column "B" and row "3")
    cell_range = f"{sheet_name}!{column_letter}{row_number}"

    # Define the new value
    update_data = {
        "values": [[new_value]]
    }

    # Send update request
    request = sheet.values().update(
        spreadsheetId=spreadsheet_id,
        range=cell_range,
        valueInputOption="RAW",
        body=update_data
    )
    response = request.execute()

    print(f"Updated {cell_range} with value: {new_value}")
    return response


def get_row_number(spreadsheet_id, sheet_name, search_criteria):
    """
    Find the row number where multiple cell values match.

    :param spreadsheet_id: Google Sheet ID
    :param sheet_name: Name of the sheet
    :param search_criteria: Dictionary with column letter as key and expected value as value
    :return: Row number if found, else None
    """
    service = authenticate_google_sheets()  # Use existing authentication function
    sheet = service.spreadsheets()

    # Read entire sheet
    range_name = f"{sheet_name}"
    result = sheet.values().get(spreadsheetId=spreadsheet_id, range=range_name).execute()
    values = result.get("values", [])

    if not values:
        print("No data found.")
        return None

    # Extract headers
    headers = values[0]
    data = values[1:]  # Exclude headers

    # Iterate through rows to find a match
    for row_index, row in enumerate(data, start=2):  # Start at 2 (1-based index after headers)
        match = all(row[headers.index(col)] == search_criteria[col] for col in search_criteria if col in headers)

        if match:
            print(f"Row found: {row_index}")
            return row_index

    print("No matching row found.")
    return None


def update_cell_by_header(spreadsheet_id, sheet_name, search_criteria, column_to_update, new_value):
    """
    Update a specific cell in Google Sheets using column headers and search criteria.

    :param spreadsheet_id: Google Sheet ID
    :param sheet_name: Name of the sheet
    :param search_criteria: Dictionary of column_name -> value to find the row
    :param column_to_update: Column header name to update
    :param new_value: New value to set
    """
    service = authenticate_google_sheets()  # Use existing authentication function
    sheet = service.spreadsheets()

    # Read entire sheet
    range_name = f"{sheet_name}"
    result = sheet.values().get(spreadsheetId=spreadsheet_id, range=range_name).execute()
    values = result.get("values", [])

    if not values:
        print("No data found.")
        return None

    # Extract headers
    headers = values[0]
    data = values[1:]  # Exclude headers

    # Find row number
    row_number = None
    for row_index, row in enumerate(data, start=2):  # Start at 2 (1-based index after headers)
        match = all(row[headers.index(col)] == search_criteria[col] for col in search_criteria if col in headers)
        if match:
            row_number = row_index
            break

    if row_number is None:
        print("No matching row found.")
        return None

    # Find column index
    if column_to_update not in headers:
        print(f"Column '{column_to_update}' not found.")
        return None

    column_index = headers.index(column_to_update)
    column_letter = chr(65 + column_index)  # Convert index to letter (0 -> 'A', 1 -> 'B', etc.)

    # Define cell range (e.g., "C3" for row 3, column "C")
    cell_range = f"{sheet_name}!{column_letter}{row_number}"

    # Update the value
    update_data = {
        "values": [[new_value]]
    }

    request = sheet.values().update(
        spreadsheetId=spreadsheet_id,
        range=cell_range,
        valueInputOption="RAW",
        body=update_data
    )
    response = request.execute()

    print(f"Updated '{column_to_update}' at row {row_number} with value: {new_value}")
    return response


# add items to the WLM Q
def add_items_to_WLM(QName,SPREADSHEET_ID,SHEET_NAME, token):
    #read google sheet
    df = read_google_sheet(SPREADSHEET_ID, SHEET_NAME)
    if df is not None:
        print(df.head())  # Print first few rows
        #LOOP THROUGH THE ROWS AND ADD ITEMS TO THE Q
        payload = {"workItems": []}
        for entry in df:
             work_item = {
                "json": {
                    "Body": reference_id,
                    "BotPath": bot_path,
                    "ProcessPriority": entry['ProcessPriority'],
                    "WorkitemPriority": entry['WorkItemPriority'],
                    "Score": entry['Score']
                }
            }
            payload["workItems"].append(work_item)


    headers = {"Content-Type": "application/json", "X-Authorization": token}
    #response = requests.post(f"{BASE_CR_URL}/v3/wlm/queues/{ACTIVE_QUEUE_ID}/workitems", json=payload,
                                 headers=headers)


def authenticate(args):
    server_hostname = args['server_hostname'] =""
    username = args['username'] = ""
    apikey = args['apikey'] = ""
    url = server_hostname + "/v2/authentication"
    payload = "{\"username\" : \"" + username + "\",\"password\" : \"" + apikey + "\"}"
    response = requests.post(url, data=payload)
    json_str = response.json()
    if response.status_code == 200:
       #return {"status_code": response.status_code, "auth_token": json_str["token"]}
       return json_str["token"]
    else:
        raise Exception("Authentication failure, code: {0}, message: {1}".format(response.status_code, json_str["message"]))



# 🔹 Step 3: Execute Script
if __name__ == "__main__":
    print(1)
    SPREADSHEET_ID = "1uBX_Y1kr433DcOMF0A6GzWwA3rlZIutJ4L7hFMzoY70"
    SHEET_NAME = "Sheet1"  # Change if needed


    #
    # # Define search criteria (column_name: expected_value)
    # SEARCH_CRITERIA = {
    #     "Name": "Jerry",
    #     "Location": "Denmark",
    #     "Department": "HR"
    # }
    #
    # row_number = get_row_number(SPREADSHEET_ID, SHEET_NAME, SEARCH_CRITERIA)
    # print(f"Matching row number: {row_number}")
    #
    # ROW_NUMBER = row_number  # Example: Update row 3
    # COLUMN_LETTER = "B"  # Example: Update column "B"
    # NEW_VALUE = "Updated Value"
    #
    # update_cell(SPREADSHEET_ID, SHEET_NAME, ROW_NUMBER, COLUMN_LETTER, NEW_VALUE)



    # Define search criteria to find the row
    SEARCH_CRITERIA = {
        "Name": "Jerry",
        "Location": "Denmark",
        "Department": "HR"
    }

    COLUMN_TO_UPDATE = "Department"  # Change this to any column header
    NEW_VALUE = "Software"

    update_cell_by_header(SPREADSHEET_ID, SHEET_NAME, SEARCH_CRITERIA, COLUMN_TO_UPDATE, NEW_VALUE)



    df = read_google_sheet(SPREADSHEET_ID, SHEET_NAME)
    print(1)
    if df is not None:
        print(df.head())  # Print first few rows

