/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bignerdranch.android.recyclerviewchoicemode.SelectableViewHolder;
import com.bignerdranch.android.recyclerviewchoicemode.SingleSelector;

import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivity;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.Distance;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.target.Target;

public class StandardRoundFragment extends NowListFragment<StandardRound> {

    private static final int NEW_STANDARD_ROUND = 1;
    private final SingleSelector mSingleSelector = new SingleSelector();
    private DrawerLayout mDrawerLayout;
    private ArrayList<StandardRound> list;
    final CheckBox[] clubs = new CheckBox[9];
    private RadioGroup location;
    private RadioGroup unit;
    private RadioGroup typ;
    private StandardRound currentSelection;

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        mEditable = false;
        mDrawerLayout = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        rootView.findViewById(R.id.right_drawer);
        mSingleSelector.setSelectable(true);
        currentSelection = (StandardRound) getArguments().getSerializable("item");
        list = DatabaseManager.getInstance(getActivity()).getStandardRounds();
        if (!list.contains(currentSelection)) {
            list.add(currentSelection);
        }
        initFilter();
        setHasOptionsMenu(true);
    }

    private void initFilter() {
        RadioGroup.OnCheckedChangeListener listener_radio_button = (group, checkedId) -> updateFilter();
        CompoundButton.OnCheckedChangeListener listener_checkbox = (buttonView, isChecked) -> updateFilter();
        location = (RadioGroup) rootView.findViewById(R.id.location);
        unit = (RadioGroup) rootView.findViewById(R.id.unit);
        typ = (RadioGroup) rootView.findViewById(R.id.round_typ);
        clubs[0] = (CheckBox) rootView.findViewById(R.id.asa);
        clubs[1] = (CheckBox) rootView.findViewById(R.id.aussie);
        clubs[2] = (CheckBox) rootView.findViewById(R.id.gnas);
        clubs[3] = (CheckBox) rootView.findViewById(R.id.ifaa);
        clubs[4] = (CheckBox) rootView.findViewById(R.id.nasp);
        clubs[5] = (CheckBox) rootView.findViewById(R.id.nfaa);
        clubs[6] = (CheckBox) rootView.findViewById(R.id.nfas);
        clubs[7] = (CheckBox) rootView.findViewById(R.id.wa);
        clubs[8] = (CheckBox) rootView.findViewById(R.id.custom);

        location.setOnCheckedChangeListener(listener_radio_button);
        unit.setOnCheckedChangeListener(listener_radio_button);
        typ.setOnCheckedChangeListener(listener_radio_button);
        for (CheckBox club : clubs) {
            club.setOnCheckedChangeListener(listener_checkbox);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        RadioButton outdoor = (RadioButton) rootView.findViewById(R.id.outdoor);
        RadioButton indoor = (RadioButton) rootView.findViewById(R.id.indoor);
        RadioButton metric = (RadioButton) rootView.findViewById(R.id.metric);
        RadioButton imperial = (RadioButton) rootView.findViewById(R.id.imperial);
        RadioButton target = (RadioButton) rootView.findViewById(R.id.target);
        RadioButton field = (RadioButton) rootView.findViewById(R.id.field);
        RadioButton three_d = (RadioButton) rootView.findViewById(R.id.three_d);
        if (prefs.getBoolean("filter_indoor", false)) {
            indoor.setChecked(true);
        } else {
            outdoor.setChecked(true);
        }
        if (prefs.getBoolean("filter_metric", prefs.getBoolean("pref_unit", true))) {
            metric.setChecked(true);
        } else {
            imperial.setChecked(true);
        }
        int round_typ = prefs.getInt("filter_typ", 0);
        if (round_typ == 0) {
            target.setChecked(true);
        } else if (round_typ == 1) {
            field.setChecked(true);
        } else if (round_typ == 2) {
            three_d.setChecked(true);
        }
        int filter = prefs.getInt("filter_club", 0x2FF);
        for (int i = 0; i < clubs.length; i++) {
            clubs[i].setChecked((1 << i & filter) != 0);
        }
        updateFilter();
    }

    private void updateFilter() {
        ArrayList<StandardRound> displayList = new ArrayList<>();
        int filter = 0;
        for (int i = 0; i < clubs.length; i++) {
            filter |= (clubs[i].isChecked() ? 1 : 0) << i;
        }
        boolean indoor = location.getCheckedRadioButtonId() == R.id.indoor;
        boolean isImperial = unit.getCheckedRadioButtonId() == R.id.imperial;
        String unit_distance = isImperial ? Distance.YARDS : Distance.METER;
        for (StandardRound r : list) {
            if (((r.institution & filter) != 0 ||
                    r.name.startsWith("NFAA/IFAA") && (filter & StandardRound.IFAA) != 0) &&
                    r.getRounds().get(0).distance.unit.equals(unit_distance) &&
                    r.indoor == indoor) {
                int checked = typ.getCheckedRadioButtonId();
                Target target = r.getRounds().get(0).target;
                if ((checked != R.id.field || target.isFieldTarget()) &&
                        (checked != R.id.three_d || target.is3DTarget())) {
                    displayList.add(r);
                }
            }
        }

        setList(displayList, new StandardRoundAdapter());
        int position = displayList.indexOf(currentSelection);
        if (position > -1) {
            mSingleSelector.setSelected(position, currentSelection.getId(), true);
            mRecyclerView.scrollToPosition(position);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_standard_round_selection;
    }

    @Override
    public void onClick(SelectableViewHolder holder, StandardRound mItem) {
        if (currentSelection.equals(mItem)) {
            super.onClick(holder, mItem);
        }
        int oldSelectedPosition = mSingleSelector.getSelectedPosition();
        currentSelection = mItem;
        int position = holder.getAdapterPosition();
        mSingleSelector.setSelected(position, currentSelection.getId(), true);
        mAdapter.notifyItemChanged(oldSelectedPosition);
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void onLongClick(SelectableViewHolder holder) {
        super.onClick(holder, (StandardRound) holder.getItem());
    }

    @Override
    protected void onEdit(StandardRound item) {
        Intent i = new Intent(getActivity(),
                SimpleFragmentActivity.EditStandardRoundActivity.class);
        i.putExtra(EditStandardRoundFragment.STANDARD_ROUND_ID, item.getId());
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null && item.getItemId() == R.id.action_filter) {
            if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
            } else {
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        startActivityForResult(
                new Intent(getActivity(), SimpleFragmentActivity.EditStandardRoundActivity.class),
                NEW_STANDARD_ROUND);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == NEW_STANDARD_ROUND) {
            getActivity().setResult(resultCode, data);
            getActivity().onBackPressed();
        }
    }

    protected class StandardRoundAdapter extends NowListAdapter<StandardRound> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.standard_round_select_item, parent, false);
            return new ViewHolder(itemView);
        }
    }

    public class ViewHolder extends SelectableViewHolder<StandardRound> {
        private final TextView mName;
        private final ImageView mImage;
        private final TextView mDetails;

        public ViewHolder(View itemView) {
            super(itemView, mSingleSelector, StandardRoundFragment.this);
            mImage = (ImageView) itemView.findViewById(R.id.image);
            mName = (TextView) itemView.findViewById(android.R.id.text1);
            mDetails = (TextView) itemView.findViewById(android.R.id.text2);
        }

        @Override
        public void bindCursor() {
            mName.setText(mItem.name);

            if (mItem.equals(currentSelection)) {
                mImage.setVisibility(View.VISIBLE);
                mDetails.setVisibility(View.VISIBLE);
                mDetails.setText(mItem.getDescription(getActivity()));
                mImage.setImageDrawable(mItem.getTargetDrawable(getActivity()));
            } else {
                mImage.setVisibility(View.GONE);
                mDetails.setVisibility(View.GONE);
            }
        }
    }
}
