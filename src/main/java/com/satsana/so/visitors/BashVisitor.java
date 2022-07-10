package com.satsana.so.visitors;

import com.satsana.so.model.Context;
import com.satsana.so.transforms.Add;
import com.satsana.so.transforms.MulMod;
import com.satsana.so.transforms.MulModInv;
import com.satsana.so.transforms.Not;
import com.satsana.so.transforms.Permutation;
import com.satsana.so.transforms.RotateLeft;
import com.satsana.so.transforms.RotateRight;
import com.satsana.so.transforms.Substract;
import com.satsana.so.transforms.Xor;

public class BashVisitor extends LanguageVisitor {
	private String variable, variableName, tempName, i, iName, result, resultName;
	private boolean hasPermutations;
	
	// Arithmetic expansion helper method
	private final String ae(String format, Object...args) {
		return String.format("(("+format+"))", args);
	}
	
	@Override
	public StringBuilder initialise(Context context) {
		// Generate variable names
		variableName = generateName(); 
		variable = "$" + variableName;
		tempName = generateName();
		iName = generateName();
		i = "$"+iName;
		resultName = "string";
		result = "$" + resultName;
		hasPermutations = context.getReverse().containsPermutation(); 
		// Write bytes in string
		StringBuilder sb = new StringBuilder();
		long[] bytes = context.getBytes();
		sb.append(resultName+"=( ");
		for (long b : bytes)
			sb.append(hex(b)+" ");
		sb.append(")\n");
		// Write for loop
		sb.append(String.format("for %s in ${!%s[@]}; do\n", iName, resultName));
		sb.append("\t"+variableName+"=${"+resultName+"["+i+"]}\n");
		return sb;
	}

	@Override
	public void finalise(StringBuilder in) {
		in.append(String.format("\t%s[%s]=%s\n", resultName, i, variable))
			.append("done\n")
			.append(String.format("unset %s\n", iName))
			.append(String.format("unset %s\n", variableName));
		if (hasPermutations)
			in.append(String.format("unset %s\n", tempName));
		in.append(resultName+"=$(printf %b \"$(printf '\\\\U%x' \"${"+resultName+"[@]}\")\")\n")
			.append("echo "+result);
	}

	@Override
	public void visit(Add a, StringBuilder in) {
		if (a.getValue() == 1) {
			in.append("\t").append(ae("%s++", variableName))
				.append("\n");
			return;
		}
		in.append("\t")
			.append(ae("%s += %s", variableName, hex(a.getValue())))
			.append("\n");
	}

	@Override
	public void visit(MulMod mm, StringBuilder in) {
		in.append("\t")
			.append(ae("%s = (%s * %s) %% %s", variableName, variableName, hex(mm.getValue()), hex(mm.getModulo())))
			.append("\n");
	}

	@Override
	public void visit(MulModInv mmi, StringBuilder in) {
		visit(MulMod.class.cast(mmi), in);
	}

	@Override
	public void visit(Not n, StringBuilder in) {
		in.append("\t")
			.append(ae("%s = ~%s & %s", variableName, variableName, hex(n.getMask())))
			.append("\n");
	}

	@Override
	public void visit(Permutation p, StringBuilder in) {
		in.append("\t")
			.append(ae("%s = ((%s >> %s) ^ (%s >> %s)) & ((1 << %s)-1)", tempName, variableName, hex(p.getPosition1()), variableName, hex(p.getPosition2()), hex(p.getBits())))
			.append("\n");
		in.append("\t")
			.append(ae("%s ^= (%s << %s) | (%s << %s)", variableName, tempName, hex(p.getPosition1()), tempName, hex(p.getPosition2())))
			.append("\n");
	}

	@Override
	public void visit(RotateLeft rl, StringBuilder in) {
		String mask = hex(rl.getMask());
		in.append("\t")
			.append(ae("%s = (((%s & %s) >> %s) | (%s << %s)) & %s", variableName, variableName, mask, hex(rl.lhs()), variableName, hex(rl.rhs()), mask))
			.append("\n");
	}

	@Override
	public void visit(RotateRight rr, StringBuilder in) {
		String mask = hex(rr.getMask());
		in.append("\t")
			.append(ae("%s = (((%s & %s) << %s) | (%s >> %s)) & %s", variableName, variableName, mask, hex(rr.lhs()), variableName, hex(rr.rhs()), mask))
			.append("\n");
	}

	@Override
	public void visit(Substract s, StringBuilder in) {
		if (s.getValue() == 1) {
			in.append("\t").append(ae("%s--", variableName))
				.append("\n");
			return;
		}
		in.append("\t")
			.append(ae("%s -= %s", variableName, hex(s.getValue())))
			.append("\n");
	}

	@Override
	public void visit(Xor x, StringBuilder in) {
		in.append("\t")
			.append(ae("%s ^= %s", variableName, hex(x.getValue())))
			.append("\n");
	}
}
