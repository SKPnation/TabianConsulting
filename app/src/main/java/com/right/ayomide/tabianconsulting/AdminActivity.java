package com.right.ayomide.tabianconsulting;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.right.ayomide.tabianconsulting.Common.Common;
import com.right.ayomide.tabianconsulting.Interface.ItemClickListener;
import com.right.ayomide.tabianconsulting.models.User;
import com.right.ayomide.tabianconsulting.utility.EmployeeViewHolder;
import com.right.ayomide.tabianconsulting.utility.EmployeesAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AdminActivity extends AppCompatActivity {

    private static final String TAG = "AdminActivity";

    private ProgressDialog mProgressDialog;

    //widgets
    private TextView mDepartments;
    private Button mAddDepartment, mSendMessage;
    private RecyclerView mRecyclerView;
    private EditText mMessage, mTitle;

    //vars
    private List<String> mDepartmentsList;
    private Set<String> mSelectedDepartments;
    private EmployeesAdapter mEmployeeAdapter;
    private ArrayList<User> mUsers;
    private Set<String> mTokens;
    private String mServerKey;
    public static boolean isActivityRunning;

    FirebaseDatabase db;
    DatabaseReference User;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<User, EmployeeViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_admin );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        toolbar.setTitle( "Skiplab Innovation" );
        setSupportActionBar( toolbar );

        db = FirebaseDatabase.getInstance();
        User = db.getReference("users");

        mDepartments = (TextView) findViewById(R.id.broadcast_departments);
        mAddDepartment = (Button) findViewById(R.id.add_department);
        mSendMessage = (Button) findViewById(R.id.btn_send_message);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mMessage = (EditText) findViewById(R.id.input_message);
        mTitle = (EditText) findViewById(R.id.input_title);

        //Load employees
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if(Common.isConnectedToTheInternet(getBaseContext()))
        {
            loadEmployees();
        }
        else
        {
            Toast.makeText(getBaseContext(), "Please check your internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        mDepartmentsList = new ArrayList<>();

        //setupEmployeeList();
        init();
    }

    private void loadEmployees()
    {
        adapter = new FirebaseRecyclerAdapter<User, EmployeeViewHolder>(
                User.class, R.layout.layout_employee_list, EmployeeViewHolder.class, User) {
            @Override
            protected void populateViewHolder(EmployeeViewHolder viewHolder, User model, int position) {

                Log.d(TAG, "getEmployeeList: getting a list of all employees");
                viewHolder.name.setText( model.getName() );
                Picasso.with( AdminActivity.this ).load( model.getProfile_image() ).into( viewHolder.profileImage );
                viewHolder.department.setText( model.getDepartment() );

                viewHolder.setItemClickListener( new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Log.d(TAG, "onClick: selected employee: " + adapter.getItem( position ).getUser_id() + " " + adapter.getItem( position ).getName());

                        //setDepartmentDialog(adapter.getRef( position ).getKey(), adapter.getItem( position ));
                    }
                } );
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter( adapter );
    }

    private void init()
    {
        mAddDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: opening dialog to add new department");
                NewDepartmentDialog dialog = new NewDepartmentDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.dialog_add_department));
            }
        });
    }


    /**
     * Get a list of all employees
     * @throws NullPointerException
     */

/*
    private void getEmployeeList()
    {
        Log.d(TAG, "getEmployeeList: getting a list of all employees");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.dbnode_users));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    Log.d(TAG, "onDataChange: found a user: " + user.getName());
                    mUsers.add(user);
                }
                mEmployeeAdapter.notifyDataSetChanged();
                getDepartmentTokens();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    */

    /**
     * Get all the tokens of the users who are in the selected departments
     */
    //private void getDepartmentTokens() {}

    /**
     * Setup the list of employees
     */


    /*
    private void setupEmployeeList()
    {
        //load deals list
        mUsers = new ArrayList<>();
        mEmployeeAdapter = new EmployeesAdapter( mUsers, AdminActivity.this );
        recyclerView.setHasFixedSize( true );
        layoutManager = new LinearLayoutManager( this );
        recyclerView.setLayoutManager( layoutManager );
        recyclerView.setAdapter(mEmployeeAdapter);
    }
*/




    @Override
    public void onStart() {
        super.onStart();
        isActivityRunning = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        isActivityRunning = false;
    }

/*
    public void setDepartmentDialog(final User user) {
        Log.d(TAG, "setDepartmentDialog: setting the department of: " + user.getName());

        AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
        builder.setIcon(R.drawable.ic_departments);
        builder.setTitle("Set a Department for " + user.getName() + ":");

        builder.setPositiveButton("done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //get the index of the department (if the user has a department assigned)
        int index = -1;
        for(int i = 0; i < mDepartmentsList.size(); i++){
            if(mDepartmentsList.contains(user.getDepartment())){
                index = i;
            }
        }

        final ListAdapter adapter = new ArrayAdapter<String>(AdminActivity.this,
                android.R.layout.simple_list_item_1, mDepartmentsList);
        builder.setSingleChoiceItems(adapter, index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child(getString(R.string.dbnode_users))
                        .child(user.getUser_id())
                        .child(getString(R.string.field_department))
                        .setValue(mDepartmentsList.get(which));
                dialog.dismiss();
                Toast.makeText(AdminActivity.this, "Department Saved", Toast.LENGTH_SHORT).show();
                //refresh the list with the new information
                mUsers.clear();
                //getEmployeeList();
            }
        });
        builder.show();
    }
*/
    /*
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(Common.DELETE))
        {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child(getString(R.string.dbnode_users))
                    .child(user.getUser_id())
                    .child(getString(R.string.field_department))
                    .removeValue();

            Log.d(TAG, "Delete: Context menu selected: ");
            //showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }

        return super.onContextItemSelected(item);
    }
     */

    /*public void deleteDepartment(User user)
    {
        Log.d(TAG, "Delete: Context menu selected: " + user.getName());

    }
     */
}
