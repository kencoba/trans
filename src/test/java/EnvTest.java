import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class EnvTest {

	@Test
	public void testBegin() {

		State initial = new State(0, new DB(
				new Vars(Map.<Var, Integer> of()), new Locks(Set.<Lock> of())),
				new Trs(Map.<Tr, TrState> of()));

		State expected = new State(1,
				new DB(new Vars(Map.<Var, Integer> of()), new Locks(Set.<Lock> of())),
				new Trs(Map.<Tr, TrState> of(
						Tr.A,
						new TrState(Map.<Var, Integer> of()))));

		State actual = Env.begin(initial, Tr.A);

		assertEquals(expected, actual);
	}

	@Test
	public void testCreate() {
		State initial = new State(0,
				new DB(new Vars(Map.<Var, Integer> of()), new Locks(Set.<Lock> of())),
				new Trs(Map.<Tr, TrState> of(
						Tr.A,
						new TrState(Map.<Var, Integer> of()))));

		State expected = new State(1,
				new DB(new Vars(Map.<Var, Integer> of(Var.X, 1)), new Locks(Set.<Lock> of())),
				new Trs(Map.<Tr, TrState> of(
						Tr.A,
						new TrState(Map.<Var, Integer> of()))));

		State actual = Env.create(initial, Tr.A, Var.X, 1);

		assertEquals(expected, actual);

	}

	@Test
	public void testRead() {
		State initial = new State(0,
				new DB(new Vars(Map.<Var, Integer> of(Var.X, 1)), new Locks(Set.<Lock> of())),
				new Trs(Map.<Tr, TrState> of(
						Tr.A,
						new TrState(Map.<Var, Integer> of()))));

		State expected = new State(1,
				new DB(new Vars(Map.<Var, Integer> of(Var.X, 1)), new Locks(Set.<Lock> of())),
				new Trs(Map.<Tr, TrState> of(
						Tr.A,
						new TrState(Map.<Var, Integer> of(Var.X, 1)))));

		State actual = Env.read(initial, Tr.A, Var.X);

		assertEquals(expected, actual);
	}

	@Test
	public void testWrite() {
		State initial = new State(0,
				new DB(new Vars(Map.<Var, Integer> of(Var.X, 1)), new Locks(Set.<Lock> of())),
				new Trs(Map.<Tr, TrState> of(
						Tr.A,
						new TrState(Map.<Var, Integer> of(Var.X, 1)))));

		State expected = new State(1,
				new DB(new Vars(Map.<Var, Integer> of(Var.X, 2)), new Locks(Set.<Lock> of())),
				new Trs(Map.<Tr, TrState> of(
						Tr.A,
						new TrState(Map.<Var, Integer> of(Var.X, 1)))));

		Integer x = initial.trs().get(Tr.A).get(Var.X);
		State actual = Env.write(initial, Tr.A, Var.X, x + 1);

		assertEquals(expected, actual);
	}

	@Test
	public void testLock_newSH() {
		State initial = new State(0,
				new DB(new Vars(Map.<Var, Integer> of(Var.X, 1)), new Locks(Set.<Lock> of())),
				new Trs(Map.<Tr, TrState> of(
						Tr.A,
						new TrState(Map.<Var, Integer> of(Var.X, 1)))));

		State expected = new State(1,
				new DB(new Vars(Map.<Var, Integer> of(Var.X, 1)), new Locks(Set.<Lock> of(
						new Lock(Var.X, Tr.A, LockType.SH)))),
				new Trs(Map.<Tr, TrState> of(
						Tr.A,
						new TrState(Map.<Var, Integer> of(Var.X, 1)))));

		State actual = Env.lock(initial, Tr.A, Var.X, LockType.SH);

		assertEquals(expected, actual);
	}

	@Test
	public void testLock_newEX() {
		State initial = new State(0,
				new DB(new Vars(Map.<Var, Integer> of(Var.X, 1)), new Locks(Set.<Lock> of())),
				new Trs(Map.<Tr, TrState> of(
						Tr.A,
						new TrState(Map.<Var, Integer> of(Var.X, 1)))));

		State expected = new State(1,
				new DB(new Vars(Map.<Var, Integer> of(Var.X, 1)), new Locks(Set.<Lock> of(
						new Lock(Var.X, Tr.A, LockType.EX)))),
				new Trs(Map.<Tr, TrState> of(
						Tr.A,
						new TrState(Map.<Var, Integer> of(Var.X, 1)))));

		State actual = Env.lock(initial, Tr.A, Var.X, LockType.EX);

		assertEquals(expected, actual);
	}

	@Test
	public void testLock_EX_EX() {
		State initial = new State(0,
				new DB(new Vars(Map.<Var, Integer> of(Var.X, 1)),
						new Locks(Set.<Lock> of(new Lock(Var.X, Tr.A, LockType.EX)))),
				new Trs(Map.<Tr, TrState> of(
						Tr.A,
						new TrState(Map.<Var, Integer> of(Var.X, 1)))));

		assertThrows(
				RuntimeException.class,
				() -> Env.lock(initial, Tr.B, Var.X, LockType.EX), "Expected lock() to throw, but it didn't.");
	}

	@Test
	public void testLock_EX_SH() {
		State initial = new State(0,
				new DB(new Vars(Map.<Var, Integer> of(Var.X, 1)),
						new Locks(Set.<Lock> of(new Lock(Var.X, Tr.A, LockType.EX)))),
				new Trs(Map.<Tr, TrState> of(
						Tr.A,
						new TrState(Map.<Var, Integer> of(Var.X, 1)))));

		assertThrows(
				RuntimeException.class,
				() -> Env.lock(initial, Tr.B, Var.X, LockType.SH), "Expected lock() to throw, but it didn't.");
	}

	@Test
	public void testLock_SH_EX() {
		State initial = new State(0,
				new DB(new Vars(Map.<Var, Integer> of(Var.X, 1)),
						new Locks(Set.<Lock> of(new Lock(Var.X, Tr.A, LockType.SH)))),
				new Trs(Map.<Tr, TrState> of(
						Tr.A,
						new TrState(Map.<Var, Integer> of(Var.X, 1)))));

		assertThrows(
				RuntimeException.class,
				() -> Env.lock(initial, Tr.B, Var.X, LockType.EX), "Expected lock() to throw, but it didn't.");
	}

	@Test
	public void testLock_SH_SH() {
		State initial = new State(0,
				new DB(new Vars(Map.<Var, Integer> of(Var.X, 1)),
						new Locks(Set.<Lock> of(new Lock(Var.X, Tr.A, LockType.SH)))),
				new Trs(Map.<Tr, TrState> of(
						Tr.A,
						new TrState(Map.<Var, Integer> of(Var.X, 1)))));

		State expected = new State(1,
				new DB(new Vars(Map.<Var, Integer> of(Var.X, 1)),
						new Locks(Set.<Lock> of(
								new Lock(Var.X, Tr.A, LockType.SH),
								new Lock(Var.X, Tr.B, LockType.SH)
								))),
				new Trs(Map.<Tr, TrState> of(
						Tr.A,
						new TrState(Map.<Var, Integer> of(Var.X, 1)))));
		
		State actual = Env.lock(initial, Tr.B, Var.X, LockType.SH);
		
		assertEquals(expected, actual);
	}

	@Test
	public void testRead_SH_lock() {
		State initial = new State(0,
				new DB(new Vars(Map.<Var, Integer> of(Var.X, 1)),
						new Locks(Set.<Lock> of(new Lock(Var.X, Tr.A, LockType.SH)))),
				new Trs(Map.<Tr, TrState> of(
						Tr.A,
						new TrState(Map.<Var, Integer> of(Var.X, 1)),
						Tr.B,
						new TrState(Map.<Var, Integer> of()))));
		
		State expected = new State(1,
				new DB(new Vars(Map.<Var, Integer> of(Var.X, 1)),
						new Locks(Set.<Lock> of(
								new Lock(Var.X, Tr.A, LockType.SH)
								))),
				new Trs(Map.<Tr, TrState> of(
						Tr.A,
						new TrState(Map.<Var, Integer> of(Var.X, 1)),
						Tr.B,
						new TrState(Map.<Var, Integer> of(Var.X, 1)))));

		State actual = Env.read(initial, Tr.B, Var.X);
		
		assertEquals(expected, actual);
	}
}
