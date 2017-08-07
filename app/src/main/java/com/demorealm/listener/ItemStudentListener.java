package com.demorealm.listener;

import com.demorealm.database.model.MyBook;
import com.demorealm.database.model.MyStudent;

import io.realm.RealmList;

/**
 * Created by HP on 07/08/2017.
 */

public interface ItemStudentListener {
    public void onItemStudent(RealmList<MyBook> books);
    public void onRemoveStudent(int id);
    public void onEditStudent(MyStudent student);
}
