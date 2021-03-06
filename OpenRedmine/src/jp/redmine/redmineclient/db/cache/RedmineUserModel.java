package jp.redmine.redmineclient.db.cache;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import jp.redmine.redmineclient.entity.RedmineConnection;
import jp.redmine.redmineclient.entity.RedmineIssue;
import jp.redmine.redmineclient.entity.RedmineJournal;
import jp.redmine.redmineclient.entity.RedmineUser;


public class RedmineUserModel implements IMasterModel<RedmineUser> {
	protected Dao<RedmineUser, Integer> dao;
	public RedmineUserModel(DatabaseCacheHelper helper) {
		try {
			dao = helper.getDao(RedmineUser.class);
		} catch (SQLException e) {
			Log.e("RedmineUserModel","getDao",e);
		}
	}

	public List<RedmineUser> fetchAll() throws SQLException{
		return dao.queryForAll();
	}

	public List<RedmineUser> fetchAll(int connection) throws SQLException{
		List<RedmineUser> item;
		item = dao.queryForEq(RedmineUser.CONNECTION, connection);
		if(item == null){
			item = new ArrayList<RedmineUser>();
		}
		return item;
	}

	public RedmineUser fetchById(int connection, int userId) throws SQLException{
		PreparedQuery<RedmineUser> query = dao.queryBuilder().where()
		.eq(RedmineUser.CONNECTION, connection)
		.and()
		.eq(RedmineUser.USER_ID, userId)
		.prepare();
		RedmineUser item = dao.queryForFirst(query);
		if(item == null)
			item = new RedmineUser();
		return item;
	}

	public RedmineUser fetchById(int id) throws SQLException{
		RedmineUser item;
		item = dao.queryForId(id);
		if(item == null)
			item = new RedmineUser();
		return item;
	}

	public int insert(RedmineUser item) throws SQLException{
		int count = dao.create(item);
		return count;
	}

	public int update(RedmineUser item) throws SQLException{
		int count = dao.update(item);
		return count;
	}
	public int delete(RedmineUser item) throws SQLException{
		int count = dao.delete(item);
		return count;
	}
	public int delete(int id) throws SQLException{
		int count = dao.deleteById(id);
		return count;
	}
	public void refreshItem(RedmineIssue data) throws SQLException{
		RedmineUser item;
		item = refreshItem(data.getConnectionId(),data.getAssigned());
		data.setAssigned(item);
		item = refreshItem(data.getConnectionId(),data.getAuthor());
		data.setAuthor(item);
	}
	public void refreshItem(RedmineJournal data) throws SQLException{
		RedmineUser item;
		item = refreshItem(data.getConnectionId(),data.getUser());
		data.setUser(item);
	}

	public RedmineUser refreshItem(RedmineConnection info,RedmineUser data) throws SQLException{
		return refreshItem(info.getId(),data);
	}
	public RedmineUser refreshItem(int connection_id,RedmineUser data) throws SQLException{
		if(data == null)
			return null;
		RedmineUser project = this.fetchById(connection_id, data.getUserId());
		data.setConnectionId(connection_id);
		if(project.getId() == null){
			this.insert(data);
			project = fetchById(connection_id, data.getUserId());
		} else {
			data.setId(project.getId());

			if(project.getModified() == null){
				project.setModified(new java.util.Date());
			}
			if(data.getModified() == null){
				data.setModified(new java.util.Date());
			}
			if(project.getModified().after(data.getModified())){
				this.update(data);
			}
		}
		return project;
	}

	@Override
	public long countByProject(int connection_id, long project_id)
			throws SQLException {
		QueryBuilder<RedmineUser, ?> builder = dao.queryBuilder();
		builder
			.setCountOf(true)
			.where()
				.eq(RedmineUser.CONNECTION, connection_id)
				;
		return dao.countOf(builder.prepare());
	}

	@Override
	public RedmineUser fetchItemByProject(int connection_id, long project_id,
			long offset, long limit) throws SQLException {
		QueryBuilder<RedmineUser, ?> builder = dao.queryBuilder();
		builder
			.limit(limit)
			.offset(offset)
			.orderBy(RedmineUser.NAME, true)
			.where()
				.eq(RedmineUser.CONNECTION, connection_id)
				;
		RedmineUser item = builder.queryForFirst();
		if(item == null)
			item = new RedmineUser();
		return item;
	}
}
