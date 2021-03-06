package ufm.universalfinancemanager.addeditaccount;


import android.support.annotation.Nullable;

import java.util.Date;

import javax.inject.Inject;

import ufm.universalfinancemanager.db.UserRepository;
import ufm.universalfinancemanager.support.AccountType;
import ufm.universalfinancemanager.db.entity.Account;
import ufm.universalfinancemanager.support.atomic.User;

/**
 * Created by smh7 on 1/23/18.
 */

public class AddEditAccountPresenter implements AddEditAccountContract.Presenter {

    private User mUser;
    private String mAccountName;
    private UserRepository mUserRepository;

    @Nullable
    private AddEditAccountContract.View mAddEditAccountView = null;

    @Inject
    AddEditAccountPresenter(User user, UserRepository userRepository, @Nullable String accountName) {
        mUser = user;
        mUserRepository = userRepository;
        mAccountName = accountName;
    }

    @Override
    public void saveAccount(String accountName, double balance, AccountType type) {
        if(isEditing()) {
            //Just save the name, don't want them to adjust the balance or type
            //It's possible but would be a lot of work to accomodate imo
            mUser.editAccountName(mAccountName, accountName);
            mUserRepository.updateTransactionAccounts(mAccountName, accountName);

            if(mAddEditAccountView != null)
                mAddEditAccountView.showLastActivity(true);
        }else {
            //Save new account
            try {
                mUser.addAccount(new Account(accountName, type, balance, new Date()));

                if(mAddEditAccountView != null)
                    mAddEditAccountView.showMessage("Account successfully saved.");
            }catch(RuntimeException e) {
                if(mAddEditAccountView != null)
                    mAddEditAccountView.showMessage("Error saving account, Account with that name already exists!");
                return;
            }

            if(mAddEditAccountView != null)
                mAddEditAccountView.showLastActivity(true);
        }
    }

    @Override
    public void deleteAccount() {
        Account account = mUser.getAccount(mAccountName);
        //user will take care of transaction deletion
        mUserRepository.deleteTransactionsByAccount(account.getName());
        mUser.deleteAccount(account);

        if(mAddEditAccountView != null)
            mAddEditAccountView.showLastActivity(true);
    }

    @Override
    public void takeView(AddEditAccountContract.View v) {
        if(v == null)
            return;

        mAddEditAccountView = v;

        if(isEditing()) {
            mAddEditAccountView.populateExistingAccountInfo(mAccountName,
                    mUser.getAccount(mAccountName).getBalance(),
                    mUser.getAccount(mAccountName).getType());
        }
    }

    @Override
    public void dropView() {
        mAddEditAccountView = null;
    }

    private boolean isEditing() {
        return mAccountName != null;
    }
}
