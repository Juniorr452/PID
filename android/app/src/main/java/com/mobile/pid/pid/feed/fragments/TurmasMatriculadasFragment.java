package com.mobile.pid.pid.feed.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.pid.pid.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TurmasMatriculadasFragment extends Fragment
{
    // Turmas Matriculadas Código
    public TurmasMatriculadasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_turmas_matriculadas, container, false);
    }
}
