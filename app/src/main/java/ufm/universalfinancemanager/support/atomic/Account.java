/* Author: Sean Hansen
* ID: 108841276
* Date Started: 10/14/17
* Date Complete: 11/27/17
* Peer Review:
*   Date:
*   Team Members:
* Contributing Team Members:
*/
package ufm.universalfinancemanager.support.atomic;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Date;

import ufm.universalfinancemanager.R;
import ufm.universalfinancemanager.db.entity.Transaction;
import ufm.universalfinancemanager.db.source.local.converter.AccountTypeConverter;
import ufm.universalfinancemanager.db.source.local.converter.DateConverter;
import ufm.universalfinancemanager.support.AccountType;
import ufm.universalfinancemanager.support.ListItem;
import ufm.universalfinancemanager.support.RowType;

public class Account implements Parcelable, Serializable, ListItem {

    @ColumnInfo(name = "account_name")
    private String name;

    @TypeConverters(AccountTypeConverter.class)
    @ColumnInfo(name = "account_type")
    private AccountType type;

    @ColumnInfo(name = "account_balance")
    private double balance;

    @TypeConverters(DateConverter.class)
    @ColumnInfo(name = "account_opendate")
    private Date OpeningDate;

    @ColumnInfo(name = "account_notes")
    private String notes;

    @Ignore
    private NumberFormat num_format;

    public Account(String name, AccountType type, double balance, Date OpeningDate, String notes) {
        this.name = name;
        this.type = type;
        this.balance = balance;
        this.OpeningDate = OpeningDate;
        this.notes = notes;
        num_format = NumberFormat.getCurrencyInstance();
    }

    public Account(String name, AccountType type, double balance, Date openingDate) {
        this.name = name;
        this.type = type;
        this.balance = balance;
        this.OpeningDate = openingDate;
        this.notes = "";
        num_format = NumberFormat.getCurrencyInstance();
    }

    /******************Getters**********************/
    public String getName() {return name;}
    public AccountType getType() {return type;}
    public double getBalance() {return balance;}
    public Date getOpeningDate() {return OpeningDate;}
    public String getNotes() {return notes;}

    /*****************Setters***********************/
    public void setName(String name) {this.name = name;}
    public void setType(AccountType type) {this.type = type;}
    public void setBalance(double balance) {this.balance = balance;}
    public void setOpeningDate(Date openingDate) {OpeningDate = openingDate;}
    public void setNotes(String notes) {this.notes = notes;}

    public void registerTransaction(Transaction t) {
        switch(type) {
            case CHECKING:
                switch(t.getFlow()) {
                    case OUTCOME:
                        balance -= t.getAmount();
                        break;
                    case INCOME:
                        balance += t.getAmount();
                        break;
                    case TRANSFER:
                        if(t.getFromAccount() == this)
                            balance -= t.getAmount();
                        else
                            balance += t.getAmount();
                }
                break;
            case CREDIT_CARD:
                switch(t.getFlow()) {
                    case OUTCOME:
                        balance += t.getAmount();
                        break;
                    case TRANSFER:
                        //Pay off credit card balance
                        balance -= t.getAmount();
                }
                break;
        }
    }

    public void unregisterTransaction(Transaction t) {
        switch(type) {
            case CHECKING:
                switch(t.getFlow()) {
                    case OUTCOME:
                        balance += t.getAmount();
                        break;
                    case INCOME:
                        balance -= t.getAmount();
                        break;
                    case TRANSFER:
                        if(t.getFromAccount() == this)
                            balance -= t.getAmount();
                        else
                            balance += t.getAmount();

                }
                break;
            case CREDIT_CARD:
                switch(t.getFlow()) {
                    case OUTCOME:
                        balance -= t.getAmount();
                        break;
                    case TRANSFER:
                        //Undo credit card payment
                        balance += t.getAmount();
                }
        }
    }

    public Account(Parcel in) {
        name = in.readString();
        type = AccountType.valueOf(in.readString());
        balance = in.readDouble();
        OpeningDate = new Date(in.readLong());
        notes = in.readString();

        num_format = NumberFormat.getCurrencyInstance();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(type.name());
        dest.writeDouble(this.balance);
        dest.writeLong(this.OpeningDate.getTime());
        dest.writeString(this.notes);
    }

    @Override
    public int getViewType() {
        return RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if(convertView == null)
            view = inflater.inflate(R.layout.net_worth_list_item, null);
        else
            view = convertView;

        //Instantiate all the textviews from the layout
        TextView accountName = view.findViewById(R.id.networth_account);
        TextView accountBalance = view.findViewById(R.id.account_balance);


        //Set the text of each textview based on its corresponding transaction attribute
        accountName.setText(this.name);
        if (this.getType() == AccountType.CREDIT_CARD) {
            accountBalance.setText(num_format.format(this.balance*(-1)));
        }
        else {
            accountBalance.setText(num_format.format(this.balance));
        }

        return view;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {
        public Account createFromParcel(Parcel p) {
            return new Account(p);
        }

        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    @Override
    public String toString() {
        return this.getName();
    }
}