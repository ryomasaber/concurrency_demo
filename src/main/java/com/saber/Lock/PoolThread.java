package com.saber.Lock;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * 连接池
 * Created by Saber on 2021/3/25 17:27
 */
@Slf4j
public class PoolThread {

	public static void main(String[] args) {

		int poolSize = 10;
		int users = 12;

		Pool pool = new Pool(poolSize);

		log.debug("------  {}  -------","start");

		List<Thread> list = new ArrayList<>(users);

		for (int i = 0; i < users; i++) {
			Thread t = new Thread(() -> {
				//获取连接
				Connection conn = pool.getConnection();

				try {
					Thread.sleep(new Random().nextInt(1000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					//释放连接
					pool.close(conn);
				}

			}, "get_t" + i);
			t.start();

			list.add(t);
		}

		list.forEach(thread -> {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		log.debug("------  {}  -------","end");

	}


}

@Slf4j
class Pool{

	//连接池大小
	private final int poolSize;
	//连接池数组
	private Connection[] connections;
	//连接状态数组,记录连接池数组对应下标连接状态 //0代表空闲 ,1代表正在使用
	private AtomicIntegerArray states;

	public Pool(int poolSize) {
		log.debug("pool init poolSize:{}",poolSize);

		this.poolSize = poolSize;
		this.states = new AtomicIntegerArray(poolSize);
		this.connections = new Connection[poolSize];
		for (int i = 0; i < poolSize; i++) {
			this.connections[i] = new MockConnection("连接"+i);
		}
	}

	/**
	 * 获取连接
	 *
	 * @return Connection
	 */
	public Connection getConnection(){
		while (true){

			//判断是否有空闲连接
			for (int i = 0; i < poolSize; i++) {
				//0:空闲, 1:使用
				if(states.get(i) == 0){
					//将获取到连接设置状态为 1:使用
					if(states.compareAndSet(i,0,1)){

						log.debug("get connection successfully!{}",connections[i]);
						return connections[i];
					}
				}
			}
			//没有空闲连接则等待
			synchronized (this){
				try {
					log.debug("no free connection waiting...");

					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 释放连接
	 * @param conn 连接
	 */
	public void close(Connection conn){

		//判断是否是池中的连接
		for (int i = 0; i < poolSize; i++) {
			//如果是池中的连接,则释放(修改状态)
			if (connections[i] == conn) {

				//0代表空闲 ,1代表正在使用
				states.set(i,0);

				log.info("close successfully!");

				//通知正在等待获取连接的线程
				synchronized (this) {
					this.notifyAll();
				}
				break;
			}
		}

	}
}

@Slf4j
class MockConnection implements Connection{

	//连接名称
	String name;

	public MockConnection(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("MockConnection{");
		sb.append("name='").append(name).append('\'');
		sb.append('}');
		return sb.toString();
	}

	@Override
	public Statement createStatement() throws SQLException {
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return null;
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		return null;
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		return null;
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {

	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return false;
	}

	@Override
	public void commit() throws SQLException {

	}

	@Override
	public void rollback() throws SQLException {

	}

	@Override
	public void close() throws SQLException {

	}

	@Override
	public boolean isClosed() throws SQLException {
		return false;
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return null;
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {

	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return false;
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {

	}

	@Override
	public String getCatalog() throws SQLException {
		return null;
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {

	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		return 0;
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return null;
	}

	@Override
	public void clearWarnings() throws SQLException {

	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return null;
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return null;
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return null;
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

	}

	@Override
	public void setHoldability(int holdability) throws SQLException {

	}

	@Override
	public int getHoldability() throws SQLException {
		return 0;
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return null;
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		return null;
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {

	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {

	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return null;
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		return null;
	}

	@Override
	public Clob createClob() throws SQLException {
		return null;
	}

	@Override
	public Blob createBlob() throws SQLException {
		return null;
	}

	@Override
	public NClob createNClob() throws SQLException {
		return null;
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return null;
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		return false;
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {

	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {

	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		return null;
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return null;
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return null;
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return null;
	}

	@Override
	public void setSchema(String schema) throws SQLException {

	}

	@Override
	public String getSchema() throws SQLException {
		return null;
	}

	@Override
	public void abort(Executor executor) throws SQLException {

	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		return 0;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}
}
