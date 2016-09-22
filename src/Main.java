/**
 * @author Shane Lacey 20013687
 * @version 1.0.0
 * @date 22/9/16
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
	String url = "jdbc:mysql://localhost:3306/test";
	private String user = "root";
	private String pass = "";

	public static void main(String[] args) {
		Retrieve main = new Retrieve();
	}

	public Retrieve() {
		run();
	}

	public void run() {
		try{
			con = DriverManager.getConnection(url, user, pass);			
		}
		catch(Exception e){
			print("Error: " + e);
		}
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
				try {
					con.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		
		add.addActionListener(new ActionListener() {
			String name = "", address = "", salary = "", sex = "", dob = "";
			@Override
			public void actionPerformed(ActionEvent e) {
				if(previous.isEnabled()){
					text0.setText(name);
					text1.setText(address);
					text2.setText(salary);
					text3.setText(sex);
					text4.setText(dob);
					maxLength = length();
					previous.setEnabled(false);
					next.setEnabled(false);
				}
				else if(!text0.getText().isEmpty() && !text1.getText().isEmpty() && !text2.getText().isEmpty() && !text3.getText().isEmpty() && !text4.getText().isEmpty()){
					String query = "INSERT into Employee (Ssn, Name, Address, Salary, Sex, Bdate) values (" + (maxLength + 1) + ", \"" + text0.getText() + "\", \"" + text1.getText() + "\", \"" + text2.getText() + "\", \"" + text3.getText()+ "\", \"" + text4.getText()  + "\");";
					previous.setEnabled(true);
					next.setEnabled(true);
					try{
						Statement st = con.createStatement();
						int res = st.executeUpdate(query);
						if(res == 1) print("Add successful");
						else print("Add unsuccessful");
						maxLength = length();
						idRow = maxLength;
						ResultSet rs = load(maxLength);
						parseAndInsert(rs, true);
					}
					catch(Exception x){
						maxLength = length();
						ResultSet rs = load(maxLength);
						parseAndInsert(rs, true);
						print("Error: " + x.getMessage());
						if(x.getMessage().contains("Incorrect date value")){
							JOptionPane.showMessageDialog(null, "Incorrect date value, please use the format yyyy-mm-dd");
						}
					}
				}
				else{
					JOptionPane.showMessageDialog(null, "Please fill in all fields");
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
				String query = "DELETE FROM `Employee` WHERE Ssn=" + idRow;
				try{
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
		print("maxlength: " + maxLength);
		if(maxLength != 0){
			ResultSet rs = load(idRow);
			parseAndInsert(rs, true);			
		}
		
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
	
	public int length(){
		int length = 0;
		try{
			Statement maxSt = con.createStatement();
			Statement minSt = con.createStatement();
			ResultSet maxRs = maxSt.executeQuery("SELECT * FROM Employee ORDER BY Ssn DESC LIMIT 1");
			ResultSet minRs = minSt.executeQuery("SELECT * FROM Employee ORDER BY Ssn ASC LIMIT 1");
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
			Statement st = con.createStatement();
			rs = st.executeQuery("select * from Employee where Ssn=" + idReq);
		} catch (Exception e) {
			print("Error: " + e);
		}
		return rs;
	}

	public void parseAndInsert(ResultSet rs, boolean direction) { // direction == true means right. direction == false means left
		try {
			String name = "", address = "", salary = "", sex = "", dob = "";
			int id = 0;
			if (rs.next()) {
				id = rs.getInt("Ssn");
				name = rs.getString("Name");
				address = rs.getString("Address");
				salary = rs.getString("Salary");
				sex = rs.getString("Sex");
				dob = rs.getString("Bdate");
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
				print(id + " " + name + " " + address + " " + salary + " " + sex + " " + dob);
				text0.setText(name);
				text1.setText(address);
				text2.setText(salary);
				text3.setText(sex);
				text4.setText(dob);
			}
		} catch (Exception x) {
			print("Error: " + x);
		}
	}
	
	public void print(String print) {
		System.out.println(print);
	}
}
