import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Env {
	
	public static State begin(final State col,final Tr trName) {
		Map<Tr,TrState> newTrans = new HashMap<Tr,TrState>();
		newTrans.putAll(col.trs().trs());
		newTrans.put(trName, new TrState(Map.<Var, Integer> of()));
		
		return new State(
				col.no() + 1,
				col.db(),
				new Trs(newTrans));
	}

	public static State create(State col, Tr trName, Var varName, int val) {
		if (col.db().vars().vars().containsKey(varName)) {
			throw new RuntimeException("The variable is already exist. " + varName);
		}
		
		Map<Var, Integer> newVars = new HashMap<Var, Integer>();
		newVars.putAll(col.db().vars().vars());
		newVars.put(varName, val);
		
		return new State(
				col.no() + 1,
				new DB(new Vars(newVars),col.db().locks()),
				col.trs());
	}
	
	public static State read(State col, Tr trName, Var varName) {
		for (Lock l : col.db().locks().locks()) {
			if (l.trName() != trName && l.var() == varName && l.type() != LockType.SH) {
				throw new RuntimeException("The varible is locked by others. " + trName + " " + varName);
			}
		}
		
		Map<Var, Integer> newTmpVars = new HashMap<Var, Integer>();
		newTmpVars.putAll(col.trs().trs().get(trName).tmpVars());
		newTmpVars.put(varName, col.db().vars().vars().get(varName));

		Map<Tr, TrState> newTransMap = new HashMap<Tr, TrState>();
		newTransMap.putAll(col.trs().trs());
		newTransMap.put(trName, new TrState(newTmpVars));
		
		return new State(
				col.no() + 1,
				col.db(),
				new Trs(newTransMap));
	}
	
	public static State write(State col, Tr trName, Var varName, Integer val) {
		for (Lock l : col.db().locks().locks()) {
			if (l.trName() != trName && l.var() == varName) {
				throw new RuntimeException("The varible is locked by others. " + trName + " " + varName);
			}
		}
		
		Map<Var, Integer> newDbVars = new HashMap<Var, Integer> ();
		newDbVars.putAll(col.db().vars().vars());
		newDbVars.put(varName, val);
		
		return new State(
				col.no() + 1,
				new DB(new Vars(newDbVars), col.db().locks()),
				col.trs());
	}
	
	public static State lock(State col, Tr trName, Var varName, LockType lt) {
		Lock newLock = new Lock(varName, trName, lt);
		for (Lock l : col.db().locks().locks()) {
			if (l.var() == varName && (lt == LockType.EX || l.type() == LockType.EX)) {
				throw new RuntimeException("Can't get lock: " + newLock);
			}
		}
		
		Set<Lock> newLocks = new HashSet<Lock>();
		newLocks.addAll(col.db().locks().locks());
		newLocks.add(newLock);
		
		return new State(
				col.no() + 1,
				new DB(col.db().vars(),new Locks(newLocks)),
				col.trs());
	}
	
	public static State unlock(State col, Tr trName, Var varName, LockType lt) {
		Set<Lock> newLocks = new HashSet<Lock>();
		newLocks.addAll(col.db().locks().locks());
		newLocks.remove(new Lock(varName,trName,lt));
		
		return new State(
				col.no() + 1,
				new DB(col.db().vars(),new Locks(newLocks)),
				col.trs());
	}

}
