package com.example.kunrui.apcheck;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.kunrui.apcheck.MethodsClass.FileOpr;
import com.example.kunrui.apcheck.MethodsClass.LanguageChoose;

public class SideBar extends Fragment {
    private LanguageChoose languageChoose = new LanguageChoose();
    private FileOpr fileOpr = new FileOpr();
    public Button language;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        languageChoose.selectLanguage(fileOpr.read_status("LANGUAGE"), getResources());
        View view = inflater.inflate(R.layout.side_bar, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        language = getActivity().findViewById(R.id.language);
        language.setOnClickListener(new mylisten());
    }

    private class mylisten implements View.OnClickListener{
        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.language:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    LayoutInflater factory =  LayoutInflater.from(getContext());
                    View textEntryView = factory.inflate(R.layout.language_set, null);
                    textEntryView.setBackgroundColor(Color.argb(20, 255, 255, 240));
                    final Spinner spinner = textEntryView.findViewById(R.id.spinner_lg);
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                            getContext(), R.array.language_l,
                            android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                    builder.setTitle(getContext().getString(R.string.language));
                    builder.setView(textEntryView);
                    builder.setPositiveButton(getContext().getString(R.string.save), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println(spinner.getSelectedItem());
                            System.out.println(spinner.getSelectedItemId());
                            switch (String.valueOf(spinner.getSelectedItem())) {
                                case "中文":
                                    System.out.println("中文");
                                    fileOpr.write_status("LANGUAGE", "zh");
                                    break;
                                case "English":
                                    fileOpr.write_status("LANGUAGE", "en");
                                    System.out.println("English");
                                    break;
                                case "日本語":
                                    fileOpr.write_status("LANGUAGE", "jp");
                                    System.out.println("日本語");
                                    break;
                                case "Le français":
                                    fileOpr.write_status("LANGUAGE", "fr");
                                    System.out.println("Le français");
                                    break;
                            }
                            SideBar sideBar = new SideBar();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.left_fragment, sideBar).commit();

                            //重启APP
                            Intent intent = getActivity().getPackageManager()
                                    .getLaunchIntentForPackage( getActivity().getPackageName() );
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            getActivity().onBackPressed();
                            startActivity(intent);

//                            Fragment right = getFragmentManager().findFragmentById(R.id.right_fragment);
//                            FragmentManager fm= getActivity().getSupportFragmentManager();
//                            Fragment fragment = fm.findFragmentById(R.id.left_fragment);
//                            if ( fragment == null ) {
//                                fragment = new SideBar();
//                                fm.beginTransaction().add(R.id.left_fragment, fragment).commit();
//                            }
                        }
                    });
                    builder.setNegativeButton(getContext().getString(R.string.cancel), null);
                    builder.show();
                    break;
            }
        }
    }
}