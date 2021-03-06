package com.example.helloworld;

import java.io.IOException;

import com.example.helloworld.entity.Server;
import com.example.helloworld.entity.User;
import com.example.helloworld.fragments.inputcells.PictureInputCellFragment;
import com.example.helloworld.fragments.inputcells.SimpleTextInputCellFragment;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends Activity {
	SimpleTextInputCellFragment fragInputCellAccount;
	SimpleTextInputCellFragment fragInputEmailAddress;
	SimpleTextInputCellFragment fragInputCellPassword;
	SimpleTextInputCellFragment fragInputCellPasswordRepeat;
	SimpleTextInputCellFragment fragInputCellName;
	PictureInputCellFragment fragInputAvatar;
	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register);

		fragInputCellAccount = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_account);
		fragInputCellName = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_name);
		fragInputEmailAddress = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_email);
		fragInputCellPassword = (SimpleTextInputCellFragment) getFragmentManager()
				.findFragmentById(R.id.input_password);
		fragInputCellPasswordRepeat = (SimpleTextInputCellFragment) getFragmentManager()
				.findFragmentById(R.id.input_password_repeat);
		fragInputAvatar = (PictureInputCellFragment) getFragmentManager().findFragmentById(R.id.input_avatar);

		findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				submit();
			}
		});
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("���Ե�");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(false);
	}

	@Override
	protected void onResume() {
		super.onResume();

		fragInputCellAccount.setLabelText("�˻���");
		{
			fragInputCellAccount.setHintText("�������˻���");
		}

		fragInputCellName.setLabelText("�ǳ�");
		{
			fragInputCellName.setHintText("�������ǳ�");
		}

		fragInputCellPassword.setLabelText("����");
		{
			fragInputCellPassword.setHintText("����������");
			fragInputCellPassword.setIsPassword(true);
		}

		fragInputCellPasswordRepeat.setLabelText("�ظ�����");
		{
			fragInputCellPasswordRepeat.setHintText("���ظ���������");
			fragInputCellPasswordRepeat.setIsPassword(true);
		}

		fragInputEmailAddress.setLabelText("�����ʼ�");
		{
			fragInputEmailAddress.setHintText("�������������");
		}
	}

	void submit() {
		String password = fragInputCellPassword.getText();
		String passwordRepeat = fragInputCellPasswordRepeat.getText();

		if (!password.equals(passwordRepeat)) {

			Toast.makeText(RegisterActivity.this, "������������벻һ��", Toast.LENGTH_SHORT).show();

			return;
		}

		password = MD5.getMD5(password);

		String account = fragInputCellAccount.getText();
		String name = fragInputCellName.getText();
		String email = fragInputEmailAddress.getText();

		progressDialog.show();

		OkHttpClient client = Server.getSharedClient();

		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM)
				.addFormDataPart("account", account).addFormDataPart("name", name).addFormDataPart("email", email)
				.addFormDataPart("password", password);

		if (fragInputAvatar.getPngData() != null) {
			requestBodyBuilder.addFormDataPart("avatar", "avatar",
					RequestBody.create(MediaType.parse("image/png"), fragInputAvatar.getPngData()));
		}
		Request request = Server.requestBuilderWithApi("register").method("get", null).post(requestBodyBuilder.build())
				.build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, final Response arg1) throws IOException {
				// TODO Auto-generated method stub
				String jsonString = arg1.body().string();
				ObjectMapper mapper = new ObjectMapper();
				User user = null;

				try {
					user = mapper.readValue(jsonString, User.class);
					if (user != null) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								RegisterActivity.this.onResponse(arg0, arg1);
							}
						});
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(RegisterActivity.this, "���ݽ����쳣", Toast.LENGTH_SHORT).show();
							progressDialog.dismiss();
						}
					});
					return;
				}

			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				RegisterActivity.this.onFailure(arg0, arg1);
			}

		});
	}

	private void onResponse(Call call, Response response) {
		progressDialog.dismiss();
		try {
			new AlertDialog.Builder(this).setTitle("ע��ɹ�").setMessage("success!")
					.setNegativeButton("ȷ��", new OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							finish();
						}
					}).show();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			onFailure(call, e);
		}
	}

	private void onFailure(Call arg0, Exception arg1) {
		progressDialog.dismiss();
		new AlertDialog.Builder(this).setTitle("����ʧ��").setMessage(arg1.getLocalizedMessage())
				.setNegativeButton("��", null).show();
	}
}
