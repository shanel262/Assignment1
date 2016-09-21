/**
 * @author Shane Lacey 20013687
 * @version 1.0.0
 * @date 21/9/16
 */
//mySQL JDBC GUI connection
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.awt.event.*;

class Retrieve {

	int idRow = 1;
	int maxLength = 0;
	int minLength = 0;
	JFrame f = new JFrame();
	JLabel label0 = new JLabel("ID: ");
	JLabel label1 = new JLabel("Name: ");
	JLabel label2 = new JLabel("Lastname: ");
	JLabel label3 = new JLabel("Email: ");
	JTextField text0 = new JTextField(20);
	JTextField text1 = new JTextField(20);
	JTextField text2 = new JTextField(20);
	JTextField text3 = new JTextField(20);
	JButton previous = new JButton("Previous");
	JButton next = new JButton("Next");
	JButton exit = new JButton("Exit");
	JButton add = new JButton("Add");
	JButton delete = new JButton("Delete");

	public static void main(String[] args) {
		Retrieve main = new Retrieve();
	}

	public Retrieve() {
		run();
	}

	public void run() {
		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(idRow < maxLength){
					idRow++;
					ResultSet rs = load(idRow);
					parseAndInsert(rs, true);
				}
			}
		});

		previous.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(idRow > minLength){
					idRow--;
					ResultSet rs = load(idRow);
					parseAndInsert(rs, false);
				}
			}
		});
		
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				print("Exiting");
				System.exit(0);
			}
		});
		
		add.addActionListener(new ActionListener() {
			String id = "", name = "", lastname = "", email = "";
			@Override
			public void actionPerformed(ActionEvent e) {
				if(previous.isEnabled()){
					text0.setText(id);
					text1.setText(name);
					text2.setText(lastname);
					text3.setText(email);
					maxLength = length();
					text0.setEnabled(false);
					previous.setEnabled(false);
					next.setEnabled(false);
				}
				else if(!text1.getText().isEmpty() && !text2.getText().isEmpty()){
					String query = "INSERT into web_members (id, name, lastname, email) values (" + (maxLength + 1) + ", \"" + text1.getText() + "\", \"" + text2.getText() + "\", \"" + text3.getText() + "\");";
					print(query);
					text0.setEnabled(true);
					previous.setEnabled(true);
					next.setEnabled(true);
					try{
						Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "dbpass10");
						Statement st = con.createStatement();
						int res = st.executeUpdate(query);
						if(res == 1) print("Update successful");
						else print("Update unsuccessful");
						maxLength = length();
						idRow = maxLength;
						ResultSet rs = load(maxLength);
						parseAndInsert(rs, true);
					}
					catch(Exception x){
						maxLength = length();
						ResultSet rs = load(maxLength);
						parseAndInsert(rs, true);
						print("Error: " + x);
					}
				}
				else{
					print("Please enter a full name");
					text0.setEnabled(true);
					previous.setEnabled(true);
					next.setEnabled(true);
					maxLength = length();
					idRow = maxLength;
					ResultSet rs = load(maxLength);
					parseAndInsert(rs, true);
					
				}
			}
		});
		
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String query = "DELETE FROM `web_members` WHERE id=" + idRow;
				try{
					Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "dbpass10");
					Statement st = con.createStatement();
					int res = st.executeUpdate(query);
					if(res == 1) print("Delete successful");
					else print("Delete unsuccessful");
					maxLength = length();
					idRow = maxLength;
					if(maxLength != 0){
						ResultSet rs = load(maxLength);
						parseAndInsert(rs, false);						
					}
					else{
						text0.setText(""); text1.setText(""); text2.setText(""); text3.setText("");
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
		print("maxlength: " + maxLength);
		if(maxLength != 0){
			ResultSet rs = load(idRow);
			parseAndInsert(rs, true);			
		}
		
		f.setPreferredSize(new Dimension(700, 200));
		JPanel master = new JPanel(new GridLayout(1, 2));
		JPanel info = new JPanel(new GridLayout(4, 2));
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
		buttons.add(previous);
		buttons.add(next);
		buttons.add(add);
		buttons.add(delete);
		buttons.add(exit);
		f.add(master);
		f.setVisible(true);
		f.pack();
	}
	
	public int length(){
		int length = 0;
		try{
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "dbpass10"); //Change password to empty
			Statement maxSt = con.createStatement();
			Statement minSt = con.createStatement();
//			ResultSet rs = st.executeQuery("SELECT count(*) FROM web_members"); //This doesn't work after deleting members that do not have the max id
			ResultSet maxRs = maxSt.executeQuery("SELECT * FROM web_members ORDER BY id DESC LIMIT 1");
			ResultSet minRs = minSt.executeQuery("SELECT * FROM web_members ORDER BY id ASC LIMIT 1");
			if(maxRs.next() && minRs.next()){
				length = maxRs.getInt(1);
				minLength = minRs.getInt(1);
				print("minLength: " + minLength);
			}
		}
		catch(Exception e){
			print("Error: " + e);
		}
		return length;
	}

	public ResultSet load(int idReq) {
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "dbpass10");
			Statement st = con.createStatement();
			rs = st.executeQuery("select * from web_members where id=" + idReq);
		} catch (Exception e) {
			print("Error: " + e);
		}
		return rs;
	}

	public void parseAndInsert(ResultSet rs, boolean direction) { // direction == true means right. direction == false means left
		try {
			String name = "", email = "", lastname = "";
			int id = 0;
			if (rs.next()) {
				id = rs.getInt("id");
				name = rs.getString("name");
				email = rs.getString("email");
				lastname = rs.getString("lastname");
			}
			if(id == 0 && direction){
				idRow = idRow++ <= maxLength ? idRow++ : idRow--;
				rs = load(idRow);
				parseAndInsert(rs, direction);
			}
			else if(id == 0 && !direction){
				idRow = idRow-- > 0 ? idRow-- : idRow++;
				rs = load(idRow);
				parseAndInsert(rs, direction);
			}
			else{
				print(id + " " + name + " " + lastname + " " + email);
				String ID = Integer.toString(id);
				text0.setText(ID);
				text1.setText(name);
				text2.setText(lastname);
				text3.setText(email);				
			}
		} catch (Exception x) {
			print("Error: " + x);
		}
	}
	
	public void print(String print) {
		System.out.println(print);
	}
}
