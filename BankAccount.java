import java.util.*;
import java.sql.*;
import java.util.regex.*;
import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

class BankAccount {
	Connection conn;
	Statement stmt;
	String cname,accno;
	
	public void setconnection()
	{
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = null;
			conn = DriverManager.getConnection("jdbc:mysql://localhost/bankdb","root1", "root1");
			stmt=conn.createStatement();
			System.out.println("Database is connected !");
		}
		catch(Exception e)
		{
			System.out.print("Do not connect to DB - Error:"+e);
		}

	}
	public int checkloginDetails(String uname, String password){
		
	    int count =0,check =0;
	    Pattern p1 = Pattern.compile("^[a-zA-Z0-9]+@[a-zA-Z0-9]+(\\.[a-z]+)+$");
		Matcher m1 = p1.matcher(uname);
	    if(m1.matches())
	        count =1;
	    Pattern p = Pattern.compile("^[a-zA-Z0-9]+$");
		Matcher m = p.matcher(uname);
	    if(m.matches())
			count =1;
		if(count ==1)
		{
			try {
					ResultSet r = stmt.executeQuery("SELECT mailid, username,password FROM bankregistration");
					while(r.next())
					{
						if( ( (uname.equals(r.getString(1))) || (uname.equals(r.getString(2))) ) && password.equals(r.getString(3)) )
						{
							check = 1;
							break;
						}
					}
				} 
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(count != 1 || check != 1)
		{
		    System.out.println("Invalid username or Password !!!");
		}
		return check;
	}
	public int checkvalidAccountNumber(String acccno, String bankname)
	{
		Pattern p = Pattern.compile("^[0-9]{16}$");
		Matcher m = p.matcher(acccno);
		int count =0,check =0;
		if(m.matches())
		{
			count =1;
		}

		if(count ==1)
		{
			try {
				ResultSet rs = stmt.executeQuery("SELECT accno , bankname FROM bankregistration");
				while(rs.next())
				{
					 if( (acccno.equals(rs.getString(1))) && (bankname.equals(rs.getString(2))))
					 {
						 check = 1;
						 break;
					 }	
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
			 
		}
		if(count != 1 || check != 1)
		{
		    System.out.println("Invalid Account Number or Choose correct Bank !!!");
		}
		else
		{
			System.out.println("Account Verified Successfully !--");
		}
		return check;
	}
	public void getCustomerDetails(String bankname)
	{
		Scanner sc = new Scanner(System.in);
		String acno ="";
		try {
			ResultSet rs = stmt.executeQuery("SELECT * FROM bankdb");
			while(rs.next())  
				acno = rs.getString(8);
		}
		catch (SQLException e1) {
			e1.printStackTrace();
		}
		int l = acno.length();
		String s = acno.substring(l-4);
		int number  = Integer.parseInt(s);
		number = number +1;
		acno = acno.substring(0,l-4) + Integer.toString(number);
		System.out.println("--------------------------------------");
		System.out.println("         **Customer Details**         ");
		System.out.println("--------------------------------------");
	    
	    int count = 1;
	    String CustomerName="";
	    String Phonenumber="";
	    String mailId = "";
	    String panno ="";
	    String aadharno= "";
	    String address = "";
		Pattern p;
		Matcher m;
		do {
			switch(count)
			{
				case 1:
					System.out.print("|  1  |  Full Name :: ");
				    CustomerName = sc.nextLine();
				    p = Pattern.compile("[a-z A-Z]+");
					m = p.matcher(CustomerName);
					if(m.matches())
					{
						count = 2;
					}
					else
					{
	    				System.out.println("Enter Valid Nmae(eg : Kausic M) !--");
					}
					break;
				case 2:
					System.out.print("|  2  |  Mobile Number :: ");
					Phonenumber = sc.next();
					p = Pattern.compile("[0-9]{10}");
					m = p.matcher(Phonenumber);
					if(m.matches())
					{
						count = 3;
					}
					else
					{
	    				System.out.println("Enter Valid Phonenumber(eg : 9894748270) !--");
					}
					break;
				case 3:
					System.out.print("|  3  |  E-mail Id :: ");
					mailId = sc.next();
					p = Pattern.compile("^[a-zA-Z0-9]+@[a-zA-Z0-9]+(\\.[a-z]+)+$");
					m = p.matcher(mailId);
					if(m.matches())
					{
						count =4;
					}
					else {
						System.out.println("Enter Valid Mail ID(ex : kausic@gmail.com) !--");
					}
					break;
				case 4:
					System.out.print("|  4  |  PAN Card Number :: ");
					panno = sc.next();
					p = Pattern.compile("^[A-Z]+[0-9]+[A-Z]+$");
					m = p.matcher(panno);
					if(m.matches())
					{
						count =5;
					}
					else {
						System.out.println("Enter Valid PAN Card Number(eg : EMPTR8978D)!--");
					}
					break;
				case 5:
					System.out.print("|  5  |  AADHAR Card Number :: ");
					aadharno = sc.next();
					p = Pattern.compile("[0-9]{12}");
					m = p.matcher(aadharno);
					if(m.matches())
					{
			    		count =6;
					}
					else {
						System.out.println("Enter Valid AADHAR Number(eg : 879645641342) !--");
			    	}
					break;
				case 6:
					System.out.print("|  6  |  Address :: ");
					sc.nextLine();
					address = sc.nextLine();
					count =0;
					break;
				}
			}while(count != 0);
		sendSms(acno, CustomerName, Phonenumber);
		System.out.println();
		System.out.println("------------------------------------------------------");
		System.out.println("|   Your Bank Account Number  |  "+acno+"  |");
		System.out.println("------------------------------------------------------");
		
	    try {
	    	String str = "INSERT INTO bankdb VALUES('"+bankname+"','"+CustomerName+"','"+Phonenumber+"','"+mailId+"','"+panno+"','"+aadharno+"','"+address+"','"+acno+"')";
			stmt.executeUpdate(str);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	    System.out.println();
	}
	public void sendSms(String acno, String uname, String phno) {
		try {
			String apiKey = "apikey=" + URLEncoder.encode("YjWCazAY7Cs-OGduOpBjRE1RKwwByiBJEaCZLiaBc7 ", "UTF-8");
			String message = "&message=" + URLEncoder.encode("Hey "+uname+", Your Accno is "+acno+"", "UTF-8");
			String numbers = "&numbers=" + URLEncoder.encode(phno, "UTF-8");
			
			String data = "https://api.textlocal.in/send/?" + apiKey + numbers + message; //+ sender;
			URL url = new URL(data);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			String sResult="";
			while ((line = rd.readLine()) != null) {
				sResult=sResult+line+" ";
			}
			rd.close();
			System.out.println(sResult);
			System.out.println("Accno sent successfully to your MobileNumber");
		} catch (Exception e) {
			System.out.println("Error SMS "+e);
			System.out.println("Oops Failed !!!!!!!!!!");
		}
	}
	public void getRegisterDetails(String bankname)
	{
		int count =0,check=0;
		Scanner sc = new Scanner(System.in);
		System.out.print("Customer Name :: ");
	    String customerName = sc.nextLine();
	    System.out.print("Account Number :: ");
	    String accno = sc.next();
	    Pattern p = Pattern.compile("^[0-9]{16}$");
	    Matcher m = p.matcher(accno);
	    if(m.matches())
	    	count++;
	    System.out.print("EmailId :: ");
	    String EmailId = sc.next();
	    p = Pattern.compile("^[a-zA-Z0-9]+@[a-zA-Z0-9]+(\\.[a-z]+)+$");
		m = p.matcher(EmailId);
		if(m.matches())
			count++;
		if(count ==2)
		{
			try {
				ResultSet rs = stmt.executeQuery("SELECT customername, mailid, accno,bankname FROM bankdb");
				while(rs.next())
					if(customerName.equals(rs.getString(1)) && EmailId.equals(rs.getString(2)) && accno.equals(rs.getString(3)) && bankname.equals(rs.getString(4)))
						check =1;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		if(check == 1)
		{
			System.out.print("username :: ");
			String uname = sc.next();
			System.out.print("Password :: ");
			String pwd = sc.next();
			System.out.print("Confirm Password :: ");
			String Repwd = sc.next();
			double amount = 0;
			if(pwd.contentEquals(Repwd))
			{
				try {
					String slr = "INSERT INTO bankregistration VALUES('"+bankname+"', '"+customerName+"','"+accno+"','"+EmailId+"','"+uname+"','"+pwd+"','"+Repwd+"', "+amount+")";
					stmt.executeUpdate(slr);
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println("Password must be same !---");
			}
		}
		if(check != 1 || count != 2)
		{
			System.out.println("Invalid Accno or Bankname or mailid or customername");
		}
	    System.out.println();
	}
	public void transferAmount(String curraccno)
	{
		int check =0,find =0, check2 =0;
		double camount = 0;
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter Amount :: ");
		double amount = sc.nextDouble();
		try {
			ResultSet rs = stmt.executeQuery("SELECT accbalance FROM bankregistration WHERE accno = '"+curraccno+"'");
			
			while(rs.next())
			{
				if((rs.getDouble(1)) >= amount)
				{
					camount = rs.getDouble(1);
					check = 1;
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		if(check == 1)
		{
			System.out.print("Enter Transfer Accno :: ");
			String taccno = sc.next();
			double initial=0;
			try {
				ResultSet rs = stmt.executeQuery("SELECT accbalance FROM bankregistration WHERE accno = '"+taccno+"'");
				
				while(rs.next())
				{
					find = 1;
					initial = rs.getDouble(1);
				}
				if(find ==1)
				{
					initial = initial + amount;
					double round = Math.round(initial*100.0);
					round = round/100.0;
					initial = round;
					String str = "UPDATE bankregistration SET accbalance = "+initial+" WHERE accno = '"+taccno+"' ";
					stmt.executeUpdate(str);
					check2 =1;
				}
				else {
					System.out.println("Invalid Account Number. Oops!!");
				}
				if(check2 ==1)
				{
					camount = camount-amount;
					double round =  Math.round(camount*100.0);
					round = round/100.0;
					camount = round;
					String str = "UPDATE bankregistration SET accbalance = "+camount+" WHERE accno = '"+curraccno+"' ";
					stmt.executeUpdate(str);
					System.out.println("--------------------------------------------------------");
					System.out.println("| Wohooo!! Rs."+amount +" Transfered Successfully !!   |");
					System.out.println("--------------------------------------------------------");
				}
				else
				{
					System.out.println("Transfer Failed.. Oops!!");
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Insufficient Balance !!!");
		}		
	}
	public void getwithdrawAmount(String curraccno)
	{
		int check =0;
		double camount =0;
		Scanner sc = new Scanner(System.in);
		System.out.println("---------------------------------------------------------------");
		System.out.println("| Supports Credit or Debit card || BHIM UPI ID || Net Banking |");
		System.out.println("---------------------------------------------------------------");
		System.out.println("You can Choose Your own Withdraw Method");
		System.out.print("Enter Withdraw Amount :: ");
		double withdraw = sc.nextDouble();
		try {
			ResultSet rs = stmt.executeQuery("SELECT accbalance FROM bankregistration WHERE accno = '"+curraccno+"'");
			
			while(rs.next())
			{
				camount = rs.getDouble(1);
				if((rs.getDouble(1)) >= withdraw)
				{
					check = 1;
				}
			}
			if(check ==1)
			{
				camount = camount-withdraw;
				double round =  Math.round(camount*100.0);
				round = round/100.0;
				camount = round;
				String str = "UPDATE bankregistration SET accbalance = "+camount+" WHERE accno = '"+curraccno+"' ";
				stmt.executeUpdate(str);
				System.out.println("--------------------------------------------------------");
				System.out.println("| Wohooo!! Rs."+withdraw +" Withdraw Successfully !!   |");
				System.out.println("--------------------------------------------------------");
			}
			else
			{
				System.out.println("Withdraw Failed.. Oops!!");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		if(check == 0)
		{
			System.out.println("Withdraw Failed!! Insufficient Balance");
		}
	}
	public void putDepositAmount(String curraccno)
	{
		int check =0;
		double camount =0;
		Scanner sc = new Scanner(System.in);
		System.out.println("---------------------------------------------------------------");
		System.out.println("| Supports Credit or Debit card || BHIM UPI ID || Net Banking |");
		System.out.println("---------------------------------------------------------------");
		System.out.println("You can Choose Your own Deposit Method");
		System.out.print("Enter Deposit Amount :: ");
		double deposit = sc.nextDouble();
		try {
			ResultSet rs = stmt.executeQuery("SELECT accbalance FROM bankregistration WHERE accno = '"+curraccno+"'");
			
			while(rs.next())
			{
				camount = rs.getDouble(1);
				if((rs.getDouble(1)) >= 0)
				{
					check = 1;
				}
			}
			if(check ==1)
			{
				camount = camount+deposit;
				double round =  Math.round(camount*100.0);
				round = round/100.0;
				camount = round;
				String str = "UPDATE bankregistration SET accbalance = "+camount+" WHERE accno = '"+curraccno+"' ";
				stmt.executeUpdate(str);
				System.out.println("--------------------------------------------------------");
				System.out.println("| Wohooo!! Rs."+deposit +" Deposited Successfully !!   |");
				System.out.println("--------------------------------------------------------");
			}
			else
			{
				System.out.println("Deposit Failed.. Oops!!");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void getCurrentBalance(String curraccno)
	{
		int check =0;
		try {
			ResultSet rs = stmt.executeQuery("SELECT accbalance FROM bankregistration WHERE accno = '"+curraccno+"'");
			
			while(rs.next())
			{
				System.out.println("----------------------------------------------------------");
				System.out.println("| Current Available Balance   | Rs.-> "+rs.getDouble(1)+"        |");
				System.out.println("----------------------------------------------------------");
				check =1;
			}
			if(check != 1)
			{
				System.out.println("Oops!! Account Not Available");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void removeAccount(String curraccno)
	{
		Scanner sc = new Scanner(System.in);
		double amount=0;
		int check =0;
		try {
			ResultSet rs = stmt.executeQuery("SELECT accbalance FROM bankregistration WHERE accno = '"+curraccno+"'");
			
			while(rs.next())
			{
				amount = rs.getDouble(1);
				System.out.println(" Current Available Balance ::  Rs.-> "+rs.getDouble(1));
			}
			if(amount > 1)
			{
				System.out.println(" If Balance is < Rs.1 then only Remove your Account");
				System.out.println(" Go and Withdraw Your Amount first !!");
			}
			else {
				System.out.print(" Are you sure to Remove your Account(Yes/No) :: ");
				String input = sc.next();
				if(input.equalsIgnoreCase("Yes"))
				{
					String str = "DELETE FROM bankregistration WHERE accno = '"+curraccno+"'";
					stmt.executeUpdate(str);
					String str1 = "DELETE FROM bankdb WHERE accno = '"+curraccno+"'";
					stmt.executeUpdate(str1);
					System.out.println("Account removed Successfully !!!!");
				}
				
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public void setconnectionClose()
	{
		try {
			conn.close();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
class BankTest
{	
	public static void main(String[] args)
	{
		Scanner sc = new Scanner(System.in);
		BankAccount b = new BankAccount();
		b.setconnection();
		int banknumber;
		System.out.println("*** WELCOME TO ONLINE BANK ACCOUNT MANAGEMENT ***");
		System.out.println();
		do{
			System.out.println("	-------------------------------");
			System.out.println("	|S.NO |        BANKNAME       |");
			System.out.println("	------------------------------");
		    System.out.println("	|  1  | Indian Overseas Bank  |");
		    System.out.println("	-------------------------------");
		    System.out.println("	|  2  |  State Bank Of India  |");
		    System.out.println("	-------------------------------");
		    System.out.println("	|  3  |     HDFC Bank         |");
		    System.out.println("	-------------------------------");
		    System.out.println("	|  4  |     ICICI Bank        |");
		    System.out.println("	-------------------------------");
		    System.out.println("	|  5  |     Canara Bank       |");
		    System.out.println("	-------------------------------");
		    System.out.println("	|  6  |     Axis Bank         |");
		    System.out.println("	-------------------------------");
		    System.out.println("	|  7  |    Indian Bank        |");
		    System.out.println("	-------------------------------");
		    System.out.println("	|  8  |       Exit            |");
		    System.out.println("	-------------------------------");
		    System.out.println();
		    System.out.print("Choose Bank Number : ");
		    banknumber = sc.nextInt();
		    System.out.println();
		    String bankname="";
		    switch(banknumber)
		    {
		        case 1:
		        case 2:
		        case 3:
		        case 4:
		        case 5:
		        case 6:
		        case 7:
		        	if(banknumber == 1)
		        		 bankname = "Indian Overseas Bank";
		        	else if(banknumber == 2)
		        		 bankname = "State Bank Of India";
		        	else if(banknumber == 3)
		        		 bankname = "HDFC Bank";
		        	else if(banknumber == 4)
		        		 bankname = "ICICI Bank";
		        	else if(banknumber == 5)
		        		 bankname = "Canara Bank";
		        	else if(banknumber == 6)
		        		 bankname = "Axis Bank";
		        	else
		        		 bankname = "Indian Bank";
		            int choiceno;
		            do{
		            	System.out.println("	-----------------------------");
		                System.out.println("	|  1  |        Login        |");
		                System.out.println("	-----------------------------");
		                System.out.println("	|  2  |       Register      |");
		                System.out.println("	-----------------------------");
		                System.out.println("	|  3  |       Go Back       |");
		                System.out.println("	-----------------------------");
		                System.out.println();
		                System.out.print("Enter Your Choice Number :: ");
		                choiceno  = sc.nextInt();
			                switch(choiceno)
			                {
			                    case 1:
			                    	System.out.print("Mailid / username :: ");
									String usname = sc.next();
									System.out.print("Password :: ");
			                    	String password = sc.next();
			                    	int checking1 = b.checkloginDetails(usname,password);
			                        System.out.println();
									int choice;
									int checking2 =0;
									String curraccno = "";
									if(checking1 == 1)
			                        do {
			                        	System.out.println("	-----------------------------");
				                        System.out.println("	| 1 | Verify Account Number |");
				                        System.out.println("	-----------------------------");
				                        System.out.println("	| 2 |   TransferAmount      |");
				                        System.out.println("	-----------------------------");
				                        System.out.println("	| 3 |      Withdraw         |");
				                        System.out.println("	-----------------------------");
				                        System.out.println("	| 4 |      Deposit          |");
				                        System.out.println("	-----------------------------");
				                        System.out.println("	| 5 |   Current Balance     |");
				                        System.out.println("	-----------------------------");
				                        System.out.println("	| 6 |    Remove Account     |");
				                        System.out.println("	-----------------------------");
				                        System.out.println("	| 7 |        Go Back        |");
				                        System.out.println("	-----------------------------");
				                        System.out.println();
				                        System.out.print("Enter your Choice Number :: "); 
										choice = sc.nextInt();
				                        switch(choice)
				                        {
				                            case 1:
				                            	System.out.print("Enter Accno :: ");
				                            	String acccno = sc.next();
				                            	curraccno = acccno;
				                            	checking2 = b.checkvalidAccountNumber(acccno, bankname);
					                            System.out.println();
					                            break;
											case 2:
												if(checking2 == 1)
												{
													b.transferAmount(curraccno);
												}
												else
												{
													System.out.println("First Verify your Account Number !!!");
													System.out.println();
												}
												break;
											case 3:
												if(checking2 == 1)
												{
													b.getwithdrawAmount(curraccno);
												}
												else
												{
													System.out.println("First Verify your Account Number !!!");
													System.out.println();
												}
					                            break;
											case 4:
												if(checking2 ==1)
												{
				                               		b.putDepositAmount(curraccno);
												}
												else
												{
													System.out.println("First Verify your Account Number !!!");
													System.out.println();
												}
				                               	break;
											case 5:
												if(checking2 ==1)
												{
				                               		b.getCurrentBalance(curraccno);
												}
												else
												{
													System.out.println("First Verify your Account Number !!!");
													System.out.println();
												}
				                            	break;
											case 6:
												if(checking2 ==1)
												{
				                               		b.removeAccount(curraccno);
												}
												else
												{
													System.out.println("First Verify your Account Number !!!");
													System.out.println();
												}
				                            	break;
				                            case 7:
				                            	System.out.println("--! Thank You !--");
				                            	System.out.println();
				                            	break;
			                            	default:
			                            		System.out.println("Enter Valid Choice");
			                            		System.out.println();
			                          	}
			                        }while(choice != 7);
			                        break;
			                    case 2:
			                        int check;
			                        do{
			                        	System.out.println();
			                        	System.out.println("	-----------------------------");
			                            System.out.println("	|  1  |  Create New Account |");
			                            System.out.println("	-----------------------------");
			                            System.out.println("	|  2  |   Register/SignIn   |");
			                            System.out.println("	-----------------------------");
			                            System.out.println("	|  3  |      Go Back        |");
			                            System.out.println("	-----------------------------");
			                            System.out.println();
			                            System.out.print("Enter Your Choice Number :: ");
			                            check = sc.nextInt();
			                            switch(check)
			                            {
			                              case 1:
			                                b.getCustomerDetails(bankname);
			                                break;
			                              case 2:
			                            	b.getRegisterDetails(bankname);
			                                break;
			                              case 3:
			                                System.out.println("--! Thank You for your Details ! --");
			                                System.out.println();
			                                break;
			                              default:
			                                System.out.println("Enter Valid Choice ");
			                                System.out.println();
			                                break;
			                            }
			        
			                        }while(check != 3);
			                        break;
			                    case 3:
			                        System.out.println(" -! Thank You !-");
			                        System.out.println();
			                        break;
			                    default:
			                        System.out.println("Enter Valid Choice");
			                        System.out.println();
			                        break;
			                }
			            }while(choiceno != 3);
			            break;
			        case 8:
			            System.out.println(" -!Thank You for Choosing our Bank !-");
			            System.out.println();
			            break;
			        default:
			            System.out.println("Enter Valid Choice");
			            System.out.println();
			            break;
			    }
			}while(banknumber!= 8);
			b.setconnectionClose();
	}
}
