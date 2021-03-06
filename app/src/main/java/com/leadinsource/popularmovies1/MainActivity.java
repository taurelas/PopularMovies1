package com.leadinsource.popularmovies1;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.leadinsource.popularmovies1.databinding.ActivityMainBinding;
import com.leadinsource.popularmovies1.model.Movie;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainActivityViewModel viewModel;
    private MenuItem sortSwitch;
    private Menu menu;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        viewModel = ViewModelProviders.of(this, new MainActivityVMF(getResources())).get(MainActivityViewModel.class);

        viewModel.getMoviesData().observe(this, data -> {
            if(binding.recyclerView.getAdapter()== null) {
                binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

                adapter = new RecyclerViewAdapter(data, movie -> {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra(DetailActivity.EXTRA_MOVIE,movie);
                    startActivity(intent);
                });

                binding.recyclerView.setAdapter(adapter);
            } else {
                adapter.updateData(data);
            }
        });

    }

    /**
     * Starts observing sortOrder data, must be called after menu has been created
     */
    private void observeSortOrder() {
        viewModel.getSortOrder().observe(this, s -> {
           sortSwitch.setTitle(s);
           onPrepareOptionsMenu(menu);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        this.menu = menu;
        sortSwitch = menu.findItem(R.id.sort_switch);

        observeSortOrder();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item == sortSwitch) {
            viewModel.switchSorting();
        }

        return super.onOptionsItemSelected(item);
    }
}
