import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * v1.2.6
 * @authors				 Nick Christensen and Shaun Flynn 
 * @last_update_date     April 22, 2016
 * 
 * CHANGES:
 *	
 *		Formatted the Code.
 *
 *		Added HTML 400 Bad Request Status Codes to the invalid type errors
 *
 *		Made the "New Order" and "Add Stock" pages load when an invade type was entered (included red warning at the top).
 */


public class Program5 {

	public class Order {												//Class for all order and list types, with several constructors
		String name;
		String item;
		int quantity;
		double ppu;
		double cost;

		Order ( String name, String item, int quant ) {					//For use in the outstanding order list
			this.name = name;
			this.item = item;
			quantity = quant;
		}
		Order ( String name, int quant, double ppu ) {					//For use in the Inventory/Stock list
			this.name = name;
			quantity = quant;
			this.ppu = ppu;
		}
		Order ( String name, String item, int quant, double ppu ) {		//The full implementation, used for the fulfilled order list
			this.name = name;
			this.item = item;
			quantity = quant;
			this.ppu = ppu;
			cost = ppu * quant;
		}

	}
	//Various DoublyLinkedLists used in the program (of the order type)
	DoublyLinkedList<Order> outstanding = new DoublyLinkedList<Order>();
	DoublyLinkedList<Order> fulfilled = new DoublyLinkedList<Order>();
	DoublyLinkedList<Order> stock = new DoublyLinkedList<Order>();

	public void server( int port ) {

		try ( ServerSocket server = new ServerSocket(5678) ) {
			String html = "<HTML><HEAD><TITLE>Generated Page</TITLE></HEAD><BODY><H1>Hello World!</H!></BODY></HTML>";//temp
			boolean running = true;
			while ( running ) {
				try ( Socket client = server.accept( );
						BufferedReader input = new BufferedReader(new InputStreamReader( client.getInputStream( ) ) );
						PrintWriter output = new PrintWriter( client.getOutputStream( ) ); ) {
					String inp = "";
					String[] request = new String[ 3 ];
					while ( ( inp = input.readLine( ) ) != null) {
						System.out.println( ">> " + inp );
						if ( "".equals( inp ) ) {
							break;
						} else if ( inp.contains( "GET" ) ) {
							request = inp.split( " " );
						} else if (inp.contains( "POST" ) ) {
							
							//POST error
							html = String.format( "<HTML><BODY><H1>POST COMMAND NOT AVALIABLE</H1></BODY></HTML>");
							output.println( "HTTP/1.0 POST error" );
							output.println( "Connection: close" );
							output.println( "" );
							output.println( html );
							System.out.println( ">" + html );
							output.println( "" );
							output.flush( );
						}
					}
					if (request[ 1 ].equals( "/" ) ) {
						request[ 1 ] += "index";
					}
					request[ 1 ] = request[ 1 ].substring( 1 ) + ".html";
					File fin = new File( request[ 1 ] );
					String orderCheck = request[ 1 ].substring( 0, 10 ); // Check used to see if an order has been placed
																	     // or if stock has been added
					
					// Home Page
					if (request[ 1 ].equals( "index.html" ) ) {
						html = "<html><body><h1>Order Fulfillment System</h1><ul>"
								+ "<li><a href='http://localhost:5678/new_order'>New Order</a></li>"
								+ "<li><a href='http://localhost:5678/outstanding_orders'>Outstanding Orders</a></li>"
								+ "<li><a href='http://localhost:5678/fulfilled_orders'>Fulfilled Orders</a></li>"
								+ "</ul><p></p><ul>"
								+ "<li><a href='http://localhost:5678/add_stock'>Add Stock</a></li>"
								+ "<li><a href='http://localhost:5678/list_inventory'>Inventory List</a></li>"
								+ "</ul></body></html>";
						output.println( "HTTP/1.0 200 OK" );
						output.println( "Content-Type: text/html" );
						output.println( "Content-Length: " + html.length( ) );
						output.println( "Connection: close" );
						output.println( "" );
						output.println( html );
						System.out.println( ">" + html );
						output.println( "" );
						output.flush( );

					// New Order Page
					} else if (request[ 1 ].equals( "new_order.html" ) ) {
						html = "<html><body><h1>New Order</h1><form>" + "Name: <input type='text' name='name'><br>"
								+ "Item: <input type='text' name='item'><br>"
								+ "Quantity: <input tytpe = 'text' name='quantity'><br>"
								+ "<input type='submit' value='Submit'></form>"
								+ "<a href='http://localhost:5678'>[Home]</a>"
								+ "<a href='http://localhost:5678/new_order'>[New Order]</a>"
								+ "<a href='http://localhost:5678/outstanding_orders'>[Outstanding Orders]</a>"
								+ "<a href='http://localhost:5678/fulfilled_orders'>[Fulfilled Orders]</a>"
								+ "</body></html>";
						output.println( "HTTP/1.0 200 OK" );
						output.println( "Content-Type: text/html" );
						output.println( "Content-Length: " + html.length( ) );
						output.println( "Connection: close" );
						output.println( "" );
						output.println( html );
						System.out.println( ">" + html );
						output.println( "" );
						output.flush( );

					// New Order Page after a submission
					} else if ( orderCheck.equals( "new_order?" ) ) {
						try {
							Boolean isFulfilled = false;
							
							//Process the GET command
							String order = request[ 1 ].substring( 10, request[ 1 ].length( ) );
							String[ ] parts = order.split( "&" );
							String name = parts[ 0 ].substring( 5, parts[ 0 ].length( ) );
							String item = parts[ 1 ].substring( 5, parts[ 1 ].length( ) );
							String quantStr =  parts[ 2 ].substring( 9, parts[ 2 ].length( ) - 5 );
	
							int i = 0;
							for ( i = 0; i < stock.getSize(); i++ ) {
								if ( item.equals( stock.get( i ).name ) ) {								//Checks if the same type of item is already in stock
									if ( Integer.parseInt( quantStr ) <= stock.get( i ).quantity ) {	//If so, checks if there is enough to fulfill the order
										stock.get( i ).quantity -= Integer.parseInt( quantStr );		//If so, removes the quantity from stock (keeps the item slot even if there are zero left)
										isFulfilled = true;												//And indicates that the order has been fulfilled
										break;
									}
								}
							}
	
							if ( isFulfilled ) {
								fulfilled.add( new Order( name, item, Integer.parseInt( quantStr ), stock.get( i ).ppu * 1.5 ) );
								html = String.format( "<html><body>" + "<p style='color:red;'><b>Order Placed: </b>"
										+ "NAME: %s ITEM: %s QUANTITY: %s</p>" 
										+ "<p style='color:blue;'><b>Order has been fulfilled.</b>"
										+ "<h1>Next Order</h1><form>"
										+ "Name: <input type='text' name='name'><br>"
										+ "Item: <input type='text' name='item'><br>"
										+ "Quantity: <input tytpe = 'text' name='quantity'><br>"		//This html is printed if the order can be immediately fulfilled
										+ "<input type='submit' value='Submit'></form>"
										+ "<a href='http://localhost:5678'>[Home]</a>"
										+ "<a href='http://localhost:5678/new_order'>[New Order]</a>"
										+ "<a href='http://localhost:5678/outstanding_orders'>[Outstanding Orders]</a>"
										+ "<a href='http://localhost:5678/fulfilled_orders'>[Fulfilled Orders]</a>"
										+ "</body></html>", name, item, quantStr );
							} else {
								outstanding.add(new Order( name, item, Integer.parseInt( quantStr ) ) );	//Adds the new order to the outstanding order list
								html = String.format( "<html><body>" 
										+ "<p style='color:red;'><b>Order Palaced: </b>"
										+ "NAME: %s ITEM: %s QUANTITY: %s</p>" 
										+ "<h1>Next Order</h1><form>"
										+ "Name: <input type='text' name='name'><br>"
										+ "Item: <input type='text' name='item'><br>"					//This html is printed when an order is added, but cannot be immediately fulfilled
										+ "Quantity: <input tytpe = 'text' name='quantity'><br>"
										+ "<input type='submit' value='Submit'></form>"
										+ "<a href='http://localhost:5678'>[Home]</a>"
										+ "<a href='http://localhost:5678/new_order'>[New Order]</a>"
										+ "<a href='http://localhost:5678/outstanding_orders'>[Outstanding Orders]</a>"
										+ "<a href='http://localhost:5678/fulfilled_orders'>[Fulfilled Orders]</a>"
										+ "</body></html>", name, item, quantStr );
							}
							output.println( "HTTP/1.0 200 OK" );
							output.println( "Content-Type: text/html" );
							output.println( "Content-Length: " + html.length( ) );
							output.println( "Connection: close" );
							output.println( "" );
							output.println( html );
							System.out.println( ">" + html );
							output.println( "" );
							output.flush( );
						} catch (NumberFormatException e) {
							html = "<html><body><p style='color:red;'><b>Invalid Argument Type</b>"               //This html is printed when an order is added with faulty arguments
									+ "<h1>New Order</h1><form>" + "Name: <input type='text' name='name'><br>"
									+ "Item: <input type='text' name='item'><br>"
									+ "Quantity: <input tytpe = 'text' name='quantity'><br>"
									+ "<input type='submit' value='Submit'></form>"
									+ "<a href='http://localhost:5678'>[Home]</a>"
									+ "<a href='http://localhost:5678/new_order'>[New Order]</a>"
									+ "<a href='http://localhost:5678/outstanding_orders'>[Outstanding Orders]</a>"
									+ "<a href='http://localhost:5678/fulfilled_orders'>[Fulfilled Orders]</a>"
									+ "</body></html>";
							output.println( "HTTP/1.0 400 Bad Request" );
							output.println( "Content-Type: text/html" );
							output.println( "Content-Length: " + html.length( ) );
							output.println( "Connection: close" );
							output.println( "" );
							output.println( html );
						}

					// Outstanding Orders Page
					} else if ( request[ 1 ].equals( "outstanding_orders.html" ) ) {
						String listContent = "";
						if ( outstanding.getSize() != 0 ) {											//Checks if the outstanding list has any elements
							for ( int i = 0; i < outstanding.getSize(); i++ ) {
								listContent += "<li><b>NAME: </b>" + outstanding.get( i ).name + "<br>"	//This is the regular construction of the list
											+ "<b>&nbsp ITEM: </b>" + outstanding.get( i ).item + "<br>"
											+ "<b>&nbsp Quantity: </b>" + outstanding.get( i ).quantity + "</li>";
							}
							html = "<html><body><h1>Outstanding Orders List</h1><form><ul>"
									+ listContent
									+ "</ul><a href='http://localhost:5678'>[Home]</a>"
									+ "<a href='http://localhost:5678/new_order'>[New Order]</a>"
									+ "<a href='http://localhost:5678/outstanding_orders'>[Outstanding Orders]</a>"	//This html is passed when there are outstanding orders
									+ "<a href='http://localhost:5678/fulfilled_orders'>[Fulfilled Orders]</a>"
									+ "</body></html>";
						} else {
							html = "<html><body><h1>Outstanding Orders List</h1>"
									+ "<p><b>There are no outstanding orders.</b></p>"		//This html is passed when there are no outstanding orders
									+ "<a href='http://localhost:5678'>[Home]</a>"
									+ "<a href='http://localhost:5678/new_order'>[New Order]</a>"
									+ "<a href='http://localhost:5678/outstanding_orders'>[Outstanding Orders]</a>"
									+ "<a href='http://localhost:5678/fulfilled_orders'>[Fulfilled Orders]</a>"
									+ "</body></html>";;
						}
						output.println( "HTTP/1.0 200 OK" );
						output.println( "Content-Type: text/html" );
						output.println( "Content-Length: " + html.length() );
						output.println( "Connection: close" );
						output.println( "" );
						output.println( html );
						System.out.println( ">" + html );
						output.println( "" );
						output.flush( );
						
					// Fulfilled Orders Page
					} else if ( request[ 1 ].equals( "fulfilled_orders.html" ) ) {
						String listContent = "";
						if ( fulfilled.getSize( ) != 0 ) {											//Checks if the outstanding list has any elements
							for ( int i = 0; i < fulfilled.getSize( ); i++ ) {
								listContent += "<li><b>NAME: </b>" + fulfilled.get( i ).name	+ "<br>"
											+ "<b>Item: </b>" + fulfilled.get( i ).item + "<br>"
											+ "<b>Quantity: </b>" + fulfilled.get( i ).quantity + "<br>"
											+ "<b>Price Per Unit: </b>$" + fulfilled.get( i ).ppu + "<br>"
											+ "<b>Total Cost: </b>$" + fulfilled.get( i ).cost + "</li>";
							}
							html = "<html><body><h1>Fulfilled Orders List</h1><ol>"
									+ listContent
									+ "</ol><a href='http://localhost:5678'>[Home]</a>"
									+ "<a href='http://localhost:5678/new_order'>[New Order]</a>"
									+ "<a href='http://localhost:5678/outstanding_orders'>[Outstanding Orders]</a>"	//This html is passed when there are outstanding orders
									+ "<a href='http://localhost:5678/fulfilled_orders'>[Fulfilled Orders]</a>"
									+ "</body></html>";
						} else {
							html = "<html><body><h1>Fulfilled Orders List</h1>"
									+ "<p><b>There are no fulfilled orders.</b></p>"		//This html is passed when there are no fulfilled orders
									+ "<a href='http://localhost:5678'>[Home]</a>"
									+ "<a href='http://localhost:5678/new_order'>[New Order]</a>"
									+ "<a href='http://localhost:5678/outstanding_orders'>[Outstanding Orders]</a>"
									+ "<a href='http://localhost:5678/fulfilled_orders'>[Fulfilled Orders]</a>"
									+ "</body></html>";
						}
						output.println( "HTTP/1.0 200 OK" );
						output.println( "Content-Type: text/html" );
						output.println( "Content-Length: " + html.length( ) );
						output.println( "Connection: close" );
						output.println( "" );
						output.println( html );
						System.out.println( ">" + html );
						output.println( "" );
						output.flush( );

					// Add Stock Page
					} else if ( request[ 1 ].equals( "add_stock.html" ) ) {
						html = "<html><body><h1>Add Stock</h1><form>" 
								+ "Name: <input type='text' name='name'><br>"
								+ "Quantity: <input type='text' name='quantity'><br>"
								+ "Cost Per-Item: <input type = 'text' name='cost'><br>"
								+ "<input type='submit' value='Submit'></form>"
								+ "<a href='http://localhost:5678'>[Home]</a>"
								+ "<a href='http://localhost:5678/add_stock'>[Add Stock]</a>"
								+ "<a href='http://localhost:5678/list_inventory'>[List Inventory]</a>"
								+ "</body></html>";
						output.println( "HTTP/1.0 200 OK" );
						output.println( "Content-Type: text/html" );
						output.println( "Content-Length: " + html.length( ) );
						output.println( "Connection: close" );
						output.println( "" );
						output.println( html );
						System.out.println( ">" + html );
						output.println( "" );
						output.flush( );
						
					// Add Stock Page after a submission
					} else if ( orderCheck.equals( "add_stock?" ) ) {
						try {
							String listcontent = "";
							ArrayList<Order> temp = new ArrayList<Order>();
							boolean isFulfilled = false;
							boolean exists = false;
							int index = 0; //temp value
						
							//process the GET Command
							String order = request[ 1 ].substring( 10, request[ 1 ].length( ) );
							String[] parts = order.split( "&" );
							String name = parts[ 0 ].substring( 5, parts[ 0 ].length( ) );
							String quantStr = parts[ 1 ].substring( 9, parts[ 1 ].length( ) );
							String ppuStr = parts[ 2 ].substring( 5, parts[ 2 ].length( ) - 5 );
							int quantity = Integer.parseInt( quantStr );
							double ppu = Double.parseDouble( ppuStr );
						
						
							//Check if the stock already exists
							for ( int j = 0; j < stock.getSize( ); j++ ) {
								if ( stock.get( j ).name.equals( name ) )  {
									stock.get( j ).quantity += quantity;
									stock.get( j ).ppu = ppu;
									index = j;
									exists = true;
								break;
								}
							}
						
							//If it doesn't already exist make a new entry
							if ( !exists ) {  
								stock.add( new Order( name, quantity, ppu ) );
								index = stock.getSize() - 1;
							}
						
							for ( int i = 0; i < outstanding.getSize(); i++ ) {
								if ( outstanding.getSize() != 0 ) {
									if ( ( stock.get( index ).name ).equals( outstanding.get( i ).item ) ) {	//Checks if the same type of item has already been ordered
										if ( stock.get( index ).quantity >= outstanding.get( i ).quantity ) {	//If so, checks if there is enough to fulfill the order
											stock.get( index ).quantity -= outstanding.get( i ).quantity;		//If so, removes the quantity from stock (keeps the item slot even if there are zero left)
											Order completedOrder = new Order ( outstanding.get( i ).name,		//Makes a new Completed order(Temporary variable)...
													outstanding.get(i).item, outstanding.get( i ).quantity,		//...
													stock.get( index ).ppu * 1.5);									//...
											temp.add( completedOrder );					//Stores the order that was fulfilled for temporary use
											fulfilled.add( completedOrder );			//Adds the completed order to the fulfilled orders list
											outstanding.remove( i );					//Removes the order from the outstanding orders list
											i--;										//Iterates back one to compensate for removing an item from the list
											isFulfilled = true;							//And indicates that the order has been fulfilled
										}
									}
								}
							}
							for ( int i = 0; i < temp.size( ); i++ ) {
								listcontent += "<li><b>NAME: </b>" + temp.get(i).name + "<br>"
										+ "<b>ITEM: </b>" + temp.get( i ).item + "<br>"
										+ "<b>Quantity: </b>" + temp.get( i ).quantity + "<br>"
										+ "<b>Price Per Item: </b> $" + temp.get( i ).ppu + "<br>"
										+ "<b>Total: </b> $" + (temp.get( i ).cost) + "</li>";
							}
							if ( !isFulfilled ) {
								html = String.format( "<html><body>" 
										+ "<p style='color:red;'><b>Stock added to inventory: </b>"
										+ "NAME: %s QUANTITY: %s COST: $%s</p>" 
										+ "<h1>Add Next Stock Item</h1><form>"
										+ "Name: <input type='text' name='name'><br>"						//This html is passed if the added stock did not fulfill any outstanding orders
										+ "Quantity: <input type='text' name='quantity'><br>"
										+ "Cost Per-Unit: <input tytpe = 'text' name='cost'><br>"		
										+ "<input type='submit' value='Submit'></form>"
										+ "<a href='http://localhost:5678'>[Home]</a>"
										+ "<a href='http://localhost:5678/add_stock'>[Add Stock]</a>"
										+ "<a href='http://localhost:5678/list_inventory'>[List Inventory]</a>"
										+ "</body></html>", name, quantity, ppu );
							} else {
								html = String.format( "<html><body>" 
										+ "<p style='color:red;'><b>Stock added to inventory: </b>"
										+ "NAME: %s QUANTITY: %s COST: $%s</p>"
										+ "<p style='color:blue;'><b>The following orders have been fulfilled.</b><ul>"
										+ listcontent
										+ "</ul><h1>Add Next Stock Item</h1><form>"
										+ "Name: <input type='text' name='name'><br>"						//This html is passed if the added stock did fulfill an outstanding order/s
										+ "Quantity: <input type='text' name='quantity'><br>"
										+ "Cost Per-Unit: <input tytpe = 'text' name='cost'><br>"		
										+ "<input type='submit' value='Submit'></form>"
										+ "<a href='http://localhost:5678'>[Home]</a>"
										+ "<a href='http://localhost:5678/add_stock'>[Add Stock]</a>"
										+ "<a href='http://localhost:5678/list_inventory'>[List Inventory]</a>"
										+ "</body></html>", name, quantity, ppu );
							}
							output.println( "HTTP/1.0 200 OK" );
							output.println( "Content-Type: text/html" );
							output.println( "Content-Length: " + html.length( ) );
							output.println( "Connection: close" );
							output.println( "" );
							output.println( html );
							System.out.println( ">" + html );
							output.println( "" );
							output.flush( );
						} catch (NumberFormatException e) {
							html = "<html><body><p style='color:red;'><b>Invalid Argument Type</b>"     //This html is printed when an order is added with faulty arguments
									+ "<h1>Add Stock</h1><form>" 
									+ "Name: <input type='text' name='name'><br>"
									+ "Quantity: <input type='text' name='quantity'><br>"
									+ "Cost Per-Item: <input type = 'text' name='cost'><br>"
									+ "<input type='submit' value='Submit'></form>"
									+ "<a href='http://localhost:5678'>[Home]</a>"
									+ "<a href='http://localhost:5678/add_stock'>[Add Stock]</a>"
									+ "<a href='http://localhost:5678/list_inventory'>[List Inventory]</a>"
									+ "</body></html>";
							output.println( "HTTP/1.0 400 Bad Request" );
							output.println( "Content-Type: text/html" );
							output.println( "Content-Length: " + html.length( ) );
							output.println( "Connection: close" );
							output.println( "" );
							output.println( html );
						}

					// Inventory List Page
					} else if ( request[ 1 ].equals( "list_inventory.html" ) ) {
						String listContent = "";
						if ( stock.getSize( ) != 0 ) {							//Checks if the outstanding list has any elements
							//generate the list
							for ( int i = 0; i < stock.getSize( ); i++ ) {
								listContent += "<li><b>Item: </b>" + stock.get( i ).name	+ "<br>"
											+ "<b>Quantity: </b>" + stock.get( i ).quantity + "<br>"
											+ "<b>Per-Item Cost: </b>$" + stock.get( i ).ppu + "</li>";
							}
							html = "<html><body><h1>Inventory List</h1><ol>"
									+ listContent
									+ "</ol><a href='http://localhost:5678'>[Home]</a>"
									+ "<a href='http://localhost:5678/add_stock'>[Add Stock]</a>"
									+ "<a href='http://localhost:5678/list_inventory'>[List Inventory]</a>"
									+ "</body></html>";
						} else {
							html = "<html><body><h1>Inventory List</h1><form>"
									+ "<b>There are no items in inventory.<br></b>"
									+ "<a href='http://localhost:5678'>[Home]</a>"
									+ "<a href='http://localhost:5678/add_stock'>[Add Stock]</a>"
									+ "<a href='http://localhost:5678/list_inventory'>[List Inventory]</a>"
									+ "</body></html>";
						}
						output.println( "HTTP/1.0 200 OK" );
						output.println( "Content-Type: text/html" );
						output.println( "Content-Length: " + html.length( ) );
						output.println( "Connection: close" );
						output.println( "" );
						output.println( html );
						System.out.println( ">" + html );
						output.println( "" );
						output.flush( );

					// 404 error
					} else if ( !fin.exists( ) ) {
						html = String.format( "<HTML><BODY><H1>FILE WAS NOT FOUND</H1>"
								+ "FILE=%s</BODY></HTML>",request[ 1 ] );
						output.println( "HTTP/1.0 404 Not Found" );
						output.println( "Connection: close" );
						output.println( "" );
						output.println( html );
						System.out.println( ">" + html );
						output.println( "" );
						output.flush( );

					// file as input
					} else {
						output.println( "HTTP/1.0 200 OK" );
						output.println( "Content-Type: text/html" );
						output.println( "Content-Length: " + fin.length( ) );
						output.println( "Connection: close" );
						output.println( "" );
						try ( FileReader reader = new FileReader( fin ) ) {
							html = "";
							while ( reader.ready( ) ) {
								html += ( char ) reader.read( );
							}
						} catch ( FileNotFoundException e ) {
							e.printStackTrace();
						} catch ( IOException e ) {
							e.printStackTrace();
						}
						output.println( html );
						System.out.println( ">" + html );
						output.println( "" );
						output.flush( );
					}
				} catch ( Exception e ) {
					e.printStackTrace( );
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Program5 self = new Program5();
		self.server(Integer.parseInt(args[0]));
	}
}