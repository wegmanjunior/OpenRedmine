package jp.redmine.redmineclient.adapter;

import java.sql.SQLException;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.db.cache.DatabaseCacheHelper;
import jp.redmine.redmineclient.db.cache.RedmineFilterModel;
import jp.redmine.redmineclient.db.cache.RedmineIssueModel;
import jp.redmine.redmineclient.entity.RedmineFilter;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.form.RedmineIssueListItemForm;

import android.view.LayoutInflater;
import android.view.View;

public class RedmineIssueListAdapter extends RedmineBaseAdapter<RedmineIssue> {
	private RedmineIssueModel model;
	private RedmineFilterModel mFilter;
	private RedmineFilter filter;
	protected Integer connection_id;
	protected Long project_id;
	@Override
	public void notifyDataSetChanged() {
		if(!isValidParameter())
			return;
		try {
			filter = mFilter.fetchByCurrent(connection_id, project_id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		super.notifyDataSetChanged();
	}
	public RedmineIssueListAdapter(DatabaseCacheHelper helper) {
		super();
		model = new RedmineIssueModel(helper);

		mFilter = new RedmineFilterModel(helper);
	}

	public void setupParameter(int connection, long project){
		connection_id = connection;
		project_id = project;
	}

	public boolean isValidParameter(){
		if(project_id == null || connection_id == null)
			return false;
		else
			return true;
	}

	@Override
	protected View getItemView(LayoutInflater infalInflater) {
		return infalInflater.inflate(R.layout.issueitem, null);
	}

	@Override
	protected void setupView(View view, RedmineIssue data) {
		RedmineIssueListItemForm form = new RedmineIssueListItemForm(view);
		form.setValue(data);
	}

	@Override
	protected int getDbCount() throws SQLException {
		if(filter == null)
			return 0;
		return (int) model.countByFilter(filter);
	}

	@Override
	protected RedmineIssue getDbItem(int position) throws SQLException {
		if(filter == null)
			return new RedmineIssue();
		return model.fetchItemByFilter(filter,(long) position, 1L);
	}

	@Override
	protected long getDbItemId(RedmineIssue item) {
		if(item == null){
			return -1;
		} else {
			return item.getId();
		}
	}

}
