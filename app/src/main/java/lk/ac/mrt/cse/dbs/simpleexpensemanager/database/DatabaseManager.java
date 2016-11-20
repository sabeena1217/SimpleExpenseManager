package lk.ac.mrt.cse.dbs.simpleexpensemanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {

    protected static final String db_name = "140325M";
    private static DatabaseManager databaseManager = null;
    private static final int db_version = 1;

    public static final String tblAccount = "Accounts";
    public static final String account_number = "accountNumber";
    public static final String bank_name = "bankName";
    public static final String account_holder_name = "accountHolderName";
    public static final String balance = "balance";

    public static final String tblTransaction = "transations";
    public static final String transaction_id = "transaction_id";
    public static final String date = "date";
    public static final String account_no = "accountNo";
    public static final String expense = "expense";
    public static final String amount = "amount";


    public DatabaseManager(Context context) {
        super(context, db_name, null, db_version);
    }

    public static DatabaseManager getInstance(Context context) {
        if (databaseManager == null)
            databaseManager = new DatabaseManager(context);
        return databaseManager;
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String tblAccount = String.format("CREATE TABLE %s(%s VARCHAR(20) NOT NULL PRIMARY KEY,%s VARCHAR(100) NULL,%s VARCHAR(100) NULL,%s DECIMAL(10,2) NULL )", "Accounts", account_number, bank_name, account_holder_name, balance);

        String tblTransaction = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,%s VARCHAR(100) NOT NULL,%s DATE NULL,%s DECIMAL(10,2) NULL,%s VARCHAR(100) NULL, FOREIGN KEY(%s) REFERENCES %s(%s))", "transactions", transaction_id, account_no, date, amount, expense, account_no, tblAccount, account_number);

        sqLiteDatabase.execSQL(tblAccount);
        sqLiteDatabase.execSQL(tblTransaction);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int j) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + tblAccount);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + tblTransaction);
        onCreate(sqLiteDatabase);

    }
}
