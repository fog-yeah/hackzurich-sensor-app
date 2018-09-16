package org.streampipes.sensordemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.streampipes.sensordemo.MainActivity;
import org.streampipes.sensordemo.util.ApplicationContextProvider;


public abstract class BaseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayout(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        performFragmentLogic(view);
    }

    protected <T extends View> T getViewById(View view, int id, Class<T> viewClass) {
        return viewClass
                .cast(view.findViewById(id));
    }

    protected <T> T deserializeFromArgs(String key, Class<T> serializableClass) {
        return serializableClass.cast(getArguments()
                .getSerializable(key));
    }


    protected void setTitle(String title) {
        ((MainActivity) getActivity()).setTitle(title);
    }

    public MainActivity mainActivity() {
        return (MainActivity) getActivity();
    }

    protected abstract int getLayout();

    protected abstract void performFragmentLogic(View view);

}
