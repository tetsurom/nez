package nez.expr;

import nez.SourceContext;
import nez.ast.SourcePosition;
import nez.runtime.Compiler;
import nez.runtime.Instruction;
import nez.util.UList;
import nez.util.UMap;

public class And extends Unary {
	And(SourcePosition s, Expression e) {
		super(s, e);
	}
	@Override
	public String getPredicate() {
		return "&";
	}
	@Override
	public boolean checkAlwaysConsumed(GrammarChecker checker, String startNonTerminal, UList<String> stack) {
		return false;
	}
	@Override
	public int inferTypestate(UMap<String> visited) {
		int t = this.inner.inferTypestate(visited);
		if(t == Typestate.ObjectType) {  // typeCheck needs to report error
			return Typestate.BooleanType;
		}
		return t;
	}
	@Override
	public Expression checkTypestate(GrammarChecker checker, Typestate c) {
		if(c.required == Typestate.ObjectType) {
			c.required = Typestate.BooleanType;
			this.inner = this.inner.checkTypestate(checker, c);
			c.required = Typestate.ObjectType;
		}
		else {
			this.inner = this.inner.checkTypestate(checker, c);
		}
		return this;
	}

	@Override
	public short acceptByte(int ch) {
		short r = this.inner.acceptByte(ch);
		if(r == Accept || r == Unconsumed) {
			return Unconsumed;
		}
		return r;
	}
	@Override
	public boolean match(SourceContext context) {
		long pos = context.getPosition();
		boolean b = this.inner.optimized.match(context);
		context.rollback(pos);
		return b;
	}
	@Override
	Expression dupUnary(Expression e) {
		return (this.inner != e) ? Factory.newAnd(this.s, e) : this;
	}
	
	@Override
	public Instruction encode(Compiler bc, Instruction next) {
		return bc.encodeAnd(this, next);
	}
	
	@Override
	protected int pattern(GEP gep) {
		return -1;
	}
	@Override
	protected void examplfy(GEP gep, StringBuilder sb, int p) {
		// TODO Auto-generated method stub
	}
	
}