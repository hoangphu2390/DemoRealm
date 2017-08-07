package com.demorealm.database.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by HP on 07/08/2017.
 */

public class MyStudent extends RealmObject {

    @PrimaryKey
    private int id;

    @Required
    private String name;

    public RealmList<MyBook> getBooks() {
        return books;
    }

    public void setBooks(RealmList<MyBook> books) {
        this.books = books;
    }

    private RealmList<MyBook> books;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
