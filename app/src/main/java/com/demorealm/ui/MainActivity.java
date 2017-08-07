package com.demorealm.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.demorealm.R;
import com.demorealm.adapter.BookAdapter;
import com.demorealm.adapter.StudentAdapter;
import com.demorealm.database.controller.BookController;
import com.demorealm.database.controller.StudentController;
import com.demorealm.database.model.MyBook;
import com.demorealm.database.model.MyStudent;
import com.demorealm.listener.EditNameListener;
import com.demorealm.listener.ItemStudentListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;

public class MainActivity extends AppCompatActivity implements RealmChangeListener, ItemStudentListener {

    @BindView(R.id.rv_book)
    RecyclerView rv_book;
    @BindView(R.id.rv_student)
    RecyclerView rv_student;

    private Realm mRealm;
    private BookAdapter bookAdapter;
    private StudentAdapter studentAdapter;
    private List<MyBook> books = new ArrayList<>();
    private List<MyStudent> students = new ArrayList<>();
    private StudentController studentController;
    private BookController bookController;
    private String student_name = "", edit_student_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRealm = Realm.getInstance(this);
        mRealm.addChangeListener(this);
        studentController = StudentController.getInstance(mRealm);
        bookController = BookController.getInstance(mRealm);

        books = bookController.getBooks();
        students = studentController.getStudents();
        setupRecycler();
        setupRecyclerSV();
    }

    @OnClick(R.id.fab_add_student)
    public void addStudent() {
        DialogFactory.createInputNameDialog(MainActivity.this, new EditNameListener() {
            @Override
            public void onInputName(String name) {
                student_name = name;
                mRealm.beginTransaction();
                MyStudent myStudent = studentController.createStudent();
                int id = studentController.incrementId();
                myStudent.setId(id);
                myStudent.setName(student_name);
                myStudent.setBooks(insertListBook_IntoStudent(id));
                mRealm.commitTransaction();

                student_name = "";
                Toast.makeText(getApplicationContext(), "Insert student successful", Toast.LENGTH_SHORT).show();
            }
        }).show();
    }

    @Override
    public void onChange() {
        studentAdapter.notifyDataSetChanged();
        bookAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemStudent(RealmList<MyBook> books) {
        String list_title = "";
        for (MyBook book : books) {
            list_title += book.getTitle() + "\n";
        }
        Toast.makeText(this, "List book of this student:\n " + list_title, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRemoveStudent(int id) {
        mRealm.beginTransaction();

        // Xóa thông tin sinh viên đó
        MyStudent student = studentController.getStudentById(id);
        if (student != null) student.removeFromRealm();

        // Xóa tất cả những cuốn sách của sinh viên đó
        List<MyBook> books = bookController.findBookByIdStudent(id);
        if (books != null && books.size() > 0) {
            for (int i = books.size() - 1; i >= 0; i--) {
                books.get(i).removeFromRealm();
            }
        }
        mRealm.commitTransaction();
        hideKeyBoard();
        Toast.makeText(this, "Remove student successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditStudent(MyStudent student) {
        final int id = student.getId();
        student_name = student.getName();

        DialogFactory.createInputNameDialog(MainActivity.this, new EditNameListener() {
            @Override
            public void onInputName(String name) {
                edit_student_name = name;
                mRealm.beginTransaction();
                MyStudent student = studentController.getStudentById(id);
                student.setName(name);

                List<MyBook> books = bookController.findBookByIdStudent(id);
                if (books != null && books.size() > 0) {
                    for (int i = books.size() - 1; i >= 0; i--) {
                        String title = books.get(i).getTitle();
                        books.get(i).setTitle(title.replace(student_name, edit_student_name));
                    }
                }
                mRealm.commitTransaction();
                Toast.makeText(getApplicationContext(), "Edit student successful", Toast.LENGTH_SHORT).show();
            }
        }).show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void setupRecycler() {
        rv_book.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_book.setLayoutManager(layoutManager);
        bookAdapter = new BookAdapter(books);
        rv_book.setAdapter(bookAdapter);
    }

    private void setupRecyclerSV() {
        rv_student.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_student.setLayoutManager(layoutManager);
        studentAdapter = new StudentAdapter(students, this);
        rv_student.setAdapter(studentAdapter);
    }

    private RealmList<MyBook> insertListBook_IntoStudent(int id_student) {
        int id;
        RealmList<MyBook> books = new RealmList<>();
        for (int i = 1; i <= 3; i++) {                 // giả sử mỗi sv ta tạo ngẫu nhiên 3 cuốn sách.
            MyBook book = bookController.createBook();
            id = bookController.incrementId();
            book.setId(id);
            book.setId_student(id_student);
            book.setTitle(student_name + ": Title Book Number " + id);
            books.add(book);
        }
        return books;
    }

    private void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
