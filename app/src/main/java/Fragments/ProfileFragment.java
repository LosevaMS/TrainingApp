package Fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.globusproject.DBHelper;
import com.example.globusproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Tables.ApproachesTable;
import Tables.HistoryTable;
import Tables.ProgramTable;


public class ProfileFragment extends Fragment {

    private SQLiteDatabase database;
    private TextView firstDate, trainingNumber, approachesNumber, weightNumber;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.VISIBLE);

        DBHelper dbHelper = new DBHelper(requireContext());
        database = dbHelper.getWritableDatabase();

        firstDate = view.findViewById(R.id.first_date);
        trainingNumber = view.findViewById(R.id.finished_cnt);
        approachesNumber = view.findViewById(R.id.app_item_count);
        weightNumber = view.findViewById(R.id.weight_cnt);

        ImageView myGif = view.findViewById(R.id.gif);

        Glide
                .with(requireContext())
                .asGif()
                .load("https://s310iva.storage.yandex.net/rdisk/f5704367a2821ec336e224dec9512ddaeffccd24a4157ef2777ce9aebf53d26d/5ea95d5e/_Htr9cliFzR5ZZpYDYqzjqrZAb0ghJLBRimknqQRctoBEW4G1OGO6_RbXVSO8cnIJvc3d-VThrODsDo2wt-qPw==?uid=146564447&filename=check.gif&disposition=inline&hash=&limit=0&content_type=image%2Fgif&owner_uid=146564447&fsize=2530955&hid=2d0119730e06119ef2f4bd6d5bcf7da9&media_type=image&tknv=v2&etag=7ebafc58b1045f9e714e75037cb9d54c&rtoken=xaLykfMJQ6qk&force_default=yes&ycrid=na-755b27b7d57be567d5eb44b5b1ff49d9-downloader1e&ts=5a46bcceb9380&s=5ca3825cb63d97f6404c73e333971e1a8d92782ce5150744153f28f62c5fc010&pb=U2FsdGVkX1-om5n-GzNMhlc3Dp4xkWW0xbgOo7jEbBRfEsC5TFgeOFzGnhI6MiW3ZX5iN9HuL09dZX37b-il3gkLOJFVMuGpNqIFtlxE3Is")
                .error(R.drawable.delete)
                .into(myGif);

    }

}