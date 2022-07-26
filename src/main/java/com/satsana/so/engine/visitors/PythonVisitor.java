package com.satsana.so.engine.visitors;

import com.satsana.so.engine.model.Context;
import com.satsana.so.engine.transforms.Add;
import com.satsana.so.engine.transforms.MulMod;
import com.satsana.so.engine.transforms.MulModInv;
import com.satsana.so.engine.transforms.Not;
import com.satsana.so.engine.transforms.Permutation;
import com.satsana.so.engine.transforms.RotateLeft;
import com.satsana.so.engine.transforms.RotateRight;
import com.satsana.so.engine.transforms.Substract;
import com.satsana.so.engine.transforms.Xor;

public class PythonVisitor extends LanguageVisitor {
	private String variable, temp, i, result, mask;
	private boolean hasPermutation;
	
	@Override
	public StringBuilder initialise(Context context) {
		// Generate variable names
		variable = generateName();
		temp = generateName();
		i = generateName();
		mask = hex(context.getMask());
		result = "string";
		hasPermutation = context.getReverse().containsPermutation();
		// Write bytes in string
		StringBuilder sb = new StringBuilder();
		sb.append(result+" = [");
		for (long b : context.getBytes())
			sb.append(hex(b)+",");
		sb.deleteCharAt(sb.length()-1) 	// remove last comma
			.append("]\n");
		// Write for loop
		sb.append(String.format("for %s in range(len(%s)):\n", i, result));
		sb.append("\t"+variable+" = "+result+"["+i+"]\n");
		return sb;
	}

	@Override
	public void finalise(StringBuilder in) {
		in.append(String.format("\t%s[%s] = chr(%s & %s)\n", result, i, variable, mask));
		if (hasPermutation)
			in.append(String.format("del %s, %s, %s\n", i, variable, temp));
		else 
			in.append(String.format("del %s, %s\n", i, variable));
		in.append(String.format("%s = ''.join(%s)\n", result, result))
			.append("print("+result+")");
	}

	@Override
	public void visit(Add a, StringBuilder in) {
		in.append("\t").append(variable)
			.append(" += ")
			.append(hex(a.getValue()))
			.append("\n");
	}

	@Override
	public void visit(MulMod mm, StringBuilder in) {
		in.append("\t").append(variable)
			.append(" = ").append("("+variable).append(" * "+hex(mm.getValue())+") % "+hex(mm.getModulo()))
			.append("\n");
	}

	@Override
	public void visit(MulModInv mmi, StringBuilder in) {
		visit(MulMod.class.cast(mmi), in);
	}

	@Override
	public void visit(Not n, StringBuilder in) {
		in.append("\t").append(variable)
			.append(" = ").append("~"+variable+" & "+hex(n.getMask()))
			.append("\n");
	}

	@Override
	public void visit(Permutation p, StringBuilder in) {
		in.append("\t").append(temp)
			.append(" = ").append("(("+variable+" >> "+hex(p.getPosition1())+")")
			.append(" ^ ("+variable+" >> "+hex(p.getPosition2())+")) & ((1 << "+hex(p.getBits())+") - 1)")
			.append("\n");
		in.append("\t").append(variable)
			.append(" ^= ").append("("+temp+" << "+hex(p.getPosition1())+") | ("+temp+" << "+hex(p.getPosition2())+")")
			.append("\n");
	}

	@Override
	public void visit(RotateLeft rl, StringBuilder in) {
		String mask = hex(rl.getMask());
		in.append("\t").append(variable)
			.append(" = ").append("((("+variable+" & "+mask+") >> "+hex(rl.lhs())+") | (")
			.append(variable+" << "+hex(rl.rhs())+")) & "+mask)
			.append("\n");
	}

	@Override
	public void visit(RotateRight rr, StringBuilder in) {
		String mask = hex(rr.getMask());
		in.append("\t").append(variable)
			.append(" = ").append("((("+variable+" & "+mask+") << "+hex(rr.lhs())+") | (")
			.append(variable+" >> "+hex(rr.rhs())+")) & "+mask)
			.append("\n");
	}

	@Override
	public void visit(Substract s, StringBuilder in) {
		in.append("\t").append(variable)
			.append(" -= ")
			.append(hex(s.getValue()))
			.append("\n");
	}

	@Override
	public void visit(Xor x, StringBuilder in) {
		in.append("\t").append(variable)
			.append(" ^= ").append(hex(x.getValue()))
			.append("\n");
	}
}
