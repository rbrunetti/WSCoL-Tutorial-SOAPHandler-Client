Specl-Tutorial-SOAPHandler-Client
=================================

Specl Analyzer Tutorial - SOAP Handler, Client Side project

Now we develop a Webservice Client for accessing to the _BookstoreWS_, and attach another handler (but on client side) for use Specl as post-condition validator and for manipulate SOAP messages.

## Table of Content

1. [Setup](#example_handler_client_setup)
    1. [Needed Libraries](#example_handler_client_libs)
	2. [Installation](#example_handler_client_install)
2. [WS Client](#example_handler_client_wsclient)
3. [Client SOAP Handler](#example_handler_client_handler)
4. [Client SOAPHandler XML File](#example_handler_client_handler_xml)
5. [Attach Handler to BookstoreWS](#example_handler_client_handler_attach)
6. [BookstoreWS Client Project Organization](#example_handler_client_proj_dir_struct)
7. [Source](#example_handler_sources)

<a name="example_handler_client_setup"></a>
### Setup
Download the required libraries, import them in your project and start coding.<br/>

<a name="example_handler_client_libs"></a>
#### Needed Libraries

* Specl-Analyzer.jar ([download]())
* Specl.jar (required by _Specl-Analyzer.jar_) ([download]())
* json-simple-1.1.1.jar (required by _Specl-Analyzer.jar_) ([download]())

Full Zip ([download]())

<a name="example_handler_client_install"></a>
#### Installation
Simply save libraries in a known folder inside your project and import them.<br/>
In Eclipse IDE right click on the project _Properties_ > _Java Build Path_ > _Libraries_ > _Add JARs..._ and then select the libraries from your project folder.<br/>
Another way is the following:

![Import Libs](http://rbrunetti.github.io/Specl-Analyser/img/soap/00-ImportLibs.png)<br/>
_Import Libraries_

<a name="example_handler_client_wsclient"></a>
### WS Client
We use `wsimport` command to parse the published service WSDL file (_http://localhost:8888/bookstorews/server?wsdl_) and generate all required files to access the service.

```
$ wsimport -keep -verbose http://localhost:8888/bookstorews/server?wsdl
parsing WSDL...



Generating code...

it/polimi/bookstore/ws/GetAllBooksTitle.java
it/polimi/bookstore/ws/GetAllBooksTitleResponse.java
it/polimi/bookstore/ws/GetBookByIsbn.java
it/polimi/bookstore/ws/GetBookByIsbnResponse.java
it/polimi/bookstore/ws/GetBooksByAuthor.java
it/polimi/bookstore/ws/GetBooksByAuthorResponse.java
it/polimi/bookstore/ws/GetBooksByIsbnList.java
it/polimi/bookstore/ws/GetBooksByIsbnListResponse.java
it/polimi/bookstore/ws/GetBooksNumberPerAuthor.java
it/polimi/bookstore/ws/GetBooksNumberPerAuthorResponse.java
it/polimi/bookstore/ws/HashMapWrapper.java
it/polimi/bookstore/ws/ObjectFactory.java
it/polimi/bookstore/ws/ServerInfoImpl.java
it/polimi/bookstore/ws/ServerInfoImplService.java
it/polimi/bookstore/ws/package-info.java

...
```

A client class and few methods that calls the web service and prints out the results.

_File: BookstoreWSClient.java_
```Java
package it.polimi.bookstore;

import it.polimi.bookstore.ws.HashMapWrapper;
import it.polimi.bookstore.ws.ServerInfoImpl;
import it.polimi.bookstore.ws.ServerInfoImplService;

import java.util.ArrayList;

import javax.xml.ws.soap.SOAPFaultException;

public class BookstoreWSClient {
	
	private static ServerInfoImplService service;
	private static ServerInfoImpl ws;
	
	@SuppressWarnings("serial")
	public static void main(String[] args) {
		service = new ServerInfoImplService();
		ws = service.getServerInfoImplPort();
				
		testGetBooksByAuthor("Larry Niven");
		testgetBookByIsbn("0871131811");
		testGetAllBooksTitle();
		testGetBooksByIsbnList(new ArrayList<String>() {
			{
				add("0553380981");
				add("0871131811");
				add("012345678");
			}
		});
		testGetBooksNumberPerAuthor();
		getBooksByPublisherAndYearRange("Spectra", 1900, 2013);
		
	}

	private static void testGetBooksByAuthor(String author) {
		try {
			System.out.println("testGetBooksByAuthor: " + ws.getBooksByAuthor(author));
		} catch (SOAPFaultException e) {
			System.err.println("testGetBooksByAuthor: " + e.getMessage());
		}
	}

	private static void testgetBookByIsbn(String isbn) {
		try {
			System.out.println("testgetBookByIsbn: " + ws.getBookByIsbn(isbn));
		} catch (SOAPFaultException e) {
			System.err.println("testgetBookByIsbn: " + e.getMessage());
		}
	}

	private static void testGetAllBooksTitle() {
		try {
			System.out.println("testGetAllBooksTitle: " + ws.getAllBooksTitle());
		} catch (SOAPFaultException e) {
			System.err.println("testGetAllBooksTitle: " + e.getMessage());
		}
		
	}

	public static void testGetBooksByIsbnList(ArrayList<String> isbns) {
		try {
			System.out.println("testGetBooksByIsbnList: " + ws.getBooksByIsbnList(isbns));
		} catch (SOAPFaultException e) {
			System.err.println("testGetBooksByIsbnList: " + e.getMessage());
		}
	}
	
	private static void testGetBooksNumberPerAuthor() {
		HashMapWrapper bnpa = ws.getBooksNumberPerAuthor();
		System.out.println("getBooksNumberPerAuthor() - Trendy Author Books Number: " + bnpa.getMap().getEntry().get(0).getValue());
	}
	
	private static void getBooksByPublisherAndYearRange(String publisher, int startY, int endY) {
		try {
			System.out.println("getBooksByPublisherAndYearRange: " + ws.getBooksByPublisherAndYearRange(publisher, startY, endY));
		} catch (SOAPFaultException e) {
			System.err.println("getBooksByPublisherAndYearRange: " + e.getMessage());
		}
	}

}

```

<a name="example_handler_client_handler"></a>
### Client SOAP Handler
Create a SOAP Handler on client side for extract informations from the response message and manipulate it.<br/>
It intercepts the `getBooksNumberPerAuthorResponse` (that returns a Map with entries where keys are the authors name and values the number of book in the store written by that author) than with Specl we find the author with the maximum number of book and we modify the SOAP message and remove all the other entries.

_File: PostValidationHandler.java_
```Java
package it.polimi.bookstore.handler;

import it.polimi.Specl.SpeclAnalyzer;
import it.polimi.Specl.helpers.SpeclException;

import java.io.IOException;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.Node;

public class PostValidationHandler implements SOAPHandler<SOAPMessageContext> {
	
	@Override
	public boolean handleMessage(SOAPMessageContext context) {

		System.out.println("Client : handleMessage()......");
		
		Boolean isRequest = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		if (!isRequest) {

			SpeclAnalyzer analyzer = new SpeclAnalyzer();
			analyzer.shutdownLogger();

			try {
				SOAPMessage soapMsg = context.getMessage();
				SOAPBody soapBody = soapMsg.getSOAPBody();
				Node responseNode = soapBody.getFirstChild();
				String name = responseNode.getLocalName();
				if (name.equals("getBooksNumberPerAuthorResponse")) {
					
					analyzer.setXMLInput(soapBody.getOwnerDocument());
					
					String assertion = 
							  "let $map_entries = /Envelope/Body/getBooksNumberPerAuthorResponse/return/map;"
							+ "let $values = $map_entries/entry/value;"
							+ "let $maximum = max($num in $values, $num > 0);"
							+ "let $author = $map_entries/entry[value==$maximum]/key;"
							+ "1==1;"; // we're interested in variables
					
					try {
						analyzer.evaluate(assertion);
						Object author = analyzer.getVariable("$author");
						
						Node map = soapBody.getFirstChild().getFirstChild().getFirstChild();
						for(int i=0; i < map.getChildNodes().getLength(); i++){
							Node child = map.getChildNodes().item(i);
							if(!child.getFirstChild().getTextContent().equals(author)){
								map.removeChild(child);
								i -= 1;
							}
						}
					} catch (SpeclException e) {
						generateSOAPErrMessage(soapMsg, "Errors while check post-conditions");
					}
				
				}
				// tracking
				soapMsg.writeTo(System.out);

			} catch (SOAPException e) {
				System.err.println(e);
			} catch (IOException e) {
				System.err.println(e);
			}

		}
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		System.out.println("Client : handleFault()......");
		return true;
	}

	@Override
	public void close(MessageContext context) {
		System.out.println("Client : close()......");
	}

	@Override
	public Set<QName> getHeaders() {
		System.out.println("Client : getHeaders()......");
		return null;
	}

	private void generateSOAPErrMessage(SOAPMessage msg, String reason) {
		try {
			SOAPBody soapBody = msg.getSOAPPart().getEnvelope().getBody();
			SOAPFault soapFault = soapBody.addFault();
			soapFault.setFaultString(reason);
			throw new SOAPFaultException(soapFault);
		} catch (SOAPException e) {
		}
	}

}
```

<a name="example_handler_client_handler_xml"></a>
### Client SOAPHandler XML File
Create a SOAP handler XML file, and puts your SOAP handler declaration.

_File: handler-chain.xml_
```XML
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<javaee:handler-chains 
     xmlns:javaee="http://java.sun.com/xml/ns/javaee" 
     xmlns:xsd="http://www.w3.org/2001/XMLSchema">
  <javaee:handler-chain>
    <javaee:handler>
      <javaee:handler-class>it.polimi.bookstore.handler.PostValidationHandler</javaee:handler-class>
    </javaee:handler>
  </javaee:handler-chain>
</javaee:handler-chains>
```

<a name="example_handler_client_handler_attach"></a>
#### Attach Handler to BookstoreWS
To attach above SOAP handler to web service ServerInfo.java, just annotate with @HandlerChain and specify the SOAP handler file name inside.

```Java
//...

@WebServiceClient(name = "ServerInfoImplService", targetNamespace = "http://ws.bookstore.polimi.it/", wsdlLocation = "http://localhost:8888/bookstorews/server?wsdl")
@HandlerChain(file = "handler-chain.xml")
public class ServerInfoImplService
    extends Service
{


//...
```

<a name="example_handler_client_proj_dir_struct"></a>
### BookstoreWS Client Project Organization
Here's the directory structure of the project
![Client Directory Structure](http://rbrunetti.github.io/Specl-Analyser/img/soap/02-ClientDirStruct.png)

<a name="example_handler_sources"></a>
### Project Sources
Project sources are available on GitHub at [Specl-Tutorial-SOAPHandler-Client](https://github.com/rbrunetti/Specl-Tutorial-SOAPHandler-Client).<br/>
Server project (on which this repository is based) is at [Specl-Tutorial-SOAPHandlerWS](https://github.com/rbrunetti/Specl-Tutorial-SOAPHandlerWS).
