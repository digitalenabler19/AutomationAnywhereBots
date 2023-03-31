/*
 * Author : SIKHA P 
 * sikha.p@automationanywhere.com
 * Partner Solution Desk(PSD)
 */
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;
using System.Xml;

namespace Rest_Web_Services
{
    class PostMethod_XmlAsInput
    {
        public static string SendRequest(string domain, string apiUrl, string cookie, string headers, string xmlFilePath, string certificatePath, string privateKey)
        {
            try
            {
                XmlDocument xmlResponse = null;
                HttpWebResponse httpWebResponse = null;
                Stream requestStream = null;
                Stream responseStream = null;

                string[] cookies = cookie.Split(';');
                CookieContainer cookieContainer = new CookieContainer();
                if (cookie.Length != 0)
                {

                    Uri target = new Uri(domain);
                    foreach (string cookie_ in cookies)
                    {
                        if (cookie_.Length != 0)
                        {
                            cookieContainer.Add(new Cookie(cookie_.Split('=')[0], cookie_.Split('=')[1]) { Domain = target.Host });
                        }
                    }
                }
                // Create HttpWebRequest for the API URL.
                var httpWebRequest = (HttpWebRequest)WebRequest.Create(apiUrl);
                httpWebRequest.CookieContainer = cookieContainer;

                try
                {
                    // Set HttpWebRequest properties
                    XmlDocument reqXml = new XmlDocument();
                    reqXml.Load(xmlFilePath);
                    var bytes = System.Text.Encoding.ASCII.GetBytes(reqXml.OuterXml);
                    httpWebRequest.Method = "POST";
                    if (headers.Length != 0)
                    {
                        string[] headers_ = headers.Split(';');
                        foreach (string header in headers_)
                        {
                            if (header.Length != 0)
                            {
                                if (header.Split('=')[0] == "Accept")
                                {
                                    httpWebRequest.Accept = header.Split('=')[1];
                                }
                                else if (header.Split('=')[0] == "Content-Type")
                                {
                                    httpWebRequest.ContentType = header.Split('=')[1];
                                }
                                else
                                {
                                    httpWebRequest.Headers.Add(header.Split('=')[0], header.Split('=')[1]);
                                }

                            }
                        }
                    }

                    if (certificatePath != "")
                    {
                        httpWebRequest = AttachClientCertificate(httpWebRequest, certificatePath, privateKey);
                    }

                    httpWebRequest.ContentLength = bytes.Length;
                    httpWebRequest.ContentType = "text/xml;encoding=utf-8";

                    //Get Stream object
                    requestStream = httpWebRequest.GetRequestStream();
                    requestStream.Write(bytes, 0, bytes.Length);
                    requestStream.Close();

                    // Post the Request.
                    httpWebResponse = (HttpWebResponse)httpWebRequest.GetResponse();

                    // If the submission is success, Status Code would be OK
                    if (httpWebResponse.StatusCode == HttpStatusCode.OK)
                    {
                        // Read response
                        responseStream = httpWebResponse.GetResponseStream();

                        if (responseStream != null)
                        {
                            var objXmlReader = new XmlTextReader(responseStream);

                            // Convert Response stream to XML
                            var xmldoc = new XmlDocument();
                            xmldoc.Load(objXmlReader);
                            xmlResponse = xmldoc;
                            objXmlReader.Close();
                        }
                    }

                    // Close Response
                    httpWebResponse.Close();
                }
                catch (WebException webException)
                {
                    throw new Exception(webException.Message);
                }
                catch (Exception exception)
                {
                    throw new Exception(exception.Message);
                }
                finally
                {
                    // Release connections
                    if (requestStream != null)
                    {
                        requestStream.Close();
                    }

                    if (responseStream != null)
                    {
                        responseStream.Close();
                    }

                    if (httpWebResponse != null)
                    {
                        httpWebResponse.Close();
                    }
                }

                // Return API Response
                return xmlResponse.OuterXml;
            }

            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                return ex.Message;
            }
        }

        public static HttpWebRequest AttachClientCertificate(HttpWebRequest request, string certPath, string privateKey)
        {
            //creating cert
            X509Certificate2Collection certificates = new X509Certificate2Collection();
            certificates.Import(certPath, privateKey, System.Security.Cryptography.X509Certificates.X509KeyStorageFlags.DefaultKeySet);

            request.AllowAutoRedirect = true;
            request.ClientCertificates = certificates;

            return request;
        }

        public static XmlDocument SubmitXmlRequest(string apiUrl, string reqXml)
        {
            XmlDocument xmlResponse = null;
            HttpWebResponse httpWebResponse = null;
            Stream requestStream = null;
            Stream responseStream = null;

            // Create HttpWebRequest for the API URL.
            var httpWebRequest = (HttpWebRequest)WebRequest.Create(apiUrl);

            try
            {
                // Set HttpWebRequest properties
                var bytes = System.Text.Encoding.ASCII.GetBytes(reqXml);
                httpWebRequest.Method = "POST";
                httpWebRequest.ContentLength = bytes.Length;
                httpWebRequest.ContentType = "text/xml;encoding =utf - 8";

                //Get Stream object
                requestStream = httpWebRequest.GetRequestStream();
                requestStream.Write(bytes, 0, bytes.Length);
                requestStream.Close();

                // Post the Request.
                httpWebResponse = (HttpWebResponse)httpWebRequest.GetResponse();

                // If the submission is success, Status Code would be OK
                if (httpWebResponse.StatusCode == HttpStatusCode.OK)
                {
                    // Read response
                    responseStream = httpWebResponse.GetResponseStream();

                    if (responseStream != null)
                    {
                        var objXmlReader = new XmlTextReader(responseStream);

                        // Convert Response stream to XML
                        var xmldoc = new XmlDocument();
                        xmldoc.Load(objXmlReader);
                        xmlResponse = xmldoc;
                        objXmlReader.Close();
                    }
                }

                // Close Response
                httpWebResponse.Close();
            }
            catch (WebException webException)
            {
                throw new Exception(webException.Message);
            }
            catch (Exception exception)
            {
                throw new Exception(exception.Message);
            }
            finally
            {
                // Release connections
                if (requestStream != null)
                {
                    requestStream.Close();
                }

                if (responseStream != null)
                {
                    responseStream.Close();
                }

                if (httpWebResponse != null)
                {
                    httpWebResponse.Close();
                }
            }

            // Return API Response
            return xmlResponse;
        }

        static string GetXmlString(string strFile)
        {
            // Load the xml file into XmlDocument object.
            XmlDocument xmlDoc = new XmlDocument();
            try
            {
                xmlDoc.Load(strFile);
            }
            catch (XmlException e)
            {
                Console.WriteLine(e.Message);
            }
            // Now create StringWriter object to get data from xml document.
            StringWriter sw = new StringWriter();
            XmlTextWriter xw = new XmlTextWriter(sw);
            xmlDoc.WriteTo(xw);
            return sw.ToString();
        }


    }
}
