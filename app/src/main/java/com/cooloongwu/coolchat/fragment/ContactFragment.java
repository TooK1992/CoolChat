package com.cooloongwu.coolchat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cooloongwu.coolchat.R;
import com.cooloongwu.coolchat.activity.FriendSearchActivity;
import com.cooloongwu.coolchat.activity.GroupActivity;
import com.cooloongwu.coolchat.adapter.ContactAdapter;
import com.cooloongwu.coolchat.base.BaseFragment;
import com.cooloongwu.coolchat.utils.GreenDAOUtils;
import com.cooloongwu.coolchat.entity.Contact;
import com.cooloongwu.greendao.gen.ContactDao;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


/**
 * 联系人界面
 */
public class ContactFragment extends BaseFragment implements View.OnClickListener {

    private ContactAdapter adapter;
    private ArrayList<Contact> listData = new ArrayList<>();

    private LinearLayout layout_addfriends;
    private RecyclerView recyclerView;

    private TextView contact_text_num;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        initViews(view);
        getFriendsListFromDB();
        return view;
    }

    private void initViews(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        layout_addfriends = (LinearLayout) view.findViewById(R.id.layout_addfriends);
        contact_text_num = (TextView) view.findViewById(R.id.contact_text_num);

        TextView text_addfriend = (TextView) view.findViewById(R.id.text_addfriend);
        TextView text_group = (TextView) view.findViewById(R.id.text_group);
        text_addfriend.setOnClickListener(this);
        text_group.setOnClickListener(this);

        adapter = new ContactAdapter(getActivity(), listData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void getFriendsListFromDB() {
        ContactDao contactDao = GreenDAOUtils.getInstance(getActivity()).getContactDao();
        List<Contact> contacts = contactDao.queryBuilder()
                .build()
                .list();
        if (contacts.size() > 0) {
            layout_addfriends.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            contact_text_num.setText("好友（" + contacts.size() + "）");
            //加载前清空下，否则更新时会导致重复加载
            listData.clear();
            listData.addAll(contacts);
            adapter.notifyDataSetChanged();
        } else {
            layout_addfriends.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onEventMainThread(Contact contact) {
        getFriendsListFromDB();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_addfriend:
                getActivity().startActivity(new Intent(getActivity(), FriendSearchActivity.class));
                break;
            case R.id.text_group:
                getActivity().startActivity(new Intent(getActivity(), GroupActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
