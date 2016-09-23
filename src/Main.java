/**
 * @author Shane Lacey 20013687
 * @version 1.0.0
 * @date 23/9/16
 */
//mySQL JDBC GUI connection
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.awt.event.*;

class Retrieve {

	int idRow = 1; // The current record
	int maxLength = 0; // The highest Ssn number
	int minLength = 0; // The lowest Ssn number
	JFrame f = new JFrame();
	JLabel label0 = new JLabel("Name: ");
	JLabel label1 = new JLabel("Address: ");
	JLabel label2 = new JLabel("Salary: ");
	JLabel label3 = new JLabel("Sex: ");
	JLabel label4 = new JLabel("Date of Birth: ");
	JTextField text0 = new JTextField(80);
	JTextField text1 = new JTextField(160);
	JTextField text2 = new JTextField(20);
	JTextField text3 = new JTextField(20);
	JTextField text4 = new JTextField(20);
	JButton previous = new JButton("Previous");
	JButton next = new JButton("Next");
	JButton exit = new JButton("Exit");
	JButton add = new JButton("Add");
	JButton delete = new JButton("Delete");
	Connection con;
	String url = "jdbc:mysql://localhost:3306/test"; //Change this to switch database
	private String user = "root"; // The username for the DB authentication
	private String pass = ""; // The password for the DB authentication

	public static void main(String[] args) {
		Retrieve main = new Retrieve();
	}

	public Retrieve() {
		run();
	}

	public void run() {
		try{
			con = DriverManager.getConnection(url, user, pass);	// Instantiating the connection object		
		}
		catch(Exception e){
			print("Error: " + e);
		}
		next.addActionListener(new ActionListener() { // Next button event listener
			@Override
			public void actionPerformed(ActionEvent e) {
				if(idRow < maxLength){ //Won't request next record if at the end
					idRow++;
					ResultSet rs = load(idRow);
					parseAndInsert(rs, true);
				}
			}
		});

		previous.addActionListener(new ActionListener() { // Previous button event listener
			@Override
			public void actionPerformed(ActionEvent e) {
				if(idRow > minLength){ // Won't request previous record if at the start
					idRow--;
					ResultSet rs = load(idRow);
					parseAndInsert(rs, false);
				}
			}
		});
		
		exit.addActionListener(new ActionListener() { // Exit button event listener
			@Override
			public void actionPerformed(ActionEvent e) {
				print("Exiting");
				try {
					con.close(); //Closes the DB connection
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				System.exit(0); //Exits the program
			}
		});
		
		add.addActionListener(new ActionListener() { // Add button event listener
			String name = "", address = "", salary = "", sex = "", dob = "";
			@Override
			public void actionPerformed(ActionEvent e) {
				if(previous.isEnabled()){ // When the add button is pressed, the next and previous buttons are disabled, this determines if this is the first or second click of 'Add'
					text0.setText(name);
					text1.setText(address);
					text2.setText(salary);
					text3.setText(sex);
					text4.setText(dob);
					maxLength = length();
					previous.setEnabled(false); // Next, previous and delete buttons are disabled when adding a new user
					next.setEnabled(false);
					delete.setEnabled(false);
					add.setText("Confirm"); // Changes the 'Add' button to 'Confirm'
				}
				else if(!text0.getText().isEmpty() && !text1.getText().isEmpty() && !text2.getText().isEmpty() && !text3.getText().isEmpty() && !text4.getText().isEmpty()){ // Checks all fields have values
					String query = "INSERT into Employee (Ssn, Name, Address, Salary, Sex, Bdate) values (" + (maxLength + 1) + ", \"" + text0.getText() + "\", \"" + text1.getText() + "\", \"" + text2.getText() + "\", \"" + text3.getText()+ "\", \"" + text4.getText()  + "\");";
					previous.setEnabled(true); // Enables the next, previous and delete buttons after clicking confirm to add a user.
					next.setEnabled(true);
					delete.setEnabled(true);
					add.setText("Add");
					try{
						Statement st = con.createStatement(); // Create a statement object
						int res = st.executeUpdate(query); // Execute the query and collect the response
						if(res == 1){ // Check the add was successful
							print("Add successful");
							JOptionPane.showMessageDialog(null, "User added");
						}
						else print("Add unsuccessful");
						maxLength = length(); // Updates the highest Ssn value
						idRow = maxLength; // Updates current row
						ResultSet rs = load(maxLength); // Get record for current row
						parseAndInsert(rs, true); // Parse and insert data for current row
					}
					catch(Exception x){
						maxLength = length();
						ResultSet rs = load(maxLength);
						parseAndInsert(rs, true);
						print("Error: " + x.getMessage());
						if(x.getMessage().contains("Incorrect date value")){ // If the date is entered incorrectly, throw this error
							JOptionPane.showMessageDialog(null, "Incorrect date value, please use the format yyyy-mm-dd");
						}
						else if(x.getMessage().contains("Incorrect decimal value")){ // If the salary is entered incorrectly, throw this error
							JOptionPane.showMessageDialog(null, "Incorrect decimal value, please enter a number for salary");
						}
					}
				}
				else{
					JOptionPane.showMessageDialog(null, "Please fill in all fields");
					previous.setEnabled(true); //Enable all buttons
					next.setEnabled(true);
					delete.setEnabled(true);
					add.setText("Add"); // Change name from 'Confirm' to 'Add'
					maxLength = length();
					idRow = maxLength;
					ResultSet rs = load(maxLength);
					parseAndInsert(rs, true);
					
				}
			}
		});
		
		delete.addActionListener(new ActionListener() { // Delete button event listener
			@Override
			public void actionPerformed(ActionEvent e) {
				String query = "DELETE FROM `Employee` WHERE Ssn=" + idRow;
				try{
					Statement st = con.createStatement(); // Create statement
					int res = st.executeUpdate(query); // Execute the query
					if(res == 1){ // Check response to confirm user deletion
						print("Delete successful");
						JOptionPane.showMessageDialog(null, "User deleted"); // Give confirmation message
					}
					else print("Delete unsuccessful");
					maxLength = length();
					idRow = maxLength;
					if(maxLength != 0){ // Check there are users still in db before requesting one
						ResultSet rs = load(maxLength);
						parseAndInsert(rs, false);						
					}
					else{ // else clear all fields
						text0.setText(""); text1.setText(""); text2.setText(""); text3.setText(""); text4.setText("");
					}
				}
				catch(Exception x){
					maxLength = length();
					ResultSet rs = load(maxLength);
					parseAndInsert(rs, false);
					print("Error: " + x);
				}
			}
		});

		maxLength = length();
		idRow = minLength; //Make first request the first user (lowest Ssn number)
		if(maxLength != 0){ // check users exist before requesting first one
			ResultSet rs = load(idRow);
			parseAndInsert(rs, true);			
		}
		
		// The following is creating the GUI panels and adding the labels, text boxes and buttons to them.
		f.setPreferredSize(new Dimension(700, 200));
		JPanel master = new JPanel(new GridLayout(1, 2));
		JPanel info = new JPanel(new GridLayout(5, 2));
		JPanel buttons = new JPanel(new GridLayout(5, 1));
		master.add(info);
		master.add(buttons);
		info.add(label0);
		info.add(text0); 
		info.add(label1);
		info.add(text1);
		info.add(label2);
		info.add(text2);
		info.add(label3);
		info.add(text3);
		info.add(label4);
		info.add(text4);
		buttons.add(previous);
		buttons.add(next);
		buttons.add(add);
		buttons.add(delete);
		buttons.add(exit);
		f.add(master);
		f.setVisible(true);
		f.pack();
	}
	
	public int length(){ // Retrieves the min and max Ssn number
		int length = 0;
		try{
			Statement maxSt = con.createStatement();
			Statement minSt = con.createStatement();
			ResultSet maxRs = maxSt.executeQuery("SELECT * FROM Employee ORDER BY Ssn DESC LIMIT 1"); // Requesting highest Ssn
			ResultSet minRs = minSt.executeQuery("SELECT * FROM Employee ORDER BY Ssn ASC LIMIT 1"); // Requesting lowest Ssn
			if(maxRs.next() && minRs.next()){ // If both exist then assign the values
				length = maxRs.getInt(1);
				minLength = minRs.getInt(1);
			}
		}
		catch(Exception e){
			print("Error: " + e);
		}
		return length; // returns the maxLength (the highest Ssn)
	}

	public ResultSet load(int idReq) { // Used to request a record
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Statement st = con.createStatement();
			rs = st.executeQuery("select * from Employee where Ssn=" + idReq); // Query to load the requested user using the Ssn
		} catch (Exception e) {
			print("Error: " + e);
		}
		return rs; // Returns the response
	}
	
	/*
	 * The following method is user to parse the response and insert the values, if they exist, in to the relevant fields.
	 * I use the boolean direction to allow the program to determine which way the user is scrolling if it encounters a Ssn that doesn't
	 * exist so that it can request the record after that.
	 * When a requested record doesn't exist it will return the Ssn as 0. In this case the program will determine if it can increment or decrement
	 * the Ssn and get a valid record based on the direction the user is scrolling.
	 */
	public void parseAndInsert(ResultSet rs, boolean direction) { // direction == true means right. direction == false means left
		try {
			String name = "", address = "", salary = "", sex = "", dob = "";
			int id = 0;
			if (rs.next()) { // If the respone exists retrieve the values
				id = rs.getInt("Ssn");
				name = rs.getString("Name");
				address = rs.getString("Address");
				salary = rs.getString("Salary");
				sex = rs.getString("Sex");
				dob = rs.getString("Bdate");
			}
			if(id == 0 && direction){ // If id is 0 and direction is 'Next'
				idRow = idRow++ <= maxLength ? idRow++ : idRow--; // If the id can be incremented then do, if not then decrement it
				rs = load(idRow);
				parseAndInsert(rs, direction);
			}
			else if(id == 0 && !direction){ // If id is 0 and direction is 'Previous'
				idRow = idRow-- > 0 ? idRow-- : idRow++; // If the id can be decremented then do, if not then increment it
				rs = load(idRow);
				parseAndInsert(rs, direction);
			}
			else{
				print(id + " " + name + " " + address + " " + salary + " " + sex + " " + dob);
				text0.setText(name); // If the records exist then fields will be filled
				text1.setText(address);
				text2.setText(salary);
				text3.setText(sex);
				text4.setText(dob);
			}
		} catch (Exception x) {
			print("Error: " + x);
		}
	}
	
	public void print(String print) { // Made this so I didn't have to type System.out.println() every time.
		System.out.println(print);
	}
}
