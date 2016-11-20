import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.db.DatabaseHelper;;


public class PersistentAccountDAO implements AccountDAO {

    private Context context;

    public PersistentAccountDAO(Context context) {
        this.context = context;
    }

    @Override
    public List<String> getAccountNumbersList() {

        //Open database connection
        DatabaseManager manager = DatabaseManager.getInstance(context);
        if( manager == null){
            System.out.print("Damn");
        }
        SQLiteDatabase db = manager.getReadableDatabase();

        //Query to select all account numbers from the account table
        String query = "SELECT "+ manager.account_number+" FROM " + manager.tblAccount+" ORDER BY " + manager.account_number + " ASC";

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<String> resultSet = new ArrayList<>();

        //Add account numbers to a list
        while (cursor.moveToNext())
        {
            resultSet.add(cursor.getString(cursor.getColumnIndex(manager.account_number)));
        }

        cursor.close();

        //Return the list of account numbers
        return resultSet;

    }

    @Override
    public List<Account> getAccountsList() {

        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase db = manager.getReadableDatabase();

        //Query to select all the details about all the accounts in the account table
        String query = "SELECT * FROM " + manager.tblAccount+" ORDER BY "+manager.account_number+" ASC";

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Account> resultSet = new ArrayList<>();

        //Add account details to a list
        while (cursor.moveToNext())
        {
            Account account = new Account(cursor.getString(cursor.getColumnIndex(manager.account_number)),
                    cursor.getString(cursor.getColumnIndex(manager.bank_name)),
                    cursor.getString(cursor.getColumnIndex(manager.account_holder_name)),
                    cursor.getDouble(cursor.getColumnIndex(manager.balance)));

            resultSet.add(account);
        }

        cursor.close();

        //Return list of account objects
        return resultSet;

    }

    @Override
    public Account getAccount(String account_no) throws InvalidAccountException {

        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase db = manager.getReadableDatabase();

        //Query to get details of the account specifiec by the account number
        String query = "SELECT * FROM " + manager.tblAccount + " WHERE " + manager.account_number + " =  '" + account_no + "'";

        Cursor cursor = db.rawQuery(query, null);

        Account account = null;

        //add the details to an account object
        if (cursor.moveToFirst()) {
            account = new Account(cursor.getString(cursor.getColumnIndex(manager.account_number)),
                    cursor.getString(cursor.getColumnIndex(manager.bank_name)),
                    cursor.getString(cursor.getColumnIndex(manager.account_holder_name)),
                    cursor.getDouble(cursor.getColumnIndex(manager.balance)));
        }
        //If account is not found throw an exception
        else {
            throw new InvalidAccountException("You have selected an invalid account number...!");
        }

        cursor.close();

        //Return the account object
        return account;
    }

    @Override
    public void addAccount(Account account) {

        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase db = manager.getWritableDatabase();

        //Save account details to the account table
        ContentValues values = new ContentValues();
        values.put(manager.account_no, account.getAccountNo());
        values.put(manager.bank_name, account.getBankName());
        values.put(manager.account_holder_name, account.getAccountHolderName());
        values.put(manager.balance, account.getBalance());

        db.insert(manager.tblAccount, null, values);

    }

    @Override
    public void removeAccount(String account_no) throws InvalidAccountException {

        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase db = manager.getWritableDatabase();
        //Query to delete a particular account from the account table
        String query = "SELECT * FROM " + manager.tblAccount + " WHERE " + manager.account_number + " =  '" + account_no + "'";

        Cursor cursor = db.rawQuery(query, null);

        Account account = null;

        //Delete the account if found in the table
        if (cursor.moveToFirst()) {
            account = new Account(cursor.getString(cursor.getColumnIndex(manager.account_number)),
                    cursor.getString(cursor.getColumnIndex(manager.bank_name)),
                    cursor.getString(cursor.getColumnIndex(manager.account_holder_name)),
                    cursor.getFloat(cursor.getColumnIndex(manager.balance)));
            db.delete(manager.tblAccount, manager.account_no + " = ?", new String[] { account_no });
            cursor.close();

        }
        //If account is not found throw an exception
        else {
            throw new InvalidAccountException("No such account found...!");
        }

    }

    @Override
    public void updateBalance(String account_no, ExpenseType expense, double amount) throws InvalidAccountException {

        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase db = manager.getWritableDatabase();

        ContentValues values = new ContentValues();

        //Retrieve the account details of the selected account
        Account account = getAccount(accountNo);

        //Update the balance if the account is found in the table
        if (account!=null) {

            double new_amount=0;

            //Deduct the amount is it is an expense
            if (expense.equals(ExpenseType.EXPENSE)) {
                new_amount = account.getBalance() - amount;
            }
            //Add the amount if it is an income
            else if (expense.equals(ExpenseType.INCOME)) {
                new_amount = account.getBalance() + amount;
            }

            //Query to update balance in the account table
            String strSQL = "UPDATE "+manager.tblAccount+" SET "+manager.balance+" = "+new_amount+" WHERE "+manager.account_number+" = '"+ account_no+"'";

            db.execSQL(strSQL);

        }
        //If account is not found throw an exception
        else {
            throw new InvalidAccountException("No such account found...!");
        }

    }
}