package com.example.gcchat222;

import com.example.enity.User;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OwnerCenterFragment extends Fragment {
	private TextView petName;
	private TextView account;
	private TextView password;
	private User user;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_ownercenter, container,
				false);
		petName = (TextView) view.findViewById(R.id.petName);
		account = (TextView) view.findViewById(R.id.account);
		password = (TextView) view.findViewById(R.id.password);
		Bundle bundle = this.getArguments();
		if (bundle != null) {
			user = (User) bundle.getSerializable("user");
		}
		petName.setText(user.getPetName());
		account.setText(user.getAccount());
		password.setText(user.getPassword());
		return view;
	}
}
