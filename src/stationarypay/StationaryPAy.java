/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stationarypay;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stegger
 */
public class StationaryPAy
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException
    {
        SQLServerDataSource dataSource = new SQLServerDataSource();
        dataSource.setServerName("127.0.0.1");
        dataSource.setDatabaseName("StationaryPay");
        dataSource.setUser("peter");
        dataSource.setPassword("peter");

        int fromAccountId = 100;
        int toAccountId = 102;
        double amountToTransfer = 500;

        try (Connection con = dataSource.getConnection())
        {
            try
            {
                con.setAutoCommit(false);
                String sqlUpdateAccount
                        = "UPDATE Account SET Balance=Balance + ? WHERE ID=?";

                PreparedStatement ps = con.prepareStatement(sqlUpdateAccount);
                ps.setDouble(1, amountToTransfer);
                ps.setInt(2, toAccountId);
                ps.executeUpdate();
                System.out.println("Executed deposit to " + toAccountId);

                Savepoint sp1 = con.setSavepoint();
                
                //throw new SQLException("I'm messing with you");
                ps.setDouble(1, -amountToTransfer);
                ps.setInt(2, fromAccountId);
                ps.executeUpdate();
                System.out.println("Executed withdrawl from " + fromAccountId);
                
                con.commit();
                System.out.println("Commit!");
            } catch (SQLException ex)
            {
                con.rollback();
                System.out.println("Rollback due to: " + ex.getMessage());
            }
        }

    }

}
