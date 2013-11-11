
package it.polimi.bookstore.ws;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "ServerInfoImpl", targetNamespace = "http://ws.bookstore.polimi.it/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ServerInfoImpl {


    /**
     * 
     * @param arg0
     * @return
     *     returns java.util.List<java.lang.String>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getBooksByAuthor", targetNamespace = "http://ws.bookstore.polimi.it/", className = "it.polimi.bookstore.ws.GetBooksByAuthor")
    @ResponseWrapper(localName = "getBooksByAuthorResponse", targetNamespace = "http://ws.bookstore.polimi.it/", className = "it.polimi.bookstore.ws.GetBooksByAuthorResponse")
    @Action(input = "http://ws.bookstore.polimi.it/ServerInfoImpl/getBooksByAuthorRequest", output = "http://ws.bookstore.polimi.it/ServerInfoImpl/getBooksByAuthorResponse")
    public List<String> getBooksByAuthor(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getBookByIsbn", targetNamespace = "http://ws.bookstore.polimi.it/", className = "it.polimi.bookstore.ws.GetBookByIsbn")
    @ResponseWrapper(localName = "getBookByIsbnResponse", targetNamespace = "http://ws.bookstore.polimi.it/", className = "it.polimi.bookstore.ws.GetBookByIsbnResponse")
    @Action(input = "http://ws.bookstore.polimi.it/ServerInfoImpl/getBookByIsbnRequest", output = "http://ws.bookstore.polimi.it/ServerInfoImpl/getBookByIsbnResponse")
    public String getBookByIsbn(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

    /**
     * 
     * @param arg0
     * @return
     *     returns java.util.List<java.lang.String>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getBooksByIsbnList", targetNamespace = "http://ws.bookstore.polimi.it/", className = "it.polimi.bookstore.ws.GetBooksByIsbnList")
    @ResponseWrapper(localName = "getBooksByIsbnListResponse", targetNamespace = "http://ws.bookstore.polimi.it/", className = "it.polimi.bookstore.ws.GetBooksByIsbnListResponse")
    @Action(input = "http://ws.bookstore.polimi.it/ServerInfoImpl/getBooksByIsbnListRequest", output = "http://ws.bookstore.polimi.it/ServerInfoImpl/getBooksByIsbnListResponse")
    public List<String> getBooksByIsbnList(
        @WebParam(name = "arg0", targetNamespace = "")
        List<String> arg0);

    /**
     * 
     * @return
     *     returns java.util.List<java.lang.String>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getAllBooksTitle", targetNamespace = "http://ws.bookstore.polimi.it/", className = "it.polimi.bookstore.ws.GetAllBooksTitle")
    @ResponseWrapper(localName = "getAllBooksTitleResponse", targetNamespace = "http://ws.bookstore.polimi.it/", className = "it.polimi.bookstore.ws.GetAllBooksTitleResponse")
    @Action(input = "http://ws.bookstore.polimi.it/ServerInfoImpl/getAllBooksTitleRequest", output = "http://ws.bookstore.polimi.it/ServerInfoImpl/getAllBooksTitleResponse")
    public List<String> getAllBooksTitle();

    /**
     * 
     * @return
     *     returns it.polimi.bookstore.ws.HashMapWrapper
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getBooksNumberPerAuthor", targetNamespace = "http://ws.bookstore.polimi.it/", className = "it.polimi.bookstore.ws.GetBooksNumberPerAuthor")
    @ResponseWrapper(localName = "getBooksNumberPerAuthorResponse", targetNamespace = "http://ws.bookstore.polimi.it/", className = "it.polimi.bookstore.ws.GetBooksNumberPerAuthorResponse")
    @Action(input = "http://ws.bookstore.polimi.it/ServerInfoImpl/getBooksNumberPerAuthorRequest", output = "http://ws.bookstore.polimi.it/ServerInfoImpl/getBooksNumberPerAuthorResponse")
    public HashMapWrapper getBooksNumberPerAuthor();

}
