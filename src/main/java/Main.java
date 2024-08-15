import java.util.Map;
import java.util.Set;

public class Main {
	public static void main(String[] args) {
	
		State st1 = new State(0,new DB(
				new Vars(Map.<Var,Integer>of()), new Locks(Set.<Lock>of())),
				new Trs(Map.<Tr,TrState>of()));

		State st2 = Env.begin(st1,Tr.A);
		System.out.println("Env.begin(st1,Tr.A); " + st2);

		State st3 = Env.create(st2,Tr.A,Var.X,1);
		System.out.println("Env.create(st2,Tr.A,Var.X,1); " + st3);

		State st4 = Env.read(st3,Tr.A,Var.X);
		System.out.println("Env.read(st3,Tr.A,Var.X); " + st4);
		
		Integer x = st4.trs().get(Tr.A).get(Var.X);
		State st5 = Env.write(st4, Tr.A, Var.X, x + 1);
		System.out.println("Env.write(st4, Tr.A, Var.X, x + 1); " + st5);
		
		State st6 = Env.lock(st5, Tr.A, Var.X, LockType.SH);
		
	}
}
