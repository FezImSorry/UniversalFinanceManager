package ufm.universalfinancemanager.addeditreminder;

import android.support.annotation.Nullable;

import java.util.Date;

import javax.inject.Inject;

import ufm.universalfinancemanager.addedittransaction.AddEditTransactionContract;
import ufm.universalfinancemanager.db.TransactionDataSource;
import ufm.universalfinancemanager.db.TransactionRepository;
import ufm.universalfinancemanager.db.entity.Transaction;
import ufm.universalfinancemanager.support.Flow;
import ufm.universalfinancemanager.support.atomic.User;


/**
 * Created by Areeba on 2/17/2018.
 */

public class AddEditReminderPresenter implements AddEditReminderContract.Presenter{

    private User mUser;
    @Nullable
    private AddEditReminderContract.View mAddEditReminderview = null;
    @Override
    public void takeView(AddEditReminderContract.View view) {
        mAddEditReminderview = view;
    }

    @Override
    public void dropView() {
        mAddEditReminderview = null;
    }

    @Inject
    AddEditReminderPresenter(User user) {
        mUser = user;
    }
}

