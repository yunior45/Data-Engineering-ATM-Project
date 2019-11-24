import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class atmMain {

	public static void main(String[] args) {
		Connection conn = null;
		Scanner keyboard = new Scanner(System.in);
		
		try {
			String uri = "jdbc:sqlite:data/ATM_Management.db";
			conn = DriverManager.getConnection(uri);
			//System.out.println("Connected!");
			
			String alterView = "CREATE VIEW IF NOT EXISTS BANK_TOTALS AS " +
							   "SELECT b.bank_name, " +
							   "a.bank_id, " +
							   "sum(a.balance) AS total " + 
							   "FROM account AS a " +
							   "INNER JOIN " +
							   "Bank AS b ON a.bank_id = b.bank_id " +
							   "GROUP BY b.bank_name;";
							   
							   
			Statement select = conn.createStatement();
			select.execute(alterView);
			
			String mainmenu = "\nWelcome to my ATM Project \n\n" +
							  " 1: Add new ATM\n 2: Add new Member\n 3: Bank balance totals\n " +
							  "4: ATM Transaction\n 5: Exit application \n\n" +
							  "Please make a selection: ";
			
			int usermenu;
			
			do {
				
			System.out.println(mainmenu);
			usermenu = keyboard.nextInt();
			
			try {
				if ( usermenu < 1 || usermenu > 5) {
					throw new Exception();
				}
			} catch (Exception e) {
				System.out.println("Please make a correct selection: ");
				usermenu = keyboard.nextInt();
			}
			
			
			switch(usermenu) {
			
			case 1:
				
				int bank_id;
				int atm_location;
				String location_name;
				int balance;
				int num_of_tran;
				
				String BeginTrans1 = "BEGIN TRANSACTION; ";
				select.execute(BeginTrans1);
				
				System.out.println("please enter bank id: ");
				bank_id = keyboard.nextInt();
				System.out.println("please enter ATM Location: ");
				atm_location = keyboard.nextInt();
				System.out.println("please enter Location name: ");
				location_name = keyboard.nextLine();
							    keyboard.nextLine();
				System.out.println("please enter ATM balance: ");
				balance = keyboard.nextInt();
				System.out.println("please enter the number of transactions: ");
				num_of_tran = keyboard.nextInt();
				
				System.out.println("Are you sure you want to commit? (1 = Yes : 0 = No): ");
				int commit;
				commit = keyboard.nextInt();
				
				if(commit == 1) {
					
					String insertATM = "INSERT INTO ATM " + 
						       		   "(bank_id, atm_location, " +
						       		   "location_name, balance, num_of_tran) " + 
						       		   "VALUES (?,?,?,?,?)";					
					
					PreparedStatement insert = conn.prepareStatement(insertATM);
					insert.setInt(1, bank_id);
					insert.setInt(2, atm_location);
					insert.setString(3, location_name);
					insert.setInt(4, balance);
					insert.setInt(5, num_of_tran);
					insert.executeUpdate();
			
					String commitQuer = "COMMIT;";
					Statement CommQuer = conn.createStatement();
					CommQuer.execute(commitQuer);
						
				}
				else if(commit == 0) {
					String noQuery = "ROLLBACK;";
					Statement rollbackQuery = conn.createStatement();
					rollbackQuery.execute(noQuery);
				}				
			break;
				
			case 2:
				
				int acct_id;
				String fname;
				String lname;
				int ssn;
				int pnum;
				String email;
				String address;
				String birthdate;
				String acct_type;
				
				System.out.println("Please enter account id: ");
				acct_id = keyboard.nextInt();
				System.out.println("Please enter first name: ");
				fname = keyboard.next();
				System.out.println("Please enter last name: ");
				lname = keyboard.next();
				System.out.println("Please enter SSN: ");
				ssn = keyboard.nextInt();
				System.out.println("Please enter Phone Number: ");
				pnum = keyboard.nextInt();
				System.out.println("Please enter email address: ");
				email = keyboard.next();
				System.out.println("Please enter address: ");
				address = keyboard.nextLine();
						  keyboard.nextLine();
				System.out.println("Please enter birthdate: ");
				birthdate = keyboard.next();
				System.out.println("Please enter bank id: ");
				bank_id = keyboard.nextInt();
				System.out.println("Please enter account type: ");
				acct_type = keyboard.next();
				System.out.println("Please enter balance amount: ");
				balance = keyboard.nextInt();
				
				System.out.println("Are you sure you want to commit? (1 = Yes : 0 = No): ");
				commit = keyboard.nextInt();
				
				if(commit == 1) {
					
					String BeginUserTrans = "BEGIN TRANSACTION; ";
					select.execute(BeginUserTrans);
					
					String insertAccount = "INSERT INTO account " +
										   "(bank_id, acct_type, balance) " +
										   "VALUES (?, ?, ?);";
					
					PreparedStatement insertA = conn.prepareStatement(insertAccount);
					insertA.setInt(1, bank_id);
					insertA.setString(2, acct_type);
					insertA.setInt(3, balance);
					insertA.executeUpdate();
					
					String insertUser = "INSERT INTO Member " +
									    "(acct_id, mem_fname, mem_lname, " +
									    "ssn, phone, email, address, birthdate) " +
									    "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
					
					PreparedStatement insertU = conn.prepareStatement(insertUser);
					insertU.setInt(1, acct_id);
					insertU.setString(2, fname);
					insertU.setString(3, lname);
					insertU.setInt(4, ssn);
					insertU.setInt(5, pnum);
					insertU.setString(6, email);
					insertU.setString(7, address);
					insertU.setString(8, birthdate);
					insertU.executeUpdate();
			
					String commitUser = "COMMIT;";
					Statement CommUser = conn.createStatement();
					CommUser.execute(commitUser);
						
				}
				else if(commit == 0) {
					String noQueryUser = "ROLLBACK;";
					Statement rollbackQueryUser = conn.createStatement();
					rollbackQueryUser.execute(noQueryUser);
				}								
			break;
			
			case 3:
				
				String selectBanks = "select Bank_name, total from [BANK_TOTALS]";
				ResultSet rs = select.executeQuery(selectBanks);
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnsNumber = rsmd.getColumnCount();
				System.out.println();
				System.out.println("New bank balances: ");
				System.out.println();
				while (rs.next()) {
					for (int i = 1; i <= columnsNumber; i++) {
				        if (i > 1) System.out.print("= ");
				        String columnValue = rs.getString(i);
				        System.out.print(columnValue + " ");
				        if (i > 1) System.out.println("");
				    }
				}	
			break;
			
			case 4:
				int selection;
				int mem_ID = 0;
				int act_ID = 0;
				int trans_dec;
				int atm = 0;
				int withdrl;
				int depos;
				
				System.out.println("");
				System.out.println("Would you like to view account balance via:\n " +
									"(1) Member ID\n (2) Account ID\n\nPlease select: ");
				selection = keyboard.nextInt();
				
				if(selection == 1) {
					
					System.out.println("Please enter Member ID: ");
					mem_ID = keyboard.nextInt();
					
					String memAccount = "SELECT m.mem_fname AS [First Name], " +
										"m.mem_lname AS [Last Name], " +
										"a.balance " +
										"FROM member AS m " +
										"INNER JOIN " +
										"account AS a ON a.acct_id = m.acct_id " +
										"WHERE m.mem_id = '" + mem_ID + "';";
					
					Statement bal = conn.createStatement();
					ResultSet rs_bal = bal.executeQuery(memAccount);
					System.out.println("\n" + rs_bal.getString(1) + " " + rs_bal.getString(2) 
										+ " = " + "$" + rs_bal.getString(3));
					
					
				}
				else if(selection == 2) {
					
					System.out.println("Please enter Account ID: ");
					act_ID = keyboard.nextInt();
					
					String AccountId = "SELECT m.mem_fname AS [First Name], " +
										"m.mem_lname AS [Last Name], " +
										"a.balance " +
										"FROM member AS m " +
										"INNER JOIN " +
										"account AS a ON a.acct_id = m.acct_id " +
										"WHERE m.acct_id = '" + act_ID + "';";
					
					Statement bal_act = conn.createStatement();
					ResultSet rs_act = bal_act.executeQuery(AccountId);
					System.out.println("\n" + rs_act.getString(1) + " " + rs_act.getString(2) 
										+ " = " + "$" + rs_act.getString(3));
				}
				
				System.out.println("\nWould You like to withdrawal or deposit?\n\n" +
									"(1) Withdrawal\n(2) Deposit\n(3) Return to Main Menu\n\n");
				trans_dec = keyboard.nextInt();
				
				if(trans_dec == 1) {
					
					System.out.println("Please enter ATM ID: ");
					atm = keyboard.nextInt();
					System.out.println("Please enter Withdrawal amount: ");
					withdrl = keyboard.nextInt();
					
					if (mem_ID == 0) {
						System.out.println("Please enter member ID: ");
						mem_ID = keyboard.nextInt();
					}
					else if (act_ID == 0) {
						System.out.println("Please enter Account ID: ");
						act_ID = keyboard.nextInt();
						
					}
					
					System.out.println("Are you sure you want to commit? (1 = Yes : 0 = No): ");
					commit = keyboard.nextInt();
					
					if(commit == 1) {
						
						String BeginWithdrl = "BEGIN TRANSACTION; ";
						select.execute(BeginWithdrl);
						
						
						String ATM_Trans = "INSERT INTO ATM_transaction ( " +
								   		   "atm_id,  mem_id, tran_amount) " +
								   		   "VALUES (?,?,?);";

					
						PreparedStatement insertW = conn.prepareStatement(ATM_Trans);
						insertW.setInt(1, atm);
						insertW.setInt(2, mem_ID);
						insertW.setInt(3, withdrl);
						insertW.executeUpdate();
						
					String act_withdrwl = "UPDATE account " +
										  "SET balance = balance - '" + withdrl + "' " +
										  "WHERE acct_id = '" + act_ID + "';";
					
					PreparedStatement Update1 = conn.prepareStatement(act_withdrwl);
					Update1.executeUpdate();
											
					String ATM_update = "UPDATE ATM " +
										"SET balance = balance - '" + withdrl + "'" +
										"WHERE atm_id = '" + atm + "'";
					
					PreparedStatement UpdateAtm = conn.prepareStatement(ATM_update);
					UpdateAtm.executeUpdate();
					
					String commitUser1 = "COMMIT;";
					Statement CommUser1 = conn.createStatement();
					CommUser1.execute(commitUser1);
					
					}
					else if (commit == 0) {
						String noQuery1 = "ROLLBACK;";
						Statement rollbackQuery1 = conn.createStatement();
						rollbackQuery1.execute(noQuery1);
					}
					
					
				}
				else if (trans_dec == 2) {
					
					System.out.println("Please enter ATM ID: ");
					atm = keyboard.nextInt();
					System.out.println("Please enter Deposit amount: ");
					depos = keyboard.nextInt();
					
					if (mem_ID == 0) {
						System.out.println("Please enter member ID: ");
						mem_ID = keyboard.nextInt();
					}
					else if (act_ID == 0) {
						System.out.println("Please enter Account ID: ");
						act_ID = keyboard.nextInt();
						
					}
					
					System.out.println("Are you sure you want to commit? (1 = Yes : 0 = No): ");
					commit = keyboard.nextInt();
					
					if(commit == 1) {
						
					String BeginDeposit = "BEGIN TRANSACTION; ";
					select.execute(BeginDeposit);
						
					String ATM_Trans1 = "INSERT INTO ATM_transaction ( " +
						   		   		"atm_id,  mem_id, tran_amount) " +
						   		   		"VALUES (?,?,?);";

			
					PreparedStatement insertD = conn.prepareStatement(ATM_Trans1);
					insertD.setInt(1, atm);
					insertD.setInt(2, mem_ID);
					insertD.setInt(3, depos);
					insertD.executeUpdate();
						
					String act_deps = "UPDATE account " +
										  "SET balance = balance + '" + depos + "' " +
										  "WHERE acct_id = '" + act_ID + "';";
					
					PreparedStatement Update2 = conn.prepareStatement(act_deps);
					Update2.executeUpdate();
											
					String ATM_update1 = "UPDATE ATM " +
										"SET balance = balance + '" + depos + "'" +
										"WHERE atm_id = '" + atm + "'";
					
					PreparedStatement UpdateAtm1 = conn.prepareStatement(ATM_update1);
					UpdateAtm1.executeUpdate();
					
					String commitUser2 = "COMMIT;";
					Statement CommUser2 = conn.createStatement();
					CommUser2.execute(commitUser2);
					
					}
					else if (commit == 0) {
						String noQuery2 = "ROLLBACK;";
						Statement rollbackQuery2 = conn.createStatement();
						rollbackQuery2.execute(noQuery2);
					}
				}
					
					String updatedAccountId = "SELECT m.mem_fname AS [First Name], " +
											  "m.mem_lname AS [Last Name], " +
											  "a.balance " +
											  "FROM member AS m " +
											  "INNER JOIN " +
											  "account AS a ON a.acct_id = m.acct_id " +
											  "WHERE m.acct_id = '" + act_ID + "';";
		
				Statement upbal_act = conn.createStatement();
				ResultSet uprs_act = upbal_act.executeQuery(updatedAccountId);
				System.out.println("\nUpdated Member Balance: " + uprs_act.getString(1) + " " + uprs_act.getString(2) 
									+ " = " + "$" + uprs_act.getString(3));
				
					String updatedBankBal = "select (select sum(balance) from account where bank_id " +
											"= (select bank_id from atm where atm_id = '" + atm + "')) + " +
											"(select sum(balance) from atm where atm_id = '" + atm + "') " +
											"from atm limit 1;";
					
					Statement UpdatedBal = conn.createStatement();
					ResultSet UpBal = UpdatedBal.executeQuery(updatedBankBal);
					System.out.println("\nUpdated Bank Balance: " + "$" + UpBal.getString(1));
			break;			
			}
			
		} while (usermenu != 5 );
			
			conn.close();
			keyboard.close();
			System.exit(0);
			
		}
		catch(SQLException e) {
			System.out.println("Standard Failure: " + e.getMessage());
		}
		finally {
			try {
				if (conn != null) {
					conn.close();
				}
			}
			catch (SQLException extra) {
				System.out.println("Yeah, super failure here: " + extra.getMessage());
			}
		}
	}
}
