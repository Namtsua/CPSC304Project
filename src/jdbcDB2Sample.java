import java.math.BigInteger;
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
import java.util.concurrent.TimeUnit;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
class jdbcDB2Sample 
{
	public static void main(String argv[]) 
	{
		// Setup the initial browser, frame (window), frame configurations and ensure that we start with Admin Mode turned off
		final Browser browser = new Browser();
		BrowserView browserView = new BrowserView(browser);
		final JFrame frame = new JFrame("Shamazon");
		final Connection con = connection();
		final boolean isAdmin = false;
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.add(browserView, BorderLayout.CENTER);
		frame.setSize(900, 700);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		// This allows us to share portions of the Java code with the current HTML file (in this case, it would be index.html)
		// Specifically we can allow the HTML/Javascript to access classes, methods, objects, values, etc.
		browser.addScriptContextListener(new ScriptContextAdapter() {
			@Override
			public void onScriptContextCreated(ScriptContextEvent event) {
				Browser browser = event.getBrowser();
				JSValue window = browser.executeJavaScriptAndReturnValue("window");
				MainView mv = new MainView();
				mv.setParentFrame(frame);
				mv.setParentBrowser(browser);
				mv.setConnection(con);
				window.asObject().setProperty("mainView", mv);
			}  
		});
		browser.loadURL("file://C:/Users/Spencer/Desktop/CS304/CPSC304Project/src/GUI/index.html");
	}

	// The Login Screen
	public static class LoginView {
		private JFrame parent;
		private Browser browser;
		private Connection con;
		private boolean isAdmin;
		private String ID;

		// Load the login page
		public void load() {
			// The following 10 lines of code are replicated throughout the application and ensure we are using the same browser, window, settings and Oracle connection
			this.browser.loadURL("file://C:/Users/Spencer/Desktop/CS304/CPSC304Project/src/GUI/index.html");
			final JFrame parent = this.parent;
			final Connection con = this.con;
			final Browser browser = this.browser;
			final boolean isAdmin = this.isAdmin;
			final String ID = this.ID;
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
					mv.setConnection(con);
					mv.setAdminMode(isAdmin);
					mv.setCurrentUser(ID);
					window.asObject().setProperty("mainView", mv);					
				}  
			});
		}

		// Setter for JFrame
		public void setParentFrame(JFrame parent){
			this.parent = parent;
		}

		// Setter for Browser
		public void setParentBrowser(Browser browser){
			this.browser = browser;
		}

		// Setter for Connection
		public void setConnection(Connection con){
			this.con = con;
		}

		// Setter for Admin Mode
		public void setAdminMode(boolean isAdmin) {
			this.isAdmin = isAdmin;

		}

		// Setter for keeping track of current user
		public void setCurrentUser(String ID) {
			this.ID = ID;

		}
	}

	public static class MainView {
		private JFrame parent;
		private Browser browser;
		private Connection con;
		private boolean isAdmin;
		private String ID;

		public void load() {
			this.browser.loadURL("file://C:/Users/Spencer/Desktop/CS304/CPSC304Project/src/GUI/item.html");
			final JFrame parent = this.parent;
			final Connection con = this.con;
			final boolean isAdmin = this.isAdmin;
			final String ID = this.ID;
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
					lv.setConnection(con);
					lv.setAdminMode(false);
					lv.setCurrentUser(ID);
					AdminView av = new AdminView();
					av.setParentFrame(parent);
					av.setParentBrowser(browser);
					av.setConnection(con);
					av.setAdminMode(isAdmin);
					av.setCurrentUser(ID);
					ItemView iv = new ItemView();
					iv.setParentFrame(parent);
					iv.setParentBrowser(browser);
					iv.setConnection(con);
					iv.setAdminMode(isAdmin);
					iv.setCurrentUser(ID);
					ReviewView rv = new ReviewView();
					rv.setParentFrame(parent);
					rv.setParentBrowser(browser);
					rv.setConnection(con);
					rv.setAdminMode(isAdmin);
					rv.setCurrentUser(ID);
					ItemResultView irv = new ItemResultView();
					irv.setConnection(con);
					irv.setParentBrowser(browser); 
					irv.setParentFrame(parent);
					irv.setAdminMode(isAdmin);
					irv.setCurrentUser(ID);
					window.asObject().setProperty("loginView", lv);
					window.asObject().setProperty("adminView", av);
					window.asObject().setProperty("itemView", iv);
					window.asObject().setProperty("reviewView", rv);
					window.asObject().setProperty("itemResultView", irv);
					window.asObject().setProperty("isAdmin", isAdmin);
				}  
			});
		}

		public boolean login(String username, String password){
			System.out.println("Attempting to log in");
			MessageDigest md;
			try {
				// We need to hash the password via MD5. I tried to edit it as much as I could, but there's not much wiggle room for this functionaliy.
				// Source: http://stackoverflow.com/questions/415953/how-can-i-generate-an-md5-hash
				md = MessageDigest.getInstance("MD5");
				byte[] thedigest = md.digest(password.getBytes());
				BigInteger bigInt = new BigInteger(1,thedigest);
				String hashtext = bigInt.toString(16);
				// Now we need to zero pad it if you actually want the full 32 chars.
				while(hashtext.length() < 32 ){
					hashtext = "0"+hashtext;
				}
				String[] queries = new String[1];
				// Go into DB and check if this email already exists
				queries[0] = "SELECT wiki_user_email FROM WikiUser WHERE wiki_user_email = '" + username + "'";
				String[][] result = SendQuery(this.con, queries, 2);
				System.out.println("Just retrieved this email " + result[0][0]);
				// No email found, return false
				if (result[0][0] == null)
					return false;
				else{
					// Retrieve MD5 hashed password from DB that corresponds to the matching email.
					queries[0] = "SELECT wiki_user_password FROM WikiUser WHERE wiki_user_email = '" + username + "'";
					result = SendQuery(this.con, queries, 2);

					// Ensure that the inputted hashed password and the password from the DB match
					if (result[0][0].equals(hashtext)){
						System.out.println("Logged in!");
						// Check to see if this user is also an Admin
						queries[0] = "SELECT wiki_user_email FROM Wikiuser w, Admin a WHERE (w.wiki_user_id = a.wiki_user_id AND w.wiki_user_email = '" + username + "')";
						result = SendQuery(this.con, queries, 2);
						// If not an admin
						if (result.length == 0){
							// Retrieve this user's ID and let the application know whose logged in.
							queries[0] = "SELECT wiki_user_id FROM Wikiuser WHERE wiki_user_email = '" + username +"'";
							result = SendQuery(this.con, queries, 2);
							setCurrentUser(result[0][0]);
							return true;
						}
						else {
							// Retrieve AdminID from the Admin table
							queries[0] = "SELECT admin_id FROM Admin a, Wikiuser w WHERE admin_id = w.wiki_user_id AND w.wiki_user_email = '" + username +"'";
							result = SendQuery(this.con, queries, 2);
							setAdminMode(true);
							setCurrentUser(result[0][0]);
							return true;
						}

					}
					else
						return false;

				}
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return false;
			}
		}

		public void setParentFrame(JFrame parent){
			this.parent = parent;
		}

		public void setParentBrowser(Browser browser){
			this.browser = browser;
		}

		public void setConnection(Connection con){
			this.con = con;
		}

		public void setAdminMode(boolean isAdmin) {
			this.isAdmin = isAdmin;
		}

		public void setCurrentUser(String ID){
			this.ID = ID;
		}
	}

	public static class AdminView {
		private JFrame parent;
		private Browser browser;
		private Connection con;
		private boolean isAdmin;
		private String ID;

		public void load() {
			this.browser.loadURL("file://C:/Users/Spencer/Desktop/CS304/CPSC304Project/src/GUI/admin.html");
			final JFrame parent = this.parent;
			final Connection con = this.con;
			final boolean isAdmin = this.isAdmin;
			final String ID = this.ID;
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
					av.setAdminMode(isAdmin);
					av.setConnection(con);
					av.setCurrentUser(ID);
					MainView mv = new MainView();
					mv.setParentFrame(parent);
					mv.setParentBrowser(browser);
					mv.setAdminMode(isAdmin);
					mv.setConnection(con);
					mv.setCurrentUser(ID);
					ReviewView rv = new ReviewView();
					rv.setParentFrame(parent);
					rv.setParentBrowser(browser);
					rv.setAdminMode(isAdmin);
					rv.setConnection(con);
					rv.setCurrentUser(ID);
					ItemView iv = new ItemView();
					iv.setParentFrame(parent);
					iv.setParentBrowser(browser);
					iv.setAdminMode(isAdmin);
					iv.setConnection(con);
					iv.setCurrentUser(ID);
					UserResultView urv = new UserResultView();
					urv.setConnection(con);
					urv.setParentBrowser(browser); 
					urv.setParentFrame(parent);
					urv.setAdminMode(isAdmin);
					urv.setCurrentUser(ID);
					ReviewResultView rrv = new ReviewResultView();
					rrv.setConnection(con);
					rrv.setParentBrowser(browser); 
					rrv.setParentFrame(parent);
					rrv.setAdminMode(isAdmin);
					rrv.setCurrentUser(ID);
					AdminQuery aq = new AdminQuery();
					aq.setConnection(con);
					aq.setCurrentUser(ID);
					window.asObject().setProperty("adminView", av);
					window.asObject().setProperty("mainView", mv);
					window.asObject().setProperty("adminQuery", aq);
					window.asObject().setProperty("reviewView", rv);
					window.asObject().setProperty("itemView", iv);
					window.asObject().setProperty("userResultView", urv);
					window.asObject().setProperty("reviewResultView", rrv);
				}  
			});
		}

		public void setCurrentUser(String ID) {
			this.ID = ID;
		}

		public void setParentFrame(JFrame parent){
			this.parent = parent;
		}

		public void setParentBrowser(Browser browser){
			this.browser = browser;
		}

		public void setConnection(Connection con) {
			this.con = con;
		}

		public void setAdminMode(boolean isAdmin){
			this.isAdmin = isAdmin;
		}
	}

	public static class ReviewView {
		private JFrame parent;
		private Browser browser;
		private Connection con;
		private boolean isAdmin;
		private String ID;

		public void load() {
			this.browser.loadURL("file://C:/Users/Spencer/Desktop/CS304/CPSC304Project/src/GUI/review.html");
			final JFrame parent = this.parent;
			final Connection con = this.con;
			final boolean isAdmin = this.isAdmin;
			final String ID = this.ID;
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
					av.setAdminMode(isAdmin);
					av.setConnection(con);
					av.setCurrentUser(ID);
					MainView mv = new MainView();
					mv.setParentFrame(parent);
					mv.setParentBrowser(browser);
					mv.setAdminMode(isAdmin);
					mv.setConnection(con);
					mv.setCurrentUser(ID);
					LoginView lv = new LoginView();
					lv.setParentFrame(parent);
					lv.setParentBrowser(browser);
					lv.setAdminMode(isAdmin);
					lv.setConnection(con);
					lv.setCurrentUser(ID);
					SearchQuery sq = new SearchQuery();
					sq.setConnection(con);
					ReviewResultView rrv = new ReviewResultView();
					rrv.setConnection(con);
					rrv.setParentBrowser(browser);
					rrv.setParentFrame(parent);
					rrv.setAdminMode(isAdmin);
					rrv.setCurrentUser(ID);
					window.asObject().setProperty("adminView", av);
					window.asObject().setProperty("mainView", mv);					
					window.asObject().setProperty("searchQuery", sq);
					window.asObject().setProperty("reviewResultView", rrv);
					window.asObject().setProperty("loginView", lv);
				}  
			});

		}

		public void setCurrentUser(String ID) {
			this.ID = ID;
		}

		public void setParentFrame(JFrame parent){
			this.parent = parent;
		}

		public void setParentBrowser(Browser browser){
			this.browser = browser;
		}

		public void setConnection(Connection con) {
			this.con = con;
		}

		public void setAdminMode(boolean isAdmin){
			this.isAdmin = isAdmin;
		}
	}
	public static class ItemView {
		private JFrame parent;
		private Browser browser;
		private Connection con;
		private boolean isAdmin;
		private String ID;

		public void load() {
			this.browser.loadURL("file://C:/Users/Spencer/Desktop/CS304/CPSC304Project/src/GUI/item.html");
			final JFrame parent = this.parent;
			final Connection con = this.con;
			final boolean isAdmin = this.isAdmin;
			final String ID = this.ID;
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
					av.setAdminMode(isAdmin);
					av.setConnection(con);
					av.setCurrentUser(ID);
					MainView mv = new MainView();
					mv.setParentFrame(parent);
					mv.setParentBrowser(browser);
					mv.setAdminMode(isAdmin);
					mv.setConnection(con);
					mv.setCurrentUser(ID);
					LoginView lv = new LoginView();
					lv.setParentFrame(parent);
					lv.setParentBrowser(browser);
					lv.setAdminMode(isAdmin);
					lv.setConnection(con);
					lv.setCurrentUser(ID);
					SearchQuery sq = new SearchQuery();
					sq.setConnection(con);
					ItemResultView irv = new ItemResultView();
					irv.setConnection(con);
					irv.setParentBrowser(browser); 
					irv.setParentFrame(parent);
					irv.setAdminMode(isAdmin);
					irv.setCurrentUser(ID);
					window.asObject().setProperty("adminView", av);
					window.asObject().setProperty("mainView", mv);					
					window.asObject().setProperty("searchQuery", sq);
					window.asObject().setProperty("itemResultView", irv);
					window.asObject().setProperty("loginView", lv);
				}  
			});
		}

		public void setCurrentUser(String ID) {
			this.ID = ID;
		}

		public void setParentFrame(JFrame parent){
			this.parent = parent;
		}

		public void setParentBrowser(Browser browser){
			this.browser = browser;
		}

		public void setConnection(Connection con) {
			this.con = con;
		}

		public void setAdminMode(boolean isAdmin){
			this.isAdmin = isAdmin;
		}
	}

	public static class ItemResultView {
		private JFrame parent;
		private Browser browser;
		private Connection con;
		private String[][] queryResult;
		private boolean isAdmin;
		private String ID;

		// Load this view with the specified search parameters
		public void load(String filter, String inputText) {
			this.browser.loadURL("file://C:/Users/Spencer/Desktop/CS304/CPSC304Project/src/GUI/itemresult.html");
			final JFrame parent = this.parent;
			final Connection con = this.con;
			final boolean isAdmin = this.isAdmin;
			final String ID = this.ID;
			parent.setResizable(false);
			parent.setLocationRelativeTo(null);
			parent.setVisible(true);
			final SearchQuery sq = new SearchQuery();
			sq.setConnection(con);
			// Since this is a result page, we need to send out the request and retrieve the results
			final String[][] result = sq.search(filter, inputText);
			this.queryResult = result;

			// Allow access to Admin view, Main view, Login view and to search query result.
			this.browser.addScriptContextListener(new ScriptContextAdapter() {
				@Override
				public void onScriptContextCreated(ScriptContextEvent event) {
					Browser browser = event.getBrowser();
					JSValue window = browser.executeJavaScriptAndReturnValue("window");
					AdminView av = new AdminView();
					av.setParentFrame(parent);
					av.setParentBrowser(browser);
					av.setConnection(con);
					av.setAdminMode(isAdmin);
					av.setCurrentUser(ID);
					MainView mv = new MainView();
					mv.setParentFrame(parent);
					mv.setParentBrowser(browser);
					mv.setConnection(con);
					mv.setAdminMode(isAdmin);
					mv.setCurrentUser(ID);
					LoginView lv = new LoginView();
					lv.setParentFrame(parent);
					lv.setParentBrowser(browser);
					lv.setAdminMode(isAdmin);
					lv.setConnection(con);
					lv.setCurrentUser(ID);
					sq.setConnection(con);
					window.asObject().setProperty("adminView", av);
					window.asObject().setProperty("mainView", mv);
					window.asObject().setProperty("loginView", lv);
					window.asObject().setProperty("result", result);
					window.asObject().setProperty("searchQuery", sq);
				}  
			});
		}

		public void setCurrentUser(String ID) {
			this.ID = ID;
		}

		public void setParentFrame(JFrame parent){
			this.parent = parent;
		}

		public void setParentBrowser(Browser browser){
			this.browser = browser;
		}

		public void setConnection(Connection con) {
			this.con = con;
		}

		public void setAdminMode(boolean isAdmin){
			this.isAdmin = isAdmin;
		}

		public String[][] getResult(){
			return this.queryResult;
		}

	}

	public static class ReviewResultView {
		private JFrame parent;
		private Browser browser;
		private Connection con;
		private String[][] queryResult;
		private boolean isAdmin;
		private String ID;

		public void load(String filter, String inputText) {
			this.browser.loadURL("file://C:/Users/Spencer/Desktop/CS304/CPSC304Project/src/GUI/reviewresult.html");
			final JFrame parent = this.parent;
			final Connection con = this.con;
			final boolean isAdmin = this.isAdmin;
			final String ID = this.ID;
			parent.setResizable(false);
			parent.setLocationRelativeTo(null);
			parent.setVisible(true);
			SearchQuery sq = new SearchQuery();
			sq.setConnection(con);
			final String[][] result = sq.search(filter, inputText);
			this.queryResult = result;

			this.browser.addScriptContextListener(new ScriptContextAdapter() {
				@Override
				public void onScriptContextCreated(ScriptContextEvent event) {
					Browser browser = event.getBrowser();
					JSValue window = browser.executeJavaScriptAndReturnValue("window");
					AdminView av = new AdminView();
					av.setParentFrame(parent);
					av.setParentBrowser(browser);
					av.setConnection(con);
					av.setAdminMode(isAdmin);
					av.setCurrentUser(ID);
					MainView mv = new MainView();
					mv.setParentFrame(parent);
					mv.setParentBrowser(browser);
					mv.setConnection(con);
					mv.setAdminMode(isAdmin);
					mv.setCurrentUser(ID);
					LoginView lv = new LoginView();
					lv.setParentFrame(parent);
					lv.setParentBrowser(browser);
					lv.setAdminMode(isAdmin);
					lv.setConnection(con);
					lv.setCurrentUser(ID);
					SearchQuery sq = new SearchQuery();
					sq.setConnection(con);
					window.asObject().setProperty("adminView", av);
					window.asObject().setProperty("mainView", mv);
					window.asObject().setProperty("loginView", lv);
					window.asObject().setProperty("result", result);
					window.asObject().setProperty("ID", ID);
					window.asObject().setProperty("searchQuery", sq);
				}  
			});
		}

		public void specialLoad(String inputText) {
			this.browser.loadURL("file://C:/Users/Spencer/Desktop/CS304/CPSC304Project/src/GUI/reviewresult.html");
			final JFrame parent = this.parent;
			final Connection con = this.con;
			final boolean isAdmin = this.isAdmin;
			final String ID = this.ID;
			parent.setResizable(false);
			parent.setLocationRelativeTo(null);
			parent.setVisible(true);
			SearchQuery sq = new SearchQuery();
			sq.setConnection(con);
			final String[][] result = sq.AverageReview();
			this.queryResult = result;

			this.browser.addScriptContextListener(new ScriptContextAdapter() {
				@Override
				public void onScriptContextCreated(ScriptContextEvent event) {
					Browser browser = event.getBrowser();
					JSValue window = browser.executeJavaScriptAndReturnValue("window");
					AdminView av = new AdminView();
					av.setParentFrame(parent);
					av.setParentBrowser(browser);
					av.setConnection(con);
					av.setAdminMode(isAdmin);
					av.setCurrentUser(ID);
					MainView mv = new MainView();
					mv.setParentFrame(parent);
					mv.setParentBrowser(browser);
					mv.setConnection(con);
					mv.setAdminMode(isAdmin);
					mv.setCurrentUser(ID);
					LoginView lv = new LoginView();
					lv.setParentFrame(parent);
					lv.setParentBrowser(browser);
					lv.setAdminMode(isAdmin);
					lv.setConnection(con);
					lv.setCurrentUser(ID);
					SearchQuery sq = new SearchQuery();
					sq.setConnection(con);
					window.asObject().setProperty("adminView", av);
					window.asObject().setProperty("mainView", mv);
					window.asObject().setProperty("loginView", lv);
					window.asObject().setProperty("result", result);
					window.asObject().setProperty("ID", ID);
					window.asObject().setProperty("searchQuery", sq);
				}  
			});
		}

		
		
		
		public void setCurrentUser(String ID) {
			this.ID = ID;
		}

		public void setAdminMode(boolean isAdmin) {
			this.isAdmin = isAdmin;

		}

		public void setParentFrame(JFrame parent){
			this.parent = parent;
		}

		public void setParentBrowser(Browser browser){
			this.browser = browser;
		}

		public void setConnection(Connection con) {
			this.con = con;
		}

		public String[][] getResult(){
			return this.queryResult;
		}

	}

	public static class UserResultView {
		private JFrame parent;
		private Browser browser;
		private Connection con;
		private String[][] queryResult;
		private boolean isAdmin;
		private String ID;

		public void load(String userIDs) {
			this.browser.loadURL("file://C:/Users/Spencer/Desktop/CS304/CPSC304Project/src/GUI/userresult.html");
			final JFrame parent = this.parent;
			final Connection con = this.con;
			final boolean isAdmin = this.isAdmin;
			final String ID = this.ID;
			parent.setResizable(false);
			parent.setLocationRelativeTo(null);
			parent.setVisible(true);
			SearchQuery sq = new SearchQuery();
			sq.setConnection(con);
			final String[][] result = sq.userSearch(userIDs);
			this.queryResult = result;

			this.browser.addScriptContextListener(new ScriptContextAdapter() {
				@Override
				public void onScriptContextCreated(ScriptContextEvent event) {
					Browser browser = event.getBrowser();
					JSValue window = browser.executeJavaScriptAndReturnValue("window");
					AdminView av = new AdminView();
					av.setParentFrame(parent);
					av.setParentBrowser(browser);
					av.setConnection(con);
					av.setAdminMode(isAdmin);
					av.setCurrentUser(ID);
					MainView mv = new MainView();
					mv.setParentFrame(parent);
					mv.setParentBrowser(browser);
					mv.setConnection(con);
					mv.setAdminMode(isAdmin);
					mv.setCurrentUser(ID);
					LoginView lv = new LoginView();
					lv.setParentFrame(parent);
					lv.setParentBrowser(browser);
					lv.setAdminMode(isAdmin);
					lv.setConnection(con);
					lv.setCurrentUser(ID);
					SearchQuery sq = new SearchQuery();
					sq.setConnection(con);
					window.asObject().setProperty("adminView", av);
					window.asObject().setProperty("mainView", mv);
					window.asObject().setProperty("loginView", lv);
					window.asObject().setProperty("result", result);
					window.asObject().setProperty("ID", ID);
					window.asObject().setProperty("searchQuery", sq);
				}  
			});
		}

		public void setCurrentUser(String ID) {
			this.ID = ID;
		}

		public void setAdminMode(boolean isAdmin) {
			this.isAdmin = isAdmin;

		}

		public void setParentFrame(JFrame parent){
			this.parent = parent;
		}

		public void setParentBrowser(Browser browser){
			this.browser = browser;
		}

		public void setConnection(Connection con) {
			this.con = con;
		}

		public String[][] getResult(){
			return this.queryResult;
		}

	}


	// This class contains the majority of the SQL calls
	public static class SearchQuery {

		private Connection con;

		// Method for both Item and Review search bars
		public String[][] search(String filter, String inputText) {
			String[] queries;
			switch(filter) {
			// Search Items without any pre-applied filters
			case "All":
				queries = new String[3];
				queries[0] = "DROP VIEW result";
				// If the user left the search bar blank.
				if (inputText.trim().equals("")) {
					queries[1] = "CREATE VIEW result AS "
							+ "SELECT item_id, item_name, age_restriction, item_rating, item_plink, wiki_country, type, genre "
							+ "FROM Item";
				} else {
					queries[1] = "CREATE VIEW result AS "
							+ "SELECT item_id, item_name, age_restriction, item_rating, item_plink, wiki_country, type, genre "
							+ "FROM Item "
							+ "WHERE CAST(item_id AS varchar(20)) LIKE '%" + inputText + "%' OR item_name LIKE '%" + inputText + "%'";
				}
				queries[2] = "SELECT * FROM result";
				return SendQuery(con, queries, 9);
				//Item Type
			case "Book":
			case "Film":
			case "Audio":
			case "Video Game":
			case "TV Series":
				queries = new String[3];
				queries[0] = "DROP VIEW result";
				if (inputText.trim().equals("")) {
					queries[1] = "CREATE VIEW result AS "
							+ "SELECT item_id, item_name, age_restriction, item_rating, item_plink, wiki_country, type, genre "
							+ "FROM Item "
							+ "WHERE type = '" + filter + "' ";

				} else {
					queries[1] = "CREATE VIEW result AS "
							+ "SELECT item_id, item_name, age_restriction, item_rating, item_plink, wiki_country, type, genre "
							+ "FROM Item "
							+ "WHERE type = '" + filter + "' AND item_id IN "
							+ "(SELECT item_id FROM Item WHERE CAST(item_id AS varchar(20)) LIKE '%" + inputText + "%' OR item_name LIKE '%" + inputText + "%')";
				}
				queries[2] = "SELECT * FROM result";
				return SendQuery(con, queries, 9);
				//Genre
			case "Comedy":
			case "Horror":
			case "Romance":
			case "Adventure":
			case "Action":
				queries = new String[3];
				queries[0] = "DROP VIEW result";
				if (inputText.trim() == "") {
					queries[1] = "CREATE VIEW result AS "
							+ "SELECT item_id, item_name, age_restriction, item_rating, item_plink, wiki_country, type, genre "
							+ "FROM Item "
							+ "WHERE genre = '" + filter + "' ";

				} else {
					queries[1] = "CREATE VIEW result AS "
							+ "SELECT item_id, item_name, age_restriction, item_rating, item_plink, wiki_country, type, genre "
							+ "FROM Item WHERE genre = '" + filter + "' AND item_id IN "
							+ "(SELECT item_id FROM Item WHERE CAST(item_id AS varchar(20)) LIKE '%" + inputText + "%' OR item_name LIKE '%" + inputText +"%')";
				}
				queries[2] = "SELECT * FROM result";
				return SendQuery(con, queries, 8);
				//Age Restriction
			case "5 And Under":
			case "13 And Under":
			case "17 And Under":
				queries = new String[3];
				// Grab the number from the string (either 5, 13 or 17)
				filter = filter.replaceAll("\\D+","");
				System.out.println(filter);
				queries[0] = "DROP VIEW result";
				if (inputText.trim().equals("")) {
					queries[1] = "CREATE VIEW result AS "
							+ "SELECT item_id, item_name, age_restriction, item_rating, item_plink, wiki_country, type, genre "
							+ "FROM Item "
							+ "WHERE age_restriction <= " + filter + " ";

				} else {
					queries[1] = "CREATE VIEW result AS "
							+ "SELECT item_id, item_name, age_restriction, item_rating, item_plink, wiki_country, type, genre "
							+ "FROM Item WHERE age_restriction <= " + filter + " AND item_id IN (SELECT item_id FROM Item "
							+ "WHERE CAST(item_id AS varcahr(20)) LIKE '%" + inputText + "%' OR item_name LIKE '%" + inputText + "%')";
				}
				queries[2] = "SELECT * FROM result";
				return SendQuery(con, queries, 8);
				//Item Rating
			case "1 Star":
			case "2 Stars":
			case "3 Stars":
			case "4 Stars":
			case "5 Stars":
				queries = new String[3];
				filter = filter.substring(0, 1);
				queries[0] = "DROP VIEW result";
				if (inputText.trim().equals("")) {
					queries[1] = "CREATE VIEW result AS "
							+ "SELECT item_id, item_name, age_restriction, item_rating, item_plink, wiki_country, type, genre "
							+ "FROM Item WHERE item_rating >= " + filter;
				} else {
					queries[1] = "CREATE VIEW result AS "
							+ "SELECT item_id, item_name, age_restriction, item_rating, item_plink, wiki_country, type, genre "
							+ "FROM Item WHERE item_rating >= " + filter + " AND item_id IN "
							+ "(SELECT item_id FROM Item "
							+ " WHERE CAST(item_id AS varchar(20)) LIKE '%" + inputText + "%' OR item_name LIKE '%" + inputText + "%')";

				}
				queries[2] = "Select * FROM result";
				return SendQuery(con, queries, 8);
				// Search for Reviews via Item
			case "Item":
				queries = new String[3];
				queries[0] = "DROP VIEW result";
				// These initially didn't have any joins, but they were added to support a cleaner looking results page.
				if (inputText.trim().equals("")) {
					queries[1] = "CREATE VIEW result AS SELECT r.review_id, r.review_text, r.reviewer_rating, r.rating_of_review, r.review_status, r.wiki_user_id, r.item_id, i.item_name, w.wiki_user_name "
							+ "FROM Review r, Item i, WikiUser w WHERE r.wiki_user_id = w.wiki_user_id AND r.item_id = i.item_id ORDER BY r.review_id";
				} else {
					queries[1] = "CREATE VIEW result AS SELECT r.review_id, r.review_text, r.reviewer_rating, r.rating_of_review, r.review_status, r.wiki_user_id, r.item_id, i.item_name, w.wiki_user_name"
							+ "FROM Review r, Item i, WikiUser w WHERE r.wiki_user_id = w.wiki_user_id AND r.item_id = i.item_id AND r.item_id IN "
							+ "(SELECT item_id FROM Item WHERE CAST(item_id AS varchar(20)) LIKE '%" + inputText + "%' OR item_name LIKE '%" + inputText + "%') ORDER BY r.review_id";
				}
				queries[2] = "SELECT * FROM result";
				return SendQuery(con, queries, 10);
				// Search for Reviews via User
			case "User":
				queries = new String[3];
				queries[0] = "DROP VIEW result";
				if (inputText.trim().equals("")) {
					queries[1] = "CREATE VIEW result AS SELECT r.review_id, r.review_text, r.reviewer_rating, r.rating_of_review, r.review_status, r.wiki_user_id, r.item_id, i.item_name, w.wiki_user_name "
							+ "FROM Review r, Item i, WikiUser w WHERE r.wiki_user_id = w.wiki_user_id AND r.item_id = i.item_id ORDER BY r.review_id";
				} else {
					queries[1] = "CREATE VIEW result AS "
							+ "SELECT r.review_id, r.review_text, r.reviewer_rating, r.rating_of_review, r.review_status, r.wiki_user_id, r.item_id, i.item_name, w.wiki_user_name "
							+ "FROM Review r, Item i, WikiUser w WHERE r.wiki_user_id = w.wiki_user_id AND r.item_id = i.item_id AND CAST(r.wiki_user_id AS varchar(20)) LIKE '%" + inputText + "%' ORDER BY r.review_id";
				}
				queries[2] = "SELECT * FROM result";
				return SendQuery(con, queries, 8);
			default:
				return null;
			}
		}

		// Search for a user
		public String[][] userSearch(String userIDs){
			String[] queries = new String[1];
			queries[0] = "SELECT wiki_user_id, wiki_user_name, wiki_user_DOB, wiki_user_email, wiki_country FROM WikiUser WHERE wiki_user_id = '" + userIDs + "'";
			return SendQuery(con, queries, 6);
		}

		// Add a review
		public void addReview(String text, String rating, String userID, String itemID){
			System.out.println("I'm here");
			String[] queries = new String[3];
			queries[0] = "INSERT INTO Review VALUES (0, '" + text + "', '" + rating + "', 0, 0, 0, 'Pending', '" + userID + "', '" + itemID + "')";
			queries[1] = "UPDATE Item SET num_ratings_of_item_rating = num_ratings_of_item_rating + 1, total_item_rating = total_item_rating + " + rating + " WHERE item_id = '" + itemID + "'";
			queries[2] = "UPDATE Item SET item_rating = total_item_rating / num_ratings_of_item_rating WHERE item_id = '" + itemID + "'";
			SendQuery(con, queries, 0);
		}

		// Rate a review
		public void rateReview(String userID, String userRating, String reviewID){
			System.out.println("I'm here with " + userID);
			String[] queries = new String[3];
			queries[0] = "UPDATE Review SET num_rating_of_review = num_rating_of_review + 1, total_rating_of_review = total_rating_of_review + " + userRating + "WHERE review_id = '" + reviewID + "'";
			queries[1] = "UPDATE Review SET rating_of_review = total_rating_of_review / num_rating_of_review WHERE review_id = '" + reviewID + "'";
			queries[2] = "INSERT INTO Rates VALUES( '" + userID + "' ,'" + reviewID + "', '" + userRating + "')";
			SendQuery(con, queries, 0);
		}

		// Find average rating/item
		public String[][] AverageReview() {
			String[] queries = new String[1];
			queries[0] = "select item_id, count(review_id), AVG(reviewer_rating) from review group by item_id";
			return SendQuery(con, queries, 4);
		}
		public void setConnection(Connection con) {
			this.con = con;
		}
	}

	// This class contains the majority of the admin-only SQL calls
	public static class AdminQuery{
		private Connection con;
		private String AdminID;

		public void setCurrentUser(String ID) {
			this.AdminID = ID;

		}

		// Add a User
		public void addUser(String name, String DOB, String email, String hashedPassword, String country) throws NoSuchAlgorithmException{
			MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(hashedPassword.getBytes());
			BigInteger bigInt = new BigInteger(1,thedigest);
			String hashtext = bigInt.toString(16);
			// Now we need to zero pad it if you actually want the full 32 chars.
			while(hashtext.length() < 32 ){
				hashtext = "0"+hashtext;
			}
			String[] queries = new String[3];
			queries[0] = "INSERT INTO WikiUser VALUES (0, '"+ name + "', '" + DOB + "', '" + email + "', '" + hashtext + "', '" + country + "' )";
			queries[1] = "INSERT INTO Reviewer VALUES( 0, wikiuser_seq.CURRVAL)";
			queries[2] = "INSERT INTO Manages VALUES ( " + this.AdminID + ", wikiuser_seq.CURRVAL)";
			System.out.println(queries[0]);
			System.out.println(queries[1]);
			System.out.println(queries[2]);
			SendQuery(con, queries, 0);
		}

		// Add an Item
		public void addItem(String name, String ageRestriction, String link, String country, String type, String genre){
			String[] queries = new String[1];
			queries[0] = "INSERT INTO Item VALUES (0, '" +  name + "', " + ageRestriction + ", 0.0, 0, 0.0, '" + link + "', '" + country + "', '" + type + "', '" + genre + "')";
			System.out.println(queries[0]);
			SendQuery(con, queries, 0);
		}

		// Remove a User
		public void removeUser(String ID) {
			String [] queries = new String[1];
			queries[0] = "DELETE FROM WikiUser WHERE wiki_user_id = " + ID;
			System.out.println(queries[0]);
			SendQuery(con, queries, 0);
		}

		// Remove an Item
		public void removeItem(String ID) {
			String [] queries = new String[1];
			queries[0] = "DELETE FROM Item WHERE item_id = " + ID;
			SendQuery(con, queries, 0);
		}

		// Remove a Review
		public void removeReview(String ID) {
			String [] queries = new String[1];
			queries[0] = "DELETE FROM Review WHERE review_id = " + ID;
			SendQuery(con, queries, 0);
		}

		// Evaluate a Review
		public void evaluateReview(String ID, String status) {
			// TODO: Fix the dropdown
			String[] queries = new String[2];
			queries[0] = "UPDATE Review SET review_status ='" + status + "' WHERE review_id = " + ID;
			queries[1] = "INSERT INTO Evaluates VALUES (0 , " + ID + ")";
			SendQuery(con, queries, 0);
		}

		// TODO: Return the result and integrate into app, can search either by name or USERID
		public void findUser(String ID) {
			String[] queries = new String[3];
			queries[0] = "DROP VIEW result";
			queries[1] = "SELECT wiki_user_id, wiki_user_name, wiki_user_DOB, wiki_user_email, wiki_country "
					+ "FROM WikiUser "
					+ "WHERE CAST(user_id AS varchar(20)) LIKE '%"+ ID + "' OR user_name LIKE '%" + ID + "%') AND wiki_user_id IN "
					+ "(SELECT wiki_user_id FROM Reviewer)";
			queries[2] = "SELECT * FROM result";
			SendQuery(con, queries, 6);
		}

		public void setConnection(Connection con){
			this.con = con;
		}
	}

	// Main classd method that takes an Oracle con, an array of queries and the number of expected values.
	// It returns a 2D array containing the results of the SQL query (if there are any)
	private static String[][] SendQuery(Connection con, String[] query, int numValues) {
		try
		{
			// Initialize variables and prepare for SQL statement
			Statement stmt = con.createStatement();
			String[] values = new String[numValues];
			String[][] results = new String[20][];
			int iterationCounter = 0;
			// This loop ensures we execute each SQL query
			for (int x = 0; x < query.length; x++){
				ResultSet rs = stmt.executeQuery(query[x]);
				// If the query doesn't start with SELECT, we don't really need the results
				if (query[x].startsWith("DROP") || query[x].startsWith("CREATE") || query[x].startsWith("DELETE") || query[x].startsWith("INSERT") || query[x].startsWith("UPDATE")) {
					continue;
				}
				while(rs.next())
				{
					System.out.println(query[x]);
					// Go through each value in the current result set and store them.
					for (int i = 1; i < numValues; i++){
						if (rs.getObject(i) != null)
							values[i-1] = rs.getObject(i).toString();
						else
							values[i-1] = null;
					}
					results[iterationCounter] = values;
					values = new String[numValues];
					iterationCounter++;
				}

			}
			// The following portion is a workaround for being unable to dynamically declare a 2D Array's size (as far as I know)
			// We created a large array and are now counting the amount of empty indices.
			int nullCounter = 0;
			for (int j = 0; j < 20; j++){
				if (results[j] == null)
					nullCounter++;
			}
			// Now that we know the amount of empty slots in the aray, we can create the appropriate 2D array size
			String[][] parsedResults = new String[20-nullCounter][];
			for (int k = 0; k < parsedResults.length; k++){
				parsedResults[k] = results[k];
			}
			return parsedResults;
		}
		catch (SQLException ex)
		{
			System.out.println(ex);

		}
		return null;

	}

	// Establish a connection to our DB, as per 304 Tutorial.
	private static Connection connection() {

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
		return con;
	}
}

