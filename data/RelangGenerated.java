import org.reldb.relang.values.*;

public class RelangGenerated {
	
	/** __main */
	
	public static class __main_closure {
		public Value i;
		public __main_closure(Value i)
		{
			this.i = i;
		}
	}
	
	public static void __main() {
		Value i;
		System.out.println(new ValueInteger(0));
		for (i = new ValueInteger(0); ((i).lt(new ValueInteger(100000000))).booleanValue(); i = (i).add(new ValueInteger(1))) {
		}
		System.out.println(new ValueInteger(1));
	}
}
