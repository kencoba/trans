import java.util.Map;

public record Trs(Map<Tr, TrState> trs) {

	public TrState get(Tr trName) {
		return trs.get(trName);
	}
}
