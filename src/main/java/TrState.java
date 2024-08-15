import java.util.Map;

public record TrState(Map<Var, Integer> tmpVars) {

	public Integer get(Var x) {
		return tmpVars.get(x);
	}

}