package bgu.spl.a2.sim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a product produced during the simulation.
 */
public class Product implements Serializable {
	long startId;
	String name;
	List<Product> _listOfParts;
	long SumID;
	
	
	/**
	* Constructor 
	* @param startId - Product start id
	* @param name - Product name
	*/
    public Product(long startId, String name){
    	this.startId=startId;
    	this.name=name;
    	this._listOfParts=new ArrayList<Product>();
    	SumID=startId;
    }
	/**
	* @return The product name as a string
	*/
    public String getName(){
    	return name;
    }

	/**
	* @return The product start ID as a long. start ID should never be changed.
	*/
    public long getStartId(){
    	return startId;
    }
    
	/**
	* @return The product final ID as a long. 
	* final ID is the ID the product received as the sum of all UseOn(); 
	*/
    public long getFinalId(){  //Thread? whenResolved?
    	return SumID;
    }

    public void sumItUp(long _numToSum){
    	SumID= SumID+_numToSum;
    }
	/**
	* @return Returns all parts of this product as a List of Products
	*/
    public List<Product> getParts(){
    	return _listOfParts;
    }

	/**
	* Add a new part to the product
	* @param p - part to be added as a Product object
	*/
    public void addPart(Product p){

    	_listOfParts.add(p);
    }

    public String toString(){
    	String _returnString="ProductName: " +name+ " Product Id = "+getFinalId()+"\n"+
    			"PartsList {\n";
    	for (Product son:_listOfParts)
    		_returnString=_returnString+son.toString();
    	_returnString=_returnString+"}\n";
    	return _returnString;
    }

}
