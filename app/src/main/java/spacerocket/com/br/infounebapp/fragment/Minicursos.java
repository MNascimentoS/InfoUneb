package spacerocket.com.br.infounebapp.fragment;


import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import spacerocket.com.br.infounebapp.R;
import spacerocket.com.br.infounebapp.adapter.WorkshopAdapter;
import spacerocket.com.br.infounebapp.model.Workshop;


/**
 * A simple {@link Fragment} subclass.
 */
public class Minicursos extends Fragment {

    private static final String TAG = "RecyclerViewFragment";
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;

    protected RecyclerView recyclerView;
    protected WorkshopAdapter adapter;
    protected List<Workshop> workshopsList = new ArrayList<>();
    protected RecyclerView.LayoutManager mLayoutManager;

    //progressDialog
    private ProgressDialog mProgressDialog;

    public Minicursos() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(getActivity());//set firebase context
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_minicursos, container, false);
        v.setTag(TAG);
        //inicialize recycle view
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ///inicialize adapter
        adapter = new WorkshopAdapter(getActivity(), workshopsList);
        //set the adapter to recycle view
        recyclerView.setAdapter(adapter);
        //progresss dialog
        mProgressDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//seta o spinner circle
        mProgressDialog.setMessage("Carregando...");
        mProgressDialog.setIndeterminate(true);
        /*load the workshop base*/
        getWorkshops();

        if(workshopsList.isEmpty()){
            mProgressDialog.dismiss();

        }

        return v;
    }

    /*Get firebase base reference*/
    public void getWorkshops()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Workshop");
        //mantenha atualizado
        //mDatabase.keepSynced(true);
        //storage = FirebaseStorage.getInstance();
        mDatabase.addValueEventListener(workListener);
    }
    /*listening the changes*/
    ValueEventListener workListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mProgressDialog.dismiss();
           readWorkshops(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(getActivity(), "Verique sua conexão com a internet!", Toast.LENGTH_SHORT).show();
        }
    };

    /*read each  data on base*/
    void readWorkshops(DataSnapshot dataSnapshot)
    {
        for(DataSnapshot dss : dataSnapshot.getChildren()){
            Workshop workshop = dss.getValue(Workshop.class);
            workshopsList.add(workshop);
        }
        /*notify the changes to adapter*/
        adapter.notifyDataSetChanged();
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
}
