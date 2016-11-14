import java.sql.*; 

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.ScriptContextAdapter;
import com.teamdev.jxbrowser.chromium.events.ScriptContextEvent;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicReference;

class jdbcDB2Sample 
{
	public static void main(String argv[]) 
	{


		//        browser.addLoadListener(new LoadAdapter() {
		//            @Override
		//            public void onFinishLoadingFrame(FinishLoadingEvent event) {
		//                if (event.isMainFrame()) {
		//                    JSValue value = browser.executeJavaScriptAndReturnValue("window");
		//                    this.helloWorld();
		//                }
		//            }
		//        });
		//    JSValue window = browser.executeJavaScriptAndReturnValue("window");
		//   window.asObject().setProperty("frame", frame);
		//    window.asObject().setProperty("userView", new UserView());

		// 	connectToDB.addActionListener(new ActionListener() {
		// 		@Override
		// 		public void actionPerformed(ActionEvent e) {
		// 			System.out.println("hello world");
		// 		}
		// 		
		// 	});	
		//    	JPanel contentPane = new JPanel();
		////    	contentPane.add(connectToDB);
		//    	 frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		//         frame.add(contentPane, BorderLayout.CENTER);
		//         frame.setSize(300, 75);
		//         frame.setLocationRelativeTo(null);
		//         frame.setVisible(true);
		final Browser browser = new Browser();
		BrowserView browserView = new BrowserView(browser);
		final JFrame frame = new JFrame("Shamazon"); 	
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.add(browserView, BorderLayout.CENTER);
		frame.setSize(700, 500);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		browser.addScriptContextListener(new ScriptContextAdapter() {
			@Override
			public void onScriptContextCreated(ScriptContextEvent event) {
				Browser browser = event.getBrowser();
				JSValue window = browser.executeJavaScriptAndReturnValue("window");
				MainView mv = new MainView();
				mv.setParentFrame(frame);
				mv.setParentBrowser(browser);
				window.asObject().setProperty("mainView", mv);

			}  
		});
		browser.loadURL("file://C:/Users/Spencer/Desktop/CS304/CPSC304Project/src/GUI/index.html");
	}

	public static class LoginView {
		private JFrame parent;
		private Browser browser;
		public void load() {
			this.browser.loadURL("file://C:/Users/Spencer/Desktop/CS304/CPSC304Project/src/GUI/index.html");
			final JFrame parent = this.parent;
			//  	 dialog.setDefaultCloseOperation(WindowConstants.);
			// Embed Browser Swing component into the dialog.
			//	this.parent.add(new BrowserView(this.browser), BorderLayout.CENTER);
			//	this.parent.setSize(700, 500);	
			parent.setResizable(false);
			parent.setLocationRelativeTo(null);
			parent.setVisible(true);

			this.browser.addScriptContextListener(new ScriptContextAdapter() {
				@Override
				public void onScriptContextCreated(ScriptContextEvent event) {
					Browser browser = event.getBrowser();
					JSValue window = browser.executeJavaScriptAndReturnValue("window");
					MainView mv = new MainView();
					mv.setParentFrame(parent);
					mv.setParentBrowser(browser);
					window.asObject().setProperty("mainView", mv);					
				}  
			});
			//connection();
		}

		public void setParentFrame(JFrame parent){
			this.parent = parent;
		}

		public void setParentBrowser(Browser browser){
			this.browser = browser;
		}
	}

	public static class MainView {
		private JFrame parent;
		private Browser browser;
		public void load() {
			this.browser.loadURL("file://C:/Users/Spencer/Desktop/CS304/CPSC304Project/src/GUI/main.html");
			final JFrame parent = this.parent;
			//  	 dialog.setDefaultCloseOperation(WindowConstants.);
			// Embed Browser Swing component into the dialog.
			//	this.parent.add(new BrowserView(this.browser), BorderLayout.CENTER);
			//	this.parent.setSize(700, 500);	
			parent.setResizable(false);
			parent.setLocationRelativeTo(null);
			parent.setVisible(true);

			this.browser.addScriptContextListener(new ScriptContextAdapter() {
				@Override
				public void onScriptContextCreated(ScriptContextEvent event) {
					Browser browser = event.getBrowser();
					JSValue window = browser.executeJavaScriptAndReturnValue("window");
					LoginView lv = new LoginView();
					lv.setParentFrame(parent);
					lv.setParentBrowser(browser);
					AdminView av = new AdminView();
					av.setParentFrame(parent);
					av.setParentBrowser(browser);
					ItemView iv = new ItemView();
					iv.setParentFrame(parent);
					iv.setParentBrowser(browser);
					ReviewView rv = new ReviewView();
					rv.setParentFrame(parent);
					rv.setParentBrowser(browser);
					window.asObject().setProperty("loginView", lv);
					window.asObject().setProperty("adminView", av);
					window.asObject().setProperty("itemView", iv);
					window.asObject().setProperty("reviewView", rv);					
				}  
			});
			//connection();
		}

		public void setParentFrame(JFrame parent){
			this.parent = parent;
		}

		public void setParentBrowser(Browser browser){
			this.browser = browser;
		}
	}

	public static class AdminView {
		private JFrame parent;
		private Browser browser;
		public void load() {
			this.browser.loadURL("file://C:/Users/Spencer/Desktop/CS304/CPSC304Project/src/GUI/admin.html");
			final JFrame parent = this.parent;

			parent.setResizable(false);
			parent.setLocationRelativeTo(null);
			parent.setVisible(true);
			this.browser.addScriptContextListener(new ScriptContextAdapter() {
				@Override
				public void onScriptContextCreated(ScriptContextEvent event) {
					Browser browser = event.getBrowser();
					JSValue window = browser.executeJavaScriptAndReturnValue("window");
					AdminView av = new AdminView();
					av.setParentFrame(parent);
					av.setParentBrowser(browser);
					MainView mv = new MainView();
					mv.setParentFrame(parent);
					mv.setParentBrowser(browser);
					AdminQuery aq = new AdminQuery();
					window.asObject().setProperty("adminView", av);
					window.asObject().setProperty("mainView", mv);
					window.asObject().setProperty("adminQuery", aq);
				}  
			});
			//  	 dialog.setDefaultCloseOperation(WindowConstants.);
			// Embed Browser Swing component into the dialog.
			//	this.parent.add(new BrowserView(this.browser), BorderLayout.CENTER);
			//	this.parent.setSize(700, 500);
			//connection();
		}

		public void setParentFrame(JFrame parent){
			this.parent = parent;
		}

		public void setParentBrowser(Browser browser){
			this.browser = browser;
		}
	}
	public static class ReviewView {
		private JFrame parent;
		private Browser browser;
		public void load() {
			this.browser.loadURL("file://C:/Users/Spencer/Desktop/CS304/CPSC304Project/src/GUI/review.html");
			final JFrame parent = this.parent;

			parent.setResizable(false);
			parent.setLocationRelativeTo(null);
			parent.setVisible(true);
			this.browser.addScriptContextListener(new ScriptContextAdapter() {
				@Override
				public void onScriptContextCreated(ScriptContextEvent event) {
					Browser browser = event.getBrowser();
					JSValue window = browser.executeJavaScriptAndReturnValue("window");
					AdminView av = new AdminView();
					av.setParentFrame(parent);
					av.setParentBrowser(browser);
					MainView mv = new MainView();
					mv.setParentFrame(parent);
					mv.setParentBrowser(browser);
					SearchQuery sq = new SearchQuery();
					window.asObject().setProperty("adminView", av);
					window.asObject().setProperty("mainView", mv);
					window.asObject().setProperty("searchQuery", sq);
				}  
			});

			//  	 dialog.setDefaultCloseOperation(WindowConstants.);
			// Embed Browser Swing component into the dialog.
			//	this.parent.add(new BrowserView(this.browser), BorderLayout.CENTER);
			//	this.parent.setSize(700, 500);
			//connection();
		}

		public void setParentFrame(JFrame parent){
			this.parent = parent;
		}

		public void setParentBrowser(Browser browser){
			this.browser = browser;
		}
	}
	public static class ItemView {
		private JFrame parent;
		private Browser browser;
		public void load() {
			this.browser.loadURL("file://C:/Users/Spencer/Desktop/CS304/CPSC304Project/src/GUI/item.html");
			final JFrame parent = this.parent;
			parent.setResizable(false);
			parent.setLocationRelativeTo(null);
			parent.setVisible(true);
			//  	 dialog.setDefaultCloseOperation(WindowConstants.);
			// Embed Browser Swing component into the dialog.
			//	this.parent.add(new BrowserView(this.browser), BorderLayout.CENTER);
			//	this.parent.setSize(700, 500);	

			this.browser.addScriptContextListener(new ScriptContextAdapter() {
				@Override
				public void onScriptContextCreated(ScriptContextEvent event) {
					Browser browser = event.getBrowser();
					JSValue window = browser.executeJavaScriptAndReturnValue("window");
					AdminView av = new AdminView();
					av.setParentFrame(parent);
					av.setParentBrowser(browser);
					MainView mv = new MainView();
					mv.setParentFrame(parent);
					mv.setParentBrowser(browser);
					SearchQuery sq = new SearchQuery();
					window.asObject().setProperty("adminView", av);
					window.asObject().setProperty("mainView", mv);					
					window.asObject().setProperty("searchQuery", sq);
				}  
			});

			//connection();
		}

		public void setParentFrame(JFrame parent){
			this.parent = parent;
		}

		public void setParentBrowser(Browser browser){
			this.browser = browser;
		}
		
	}


	public static class SearchQuery {
		public void search(String filter, String inputText) {
			System.out.println(filter + " and " + inputText);
		}
		
		public void searchPredefined(String searchID){
			System.out.println(searchID);
			switch(searchID){
			case "Highest Rated Items":
				highestRatedItems();
				break;
			case "Lowest Rated Items":
				lowerRatedItems();
				break;
			case "Most Reviewed Items":
				mostReviewedItems();
				break;
			case "Items Available In Your Country":
				currentCountryAvailability();
				break;
			case "Highest Rated Reviews":
				highestRatedReviews();
				break;
			case "Lowest Rated Reviews":
				lowerRatedReviews();
				break;
			default:
					break;
			}
		}
		
		private void highestRatedItems(){
			
		}
		
		private void lowerRatedItems(){
			
		}
		
		private void mostReviewedItems(){
			
		}
		
		private void currentCountryAvailability(){
			
		}
		
		private void highestRatedReviews(){
			
		}
		
		private void lowerRatedReviews(){
			
		}
	}
	
	public static class AdminQuery{
		public void remove(String ID){
			System.out.println(ID);
		}
		
		public void addUser(String name, String DOB, String email, String country){
			System.out.println(name + " AND " + DOB + " AND " + email + " AND " + country);
		}
		
		public void addItem(String ID, String name, String ageRestriction, String link, String country){
			System.out.println(ID + " AND " + name + " AND " + ageRestriction + " AND " + link + " AND " + country);
		}
	}
	
	private static void connection() {

		Connection con = null;

		try
		{
			System.out.println("Loading driver ...");

			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());


			System.out.println("Driver loaded.");
		}
		catch (Exception e)
		{
			System.out.println("Unable to load driver\n" + e);
			System.exit(-1);
		}

		try 
		{  
			System.out.println("Connecting to NetDB2 ...");
			con = DriverManager.getConnection(
					"jdbc:oracle:thin:@localhost:1522:ug", "ora_e3u9a", "a37106135");

			System.out.println("Connection successful.");

		} 
		catch( Exception e ) 
		{
			System.out.println("Connection failed\n" + e);
		}

		try
		{
			Statement stmt = con.createStatement();
			// Create item table as per spec
			stmt.executeQuery("create table Item(item_id int not null, genre varchar(20), name varchar(50) not null, age_restriction int, purchase_link varchar(100),rating float not null , primary key (item_id))");
			System.out.println("Table has been created");
			// Insert Item tuples
			stmt.executeQuery("insert into Item values(12051, 'Comedy', 'Home Alone', null, null, 9.5)");
			System.out.println("Inserted new Item");
			stmt.executeQuery("insert into Item values(49457, 'Horror', 'It Follows', 18, null, 7.1)");
			System.out.println("Inserted new Item");
			stmt.executeQuery("insert into Item values(29316, 'Romance', 'Twilight', null, 'https://www.amazon.com/Twilight-Saga-Book-1/dp/0316015849', 7.6)");

			// Retrieve all items with a rating of 7.5 or more.
			ResultSet rs = stmt.executeQuery("select * from item where rating > 7.5");   
			while(rs.next())
			{
				System.out.println("The following items have a rating of 7.5 or more:");
				System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3) + " " + rs.getInt(4) + " " + rs.getString(5) + " " + rs.getFloat(6));
			}
		}
		catch (SQLException ex)
		{
			System.out.println(ex);
		}
	}
}

