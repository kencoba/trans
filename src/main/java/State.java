public record State(Integer no, DB db, Trs trs) {

	public String toString() {
		return String.format("State(%d, %s, %s)", no, db.toString(), trs.toString());
	}
}
