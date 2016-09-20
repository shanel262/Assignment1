
//mySQL JDBC GUI connection
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.awt.event.*;

class Retrieve {

	int idRow = 1;
	int maxLength = 0;
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
	JButton insert = new JButton("Insert");

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
				if(idRow < maxLength) idRow++;
				ResultSet rs = load(idRow);
				parseAndInsert(rs);
			}
		});

		previous.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(idRow > 1) idRow--;
				ResultSet rs = load(idRow);
				parseAndInsert(rs);
			}
		});
		
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				print("Exiting");
				System.exit(0);
			}
		});
		
		insert.addActionListener(new ActionListener() {
			String id = "", name = "", lastname = "", email = "";
			@Override
			public void actionPerformed(ActionEvent e) {
				if(previous.isEnabled()){
					text0.setText(id);
					text1.setText(name);
					text2.setText(lastname);
					text3.setText(email);
					maxLength = length();
//					text0.setVisible(false);
					text0.setEnabled(false);
					previous.setEnabled(false);
					next.setEnabled(false);
				}
				else{
					String query = "INSERT into web_members (id, name, lastname, email) values (" + (maxLength + 1) + ", \"" + text1.getText() + "\", \"" + text2.getText() + "\", \"" + text3.getText() + "\");";
					print(query);
//					text0.setVisible(true);
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
						parseAndInsert(rs);
					}
					catch(Exception x){
						maxLength = length();
						ResultSet rs = load(maxLength);
						parseAndInsert(rs);
						print("Error: " + x);
					}
				}
			}
		});

		ResultSet rs = load(idRow);
		parseAndInsert(rs);
		maxLength = length();
		
		JPanel p = new JPanel(new GridLayout(3, 4));
		p.add(label0);
		p.add(label1);
		p.add(label2);
		p.add(label3);
		p.add(text0);
		p.add(text1);
		p.add(text2);
		p.add(text3);
		p.add(previous);
		p.add(next);
		p.add(insert);
		p.add(exit);
		f.add(p);
		f.setVisible(true);
		f.pack();
	}
	
	public int length(){
		int length = 0;
		try{
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "dbpass10"); //Change password to empty
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT count(*) FROM web_members");
			if(rs.next()){
				length = rs.getInt(1);
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

	public void parseAndInsert(ResultSet rs) {
		try {
			String name = "", email = "", lastname = "";
			int id = 0;
			if (rs.next()) {
				id = rs.getInt("id");
				name = rs.getString("name");
				email = rs.getString("email");
				lastname = rs.getString("lastname");
			}
			String ID = Integer.toString(id);
			text0.setText(ID);
			text1.setText(name);
			text2.setText(lastname);
			text3.setText(email);
		} catch (Exception x) {
			print("Error: " + x);
		}
	}
	
	public void print(String print) {
		System.out.println(print);
	}
}
