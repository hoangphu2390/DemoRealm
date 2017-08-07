package com.demorealm.database.controller;

import com.demorealm.database.model.MyStudent;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by HP on 07/08/2017.
 */

public class StudentController {

    private static StudentController instance;
    private static Realm realm;

    public static StudentController getInstance(Realm rm) {
        if (instance == null) {
            instance = new StudentController();
            realm = rm;
        }
        return instance;
    }

    //clear all objects from Book.class
    public void clearAll() {
        realm.beginTransaction();
        realm.clear(MyStudent.class);
        realm.commitTransaction();
    }

    public MyStudent createStudent() {
        return realm.createObject(MyStudent.class);
    }

    public int incrementId() {
        int id = 0;
        try {
            id = realm.where(MyStudent.class).max("id").intValue() + 1;
        } catch (Exception ex) {
            id = 0;
        }
        return id;
    }

    public RealmResults<MyStudent> getStudents() {
        return realm.where(MyStudent.class).findAll();
    }

    public MyStudent getStudentById(int id) {
        return realm.where(MyStudent.class).equalTo("id", id).findFirst();
    }

    public boolean hasStudents() {
        return !realm.allObjects(MyStudent.class).isEmpty();
    }

    public RealmResults<MyStudent> findStudentByName(String name) {
        return realm.where(MyStudent.class)
                .contains("name", name)
                .findAll();
    }
}
