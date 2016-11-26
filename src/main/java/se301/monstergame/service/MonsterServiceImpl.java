package se301.monstergame.service;

import java.net.UnknownHostException;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.DBAddress;
import com.mongodb.MongoClient;

import se301.monstergame.model.Monster;

@Service("monsterService")
public class MonsterServiceImpl implements MonsterService {

	private static MongoOperations mongoOps;

	static {
		try {
			String address = "127.0.0.1:27017";
			String dbname = "monstergame";
			mongoOps = new MongoTemplate(new MongoClient(new DBAddress(address)), dbname);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("Is MongoDB running ?");
		}
	}

	public List<Monster> findAllMonsters() {
		return mongoOps.findAll(Monster.class);
	}

	public Monster findById(long id) {
		BasicQuery query = new BasicQuery("{ id : " + id + " }");
		return mongoOps.findOne(query, Monster.class);
	}

	public Monster findByName(String name) {
		BasicQuery query = new BasicQuery("{ monstername : \"" + name + "\" }");
		return mongoOps.findOne(query, Monster.class);
	}

	public long getNextId() {
		//More than just a "Basic" query ...
		Query query = new Query();
		query.with(new Sort(Sort.Direction.DESC, "_id"));
		query.limit(1);
		Monster maxIdMonster = mongoOps.findOne(query, Monster.class);
		if(maxIdMonster == null) {
			return 0;
		}
		long nextId = maxIdMonster.getId() + 1;
		return nextId;
	}

	public void saveMonster(Monster monster) {
		monster.setId(getNextId());
		mongoOps.insert(monster);
	}

	public void updateMonster(Monster monster) {
		// Save is an "upsert" 
		//   ->if monster id does not exist, it will insert.
		//   ->if monster id does exist, it will update it.
		mongoOps.save(monster);
	}

	public void deleteMonsterById(long id) {
		BasicQuery query = new BasicQuery("{ id : " + id + " }");
		mongoOps.remove(query, Monster.class);
	}

	public boolean doesMonsterExist(Monster monster) {
		return findByName(monster.getMonstername()) != null;
	}

	public void deleteAllMonsters() {
		mongoOps.dropCollection(Monster.class);
	}
}
