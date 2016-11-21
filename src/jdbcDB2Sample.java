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
		browser.addScriptContextListener(new ScriptContextAdapter() {
			@Override
			public void onScriptContextCreated(ScriptContextEvent event) {
				Browser browser = event.getBrowser();
				JSValue window = browser.executeJavaScriptAndReturnValue("window");
				MainView mv = new MainView();
				mv.setParentFrame(frame);
				mv.setParentBrowser(browser);
				mv.setConnection(con);
				String[] queries = new String[1];
				//queries[0] = "start /home/e/e3u9a/cs304/project-final/create_ewiki.sql";
				//	queries[1] = "start /home/e/e3u9a/cs304/project-final/insert_ewiki.sql";
				//SendQuery(con, queries, 1);
				window.asObject().setProperty("mainView", mv);

			}  
		});
		browser.loadURL("file://C:/Users/Spencer/Desktop/CS304/CPSC304Project/src/GUI/index.html");
	}

	public static class LoginView {
		private JFrame parent;
		private Browser browser;
		private Connection con;
		private boolean isAdmin;
		private String ID;
		public void load() {
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
					//TODO: Maybe set admin mode here?
					window.asObject().setProperty("mainView", mv);					
				}  
			});
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
			System.out.println("IS ADMIN MODE ON? " + isAdmin);
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
			//TODO: Hash password
			System.out.println("Attempting to log in");
			MessageDigest md;
			try {

				md = MessageDigest.getInstance("MD5");
				byte[] thedigest = md.digest(password.getBytes());
				BigInteger bigInt = new BigInteger(1,thedigest);
				String hashtext = bigInt.toString(16);
				// Now we need to zero pad it if you actually want the full 32 chars.
				while(hashtext.length() < 32 ){
					hashtext = "0"+hashtext;
				}
				String[] queries = new String[1];
				queries[0] = "SELECT wiki_user_email FROM WikiUser WHERE wiki_user_email = '" + username + "'";
				String[][] result = SendQuery(this.con, queries, 2);
				System.out.println("Just retrieved this email " + result[0][0]);
				// No result
				if (result[0][0] == null)
					return false;
				else{
					System.out.println("Trying to get password");
					queries[0] = "SELECT wiki_user_password FROM WikiUser WHERE wiki_user_email = '" + username + "'";
					result = SendQuery(this.con, queries, 2);
					System.out.println("Just retrieved this password " + result[0][0]);
					// Succesful email + password
					if (result[0][0].equals(hashtext)){
						System.out.println("Got in!");
						queries[0] = "SELECT wiki_user_email FROM Wikiuser w, Admin a WHERE (w.wiki_user_id = a.wiki_user_id AND w.wiki_user_email = '" + username + "')";
						System.out.println(queries[0]);
						result = SendQuery(this.con, queries, 2);
						if (result.length == 0){
							queries[0] = "SELECT wiki_user_id FROM Wikiuser WHERE wiki_user_email = '" + username +"'";
							result = SendQuery(this.con, queries, 2);
							setCurrentUser(result[0][0]);
							return true;
						}
						else {
							System.out.println("Admin mode should be on");
							queries[0] = "SELECT wiki_user_id FROM Wikiuser WHERE wiki_user_email = '" + username +"'";
							result = SendQuery(this.con, queries, 2);
							System.out.println("goodbye and user id is " + result[0][0]);
							setAdminMode(true);
							setCurrentUser(result[0][0]);
							return true;
						}

					}
					else
						return false;

				}
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
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
					AdminQuery aq = new AdminQuery();
					aq.setConnection(con);
					aq.setCurrentUser(ID);
					window.asObject().setProperty("adminView", av);
					window.asObject().setProperty("mainView", mv);
					window.asObject().setProperty("adminQuery", aq);
					window.asObject().setProperty("reviewView", rv);
					window.asObject().setProperty("itemView", iv);
					
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

			//connection();
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
		
		public void load(String filter, String inputText) {
			this.browser.loadURL("file://C:/Users/Spencer/Desktop/CS304/CPSC304Project/src/GUI/itemresult.html");
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
					window.asObject().setProperty("adminView", av);
					window.asObject().setProperty("mainView", mv);
					window.asObject().setProperty("loginView", lv);
					window.asObject().setProperty("result", result);
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
					window.asObject().setProperty("adminView", av);
					window.asObject().setProperty("mainView", mv);
					window.asObject().setProperty("loginView", lv);
					window.asObject().setProperty("result", result);
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


	public static class SearchQuery {

		private Connection con;
		public String[][] search(String filter, String inputText) {
			String[] queries;
			System.out.println("This is con " + con);
			System.out.println(filter + " and " + inputText);
			switch(filter) {
			case "All":
				queries = new String[3];
				queries[0] = "DROP VIEW result";
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
			case "5 And Under":
			case "13 And Under":
			case "17 And Under":
				queries = new String[3];
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
			case "Item":
				queries = new String[3];
				queries[0] = "DROP VIEW result";
				if (inputText.trim().equals("")) {
					queries[1] = "CREATE VIEW result AS SELECT review_id, review_text, reviewer_rating, rating_of_review, review_status, wiki_user_id, item_id "
							+ "FROM Review";
				} else {
					queries[1] = "CREATE VIEW result AS SELECT review_id, review_text, reviewer_rating, rating_of_review, review_status, wiki_user_id, item_id "
							+ "FROM Review WHERE item_id IN "
							+ "(SELECT item_id FROM Item WHERE CAST(item_id AS varchar(20)) LIKE '%" + inputText + "%' OR item_name LIKE '%" + inputText + "%')";
				}
				queries[2] = "SELECT * FROM result";
				return SendQuery(con, queries, 8);
			case "User":
				queries = new String[3];
				queries[0] = "DROP VIEW result";
				if (inputText.trim().equals("")) {
					queries[1] = "CREATE VIEW result AS SELECT review_id, review_text, reviewer_rating, rating_of_review, review_status, wiki_user_id, item_id "
							+ "FROM Review";
				} else {
					queries[1] = "CREATE VIEW result AS "
							+ "SELECT review_id, review_text, reviewer_rating, rating_of_review, review_status, wiki_user_id, item_id "
							+ "FROM Review WHERE CAST(wiki_user_id AS varchar(20)) LIKE '%" + inputText + "%' ";
				}
				queries[2] = "SELECT * FROM result";
				return SendQuery(con, queries, 8);
			default:
				return null;
			}
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
			String[] queries = new String[3];
			queries[0] = "SELECT * FROM result ORDER BY item_rating DESC;";
			SendQuery(con, queries, 10);
		}

		private void lowerRatedItems(){
			String[] queries = new String[3];
			queries[0] = "SELECT * FROM result ORDER BY item_rating;";
			SendQuery(con, queries, 10);
		}

		private void mostReviewedItems(){
			String[] queries = new String[3];
			queries[0] = "SELECT * FROM result ORDER BY number_of_ratings_of_item_rating;";
			SendQuery(con, queries, 10);
		}

		private void currentCountryAvailability(){
			String[] queries = new String[3];
			queries[0] = "SELECT * FROM result WHERE wiki_country = User.wiki_country;";
			SendQuery(con, queries, 10);
		}

		// TODO: check how many results these should return
		private void highestRatedReviews(){
			String[] queries = new String[3];
			queries[0] = "SELECT * FROM result ORDER BY rating_of_review DESC";
			SendQuery(con, queries, 10);
		}

		private void lowerRatedReviews(){
			String[] queries = new String[3];
			queries[0] = "SELECT * FROM result ORDER BY rating_of_review";
			SendQuery(con, queries, 10);
		}

		public void setConnection(Connection con) {
			this.con = con;
		}
	}

	public static class AdminQuery{
		private Connection con;
		private String AdminID;

		public void setCurrentUser(String ID) {
			this.AdminID = ID;
			
		}

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


		public void addItem(String name, String ageRestriction, String link, String country, String type, String genre){
			String[] queries = new String[1];
			queries[0] = "INSERT INTO Item VALUES (0, '" +  name + "', " + ageRestriction + ", 0.0, 0, 0.0, '" + link + "', '" + country + "', '" + type + "', '" + genre + "')";
			System.out.println(queries[0]);
			SendQuery(con, queries, 0);
		}

		public void removeUser(String ID) {
			String [] queries = new String[1];
			queries[0] = "DELETE FROM WikiUser WHERE wiki_user_id = " + ID;
			System.out.println(queries[0]);
			SendQuery(con, queries, 0);
		}

		public void removeItem(String ID) {
			String [] queries = new String[1];
			queries[0] = "DELETE FROM Item WHERE item_id = " + ID;
			SendQuery(con, queries, 0);
		}

		//TODO: Add this? Unsure where it goes
		public void removeReview(String ID) {
			String [] queries = new String[1];
			queries[0] = "DELETE FROM Review WHERE review_id = " + ID;
			SendQuery(con, queries, 0);
		}
		
		public void evaluateReview(String ID, String status) {
			String[] queries = new String[2];
			queries[0] = "UPDATE Review SET review_status ='" + status + "' WHERE review_id = " + ID;
			queries[1] = "INSERT INTO Evaluates VALUES (" + this.AdminID + ", " + ID + ")";
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

	private static String[][] SendQuery(Connection con, String[] query, int numValues) {
		try
		{
			Statement stmt = con.createStatement();

			String[] values = new String[numValues];
			String[][] results = new String[20][];
			int iterationCounter = 0;
			for (int x = 0; x < query.length; x++){
				ResultSet rs = stmt.executeQuery(query[x]);
				if (query[x].startsWith("DROP") || query[x].startsWith("CREATE") || query[x].startsWith("DELETE") || query[x].startsWith("INSERT")) {
					continue;
				}
				while(rs.next())
				{
					System.out.println(query[x]);
					for (int i = 1; i < numValues; i++){
						System.out.println(i);
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
			int nullCounter = 0;
			for (int j = 0; j < 20; j++){
				if (results[j] == null)
					nullCounter++;
			}
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

