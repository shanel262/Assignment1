
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
	JLabel label2 = new JLabel("Address: ");
	JTextField text0 = new JTextField(20);
	JTextField text1 = new JTextField(20);
	JTextField text2 = new JTextField(20);
	JButton previous = new JButton("Previous");
	JButton next = new JButton("Next");

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
		
		ResultSet rs = load(idRow);
		parseAndInsert(rs);
		length();
		
		JPanel p = new JPanel(new GridLayout(3, 3));
		p.add(label0);
		p.add(label1);
		p.add(label2);
		p.add(text0);
		p.add(text1);
		p.add(text2);
		p.add(previous);
		p.add(next);
		f.add(p);
		f.setVisible(true);
		f.pack();
	}
	
	public void length(){
		try{
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "dbpass10");
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT count(*) FROM web_members");
			if(rs.next()){
				System.out.println("LENGTH: " + rs.getInt(1));	
				maxLength = rs.getInt(1);
			}
		}
		catch(Exception e){
			print("Error: " + e);
		}
	}

	public ResultSet load(int idReq) {
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "dbpass10");
			Statement st = con.createStatement();
			rs = st.executeQuery("select * from web_members where id=" + idRow);
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
			name = name + " " + lastname;
			text0.setText(ID);
			text1.setText(name);
			text2.setText(email);
		} catch (Exception x) {
			print("Error: " + x);
		}
	}

	public void print(String print) {
		System.out.println(print);
	}
}
