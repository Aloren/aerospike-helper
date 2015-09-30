package com.aerospike.helper.query;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.Value;
import com.aerospike.client.query.Statement;

public class DeleterTests extends HelperTest{

	public DeleterTests(boolean useAuth) {
		super(useAuth);
	}
	@Test
	public void deleteByKey(){
		for (int x = 1; x <= QueryEngineTests.RECORD_COUNT; x++){
			String keyString = "selector-test:"+x;
			Key key = new Key(QueryEngineTests.NAMESPACE, QueryEngineTests.SET_NAME, keyString);
			KeyQualifier kq = new KeyQualifier(Value.get(keyString));
			Statement stmt = new Statement();
			stmt.setNamespace(QueryEngineTests.NAMESPACE);
			stmt.setSetName(QueryEngineTests.SET_NAME);
			Map<String, Long> counts = queryEngine.delete(stmt, kq);
			Assert.assertEquals((Long)1L, (Long)counts.get("write"));
			Record record = this.client.get(null, key);
			Assert.assertNull(record);
		}
	}
	@Test
	public void deleteByDigest(){
		for (int x = 1; x <= QueryEngineTests.RECORD_COUNT; x++){
			String keyString = "selector-test:"+x;
			Key key = new Key(QueryEngineTests.NAMESPACE, QueryEngineTests.SET_NAME, keyString);
			KeyQualifier kq = new KeyQualifier(key.digest);
			Statement stmt = new Statement();
			stmt.setNamespace(QueryEngineTests.NAMESPACE);
			stmt.setSetName(QueryEngineTests.SET_NAME);
			Map<String, Long> counts = queryEngine.delete(stmt, kq);
			Assert.assertEquals((Long)1L, (Long)counts.get("write"));
			Record record = this.client.get(null, key);
			Assert.assertNull(record);
		}
	}
	@Test
	public void deleteStartsWith() {
		Qualifier qual1 = new Qualifier("color", Qualifier.FilterOperation.ENDS_WITH, Value.get("e"));
		Statement stmt = new Statement();
		stmt.setNamespace(QueryEngineTests.NAMESPACE);
		stmt.setSetName(QueryEngineTests.SET_NAME);
		Map<String, Long> counts = queryEngine.delete(stmt, qual1);
		//System.out.println(counts);
		Assert.assertEquals((Long)40L, (Long)counts.get("read"));
		Assert.assertEquals((Long)40L, (Long)counts.get("write"));
		
	}
	@Test
	public void deleteEndsWith() throws IOException {
		Qualifier qual1 = new Qualifier("color", Qualifier.FilterOperation.EQ, Value.get("blue"));
		Qualifier qual2 = new Qualifier("name", Qualifier.FilterOperation.START_WITH, Value.get("na"));
		Statement stmt = new Statement();
		stmt.setNamespace(QueryEngineTests.NAMESPACE);
		stmt.setSetName(QueryEngineTests.SET_NAME);
		Map<String, Long> counts = queryEngine.delete(stmt, qual1, qual2);
		//System.out.println(counts);
		Assert.assertEquals((Long)20L, (Long)counts.get("read"));
		Assert.assertEquals((Long)20L, (Long)counts.get("write"));
	}
	@Test
	public void deleteWithFilter() throws Exception {
		Key key = new Key(QueryEngineTests.NAMESPACE, QueryEngineTests.SET_NAME, "first-name-1");
		Bin firstNameBin = new Bin("first_name", "first-name-1");
		Bin lastNameBin = new Bin("last_name", "last-name-1");
		int age = 25;
		Bin ageBin = new Bin("age", age);
		this.client.put(null, key, firstNameBin, lastNameBin, ageBin);

		Qualifier qual1 = new Qualifier("last_name", Qualifier.FilterOperation.EQ, Value.get("last-name-1"));
		//DELETE FROM test.people WHERE last_name='last-name-1'
		Statement stmt = new Statement();
		stmt.setNamespace(QueryEngineTests.NAMESPACE);
		stmt.setSetName(QueryEngineTests.SET_NAME);
		Map<String, Long> counts = queryEngine.delete(stmt, qual1);
		Assert.assertEquals((Long)1L, (Long)counts.get("read"));
		Assert.assertEquals((Long)1L, (Long)counts.get("write"));
		Record record = this.client.get(null, key);
		Assert.assertNull(record);
	}

}
