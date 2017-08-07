package com.demorealm.database.controller;

import com.demorealm.database.model.MyBook;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by HP on 07/08/2017.
 */

public class BookController {

    private static BookController instance;
    private static Realm realm;

    public static BookController getInstance(Realm rm) {
        if (instance == null) {
            instance = new BookController();
            realm = rm;
        }
        return instance;
    }

    //clear all objects from Book.class
    public void clearAll() {
        realm.beginTransaction();
        realm.clear(MyBook.class);
        realm.commitTransaction();
    }

    public MyBook createBook() {
        return realm.createObject(MyBook.class);
    }

    public int incrementId() {
        int id = 0;
        try {
            id = realm.where(MyBook.class).max("id").intValue() + 1;
        } catch (Exception ex) {
            id = 0;
        }
        return id;
    }

    public RealmResults<MyBook> getBooks() {
        return realm.where(MyBook.class).findAll();
    }

    public MyBook getBook(int id) {
        return realm.where(MyBook.class).equalTo("id", id).findFirst();
    }

    public boolean hasBooks() {
        return !realm.allObjects(MyBook.class).isEmpty();
    }

    public RealmResults<MyBook> findBookByIdStudent(int id_student) {
        return realm.where(MyBook.class)
                .equalTo("id_student", id_student)
                .findAll();
    }
}
