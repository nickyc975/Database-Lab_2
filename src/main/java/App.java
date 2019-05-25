import java.sql.*;

public class App {
    public static void main(String[] args) {
        if (args.length < 4 || !args[0].equals("-q") || !args[2].equals("-p")) {
            System.out.println("Usage: java App -q <query_no> -p [params, ...]");
            System.exit(0);
        }   
        
        try {
            Class.forName("com.mysql.jdbc.Driver");

            // Added characterEncoding=utf-8 to fix encoding problems.
            Connection connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/COMPANY?user=root&password=123456&characterEncoding=utf-8");
            
            String queryString;
            Statement statement = connection.createStatement();
            switch(args[1]) {
                case "1":
                    queryString = "select ESSN from WORKS_ON where PNO = \"" + args[3] + "\";";
                    break;
                case "2":
                    queryString = "select ENAME " + 
                                  "from EMPLOYEE, WORKS_ON, PROJECT " + 
                                  "where EMPLOYEE.ESSN = WORKS_ON.ESSN and WORKS_ON.PNO = PROJECT.PNO and PNAME = \"" + args[3] + "\";";
                    break;
                case "3":
                    queryString = "select ENAME, ADDRESS " + 
                                  "from EMPLOYEE, DEPARTMENT " + 
                                  "where EMPLOYEE.DNO = DEPARTMENT.DNO and DNAME = \"" + args[3] + "\";";
                    break;
                case "4":
                    queryString = "select ENAME, ADDRESS " + 
                                  "from EMPLOYEE, DEPARTMENT " + 
                                  "where EMPLOYEE.DNO = DEPARTMENT.DNO and DNAME = \"" + args[3] + "\" and SALARY < " + args[4] + ";";
                    break;
                case "5":
                    queryString = "select ENAME " + 
                                  "from EMPLOYEE " + 
                                  "where ESSN not in (" + 
                                      "select ESSN " + 
                                      "from WORKS_ON " + 
                                      "where PNO = \"" + args[3] + "\"" +
                                  ");";
                    break;
                case "6":
                    queryString = "select ENAME, DNAME " +
                                  "from EMPLOYEE, DEPARTMENT " + 
                                  "where EMPLOYEE.SUPERSSN in (" + 
                                      "select ESSN " + 
                                      "from EMPLOYEE " + 
                                      "where EMPLOYEE.ENAME = \"" + args[3] + "\"" +
                                  ") and EMPLOYEE.DNO = DEPARTMENT.DNO;";
                    break;
                case "7":
                    queryString = "select W1.ESSN " +
                                  "from WORKS_ON W1, WORKS_ON W2 " +
                                  "where W1.PNO =  \"" + args[3] + "\" and W2.PNO =  \"" + args[4] + "\" and W1.ESSN = W2.ESSN;";
                    break;
                case "8":
                    queryString = "select DNAME " +
                                   "from DEPARTMENT, ( " +
                                        "select DNO " +
                                        "from EMPLOYEE " +
                                        "group by DNO having avg(SALARY) < " + args[3] + " " +
                                   ") E " +
                                   "where DEPARTMENT.DNO = E.DNO;";
                    break;
                case "9":
                    queryString = "select ENAME " +
                                  "from EMPLOYEE, ( " +
                                      "select ESSN " +
                                      "from WORKS_ON " +
                                      "group by ESSN having count(*) >= " + args[3] + " and sum(HOURS) <= " + args[4] + " " +
                                  ") W " +
                                  "where EMPLOYEE.ESSN = W.ESSN;";
                    break;
                default:
                    System.out.println("Unknown query no. " + args[1]);
                    return;
            }

            ResultSet result = statement.executeQuery(queryString);

            printResult(result);

            result.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printResult(ResultSet result) throws SQLException {
        // see if the query result is NULL, if so, print empty result to the console
        if (!result.next()) {
            System.out.println("Empty result");
            return;
        }
        result.previous(); // when check if result is NULL, the cursor had been move forward one unit

        ResultSetMetaData resultMetaData = result.getMetaData();
        // print table property name
        for (int i = 1; i <= resultMetaData.getColumnCount(); i++) {
            System.out.print(resultMetaData.getColumnName(i));
            System.out.print("\t");
        }

        System.out.println();

        while (result.next()) {
            for (int i = 1; i <= resultMetaData.getColumnCount(); i++) {
                System.out.print(result.getString(i));
                System.out.print("\t");
            }
            System.out.println();
        }
    }
}
