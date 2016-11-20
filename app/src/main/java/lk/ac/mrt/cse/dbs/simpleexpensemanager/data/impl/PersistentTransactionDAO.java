package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.database.DatabaseManager;


/**
 * Created by Lahiru Sandeepa on 12/7/2015.
 */
public class PersistentTransactionDAO implements TransactionDAO {

    private Context context;

    //Constructor
    public PersistentTransactionDAO(Context context) {
        this.context = context;
    }

    @Override
    public void logTransaction(Date date, String account_no, ExpenseType expense, double amount) {

        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase db = manager.getWritableDatabase();

        //Save transaction details to the transaction_log table
        ContentValues values = new ContentValues();
        values.put(manager.account_no, account_no);
        values.put(manager.date, convertDateToString(date));
        values.put(manager.amount, amount);
        values.put(manager.expense, expense.toString());

        db.insert(manager.tbTransaction,null,values);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return getPaginatedTransactionLogs(0);
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        DatabaseManager manager = DatabaseManager.getInstance(context);
        SQLiteDatabase db = manager.getWritableDatabase();

        //Query to get details of all the transactions
        String query = "SELECT "+ manager.account_no + ", " +
                manager.date + ", " +
                manager.expense+", " +
                manager.amount +
                " FROM " + manager.tblTransaction + " ORDER BY " + manager.transaction_id + " DESC";

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Transaction> transactionLogs = new ArrayList<>();

        //Add the transaction details to a list
        while (cursor.moveToNext())
        {
            try {

                ExpenseType expense = null;
                if (cursor.getString(cursor.getColumnIndex(manager.expense)).equals(ExpenseType.INCOME.toString())) {
                    expense = ExpenseType.INCOME;
                }
                else{
                    expense = ExpenseType.EXPENSE;
                }

                String dateString = cursor.getString(cursor.getColumnIndex(manager.date));
                Date date = convertStringToDate(dateString);

                Transaction tans = new Transaction(
                        date,
                        cursor.getString(cursor.getColumnIndex(manager.account_no)),
                        expense,
                        cursor.getDouble(cursor.getColumnIndex(manager.amount)));

                transactionLogs.add(tans);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        //Return the list of transactions
        return transactionLogs;
    }

    //Method to convert a date object to a string
    public static String convertDateToString(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = dateFormat.format(date);
        return dateString;

    }

    //Method to convert a string to a date object
    public static Date convertStringToDate(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date strDate = dateFormat.parse(date);
        return strDate;
    }
}