# RPA
#This DLL source code , which can be used to call file download /Upload APIs

Please find the DLL details below


DLL Name :Rest Web Services.dll

Dependant DLL : Newtonsoft.Json.dll

-----------------------------------------------------------------------
File download (also can be use for the normal api calls)
-----------------------------------------------------------------------

namespace : Rest_Web_Services

class : RestWebServiceRequest

function : SendRequest

 Input Params : 

     Parameter Name	Datatype	Sample value

     - domain	string	so***-av.***.sp***.com

     - apiUrl	string	https://so***-av.***.sp***.com/getFile/123

     - Cookie	string	key=value;key2=value2

     - Headers	string	key=value;key2=value2

     - method,	string	GET

     - inputData	string	{ "companyId":"IQ24143"}

     - fileDownload	Boolean	true

     - outputFolder	String	Local folder path

     - certificatePath	String	Local file path

     - privateKey	String	*******



-----------------------------------------------------------------------
File Upload
-----------------------------------------------------------------------
Namespace : Rest_Web_Services

Class : RestWebServiceRequest

Functions :

1 . FileUploadRequest

   Inputs :
   
        - string uri (eg :http://localhost:8080/upload)
        
        - string token (eg : Bearer shdgajsgdhgas)
        
        - string paramsJson (eg :  {"name":"BASHlog.txt","parent":"232"} )
